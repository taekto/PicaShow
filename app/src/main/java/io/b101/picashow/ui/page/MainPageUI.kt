package io.b101.picashow.ui.page

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import io.b101.picashow.database.AppDatabase
import io.b101.picashow.entity.Schedule
import io.b101.picashow.repository.ScheduleRepository
import io.b101.picashow.ui.theme.NoneSelectBackground
import io.b101.picashow.viewmodel.ScheduleViewModel
import io.b101.picashow.viewmodel.ScheduleViewModelFactory
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
fun MainPage(navController : NavController) {

    val context = LocalContext.current

    // 1. ScheduleDao의 인스턴스를 생성합니다.
    val scheduleDao = AppDatabase.getDatabase(context).scheduleDao()
    // 2. ScheduleDao 인스턴스를 사용하여 ScheduleRepository의 인스턴스를 생성합니다.
    val repository = ScheduleRepository(scheduleDao)
    // 3. ScheduleRepository 인스턴스를 사용하여 ScheduleViewModelFactory의 인스턴스를 생성합니다.
    val viewModelFactory = ScheduleViewModelFactory(repository)
    // 4. ScheduleViewModelFactory를 사용하여 ViewModel 인스턴스를 얻습니다.
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = viewModelFactory)

    val today = LocalDate.now()
    val currentYear = today.year
    val currentMonth = today.monthValue // monthValue는 1부터 시작함
    val currentDay = today.dayOfMonth

    // 선택된 날짜를 관리하는 상태
    var selectedDay by remember { mutableIntStateOf(currentDay) }
    var selectedMonth by remember { mutableIntStateOf(currentMonth) }

    LaunchedEffect(key1 = Unit) { // key1 = Unit을 사용하여 페이지가 처음 로딩될 때 한번만 실행되게 함
        scheduleViewModel.fetchSchedulesForDate(currentYear, currentMonth, currentDay)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Calendar(currentYear, currentMonth, currentDay, selectedDay) { newSelectedDay, selectedMonthLocal, check ->
            selectedDay = newSelectedDay
            selectedMonth = if(selectedDay < currentDay && check) {
                selectedMonthLocal + 1
            } else {
                selectedMonthLocal
            }

            // 선택된 날짜의 데이터를 Room에서 가져옴
            scheduleViewModel.fetchSchedulesForDate(currentYear, selectedMonth, selectedDay)
        }
        Spacer(modifier = Modifier.height(16.dp))

        val schedules by scheduleViewModel.schedules.observeAsState(emptyList())

        Tasks(schedules, onTaskClick = { schedule ->
            // 상세 페이지로 이동
            navController.navigate("detailPage/${schedule.scheduleSeq}")
        })
    }
}

@Composable
fun Calendar(year: Int, month: Int, startDay: Int, selectedDay: Int, onDaySelected: (Int, Int, Boolean) -> Unit) {
    val daysInMonth = daysInMonth(year, month)
    val daysToShow = mutableListOf<Int?>()
    val context = LocalContext.current
    // 날짜 선택 상태를 저장하기 위한 mutable state
    var (selectedDate, setSelectedDate) = remember { mutableStateOf(LocalDate.of(year, month, selectedDay)) }
    var displayMonth by remember { mutableStateOf(month) }

    val dayNames = getWeekDayNamesBasedOnStartDay(year, month, startDay)
    var showDatePicker by remember { mutableStateOf(false) }
//    val selectedMonth = if(selectedDay < startDay) {
//        month + 1
//    } else {
//        month
//    }

    for (i in 0 until 30) {
        val currentDay = startDay + i
        if (currentDay <= daysInMonth) {
            daysToShow.add(currentDay)
        } else {
            daysToShow.add(currentDay - daysInMonth)  // 다음 달의 날짜로 업데이트
        }
    }


    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonthOfYear, dayOfMonth ->
                val actualMonth = selectedMonthOfYear + 1
                val newSelectedDate = LocalDate.of(selectedYear, actualMonth, dayOfMonth)
                setSelectedDate(newSelectedDate) // 선택된 날짜 상태 업데이트
                displayMonth = actualMonth
                onDaySelected(dayOfMonth, actualMonth, false)
                showDatePicker = false // DatePickerDialog 닫기
            },
            selectedDate.year,
            selectedDate.monthValue - 1, // DatePicker에서 월이 0부터 시작하므로 실제 월에서 1을 뺍니다.
            selectedDate.dayOfMonth
        ).show()
    }

    Text(
        text = "${selectedDate.year} / $displayMonth / ${selectedDate.dayOfMonth}",
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = Color.White,
        modifier = Modifier.clickable { showDatePicker = true }  // Text를 클릭하면 DatePickerDialog를 표시
    )
    Spacer(modifier = Modifier.height(16.dp))
//    ShowDatePicker()

    val lazyListState = rememberLazyListState()

    LazyRow(
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        itemsIndexed(daysToShow) { index, day ->
            DayWithBackground(day, dayNames[index], day == selectedDate.dayOfMonth) { // Change comparison to selectedDate
                day?.let {
                    // Update the selectedDate state here
                    setSelectedDate(LocalDate.of(year, month, it))
                    displayMonth = if(startDay > it) {
                        month + 1
                    } else {
                        month
                    }
                    // Then call onDaySelected
                    onDaySelected(it, month, true)
                }
            }
        }
    }
}

@Composable
fun DayWithBackground(day: Int?, dayName: String, isSelected: Boolean, onDayClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White else NoneSelectBackground
    val textColor = if (isSelected) Color.Black else Color.White

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(backgroundColor)
            .clickable(onClick = onDayClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = dayName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = day?.toString() ?: "", fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

fun getWeekDayNamesBasedOnStartDay(year: Int, month: Int, startDay: Int): List<String> {
    val startDate = LocalDate.of(year, month, startDay)
    val dayNames = mutableListOf<String>()

    val repeatCount = 30 // 30번 반복

    for (j in 0 until repeatCount) {
        val dayOfWeek = startDate.plusDays(j.toLong()).dayOfWeek
        dayNames.add(
            when (dayOfWeek) {
                DayOfWeek.MONDAY -> "Mon"
                DayOfWeek.TUESDAY -> "Tue"
                DayOfWeek.WEDNESDAY -> "Wed"
                DayOfWeek.THURSDAY -> "Thu"
                DayOfWeek.FRIDAY -> "Fri"
                DayOfWeek.SATURDAY -> "Sat"
                DayOfWeek.SUNDAY -> "Sun"
            }
        )
    }

    return dayNames
}

fun daysInMonth(year: Int, month: Int): Int {
    // API 레벨 26부터 사용 가능
    val yearMonth = YearMonth.of(year, month)
    return yearMonth.lengthOfMonth()
}

fun monthToName(month: Int): String {
    return when(month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> throw IllegalArgumentException("Invalid month: $month")
    }
}

@Composable
fun Tasks(schedules: List<Schedule>, onTaskClick: (Schedule) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            Text(
                text = "Today's Tasks",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(schedules.size) { index ->
            val schedule = schedules[index]
            TaskItem(schedule = schedule, onClick = { onTaskClick(schedule)})
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TaskItem(schedule: Schedule,  onClick: () -> Unit) {

    // 시간 포맷을 정의
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick) // 클릭 이벤트 처리
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF00A7A7), Color(0xFF2C2C2C)),
                    startY = 0f,
                    endY = 8f
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = schedule.scheduleName.toString(),
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${schedule.startDate?.let { timeFormat.format(it) }} - ${timeFormat.format(schedule.endDate)}",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
                color = Color.White
            )
        }
    }
}
