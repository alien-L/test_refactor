package com.rainist.banksalad.interview.compose.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun CheckboxWithText(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit,
                     textColor: Color = Color.Unspecified) {
    // 1. 기본값을 설정해 기존 호출부의 에러를 방지하면서 확장함
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(
            text = text,
            color = textColor
        )
    }
}
