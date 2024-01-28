package io.b101.picashow.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.b101.picashow.entity.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao: BaseDao<Schedule> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(schedule: Schedule): Long
    @Query("SELECT * FROM schedule")
    fun getAll() : Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE startDate >= :startTimestamp AND startDate < :endTimestamp")
    fun getSchedulesForDate(startTimestamp: Long, endTimestamp: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE scheduleSeq = :scheduleSeq")
    fun getScheduleById(scheduleSeq: String): Flow<Schedule?>

    @Update
    override suspend fun update(schedule: Schedule)

    @Query("UPDATE schedule SET wallpaperUrl = :newImgUrl WHERE scheduleSeq = :scheduleSeq")
    suspend fun updateWallpaperUrl(scheduleSeq: String, newImgUrl: String)
}