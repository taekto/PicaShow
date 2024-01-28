package io.b101.picashow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.b101.picashow.entity.Schedule
import io.b101.picashow.repository.ScheduleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    private val _schedules = MutableLiveData<List<Schedule>>()

    val schedules: LiveData<List<Schedule>> = _schedules

    fun saveSchedule(schedule: Schedule): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            // Room에서 새로 삽입된 행의 ID를 반환받습니다.
            val scheduleSeq = repository.insert(schedule)
            result.postValue(scheduleSeq)
        }
        return result
    }

    fun fetchSchedulesForDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val date = LocalDate.of(year, month, day)
            repository.getSchedulesByDate(date).collect { schedulesForDate ->
                _schedules.postValue(schedulesForDate)
            }
        }
    }

    fun getScheduleById(id: String, onScheduleLoaded: (Schedule?) -> Unit) {
        viewModelScope.launch {
            repository.getScheduleById(id)
                .collect { schedule ->
                    // collect는 백그라운드 스레드에서 실행됩니다.
                    // UI 스레드에서 콜백을 호출하기 위해 withContext를 사용합니다.
                    withContext(Dispatchers.Main) {
                        onScheduleLoaded(schedule)
                    }
                }
        }
    }

    fun updateSchedule(scheduleSeq: String, updatedScheduleData: Schedule) {
        viewModelScope.launch(Dispatchers.IO) { // 백그라운드 스레드에서 실행
            val existingSchedule = repository.getScheduleById(scheduleSeq).firstOrNull()
            existingSchedule?.let { schedule ->
                // 업데이트할 속성 설정
                schedule.scheduleName = updatedScheduleData.scheduleName
                schedule.content = updatedScheduleData.content
                schedule.endDate = updatedScheduleData.endDate
                schedule.startDate = updatedScheduleData.startDate
                // 이미지 다시 뽑기가 필요하면 아래 주석을 풀면 된다
                // schedule.wallpaperUrl = updatedScheduleData.wallpaperUrl

                // 업데이트 메서드 호출
                repository.updateSchedule(schedule)
            }
            // 필요하다면 결과를 메인 스레드로 보내는 코드 추가
        }
    }


}