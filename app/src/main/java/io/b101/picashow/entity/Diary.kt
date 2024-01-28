package io.b101.picashow.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.b101.picashow.converter.Converters
import java.util.Date

@Entity(tableName = "diary")
@TypeConverters(Converters::class)
data class Diary (
    @PrimaryKey(autoGenerate = true) val diarySeq : Long?,
    val date : Date?,
    val title : String?,
    var content : String?,
    var url : String?
    )