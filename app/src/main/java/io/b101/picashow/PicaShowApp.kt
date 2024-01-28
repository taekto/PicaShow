package io.b101.picashow

import android.app.Application
import io.b101.picashow.database.AppDatabase
import io.b101.picashow.repository.DiaryRepository
import io.b101.picashow.repository.MemberRepository
import io.b101.picashow.repository.ScheduleRepository

class PicaShowApp:Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { MemberRepository(database.memberDao()) }
    val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(database.scheduleDao()) }
    val diaryRepository: DiaryRepository by lazy { DiaryRepository(database.diaryDao()) }
}