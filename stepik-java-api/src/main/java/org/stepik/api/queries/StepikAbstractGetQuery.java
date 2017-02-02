package org.stepik.api.queries;

import com.google.gson.JsonSyntaxException;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.Utils;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.AbstractObject;
import org.stepik.api.objects.AbstractObjectWithStringId;
import org.stepik.api.objects.ObjectsContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author meanmail
 */
public abstract class StepikAbstractGetQuery<T extends StepikAbstractGetQuery, R extends ObjectsContainer> extends StepikAbstractQuery<R> {
    private static final String IDS_KEY = "ids[]";
    private static final String JSON_EXTENSION = ".json";

    protected StepikAbstractGetQuery(@NotNull StepikAbstractAction stepikAction, @NotNull Class<R> responseClass) {
        super(stepikAction, responseClass, QueryMethod.GET);
    }

    @NotNull
    public T id(@NotNull long... values) {
        addParam(IDS_KEY, values);
        //noinspection unchecked
        return (T) this;
    }

    @NotNull
    public <V> T id(@NotNull List<V> values) {
        addParam(IDS_KEY, values);
        //noinspection unchecked
        return (T) this;
    }

    protected boolean isCacheEnabled() {
        return true;
    }

    protected String getCacheSubdirectory() {
        return getResponseClass().getSimpleName().toLowerCase();
    }

    protected long getCacheLifeTime() {
        return 5 * 60 * 1000;
    }

    @NotNull
    @Override
    public R execute() {
        StepikApiClient stepikApiClient = getStepikAction().getStepikApiClient();

        if (!isCacheEnabled() || !stepikApiClient.isCacheEnabled()) {
            return super.execute();
        }

        List<String> ids = getParam(IDS_KEY);

        if (ids.isEmpty()) {
            return super.execute();
        }

        Path cachePath = stepikApiClient.getCachePath();
        Path courseCache = cachePath.resolve(getCacheSubdirectory());

        R items;
        try {
            items = getResponseClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new StepikClientException("Can't create a new instance for a response class", e);
        }

        List<String> idsForQuery;

        if (Files.exists(courseCache)) {
            idsForQuery = new ArrayList<>();
            for (String id : ids) {
                Path file = courseCache.resolve(id + JSON_EXTENSION);
                if (Files.exists(file)) {
                    long updateFileTime = file.toFile().lastModified();
                    long currentTime = new Date().getTime();

                    long diff = currentTime - updateFileTime;
                    if (diff > 0 && diff <= getCacheLifeTime()) {
                        Object item = null;
                        try {
                            String text = Utils.readFile(file);
                            //noinspection unchecked
                            item = getJsonConverter().fromJson(text, items.getItemClass());
                        } catch (JsonSyntaxException ignored) {
                        }
                        if (item != null) {
                            //noinspection unchecked
                            items.getItems().add(item);
                            continue;
                        }
                    }
                }

                idsForQuery.add(id);
            }
        } else {
            idsForQuery = ids;
        }
        if (!idsForQuery.isEmpty()) {
            id(idsForQuery);
            R loadedItems = super.execute();
//            noinspection unchecked
            loadedItems.getItems().forEach((item) -> flushCourse(item, courseCache));
            //noinspection unchecked
            items.getItems().addAll(loadedItems.getItems());
        }

        return items;
    }

    private void flushCourse(Object item, Path cachePath) {
        String id;
        if (item instanceof AbstractObject) {
            id = String.valueOf(((AbstractObject) item).getId());
        } else if (item instanceof AbstractObjectWithStringId) {
            id = ((AbstractObjectWithStringId) item).getId();
        } else {
            return;
        }

        Path courseCache = cachePath.resolve(id + JSON_EXTENSION);

        try {
            Files.createDirectories(courseCache.getParent());
            byte[] content = getJsonConverter().toJson(item).getBytes(StandardCharsets.UTF_8);
            Files.write(courseCache, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {
        }
    }
}
