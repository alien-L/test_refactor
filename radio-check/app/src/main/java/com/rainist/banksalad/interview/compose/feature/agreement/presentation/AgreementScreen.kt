package com.rainist.banksalad.interview.compose.feature.agreement.presentation

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementIntent
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementUiState
import com.rainist.banksalad.interview.compose.ui.component.CheckboxWithText
import com.rainist.banksalad.interview.compose.ui.component.RadioButtonWithText


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AgreementScreen(
    state: AgreementUiState,
    onIntent: (AgreementIntent) -> Unit,
    modifier: Modifier = Modifier // [정석] Modifier 파라미터 추가
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "약관 동의") })
        }
    ) { paddingValues -> // [교정] 안 쓰던 paddingValues를 Modifier.padding에 적용

        if (state.isLoading) {
            LoadingBox()
        } else {
            // [분리] 메인 컨텐츠를 별도 함수로 추출하여 가독성 향상
            AgreementContent(
                state = state,
                onIntent = onIntent,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}


@Composable
private fun AgreementContent(
    state: AgreementUiState,
    onIntent: (AgreementIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 1. 선택 영역 (라디오 버튼 & 체크박스 리스트)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 라디오 버튼 그룹
            AgreementTypeSelectors(state, onIntent)

            // 개별 체크박스 리스트
            AgreementItemList(state.agreementItems, onIntent)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. 컨트롤 버튼 영역
        ActionButtons(onIntent)

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 로그 콘솔 영역 (LazyColumn으로 성능 최적화)
        LogConsole(logs = state.logs, modifier = Modifier.weight(1f))
    }
}

// --------------------------------------------------------------------
// 하위 컴포넌트들 (Bottom area)
// --------------------------------------------------------------------

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier
            .fillMaxSize(), // 전체 화면을 꽉 채우고
        contentAlignment = Alignment.Center // 자식인 프로그레스를 정중앙에 배치
    ) {
        CircularProgressIndicator(
            // 뱅샐 컬러가 있다면 여기서 지정 (예: Color(0xFF00C7AE))
            color = MaterialTheme.colors.primary
        )
    }
}
@Composable
private fun AgreementTypeSelectors(
    state: AgreementUiState,
    onIntent: (AgreementIntent) -> Unit
) {
    // 초기 상태 모두 체크 안한 상태 유지했음 좋겠어
    Column(modifier = Modifier.selectableGroup()) {
        RadioButtonWithText(
            text = "모든 약관 동의",
            selected = state.isAllAgree,
            onClick = { onIntent(AgreementIntent.OnSelectAll(true)) }
        )
        RadioButtonWithText(
            text = "필수 약관만 동의",
            selected = state.isPartiallyAgreed,
            onClick = { onIntent(AgreementIntent.OnSelectRequiredOnly) }
        )
        RadioButtonWithText(
            text = "모든 약관 미동의",
            selected = state.isNoneAgreed,
            onClick = { onIntent(AgreementIntent.OnSelectAll(false)) }
        )
    }
}

@Composable
private fun AgreementItemList(
    agreements: List<AgreementItem>,
    onIntent: (AgreementIntent) -> Unit
) {
    Column {
        agreements.forEach { item ->
            CheckboxWithText(
                text = item.title,
                checked = item.isChecked,
                onCheckedChange = { isChecked ->
                    onIntent(AgreementIntent.OnAgreementChecked(item.id))
                }
            )
        }
    }
}
// 하위 컴포넌트

@Composable
private fun ActionButtons(onIntent: (AgreementIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onIntent(AgreementIntent.Play) }) {
            Text("PLAY")
        }
        Button(onClick = { onIntent(AgreementIntent.Rewind) }) {
            Text("REWIND")
        }
        // 복구 제한 무제한으로 관리할 것인지 20개 이런식으로 관리 할 것인지 , 복구가 더이상 안될때 색 변경 및 팝업 노출
        // 몇번쨰 스택인지 적어야 할듯? 숫자로 표시 예를 들어 2/ 12 이런식
    }
}

@Composable
private fun LogConsole(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    // 로그 추가 시 자동 스크롤
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            lazyListState.animateScrollToItem(logs.size - 1)
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray.copy(alpha = 0.2f))
            .padding(8.dp)
    ) {
        items(logs) { log ->
            Text(text = "> $log", style = MaterialTheme.typography.caption)
        }
    }
}