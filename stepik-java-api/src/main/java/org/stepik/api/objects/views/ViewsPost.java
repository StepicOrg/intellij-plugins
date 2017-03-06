package org.stepik.api.objects.views;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class ViewsPost {
    private ViewPost view;

    @NotNull
    public ViewPost getView() {
        if (view == null) {
            view = new ViewPost();
        }
        return view;
    }

    public void setView(@Nullable ViewPost view) {
        this.view = view;
    }
}
