package com.rainist.banksalad.interview.testui.terms

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rainist.banksalad.interview.compose.ui.component.CheckboxWithText
import com.rainist.banksalad.interview.compose.ui.component.RadioButtonWithText

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun TestMain() {

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Checkbox & RadioButton")
                    }
                )
            }
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.selectableGroup()) {
                        RadioButtonWithText(text = "모든 약관 동의", selected = false, onClick = { })
                        RadioButtonWithText(text = "일부 약관 동의", selected = false, onClick = { })
                        RadioButtonWithText(text = "모든 약관 미동의", selected = false, onClick = { })
                    }

                    Column {
                        CheckboxWithText(text = "약관 1 동의", checked = false, onCheckedChange = { })
                        CheckboxWithText(text = "약관 2 동의", checked = false, onCheckedChange = { })
                        CheckboxWithText(text = "약관 3 동의", checked = false, onCheckedChange = { })
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .widthIn(min = 49.dp)
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "PLAY",
                            textAlign = TextAlign.Center,
                        )
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .widthIn(min = 49.dp)
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "REWIND",
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "동작에 따른 출력을 담당하는 텍스트 화면",
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                }
            }
        }
    }
}