package org.stepik.plugin.auth.webkit.network.PublicSuffixes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.CookieHandler;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * An RFC 6265-compliant cookie handler.
 */
public final class CookieManager extends CookieHandler {
    private final CookieStore store = new CookieStore();

    /**
     * Creates a new {@code CookieManager}.
     */
    public CookieManager() {
    }

    /**
     * Canonicalize a hostname as required by RFC 6265.
     */
    @NotNull
    private static String canonicalize(@NotNull String hostname) {
        // The hostname is already all-ASCII at this point
        return hostname.toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Map<String, List<String>> get(@Nullable URI uri, @Nullable Map<String, List<String>> requestHeaders) {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        if (requestHeaders == null) {
            throw new IllegalArgumentException("requestHeaders is null");
        }

        String cookieString = get(uri);

        Map<String, List<String>> result;
        if (cookieString != null) {
            result = new HashMap<>();
            result.put("Cookie", Collections.singletonList(cookieString));
        } else {
            result = Collections.emptyMap();
        }

        return result;
    }

    /**
     * Returns the cookie string for a given URI.
     */
    @Nullable
    private String get(@NotNull URI uri) {
        String host = uri.getHost();
        if (host == null || host.length() == 0) {
            return null;
        }
        host = canonicalize(host);

        String scheme = uri.getScheme();
        boolean secureProtocol = "https".equalsIgnoreCase(scheme)
                || "javascripts".equalsIgnoreCase(scheme);
        boolean httpApi = "http".equalsIgnoreCase(scheme)
                || "https".equalsIgnoreCase(scheme);

        List<Cookie> cookieList;
        synchronized (store) {
            cookieList = store.get(host, uri.getPath(),
                    secureProtocol, httpApi);
        }

        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookieList) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            sb.append(cookie.getName());
            sb.append('=');
            sb.append(cookie.getValue());
        }

        return sb.length() > 0 ? sb.toString() : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(@Nullable URI uri, @Nullable Map<String, List<String>> responseHeaders) {
        if (uri == null) {
            throw new IllegalArgumentException("uri is null");
        }
        if (responseHeaders == null) {
            throw new IllegalArgumentException("responseHeaders is null");
        }

        for (Map.Entry<String, List<String>> entry : responseHeaders.entrySet()) {
            String key = entry.getKey();
            if (!"Set-Cookie".equalsIgnoreCase(key)) {
                continue;
            }
            ExtendedTime currentTime = ExtendedTime.currentTime();
            // RT-15907: Process the list of headers in reverse order,
            // effectively restoring the order in which the headers were
            // received from the server. This is a temporary workaround for
            // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7059532
            ListIterator<String> it =
                    entry.getValue().listIterator(entry.getValue().size());
            while (it.hasPrevious()) {
                Cookie cookie = Cookie.parse(it.previous(), currentTime);
                if (cookie != null) {
                    put(uri, cookie);
                    currentTime = currentTime.incrementSubtime();
                }
            }
        }
    }

    /**
     * Puts an individual cookie.
     */
    private void put(@NotNull URI uri, @NotNull Cookie cookie) {
        String host = uri.getHost();
        if (host == null || host.length() == 0) {
            return;
        }
        host = canonicalize(host);

        if (PublicSuffixes.isPublicSuffix(cookie.getDomain())) {
            if (host.equals(cookie.getDomain())) {
                cookie.setDomain("");
            } else {
                return;
            }
        }

        if (cookie.getDomain().length() > 0) {
            if (Cookie.domainNotMatches(host, cookie.getDomain())) {
                return;
            } else {
                cookie.setHostOnly(false);
            }
        } else {
            cookie.setHostOnly(true);
            cookie.setDomain(host);
        }

        if (cookie.getPath() == null) {
            cookie.setPath(Cookie.defaultPath(uri));
        }

        boolean httpApi = "http".equalsIgnoreCase(uri.getScheme())
                || "https".equalsIgnoreCase(uri.getScheme());
        if (cookie.getHttpOnly() && !httpApi) {
            return;
        }

        synchronized (store) {
            Cookie oldCookie = store.get(cookie);
            if (oldCookie != null) {
                if (oldCookie.getHttpOnly() && !httpApi) {
                    return;
                }
                cookie.setCreationTime(oldCookie.getCreationTime());
            }

            store.put(cookie);
        }
    }

    public void clear() {
        store.removeAll();
    }

    public void save() {
        store.save();
    }
}
