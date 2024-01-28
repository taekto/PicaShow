package io.b101.picashow

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Date
import java.util.concurrent.TimeUnit

fun scheduleWallpaperChange(context: Context, startDate: Date, imageUrl: String) {
    val triggerTime = startDate.time - TimeUnit.MINUTES.toMillis(10)  // 10분 전 시간
    val delayMillis = triggerTime - System.currentTimeMillis()  // 현재로부터의 지연 시간
    val data = workDataOf("imageUrl" to imageUrl)  // imageUrl 데이터 설정

    if (delayMillis > 0) {  // 설정한 시간의 10분 전이 아직 오지 않았다면
        val workRequest = OneTimeWorkRequestBuilder<WallpaperChangeWorker>()
            .setInputData(data)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            // uniqueWorkName이 같으면 기존 작업 취소 - startDate 이름에 붙였음
            "wallpaperChangeScheduled$startDate",
            ExistingWorkPolicy.REPLACE,
            workRequest)
    } else {
        val changeWallpaperRequest =
            OneTimeWorkRequestBuilder<WallpaperChangeWorker>()
                .setInputData(data)
                .build()
        WorkManager.getInstance(context).enqueue(changeWallpaperRequest)
    }
}
