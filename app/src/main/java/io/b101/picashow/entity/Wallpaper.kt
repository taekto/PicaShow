package io.b101.picashow.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallpaper")
data class Wallpaper (
    @PrimaryKey(autoGenerate = true) val wallpaperSeq : Long?,
    val url : String?,
    val createdAt : String?,
    )