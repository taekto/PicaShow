package io.b101.picashow.ui.page

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import io.b101.picashow.UpdateImageService
import io.b101.picashow.api.ApiObject
import io.b101.picashow.api.image.CreateImageRequest
import io.b101.picashow.database.AppDatabase
import io.b101.picashow.entity.Diary
import io.b101.picashow.repository.DiaryRepository
import io.b101.picashow.repository.ScheduleRepository
import io.b101.picashow.repository.ThemeRepository
import io.b101.picashow.ui.theme.Purple40
import io.b101.picashow.ui.theme.teal40
import io.b101.picashow.util.await
import io.b101.picashow.viewmodel.DiaryViewModel
import io.b101.picashow.viewmodel.DiaryViewModelFactory
import io.b101.picashow.viewmodel.ScheduleViewModel
import io.b101.picashow.viewmodel.ScheduleViewModelFactory
import io.b101.picashow.viewmodel.ThemeViewModel
import io.b101.picashow.viewmodel.ThemeViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


val inputDateTime: LocalDateTime = LocalDateTime.now()
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
val formattedDate: String = inputDateTime.format(formatter)
var diaryTitle = mutableStateOf(formattedDate)
var diaryDatePickerFlag = mutableStateOf(false)
var targetPage = mutableIntStateOf(999999)
var changeCheck = mutableStateOf(false)

@Composable
fun DiaryPage() {
    // 의존성 주입
    val context = LocalContext.current

    val diaryDao = AppDatabase.getDatabase(context).diaryDao()
    val diaryRepository = DiaryRepository(diaryDao)
    val diaryViewModelFactory = DiaryViewModelFactory(diaryRepository)

    val diaryViewModel = viewModel<DiaryViewModel>(
        factory = diaryViewModelFactory
    )
    val scheduleDao = AppDatabase.getDatabase(context).scheduleDao()
    // 2. ScheduleDao 인스턴스를 사용하여 ScheduleRepository의 인스턴스를 생성합니다.
    val repository = ScheduleRepository(scheduleDao)
    // 3. ScheduleRepository 인스턴스를 사용하여 ScheduleViewModelFactory의 인스턴스를 생성합니다.
    val viewModelFactory = ScheduleViewModelFactory(repository)
    // 4. ScheduleViewModelFactory를 사용하여 ViewModel 인스턴스를 얻습니다.
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = viewModelFactory)

    ImageCompo(diaryViewModel = diaryViewModel, scheduleViewModel = scheduleViewModel) // 페이지별로 Image를 그립니다.

    if(diaryDatePickerFlag.value) {
        ShowDatePicker(diaryViewModel, context)
        diaryDatePickerFlag.value=false
    }

}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageCompo(diaryViewModel: DiaryViewModel, scheduleViewModel: ScheduleViewModel) {
    val coroutineScope = rememberCoroutineScope()

    val diaryList by diaryViewModel.diaryList.observeAsState(initial = emptyList())
    var selectedDiary by remember { mutableStateOf(diaryList.firstOrNull()) }

    selectedDiary = if (!diaryList.isNullOrEmpty()) {
        diaryList[0]
    } else {
        null
    }

    val today = LocalDate.now()
    val initialPage = (today.toEpochDay() - LocalDate.now().toEpochDay()).toInt() + 1000000
    val pagerState = rememberPagerState(pageCount = 2000000, initialPage = initialPage)

    LaunchedEffect(pagerState.currentPage) {
        val date = LocalDate.now().plusDays(pagerState.currentPage.toLong() - 1000000L)
        diaryTitle.value = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        if(changeCheck.value) {
            coroutineScope.launch {
                pagerState.scrollToPage(targetPage.intValue)
                changeCheck.value = false
            }
        }
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val selectedDate = dateFormatter.parse(diaryTitle.value)
        if (selectedDate != null) {
            diaryViewModel.getDiaryByDate(selectedDate.time)
        }
    }

    HorizontalPager(state = pagerState) { page ->
        // 페이지를 그리는 로직은 그대로 유지합니다.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = {
                item {
                    var imageUrl = selectedDiary?.url
                    if (imageUrl == null) {
                        imageUrl = "https://comercial-wallpaper.s3.ap-northeast-2.amazonaws.com/images/5089873592208240427.png"
                    }
                    val painter = rememberImagePainter(data = imageUrl)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        DateText(diaryTitle.value) { }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp)
                    ) {
                        Image(
                            contentScale = ContentScale.Crop,
                            painter = painter,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }

                    if (!diaryList.isNullOrEmpty()) {
                        DiaryText(selectedDiary!!, diaryViewModel)
                    } else {
                        TextPlaceHolder(diaryViewModel)
                    }
                }
            }
        )
    }
}


@Composable
fun DiaryText(diary: Diary, diaryViewModel: DiaryViewModel) {
    var isEditing by remember { mutableStateOf(false) }
    var editText by remember(diary) { mutableStateOf(diary.content) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleBoxModifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(1.dp))
            .padding(4.dp)
            .heightIn(min = 90.dp)

        Box(
            modifier = titleBoxModifier,
            contentAlignment = Alignment.Center
        ) {
            if (!isEditing) {
                Text(
                    text = diary.content ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                )
            } else {
                BasicTextField(
                    value = editText ?: "",
                    onValueChange = { newText -> editText = newText },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp)
                        .background(Color.Transparent),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(Color.White),
                    singleLine = false
                )
            }
        }

        if (!isEditing) {
            Button(
                onClick = { isEditing = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(teal40)
            ) {
                Text(text = "Modify")
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            diary.content = editText
                            diaryViewModel.updateDiary(diary)
                            isEditing = false
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(teal40)
                ) {
                    Text(text = "Complete")
                }

                Button(
                    onClick = {
                        editText = diary.content ?: ""
                        isEditing = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(Purple40)
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@Composable
fun TextPlaceHolder(viewModel: DiaryViewModel) {
    val context = LocalContext.current

    var text by remember { mutableStateOf("") }
    val imageUrl by remember { mutableStateOf("https://comercial-wallpaper.s3.ap-northeast-2.amazonaws.com/images/5089873592208240427.png") }

    val themeDao = AppDatabase.getDatabase(context).themeDao()
    val themeRepository = ThemeRepository(themeDao)
    val themeViewModelFactory = ThemeViewModelFactory(themeRepository)
    val themeViewModel: ThemeViewModel = viewModel(factory = themeViewModelFactory)

    val themeListState = themeViewModel.allKeywords.observeAsState(initial = emptyList())
    var randomKeyword by remember { mutableStateOf<String?>(null) }

    if (themeListState.value.isNotEmpty()) {
        randomKeyword = themeListState.value.random()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val titleBoxModifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(1.dp))
            .padding(4.dp)

        Box(
            modifier = titleBoxModifier,
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newText -> text = newText },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp)
                    .background(Color.Transparent),
                visualTransformation = VisualTransformation.None,
                singleLine = false
            )
            if (text.isEmpty()) {
                Text(
                    text = "diary content",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val diary = Diary(
                        diarySeq = null,
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(diaryTitle.value),
                        title = null,
                        content = text,
                        url = "android.resource://io.b101.picashow/drawable/null_image"
                    )

                    CoroutineScope(Dispatchers.Main).launch {
                        // 일정 추가
                        withContext(Dispatchers.IO) {
                            val diarySeq = viewModel.saveDiary(diary).await()
                            try {
                                val response = ApiObject.ImageService.createImage(CreateImageRequest(text, randomKeyword!!))
                                if (response.isSuccessful) {
                                    // 성공적으로 URL을 받아옵니다.
                                    val responseImageUrl = response.body().toString()
                                    // 이미지 URL이 성공적으로 받아졌다면, 업데이트 로직 수행
                                    val intent = Intent(context, UpdateImageService::class.java).apply {
                                        putExtra("scheduleSeq", diarySeq.toString())
                                        putExtra("newImgUrl", responseImageUrl)
                                        putExtra("kind", "diaryPage")
                                    }
                                    context.startService(intent)
                                } else {
                                    Toast.makeText(context, "Failed to generate image", Toast.LENGTH_LONG).show()
                                    Log.e("ERROR", "이미지 생성 오류: ${response.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ERROR", "이미지 생성 예외 발생", e)
                            }
                        }
                    }
                    Toast.makeText(context, "diary has been added", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                colors = ButtonDefaults.buttonColors(teal40)
            ) {
                Text(text = "Save & Image Create", fontSize = 14.sp)
            }
        }
    }
}



@Composable
fun DateText(selectedDate: String, onDateTextClicked: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.Transparent)
            .padding(4.dp)
            .clickable(onClick = onDateTextClicked), // Box를 클릭 가능하게 만듭니다.
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = AnnotatedString(selectedDate),
            color = Color.LightGray,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {diaryDatePickerFlag.value=true}
        )
    }
}


@Composable
fun ShowDatePicker(diaryViewModel: DiaryViewModel, context: Context) {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        DatePickerDialog(context, { _, mYear, mMonth, mDay ->
            val newSelectedDate = LocalDate.of(mYear, mMonth + 1, mDay)
            diaryTitle.value =
                newSelectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            //TODO : 이거 수정 해야 됨
            targetPage.intValue = (newSelectedDate.toEpochDay() - LocalDate.now()
                .toEpochDay()).toInt() + 999999
            changeCheck.value = true
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val selectedDate = dateFormatter.parse(diaryTitle.value)
            if (selectedDate != null) {
                diaryViewModel.getDiaryByDate(selectedDate.time)
            }
        }, year, month, day).show()
    }
}