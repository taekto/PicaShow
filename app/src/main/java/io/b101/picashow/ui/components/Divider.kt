package io.b101.picashow.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun GrayDivider() {
    Divider(
        color = Color.Gray, // Divider의 색상을 지정
        thickness = 1.dp,    // Divider의 두께를 지정
        modifier = Modifier.fillMaxWidth() // Divider의 너비를 TextField와 같게 설정
    )
}