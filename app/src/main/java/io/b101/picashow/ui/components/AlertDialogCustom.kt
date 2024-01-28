package io.b101.picashow.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomAlertDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onConfirm, // 닫기 동작이 onConfirm과 동일하게 동작하도록 설정
        title = {
            Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = description)
        },
        confirmButton = {
            // 버튼 스타일을 커스터마이징
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.padding(bottom = 12.dp, end = 12.dp)
            ) {
                Text("OK", color = Color.Black, fontSize = 13.sp)
            }
        },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color.White
    )
}