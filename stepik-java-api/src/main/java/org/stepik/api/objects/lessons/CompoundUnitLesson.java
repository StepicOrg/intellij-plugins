package org.stepik.api.objects.lessons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;
import org.stepik.api.objects.units.Unit;

/**
 * @author meanmail
 */
public class CompoundUnitLesson extends AbstractObject {
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

    public long getId() {
        return getLesson().getId();
    }

    @Override
    public void setId(long id) {
        getLesson().setId(id);
    }
}
