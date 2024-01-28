package io.b101.picashow.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "theme")
data class Theme(
    @PrimaryKey(autoGenerate = true) val themeSeq : Long?,
    val keyWord : String?,

    )
