package org.stepik.api.objects.lessons

import com.google.gson.annotations.SerializedName
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.units.Unit
import java.util.*

data class CompoundUnitLesson(
        @SerializedName("unit")
        var unit: Unit = Unit(),
        
        @SerializedName("lesson")
        var lesson: Lesson = Lesson()
) : StudyObject() {
    
    override var id: Long
        get() = lesson.id
        set(value) {
            lesson.id = value
        }
    
    override var title: String
        get() = lesson.title
        set(value) {
            lesson.title = value
        }
    
    override var description: String
        get() = lesson.description
        set(value) {
            lesson.description = value
        }
    
    override var position: Int
        get() = unit.position
        set(value) {
            unit.position = value
        }
    
    override var progress: String
        get() = lesson.progress
        set(value) {
            lesson.progress = value
        }
    
    override var createDate
        get() = lesson.createDate
        set(value) {
            lesson.createDate = value
        }
    
    override var updateDate: Date
        get() = lesson.updateDate
        set(value) {
            lesson.updateDate = value
        }
}
