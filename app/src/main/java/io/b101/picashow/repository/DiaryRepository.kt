package io.b101.picashow.repository

import androidx.annotation.WorkerThread
import io.b101.picashow.dao.DiaryDao
import io.b101.picashow.entity.Diary
import kotlinx.coroutines.flow.Flow

class DiaryRepository(private val diaryDao: DiaryDao) {
    val allDiarys: Flow<List<Diary>> = diaryDao.getAll()
    @WorkerThread
    suspend fun insert(diary: Diary) : Long {
        return diaryDao.insert(diary)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(diary: Diary) {
        diaryDao.delete(diary)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(diary: Diary) {
        diaryDao.update(diary)
    }

    // DiaryRepository.kt에 새로운 함수 추가
    fun getDiaryByDate(selectedDate: Long): Flow<List<Diary>> {
        return diaryDao.getDiaryByDate(selectedDate)
    }

    @WorkerThread
    suspend fun updateDiaryImgUrl(diarySeq: String, newImgUrl: String) {
        diaryDao.updateImageUrl(diarySeq, newImgUrl)
    }

}