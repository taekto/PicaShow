package io.b101.picashow.api

import io.b101.picashow.api.image.ImageAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiObject {
    private const val BASE_URL = "https://k9b101.p.ssafy.io/"

    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // BODY는 요청/응답의 헤더 및 본문을 모두 로그에 출력합니다.
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃을 30초로 설정
        .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃을 30초로 설정
        .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃을 30초로 설정
        .addInterceptor(loggingInterceptor) // 로깅 인터셉터 추가
        .build()

    // API 요청시 log 띄우기
    private val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // API 요청
    private val getRetrofit by lazy{
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ImageService: ImageAPI by lazy {
        getRetrofit.create(ImageAPI::class.java)
    }


}