package io.b101.picashow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.b101.picashow.entity.Diary
import io.b101.picashow.repository.DiaryRepository
import kotlinx.coroutines.launch

class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {

    private val _myDiary = MutableLiveData<Diary?>()
    val myDiary: LiveData<Diary?> get() = _myDiary
    private val _diaryList = MutableLiveData<List<Diary>>()
    val diaryList: LiveData<List<Diary>> get() = _diaryList

    // Diary를 저장하는 함수
    fun saveDiary(diary: Diary): LiveData<Long> {
        val result = MutableLiveData<Long>()
        viewModelScope.launch {
            // Diary를 저장하고 저장된 Diary 객체를 _myDiary LiveData에 할당
            val savedDiary = repository.insert(diary)
            result.postValue(savedDiary)
            _myDiary.value = diary
        }
        return result
    }

    // Diary를 업데이트하는 함수
    fun updateDiary(diary: Diary) {
        viewModelScope.launch {
            // Diary를 업데이트하고 업데이트된 Diary 객체를 _myDiary LiveData에 할당
            val updatedDiary = repository.update(diary)
            _myDiary.value = diary
        }
    }

    // Diary를 삭제하는 함수
    fun deleteDiary(diary: Diary) {
        viewModelScope.launch {
            // Diary를 삭제하고 LiveData를 null로 설정
            repository.delete(diary)
            _myDiary.value = null
        }
    }

    fun getDiaryByDate(selectedDate: Long) {
        viewModelScope.launch {
            repository.getDiaryByDate(selectedDate).collect {
                _diaryList.postValue(it)
            }
        }
    }
}

