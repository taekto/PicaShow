package io.b101.picashow.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.b101.picashow.entity.Member
import io.b101.picashow.repository.MemberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

var please = mutableStateOf(false);
val _myInfo = MutableLiveData<Member?>()
class MemberViewModel(private val repository: MemberRepository) : ViewModel() {

//    val myInfo: LiveData<Member?> get() = _myInfo

    fun getMember(memberSeq: Long) {
        viewModelScope.launch {
            // 백그라운드 스레드에서 데이터베이스에 액세스
            var member = withContext(Dispatchers.IO) {
                repository.getMember(memberSeq)
            }
            if(member == null) member = Member(1,false, null);
            // UI 업데이트는 메인 스레드에서 수행되어야 함
            withContext(Dispatchers.Main) {
                _myInfo.value = member
                please.value = true
            }
        }
    }

    // Function to save a member
    fun saveMember(member: Member) {
        viewModelScope.launch {
            repository.insert(member)
            _myInfo.value = member
        }
    }

    // Function to update a member
    fun updateMember(member: Member) {
        viewModelScope.launch {
            repository.update(member)
            _myInfo.value = member
        }
    }

    // Function to delete a member
    fun deleteMember(member: Member) {
        viewModelScope.launch {
            repository.delete(member)
            _myInfo.value = null
        }
    }
}
