package io.b101.picashow

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.IOException

class WallpaperChangeWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val imageUrl = inputData.getString("imageUrl") // WorkRequest에서 전달받은 이미지 URL
        val isLockScreen = inputData.getBoolean("isLockScreen", false) // WorkRequest에서 전달받은 플래그
        if (imageUrl != null) {
            changeWallpaper(imageUrl, isLockScreen)
        }
        return Result.success()
    }

    private fun changeWallpaper(imageUrl: String, isLockScreen: Boolean) {
        Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    try {
                        if (Build.VERSION.SDK_INT >= 26 && isLockScreen) {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK)
                        } else {
                            wallpaperManager.setBitmap(resource)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                    // Implement if needed
                }
            })
    }
}