package com.rainist.banksalad.interview.testui.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rainist.banksalad.interview.compose.ui.component.CheckboxWithText
import com.rainist.banksalad.interview.compose.ui.component.RadioButtonWithText

@Composable
fun TermsScreen(viewModel: TermsViewModel) {
    // 1. ViewModel의 상태를 구독 (상태가 변하면 자동으로 UI 재구성)
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // 2. 로그가 추가될 때마다 최하단으로 자동 스크롤하는 효과
    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) {
            scrollState.animateScrollTo(Int.MAX_VALUE)
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("약관 동의 (MVI)") }) }
    ) { paddingValues ->
        // 로딩 중일 때 표시
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // 상단 영역: 라디오 버튼(왼쪽) + 체크박스(오른쪽)
                Row(modifier = Modifier.fillMaxWidth()) {
                    // 왼쪽: 전체 동의 상태 라디오 버튼
                    Column(modifier = Modifier.weight(1f)) {
                        Text("전체 상태", style = MaterialTheme.typography.subtitle2)
                        RadioButtonWithText(
                            text = "모든 약관 동의",
                            selected = state.isAllAgreed,
                            onClick = { viewModel.handleIntent(TermsIntent.SetAll(true)) }
                        )
                        RadioButtonWithText(
                            text = "일부 약관 동의",
                            selected = //state.isMandatoryOnlyAgreed, // <--- 계산된 프로퍼티 연결
                                !state.isAllAgreed && state.terms.any { it.isChecked },
                            onClick = {
                                viewModel.handleIntent(TermsIntent.SetMandatoryOnly)
                            /* 읽기 전용으로 두거나 힌트 제공 */ }
                        )
                        RadioButtonWithText(
                            text = "모든 약관 미동의",
                            selected = state.terms.none { it.isChecked },
                            onClick = { viewModel.handleIntent(TermsIntent.SetAll(false)) }
                        )
                    }

                    // 오른쪽: 개별 약관 체크박스 리스트
                    Column(modifier = Modifier.weight(1f)) {
                        Text("개별 동의", style = MaterialTheme.typography.subtitle2)
                        state.terms.forEach { term ->
                            // 이제 색상과 텍스트를 조건에 맞게 넘길 수 있습니다.
                            CheckboxWithText(
                                text = if (term.isRequired) "[필수] ${term.title}" else "[선택] ${term.title}",
                                checked = term.isChecked,
                                onCheckedChange = { viewModel.handleIntent(TermsIntent.ToggleTerm(term.id)) },
                                textColor = if (term.isRequired) Color.Black else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 중간 영역: 제어 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.handleIntent(TermsIntent.Play) },
                        modifier = Modifier.width(120.dp)
                    ) {
                        Text("PLAY")
                    }

                    Button(
                        onClick = { viewModel.handleIntent(TermsIntent.Rewind) },
                        modifier = Modifier.width(120.dp),
                        enabled = state.isHistoryAvailable // 히스토리가 있을 때만 활성화
                    ) {
                        Text("REWIND")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 하단 영역: 로그 출력창
                Text("동작 로그", style = MaterialTheme.typography.subtitle2)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // 남은 공간 모두 차지
                        .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (state.logs.isEmpty()) "로그가 없습니다." else state.logs.joinToString("\n"),
                        modifier = Modifier.verticalScroll(scrollState),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
    }
}