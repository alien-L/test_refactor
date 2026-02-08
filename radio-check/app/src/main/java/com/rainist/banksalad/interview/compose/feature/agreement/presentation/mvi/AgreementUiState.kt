package com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi

import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem

/*
라이브 멘트 시나리오

“AgreementUiState는 화면을 그리기 위해 필요한 모든 상태를
단일 객체로 관리하는 역할을 합니다.”

“Compose에서는 상태가 곧 UI이기 때문에,
이 UiState만 바뀌면 화면은 자동으로 재구성되도록 설계했습니다.”

“중요한 점은,
UI에서 계산하지 않고 ‘파생 상태(derived state)’를
UiState 내부에서 제공한다는 점입니다.”
*/
data class AgreementUiState(

    /*
    “현재 로딩 중인지 여부입니다.
    추후에는 Boolean 대신 LoadState나 sealed class로 확장할 수 있습니다.”
    */
    val isLoading: Boolean = false, // todo LoadState

    /*
    “약관 목록 데이터입니다.
    각 아이템은 체크 여부와 필수 여부를 포함하고 있습니다.”
    */
    val agreementItems: List<AgreementItem> = emptyList(),

    /*
    “사용자 액션 히스토리를 화면에 보여주기 위한 로그 리스트입니다.
    단순 디버그용이 아니라 UX 실험을 위한 상태로 취급했습니다.”
    */
    val logs: List<String> = emptyList(), // 로그뷰

    /*
    “되감기(Rewind)가 가능한지 여부를 나타냅니다.
    버튼 활성/비활성 판단을 UI에서 하지 않도록 분리했습니다.”
    */
    val isHistoryAvailable: Boolean = false
) {

    /*
    라이브 멘트

    “이 아래 값들은 서버에서 내려오는 데이터가 아니라,
    현재 상태를 기반으로 계산되는 ‘파생 상태’입니다.”

    “Composable에서 if 조건이나 계산 로직을 반복하지 않기 위해
    UiState에서 책임지도록 했습니다.”
    */

    // [전체 동의]
    // 모든 항목이 체크된 상태

    /**
     * [파생 상태 설계]
     * 라이브 멘트: "라디오 버튼의 선택 여부를 별도의 변수로 관리하지 않고,
     * agreementItems의 상태를 보고 실시간으로 '유도'되도록 프로퍼티를 구성하겠습니다.
     * 이렇게 하면 체크박스를 건드릴 때 라디오 버튼이 꼬이는 싱크 버그를 완전히 방지할 수 있습니다."
     */
    val isAllAgree get() =
        agreementItems.isNotEmpty() &&
                agreementItems.all { it.isChecked }

    // [부분 동의]
    // 필수 항목만 체크되고, 선택 항목은 체크되지 않은 상태
    val isPartiallyAgreed get() =
        agreementItems.isNotEmpty() &&
                agreementItems.all { item ->
                    if (item.isRequired) item.isChecked else !item.isChecked
                }

    // [미동의]
    // 모든 항목이 체크 해제된 상태
    // 멘트: "아무것도 선택되지 않은 상태 역시 컬렉션 함수인 all을 사용하여 선언적으로 표현하겠습니다."
    val isNoneAgreed get() =
        agreementItems.isNotEmpty() &&
                agreementItems.all { !it.isChecked }
}

/*
========================
예상 질문 & 모범 답변
========================

Q. 왜 UI에서 계산하지 않고 UiState에 계산 로직을 넣었나요?
A.
“Compose에서는 상태가 UI를 결정하기 때문에,
계산 로직이 UI에 흩어지면 가독성과 테스트성이 급격히 떨어집니다.
UiState에서 파생 상태를 제공하면
Composable은 ‘그리기’에만 집중할 수 있습니다.”

Q. isAllAgree / isNoneAgreed가 없어도 UI에서 계산할 수 있지 않나요?
A.
“가능은 하지만,
여러 Composable에서 같은 로직을 반복하게 됩니다.
중복 로직을 제거하고 단일 진실 공급원(Single Source of Truth)을
유지하기 위해 UiState로 올렸습니다.”

Q. 파생 상태가 많아지면 성능 문제는 없나요?
A.
“getter 기반 계산은 리스트 크기만큼 O(n)이고,
Compose는 상태 변경 시에만 recomposition이 일어나기 때문에
이 정도 계산은 충분히 안전합니다.
필요하면 remember나 derivedStateOf로 최적화할 수 있습니다.”

Q. isPartiallyAgreed 로직이 복잡한데 이렇게 둔 이유는요?
A.
“‘필수 약관만 동의’라는 비즈니스 규칙을
UI가 아니라 상태 모델에 명시적으로 표현하고 싶었습니다.
이렇게 하면 요구사항 변경 시 수정 범위가 명확해집니다.”

Q. agreementItems가 비어 있을 때 false로 처리한 이유는요?
A.
“초기 로딩 상태에서
라디오 버튼이 잘못 선택되는 걸 방지하기 위함입니다.
명시적으로 ‘아무 상태도 아님’을 표현했습니다.”

Q1. 왜 라디오 버튼 상태(isAllAgree 등)를 별도의 State 변수로 만들지 않았나요?
✅ 모범 답변: "상태의 **중복(Redundancy)**을 피하기 위해서입니다. 만약 isAllAgree를 별도 변수로 두면,
개별 체크박스가 바뀔 때마다 해당 변수도 매번 수동으로 업데이트해야 합니다.
이는 로직이 복잡해질수록 데이터가 불일치하는 버그를 유발합니다.
리스트라는 **'단일 진실 공급원'**에서 값을 유도하면 항상 일관된 UI를 보장할 수 있습니다."

Q2. isLoading을 todo로 적으셨는데, 어떻게 개선하고 싶으신가요? (LoadState 질문)
✅ 모범 답변: "현재는 단순 Boolean이지만, 실무라면 Loading, Success, Error 상태를 포함하는 sealed interface 기반의
LoadState로 관리했을 것입니다.
 그렇게 하면 화면 진입 시 로딩과 데이터를 새로고침할 때의 로딩을 구분하거나, 에러 메시지를 더 구조적으로 전달할 수 있기 때문입니다."

Q3. 약관 리스트가 수천 개라면 이 파생 변수(get()) 호출이 성능에 문제를 주지 않을까요?
✅ 모범 답변: "현재 약관 도메인에서는 항목이 적어 문제가 없지만, 리스트가 방대해진다면
Compose의 **derivedStateOf**를 활용하여 계산 결과가 바뀔 때만 재구성이 일어나도록 최적화할 수 있습니다.
또한 AgreementUiState 자체를 불변으로 관리하므로 리스트 객체가 바뀔 때만 연산이 수행되도록 설계했습니다."
*/



//enum class LoadState {
//    IDLE, LOADING, SUCCESS, ERROR
//}