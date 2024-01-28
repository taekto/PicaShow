package io.b101.picashow.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.b101.picashow.entity.Wallpaper
import io.b101.picashow.repository.WallpaperRepository
import kotlinx.coroutines.launch

class WallpaperViewModel(private val repository: WallpaperRepository) : ViewModel() {
    private val _myInfo = MutableLiveData<Wallpaper?>()
    val myInfo: LiveData<Wallpaper?> get() = _myInfo

    fun saveWallpaper(wallpaper: Wallpaper) {
        viewModelScope.launch {
            // Diary를 저장하고 저장된 Diary 객체를 _myInfo LiveData에 할당
            val savedDiary = repository.insert(wallpaper)
            _myInfo.value = wallpaper
        }
    }

    fun updateWallpaper(wallpaper: Wallpaper) {
        viewModelScope.launch {
            // Diary를 업데이트하고 업데이트된 Diary 객체를 _myInfo LiveData에 할당
            val updatedDiary = repository.update(wallpaper)
            _myInfo.value = wallpaper
        }
    }

    // Diary를 삭제하는 함수
    fun deleteWallpaper(wallpaper: Wallpaper) {
        viewModelScope.launch {
            // Diary를 삭제하고 LiveData를 null로 설정
            repository.delete(wallpaper)
            _myInfo.value = null
        }
    }
}