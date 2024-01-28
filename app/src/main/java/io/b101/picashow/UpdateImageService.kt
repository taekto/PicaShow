package io.b101.picashow

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UpdateImageService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val seq = intent?.getStringExtra("scheduleSeq")
        val newImgUrl = intent?.getStringExtra("newImgUrl")
        val kind = intent?.getStringExtra("kind")

        Log.d("UpdateImageService", "onStartCommand: seq=$seq, newImgUrl=$newImgUrl, kind = $kind")

        if (seq != null && newImgUrl != null) {
            serviceScope.launch {
                try {
                    if(kind.equals("schedulePage")) {
                        updateScheduleImgUrl(seq, newImgUrl)
                    } else if(kind.equals("diaryPage")) {
                        updateDiaryImgUrl(seq, newImgUrl)
                    }
                } catch (e: Exception) {
                    Log.e("UpdateImageService", "Exception in updateScheduleImgUrl", e)
                }
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }

    private suspend fun updateScheduleImgUrl(scheduleSeq: String, newImgUrl: String) {
        Log.d("UpdateImageService", "Updating image URL...")
        // Application 인스턴스를 가져옵니다.
        val app = applicationContext as PicaShowApp
        // Repository에 접근합니다.
        val scheduleRepository = app.scheduleRepository
        // Repository의 함수를 호출하여 이미지 URL을 업데이트합니다.
        scheduleRepository.updateScheduleImgUrl(scheduleSeq, newImgUrl)
        Log.d("UpdateImageService", "Image URL updated")
    }

    private suspend fun updateDiaryImgUrl(diarySeq: String, newImgUrl: String) {
        Log.d("UpdateImageService", "Updating image URL...")
        // Application 인스턴스를 가져옵니다.
        val app = applicationContext as PicaShowApp
        // Repository에 접근합니다.
        val diaryRepository = app.diaryRepository
        // Repository의 함수를 호출하여 이미지 URL을 업데이트합니다.
        diaryRepository.updateDiaryImgUrl(diarySeq, newImgUrl)
        Log.d("UpdateImageService", "Image URL updated")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // 서비스가 종료될 때 코루틴을 취소합니다.
    }
}