package io.b101.picashow.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    // 반환 되는 Seq 값이 Long 타입이어야 함
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj : T) : Long

    @Delete
    suspend fun delete(obj : T)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun update(obj : T)

}