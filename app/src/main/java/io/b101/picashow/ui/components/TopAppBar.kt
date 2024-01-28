package io.b101.picashow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun TopAppBar(
    startContent: @Composable (Modifier) -> Unit,
    title: String,
    showIcon: Boolean,
    endContent: @Composable (Modifier) -> Unit // endContent는 @Composable 람다식으로 받습니다.
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(), // Icon을 포함한 모든 요소의 상하 패딩을 조절합니다.
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically // Row 내의 모든 요소를 세로 중앙 정렬합니다.
    ) {
        if (showIcon) {
            startContent(Modifier)
        } else {
            Box {}
        }

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = Color.White,
        )

        endContent(Modifier) // 여기에 추가적인 offset 없이 Modifier를 그대로 전달합니다.
    }
}