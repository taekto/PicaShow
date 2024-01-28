package io.b101.picashow.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.b101.picashow.converter.Converters

@Entity(tableName = "member")
@TypeConverters(Converters::class)
data class Member (
    @PrimaryKey(autoGenerate = true) val memberSeq : Long? = 1L,
    val isTutorial : Boolean?,
    val deviceId : String?,
    )