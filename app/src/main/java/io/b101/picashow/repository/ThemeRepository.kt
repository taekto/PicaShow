package io.b101.picashow.repository

import androidx.annotation.WorkerThread
import io.b101.picashow.dao.ThemeDao
import io.b101.picashow.entity.Theme
import kotlinx.coroutines.flow.Flow

class ThemeRepository(private val themeDao: ThemeDao) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(theme: Theme) {
        themeDao.insert(theme)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(theme: Theme) {
        themeDao.delete(theme)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(theme: Theme) {
        themeDao.update(theme)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        themeDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAll(themes: List<Theme>) {
        themeDao.insertAll(themes)
    }

    // 모든 테마의 keyWord 데이터를 불러오는 함수
    val allKeywords: Flow<List<String>> = themeDao.getAllKeywords()
}