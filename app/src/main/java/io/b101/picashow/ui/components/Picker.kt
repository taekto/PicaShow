package io.b101.picashow.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.util.Calendar

@Composable
fun ShowDatePicker() {
    val context = LocalContext.current
    var selectedDate by remember { mutableStateOf("") }

    Icon(
        Icons.Default.DateRange,
        contentDescription = "Open Date Picker",
        modifier = Modifier.clickable {
            val calendar: Calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            DatePickerDialog(context, { _, mYear, mMonth, mDay ->
                selectedDate = "${mDay}/${mMonth + 1}/$mYear"
                // selectedDate 변수에 선택된 날짜가 저장

            }, year, month, day).show()
        }
    )
    if (selectedDate.isNotEmpty()) {
        Text("Selected Date: $selectedDate")
    }
}



@Composable
fun ShowTimePicker() {
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf("") }

    Icon(
        Icons.Rounded.Build,
        contentDescription = "Open Time Picker",
        modifier = Modifier.clickable {
            val calendar: Calendar = Calendar.getInstance()
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(context, { _, mHour, mMinute ->
                selectedTime= "${mHour}:${mMinute}"
                // selectedTime 변수에 선택된 시간이 저장됩니다.
                // 이 변수를 원하는 곳에서 사용하세요.

            }, hourOfDay, minute, true).show()
        }
    )
    if (selectedTime.isNotEmpty()) {
        Text("Selected Date: $selectedTime")
    }
}

@Composable
fun CustomTimePicker(
    selectedHour: MutableState<Int>,
    selectedMinute: MutableState<Int>,
    onTimeSelected: () -> Unit // 시간을 선택하면 호출될 콜백
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp),
                verticalArrangement = Arrangement.Center
            ) {
                itemsIndexed(items = (0..23).toList()) { _, hour ->
                    Text(
                        text = hour.toString().padStart(2, '0'),
                        color = if (hour == selectedHour.value) Color.White else Color.Gray,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedHour.value = hour
                                onTimeSelected() // 사용자가 시간을 선택했습니다
                            }
                            .padding(8.dp)
                    )
                }
            }
            // 여기서 ":" 문자를 추가
            Text(
                text = ":",
                fontSize = 32.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterVertically)
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .height(150.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // 5분 간격으로 표시 (0, 5, 10, ... 55)
                itemsIndexed(items = (0..59 step 5).toList()) { _, minute ->
                    Text(
                        text = minute.toString().padStart(2, '0'),
                        color = if (minute == selectedMinute.value) Color.White else Color.Gray,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedMinute.value = minute
                                onTimeSelected() // 사용자가 시간을 선택했습니다
                            }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

fun showDatePicker(context: Context, dateSetListener: (Int, Int, Int) -> Unit) {
    val currentDateTime = LocalDateTime.now()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dateSetListener(year, month + 1, dayOfMonth)
        },
        currentDateTime.year,
        currentDateTime.monthValue - 1,
        currentDateTime.dayOfMonth
    )
    datePickerDialog.show()
}