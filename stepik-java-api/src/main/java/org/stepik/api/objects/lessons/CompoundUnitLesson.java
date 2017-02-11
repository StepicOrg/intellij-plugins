package org.stepik.api.objects.lessons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.units.Unit;

/**
 * @author meanmail
 */
public class CompoundUnitLesson {
    private Unit unit;
    private Lesson lesson;

    public CompoundUnitLesson() {
    }

    public CompoundUnitLesson(@Nullable Unit unit, @NotNull Lesson lesson) {
        this.unit = unit;
        this.lesson = lesson;
    }

    @NotNull
    public Unit getUnit() {
        if (unit == null) {
            unit = new Unit();
        }
        return unit;
    }

    public void setUnit(@Nullable Unit unit) {
        this.unit = unit;
    }

    @NotNull
    public Lesson getLesson() {
        if (lesson == null) {
            lesson = new Lesson();
        }
        return lesson;
    }

    public void setLesson(@Nullable Lesson lesson) {
        this.lesson = lesson;
    }
}
