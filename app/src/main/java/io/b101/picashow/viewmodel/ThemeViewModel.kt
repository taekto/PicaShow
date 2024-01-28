package io.b101.picashow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import io.b101.picashow.entity.Theme
import io.b101.picashow.repository.ThemeRepository
import kotlinx.coroutines.launch

class ThemeViewModel(private val repository: ThemeRepository) : ViewModel() {
    private val _myInfo = MutableLiveData<Theme?>()
    val myInfo: LiveData<Theme?> get() = _myInfo

    private val _allKeywords = MutableLiveData<List<String>>()
    // 모든 테마의 keyWord 데이터를 불러오는 함수
    val allKeywords: LiveData<List<String>> = repository.allKeywords.asLiveData()

    // Theme를 저장하는 함수
    fun saveTheme(theme: Theme) {
        viewModelScope.launch {
            // Theme를 저장하고 저장된 Theme 객체를 _myInfo LiveData에 할당
            val savedTheme = repository.insert(theme)
            _myInfo.value = theme
        }
    }

    // Theme를 업데이트하는 함수
    fun updateTheme(theme: Theme) {
        viewModelScope.launch {
            // Theme를 업데이트하고 업데이트된 Theme 객체를 _myInfo LiveData에 할당
            val updatedTheme = repository.update(theme)
            _myInfo.value = theme
        }
    }

    // Theme를 삭제하는 함수
    fun deleteTheme(theme: Theme) {
        viewModelScope.launch {
            // Theme를 삭제하고 LiveData를 null로 설정
            repository.delete(theme)
            _myInfo.value = null
        }
    }

    // 모든 Theme 삭제
    fun deleteAllThemes() {
        viewModelScope.launch {
            repository.deleteAll()
            _myInfo.value = null
        }
    }

    // 여러 Theme 추가
    fun insertAllThemes(themes: List<Theme>) {
        viewModelScope.launch {
            repository.insertAll(themes)
        }
    }


}


