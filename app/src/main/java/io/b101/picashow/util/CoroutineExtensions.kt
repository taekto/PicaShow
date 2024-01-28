package io.b101.picashow.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun <T> LiveData<T>.await(): T {
    return withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { continuation ->
            // LiveData의 Observer를 생성합니다.
            val observer = object : Observer<T> {
                override fun onChanged(value: T) {
                    if (value != null) {
                        continuation.resume(value)
                        // 값을 받았으니 Observer를 제거합니다.
                        this@await.removeObserver(this)
                    }
                }
            }
            // Observer를 LiveData에 등록합니다.
            observeForever(observer)
            // 코루틴이 취소되면 Observer를 제거합니다.
            continuation.invokeOnCancellation {
                removeObserver(observer)
            }
        }
    }
}