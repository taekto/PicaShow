package io.b101.picashow.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.b101.picashow.converter.Converters
import java.util.Date

@Entity(tableName = "schedule")
@TypeConverters(Converters::class)
data class Schedule(
    @PrimaryKey(autoGenerate = true) val scheduleSeq : Long?,
    var startDate : Date?,
    var endDate: Date?,
    var scheduleName : String?,
    var wallpaperUrl : String?,
    var content : String?,
)