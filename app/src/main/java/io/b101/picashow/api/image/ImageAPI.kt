package io.b101.picashow.api.image

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ImageAPI {

    // 일일 계획에 대한 이미지를 가져옵니다.
    @GET("list")
    suspend fun getAllImages(@Query("page") page: Int): Response<ImageResponse>
    @POST("download")
    suspend fun downloadCountPlus(@Body data: DownloadItem) : Response<String>

    @POST("image/dalle")
    suspend fun createImage(@Body request: CreateImageRequest) : Response<String>
}