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

//Q: @SuppressLint("UnusedMaterialScaffoldPaddingParameter")를 제거하고 왜 padding을 굳이 적용했나요?
//
//A: "Scaffold 내부에 TopAppBar나 BottomBar가 있을 경우,
// 시스템이 계산한 여백을 무시하면 UI가 가려질 수 있습니다.
// 주석으로 경고를 끄는 대신 안드로이드 권장 가이드라인에 따라
// Modifier.padding(paddingValues)를 적용하는 것이 안전하기 때문입니다."
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AgreementScreen(
    state: AgreementUiState,
    onIntent: (AgreementIntent) -> Unit,
    modifier: Modifier = Modifier // [정석] Modifier 파라미터 추가
) {


    /*
 * [라이브 멘트]
 * "화면의 기본 골격인 Scaffold를 사용하여 상단 바와 컨텐츠 영역을 구성하겠습니다.
 * 여기서 중요한 점은 Scaffold가 제공하는 paddingValues를
 * 내부 컨텐츠에 전달하여 시스템 레이아웃과 UI가 겹치지 않도록 하는 것입니다.
 *  또한, 상태에 따라 LoadingBox와 AgreementContent를 교체하여 사용자에게 현재 앱의 상태를 명확히 전달하겠습니다."
 *
 * "AgreementScreen은 상태와 이벤트 콜백만을 입력으로 받는
 *  Stateless Composable입니다."
 *
 * "ViewModel이나 Context 같은 프레임워크 의존성은 전혀 없고,
 *  오직 화면을 그리는 책임만 가집니다."
 */
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(text = "약관 동의") })
        }
    ) { paddingValues -> // [교정] 안 쓰던 paddingValues를 Modifier.padding에 적용

        if (state.isLoading) {
            LoadingBox() // todo core/ui/component
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

    /*
 * [라이브 멘트]
 * "화면을 역할 단위로 나누기 위해
 *  실제 UI 구성은 AgreementContent로 분리했습니다."
 *
 * "이렇게 하면 Screen은 구조를 한눈에 파악할 수 있고,
 *  각 영역은 테스트와 유지보수가 쉬워집니다."
 */

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

            /*
         * [라이브 멘트]
         * "라디오 버튼은 상태 계산 결과(isAllAgree, isPartiallyAgreed 등)를
         *  그대로 사용해서 선택 여부를 결정합니다."
         *
         * "UI에서 상태를 계산하지 않고,
         *  이미 계산된 값을 ViewModel → State에서 내려받습니다."
         */


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

    /*
  * [라이브 멘트]
  * "로딩 UI는 공통 컴포넌트로 분리해서
  *  다른 화면에서도 재사용 가능하도록 설계했습니다."
  * A: "코드의 가독성을 높이고, 나중에 다른 화면에서도 재사용할 수 있는 공통 컴포넌트로 확장하기 위해서입니다."
  */

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
    /*
 * [라이브 멘트]
 * "RadioButton 그룹은 selectableGroup으로 묶어
 *  접근성과 단일 선택 의미를 명확히 했습니다."
 *
 * "라디오 버튼 그룹은 Modifier.selectableGroup()을 사용하여 접근성을 높였습니다.
 *  여기서 핵심은 UI가 상태를 직접 계산하지 않는 것입니다.
 * 라디오 버튼의 selected 여부는 UiState에서 계산된 파생 상태(isAllAgree 등)를 그대로 구독하게 하여,
 * 데이터의 일관성을 보장했습니다."
 *
 * Q: 라디오 버튼 클릭 시 OnSelectAll(true)처럼 직접 값을 넘기는 이유는?

A: "사용자가 '모든 약관 동의'라는 의사결정을 했을 때,
*  ViewModel이 리스트 전체를 한 번에 업데이트하도록 의도를 명확히 전달하기 위해서입니다."

Q: 세 개의 라디오 버튼 중 하나만 선택되게 하는 로직은 어디에 있나요?

A: "별도의 변수를 두지 않고 agreementItems 리스트의 체크 상태에 따라 실시간으로 계산됩니다.
* 덕분에 상태가 꼬이는 버그를 원천적으로 방지할 수 있습니다."
 */

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

    /*
 * [라이브 멘트]
 * "리스트가 크지 않기 때문에 단순 Column + forEach를 사용했고,
 *  항목이 많아질 경우 LazyColumn으로 쉽게 교체 가능합니다."
 *
 * "개별 약관 리스트는 forEach를 사용하여 배치했습니다. 항목이 많지 않고,
 * 아래에 있는 로그 콘솔(LazyColumn)과의 중첩 스크롤(Nested Scroll) 충돌을
 * 피하기 위해 리사이클러 뷰 방식이 아닌 일반 컬럼 방식을 선택했습니다."
 *
 *
Q: 왜 LazyColumn을 안 쓰고 Column + forEach를 썼나요?

A: "약관 리스트는 보통 갯수가 적고 고정적입니다.
*  화면 전체 스크롤이 필요한 상황에서 내부에 또 다른 LazyColumn이 있으면 스크롤 성능이 저하되거나 구현이
*  복잡해질 수 있습니다. 대신 성능이 정말 필요한 로그 뷰에 LazyColumn을 양보했습니다."
 *
 */

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

    /*
 * [라이브 멘트]
 * "버튼은 UI 이벤트만 전달하고,
 *  실제 로직은 ViewModel에서 처리됩니다."
 *
 * "플레이와 되감기 버튼 영역입니다. onIntent를 통해 모든 상호작용을 단방향으로 전달합니다.
 *  주석으로 메모해두었듯, 실무 환경이라면 Rewind 버튼에 enabled = state.isHistoryAvailable 처리를
 *  추가하여 사용자가 불가능한 동작을 시도하지 못하게 시각적 가이드를 주었을 것입니다."
 */

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

    /*
 * [라이브 멘트]
 * "로그가 추가될 때마다 자동으로 하단으로 스크롤되도록
 *  LaunchedEffect를 사용했습니다."
 *" 로그 뷰는 무한히 길어질 수 있으므로 LazyColumn을 사용해 화면에 보이는 부분만 렌더링하도록 최적화했습니다.
 * 특히 새로운 로그가 추가될 때 사용자가 직접 스크롤할 필요가 없도록,
 *  LaunchedEffect를 활용해 자동 하단 스크롤 기능을 구현하여 UX를 개선했습니다."
 * "이것은 UI 상태 변화에 따른 Side Effect의 좋은 예시입니다."
 *
 * Q: LaunchedEffect(logs.size)를 쓴 이유는 무엇인가요?
    A: "로그 리스트의 크기가 변경될 때만 스크롤 로직을 실행하기 위해서입니다.
    *  컴포즈의 부수 효과 관리 기능을 통해 불필요한 애니메이션 실행을 방지했습니다."
 */
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


/*
======================
[예상 질문 & 모범 답변]
======================

"현재는 모든 컴포넌트를 한 파일에 두었지만, 실제 프로젝트라면 ui.component 패키지에 디자인 시스템화하여 관리할 것입니다.
또한 로그 영역에 Modifier.weight(1f)를 주어 기기
사이즈에 관계없이 버튼들이 가려지지 않고 로그 뷰가 가변적으로 확장되도록 설계했습니다."

Q. 왜 Screen에서 상태 계산을 안 하나요?
A.
"상태 계산은 ViewModel 책임입니다.
 UI는 계산된 결과를 그대로 표현하는 역할만 해야
 단방향 데이터 흐름이 유지됩니다."

Q. 이 구조가 State Hoisting에 맞나요?
A.
"네. 모든 상태는 ViewModel → Route → Screen으로 내려오고,
 UI 이벤트는 Intent로만 위로 전달됩니다."

Q. Compose에서 Side Effect는 어디서 처리하나요?
A.
"토스트나 자동 스크롤 같은 Side Effect는
 LaunchedEffect를 사용해 명확히 분리했습니다."

Q. 이 화면이 커지면 어떻게 확장하나요?
A.
"이미 역할 단위로 Composable이 분리되어 있어서
 기능 단위로 파일을 나누거나 재사용하기 쉽습니다."
*/