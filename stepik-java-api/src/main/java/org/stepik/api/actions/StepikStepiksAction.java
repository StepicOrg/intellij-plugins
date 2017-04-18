package org.stepik.api.actions;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.users.User;
import org.stepik.api.queries.stepiks.StepikStepiksGetQuery;

/**
 * @author meanmail
 */
public class StepikStepiksAction extends StepikAbstractAction {
    public StepikStepiksAction(@NotNull StepikApiClient stepikApiClient) {
        super(stepikApiClient);
    }

    @NotNull
    public StepikStepiksGetQuery get() {
        return new StepikStepiksGetQuery(this);
    }

    public User getCurrentUser() {
        return this.get()
                .id(1)
                .execute().getUser();
    }
}
