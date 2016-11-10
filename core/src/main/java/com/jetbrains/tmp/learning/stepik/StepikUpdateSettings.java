package com.jetbrains.tmp.learning.stepik;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "StepikUpdateSettings", storages = @Storage("other.xml"))
public class StepikUpdateSettings implements PersistentStateComponent<StepikUpdateSettings> {
    private long nextTimeCheck = 0;

    public StepikUpdateSettings() {
    }

    public long getNextTimeCheck() {
        return nextTimeCheck;
    }

    public void setNextTimeCheck(long timeChecked) {
        nextTimeCheck = timeChecked;
    }

    @Nullable
    @Override
    public StepikUpdateSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull StepikUpdateSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Nullable
    public static StepikUpdateSettings getInstance() {
        return ServiceManager.getService(StepikUpdateSettings.class);
    }
}
