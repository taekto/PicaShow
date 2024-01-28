package io.b101.picashow.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.b101.picashow.entity.Diary
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao : BaseDao<Diary> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(diary: Diary): Long

    @Query("SELECT * FROM diary")
    fun getAll() : Flow<List<Diary>>

    @Query("SELECT * FROM diary WHERE date = :selectedDate")
    fun getDiaryByDate(selectedDate: Long): Flow<List<Diary>>

    @Query("UPDATE diary SET url = :newImgUrl WHERE diarySeq = :diarySeq")
    suspend fun updateImageUrl(diarySeq: String, newImgUrl: String)
}