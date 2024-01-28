package io.b101.picashow.ui.page

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.b101.picashow.R
import io.b101.picashow.WallpaperChangeWorker
import io.b101.picashow.api.ApiObject
import io.b101.picashow.api.image.DownloadItem
import io.b101.picashow.deviceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

var showBigImage =  mutableStateOf(false) // 이미지 크게 보기 상태 관리
var showDownloadDialog =  mutableStateOf(false) // 다운로드 다이얼로그 상태 관리
var selectedImageUrl=  mutableStateOf("") // 선택된 이미지의 URL
var selectedImageIndex =  mutableIntStateOf(0) // 선택된 이미지의 인덱스
var nowPage = mutableIntStateOf(1)
val imageUrls = mutableStateOf(emptyList<String>())
val lastPageNum = mutableIntStateOf(1)

@Composable
fun firstPage() {
    // 랜더링 이전에 사진 요청
    LaunchedEffect(Unit) {
        if(nowPage.value==1) getImagesFromS3(1)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        ImageListFromUrls()
        Dialog();
        detailDialog();
    }
}

suspend fun getImagesFromS3(page: Int) {
    try {
        val response = ApiObject.ImageService.getAllImages(page)
        val urlList = response.body()?.list
        if(page == 1) lastPageNum.value = response.body()?.lastPageNum!!
        if (!urlList.isNullOrEmpty()) {
            val urls = urlList.map { it.url }

            imageUrls.value = imageUrls.value + urls
        }
    } catch (e: Exception) {
        Log.d("imageList error",e.printStackTrace().toString())
    }
}

@Composable
fun ImageListFromUrls() {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        items(imageUrls.value.chunked(3)) { chunk ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                for (imageUrl in chunk) {
                    Image(
                        painter = rememberImagePainter(
                            data = imageUrl,
                            builder = {
                                crossfade(true)
                            }
                        ),
                        contentDescription = "인공지능이 생성한 바탕화면",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size((screenWidth / 3) - 17.dp, ((screenWidth / 2) - 5.dp))
                            .fillMaxHeight()
                            .padding(2.5.dp)
                            .clip(shape = RoundedCornerShape(8.dp))
                            .clickable {
                                selectedImageUrl.value = imageUrl
                                selectedImageIndex.value = imageUrls.value.indexOf(imageUrl)
                                showBigImage.value = true
                            }
                    )
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= imageUrls.value.size/3 - 1 && (imageUrls.value.size/15)+1> nowPage.value &&  (imageUrls.value.size/15)+1 <= lastPageNum.value) {
                    coroutineScope.launch {
                        nowPage.value = ((imageUrls.value.size/15)+1).coerceAtLeast(nowPage.value)
                        getImagesFromS3( (imageUrls.value.size/15)+1)
                    }
                }
            }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Dialog() {
    val pagerState = rememberPagerState(pageCount = imageUrls.value.size)
    val coroutineScope = rememberCoroutineScope()
    if (showBigImage.value) {
        Dialog(
            onDismissRequest = {
                showBigImage.value = false
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // experimental
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    HorizontalPager(state = pagerState) { page ->
                        Image(
                            painter = rememberImagePainter(
                                data = imageUrls.value[page],
                                builder = { crossfade(true) }
                            ),
                            contentDescription = "generated background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(modifier=Modifier.align(Alignment.BottomCenter)) {
                            Image(
                                painter=painterResource(id=R.drawable.download),
                                contentDescription=null,
                                modifier= Modifier
                                    .size(100.dp)
                                    .padding(bottom = 50.dp)
                                    .clickable {
                                        showDownloadDialog.value = true;
                                        selectedImageUrl.value = imageUrls.value[page];
                                    }

                            )
                        }
                    }
                    LaunchedEffect(Unit) {
                        pagerState.scrollToPage(selectedImageIndex.value)
                    }

                    LaunchedEffect(pagerState.currentPage) {
                        if (pagerState.currentPage == imageUrls.value.size - 1 && (pagerState.currentPage+1)/15+1>nowPage.value && (pagerState.currentPage+1)/15+1 <= lastPageNum.value) {
                            coroutineScope.launch {
                                nowPage.value = ((pagerState.currentPage + 1) / 15 + 1).coerceAtLeast(nowPage.value);
                                getImagesFromS3((pagerState.currentPage+1)/15+1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun detailDialog() {
    val context = LocalContext.current
    if (showDownloadDialog.value) {
        var backgroundFlag = true
        var lockFlag = true
        var downloadFlag = true
        Dialog(
            onDismissRequest = { showDownloadDialog.value = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false // experimental
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.Transparent)
                    .clickable { showDownloadDialog.value = false }
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .height(230.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                        .clickable { }
                ) {
                    Divider(
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .width(150.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp, start = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(30.dp)
                    ) {
                        Text(
                            fontSize = 20.sp,
                            text = "\uD83D\uDCF1  Set as background",
                            color = Color.White,
                            modifier = Modifier.clickable {
                                if(backgroundFlag) {
                                    val imageUrl = selectedImageUrl.value // 변경하려는 이미지의 URL
                                    val inputData = workDataOf("imageUrl" to imageUrl)
                                    val changeWallpaperRequest =
                                        OneTimeWorkRequestBuilder<WallpaperChangeWorker>()
                                            .setInputData(inputData)
                                            .build()
                                    WorkManager.getInstance(context).enqueue(changeWallpaperRequest)
                                    Toast.makeText(
                                        context,
                                        "The image has been set as the background.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    backgroundFlag = false
                                }},
                        )

                        Text(
                            fontSize = 20.sp,
                            text = "\uD83D\uDD12  Set as lock screen",
                            color = Color.White,
                            modifier = Modifier.clickable {
                                if (lockFlag) {
                                    val imageUrl = selectedImageUrl.value // 변경하려는 이미지의 URL
                                    val inputData =
                                        workDataOf("imageUrl" to imageUrl, "isLockScreen" to true)
                                    val changeWallpaperRequest =
                                        OneTimeWorkRequestBuilder<WallpaperChangeWorker>()
                                            .setInputData(inputData)
                                            .build()
                                    WorkManager.getInstance(context).enqueue(changeWallpaperRequest)
                                    Toast.makeText(
                                        context,
                                        "The image has been set as the lock screen.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    lockFlag = false
                                }
                            }
                        )

                        Text(
                            fontSize = 20.sp,
                            text = "\uD83D\uDDBC️  Save to my gallery",
                            modifier = Modifier.clickable {
                                if(downloadFlag) {
                                    downloadImage(
                                        context,
                                        selectedImageUrl.value,
                                        "Downloading background image.png",
                                        "Downloading images.."
                                    )
                                    Toast.makeText(
                                        context,
                                        "The download is in progress.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    downloadFlag = false
                                    val item = DownloadItem(selectedImageUrl.value, deviceId.value)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        try {ApiObject.ImageService.downloadCountPlus(item)}
                                        catch (e: Exception) {
                                            Log.d("imageList error",e.printStackTrace().toString())
                                        }
                                    }
                                } },
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


fun downloadImage(context: Context, url: String, title: String, description: String) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(title)
        .setDescription(description)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)
        .setAllowedOverMetered(true)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
}


