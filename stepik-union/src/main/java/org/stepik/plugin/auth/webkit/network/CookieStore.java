package org.stepik.plugin.auth.webkit.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * A cookie store.
 */
final class CookieStore {
    private static final Logger logger = Logger.getInstance(CookieStore.class);
    private static final int MAX_BUCKET_SIZE = 50;
    private static final int TOTAL_COUNT_LOWER_THRESHOLD = 3000;
    private static final int TOTAL_COUNT_UPPER_THRESHOLD = 4000;

    private final Type bucketsType = new TypeToken<Map<String, Set<Cookie>>>() {}.getType();
    private final Gson gson = new GsonBuilder().create();

    /**
     * The mapping from domain names to cookie buckets.
     * Each cookie bucket stores the cookies associated with the
     * corresponding domain. Each cookie bucket is represented
     * by a Map<Cookie,Cookie> to facilitate retrieval of a cookie
     * by another cookie with the same name, domain, and path.
     */
    private final Map<String, Map<Cookie, Cookie>> buckets;

    /**
     * The total number of cookies currently in the store.
     */
    private int totalCount = 0;


    /**
     * Creates a new {@code CookieStore}.
     */

    CookieStore() {
        buckets = loadCookies();
        buckets.forEach((key, value) -> this.totalCount += value.size());
    }

    @NotNull
    private Map<String, Map<Cookie, Cookie>> loadCookies() {
        CredentialAttributes attributes;
        String serviceName = this.getClass().getName();
        attributes = new CredentialAttributes(serviceName, "cookies", getClass(), false);
        Credentials credentials = PasswordSafe.getInstance().get(attributes);
        String json = credentials != null ? credentials.getPasswordAsString() : null;

        Map<String, Set<Cookie>> cookies = new HashMap<>();
        if (json != null) {
            try {
                cookies = gson.fromJson(json, bucketsType);
            } catch (JsonSyntaxException e) {
                logger.warn(e);
            }
        }

        Map<String, Map<Cookie, Cookie>> buckets = new HashMap<>();

        cookies.forEach((key, value) -> {
            Map<Cookie, Cookie> bucket = new HashMap<>();
            value.forEach(cookie -> bucket.put(cookie, cookie));
            buckets.put(key, bucket);
        });

        return buckets;
    }

    /**
     * Returns the currently stored cookie with the same name, domain, and
     * path as the given cookie.
     */
    @Nullable
    Cookie get(@NotNull Cookie cookie) {
        Map<Cookie, Cookie> bucket = buckets.get(cookie.getDomain());
        if (bucket == null) {
            return null;
        }
        Cookie storedCookie = bucket.get(cookie);
        if (storedCookie == null) {
            return null;
        }
        if (storedCookie.hasExpired()) {
            bucket.remove(storedCookie);
            totalCount--;
            return null;
        }
        return storedCookie;
    }

    /**
     * Returns all the currently stored cookies that match the given query.
     */
    @NotNull
    List<Cookie> get(@NotNull String hostname, @Nullable String path, boolean secureProtocol, boolean httpApi) {
        ArrayList<Cookie> result = new ArrayList<>();

        String domain = hostname;
        while (domain.length() > 0) {
            Map<Cookie, Cookie> bucket = buckets.get(domain);
            if (bucket != null) {
                find(result, bucket, hostname, path, secureProtocol, httpApi);
            }
            int nextPoint = domain.indexOf('.');
            if (nextPoint != -1) {
                domain = domain.substring(nextPoint + 1);
            } else {
                break;
            }
        }

        result.sort(new GetComparator());

        long currentTime = System.currentTimeMillis();
        for (Cookie cookie : result) {
            cookie.setLastAccessTime(currentTime);
        }

        return result;
    }

    /**
     * Finds all the cookies that are stored in the given bucket and
     * match the given query.
     */
    private void find(
            @NotNull List<Cookie> list,
            @NotNull Map<Cookie, Cookie> bucket,
            @NotNull String hostname,
            @Nullable String path,
            boolean secureProtocol,
            boolean httpApi) {
        Iterator<Cookie> it = bucket.values().iterator();
        while (it.hasNext()) {
            Cookie cookie = it.next();
            if (cookie.hasExpired()) {
                it.remove();
                totalCount--;
                continue;
            }

            if (cookie.getHostOnly()) {
                if (!hostname.equalsIgnoreCase(cookie.getDomain())) {
                    continue;
                }
            } else {
                if (Cookie.domainNotMatches(hostname, cookie.getDomain())) {
                    continue;
                }
            }

            if (!Cookie.pathMatches(path, cookie.getPath())) {
                continue;
            }

            if (cookie.getSecureOnly() && !secureProtocol) {
                continue;
            }

            if (cookie.getHttpOnly() && !httpApi) {
                continue;
            }

            list.add(cookie);
        }
    }

    void removeAll() {
        totalCount = 0;
        buckets.clear();
    }

    void save() {
        CredentialAttributes attributes;
        String serviceName = getClass().getName();
        attributes = new CredentialAttributes(serviceName, "cookies", getClass(), false);
        Map<String, Set<Cookie>> cookies = new HashMap<>();
        buckets.forEach((key, value) -> cookies.put(key, new HashSet<>(value.values())));
        String serialized = gson.toJson(cookies, bucketsType);
        Credentials credentials = new Credentials(attributes.getUserName(), serialized);
        PasswordSafe.getInstance().set(attributes, credentials);
    }

    /**
     * Stores the given cookie.
     */
    void put(@NotNull Cookie cookie) {
        Map<Cookie, Cookie> bucket = buckets.computeIfAbsent(cookie.getDomain(), k -> new LinkedHashMap<>(20));
        if (cookie.hasExpired()) {
            if (bucket.remove(cookie) != null) {
                totalCount--;
            }
        } else {
            if (bucket.put(cookie, cookie) == null) {
                totalCount++;
                if (bucket.size() > MAX_BUCKET_SIZE) {
                    purge(bucket);
                }
                if (totalCount > TOTAL_COUNT_UPPER_THRESHOLD) {
                    purge();
                }
            }
        }
    }

    /**
     * Removes excess cookies from a given bucket.
     */
    private void purge(@NotNull Map<Cookie, Cookie> bucket) {
        Cookie earliestCookie = null;
        Iterator<Cookie> it = bucket.values().iterator();
        while (it.hasNext()) {
            Cookie cookie = it.next();
            if (cookie.hasExpired()) {
                it.remove();
                totalCount--;
            } else {
                if (earliestCookie == null || cookie.getLastAccessTime()
                        < earliestCookie.getLastAccessTime()) {
                    earliestCookie = cookie;
                }
            }
        }
        if (bucket.size() > MAX_BUCKET_SIZE) {
            bucket.remove(earliestCookie);
            totalCount--;
        }
    }

    /**
     * Removes excess cookies globally.
     */
    private void purge() {
        Queue<Cookie> removalQueue = new PriorityQueue<>(totalCount / 2,
                new CookieStore.RemovalComparator());

        for (Map.Entry<String, Map<Cookie, Cookie>> entry : buckets.entrySet()) {
            Map<Cookie, Cookie> bucket = entry.getValue();
            Iterator<Cookie> it = bucket.values().iterator();
            while (it.hasNext()) {
                Cookie cookie = it.next();
                if (cookie.hasExpired()) {
                    it.remove();
                    totalCount--;
                } else {
                    removalQueue.add(cookie);
                }
            }
        }

        while (totalCount > TOTAL_COUNT_LOWER_THRESHOLD) {
            Cookie cookie = removalQueue.remove();
            Map<Cookie, Cookie> bucket = buckets.get(cookie.getDomain());
            if (bucket != null) {
                bucket.remove(cookie);
                totalCount--;
            }
        }
    }

    private static final class GetComparator implements Comparator<Cookie> {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            String path1 = c1.getPath();
            String path2 = c2.getPath();

            int d = Integer.compare(path1 != null ? path1.length() : 0, path2 != null ? path2.length() : 0);
            if (d != 0) {
                return d;
            }
            return c1.getCreationTime().compareTo(c2.getCreationTime());
        }
    }

    private static final class RemovalComparator implements Comparator<Cookie> {
        @Override
        public int compare(Cookie c1, Cookie c2) {
            return (int) (c1.getLastAccessTime() - c2.getLastAccessTime());
        }
    }
}
