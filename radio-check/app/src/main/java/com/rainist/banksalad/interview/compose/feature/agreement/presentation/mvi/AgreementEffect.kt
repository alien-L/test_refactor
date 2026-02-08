package com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi


/**
 * [STEP: AgreementEffect 정의]
 * 라이브 멘트: "이제 UI 상태(State)와는 별개로, 한 번만 실행되어야 하는
 * '일회성 이벤트'를 처리하기 위해 AgreementEffect를 정의하겠습니다."
 */

//todo
// sealed interface 가 뭔데 ?
//sealed interface를 사용함으로써 컴파일 타임에 모든 이펙트의 종류를 파악
sealed  interface AgreementEffect {

    /*
라이브 멘트 시나리오

“AgreementEffect는 UI에서 단발성으로 처리해야 하는 이벤트를 표현하기 위한
Side Effect 전용 sealed interface입니다.

Toast, Snackbar, Dialog 같은 UI 이벤트는
State로 관리하면 재구성(recomposition) 시 중복 실행 위험이 있기 때문에,
State와 분리된 Effect 스트림으로 관리했습니다.”

“ViewModel은 Effect를 발행만 하고,
실제 UI 표현은 Route 레벨에서 처리하도록 책임을 나눴습니다.”
*/
    data class  ShowError(val message: String) : AgreementEffect

    /*
     “에러 상황에서 사용자에게 즉시 알려야 하는 경우를 위한 Effect입니다.
     서버 에러, 필수 약관 미동의 같은 경우에 사용될 수 있습니다.”
     */
    data class ShowToastMessage(val message: String) : AgreementEffect

    /*
  “일반적인 안내성 메시지를 표시하기 위한 Effect입니다.
  성공 메시지나 간단한 피드백에 사용합니다.”
  */
}

/*
*  면접관이 **"왜 굳이 상태(State)에 안 넣고 이펙트를 따로 만드나요?"**라고 물을 때가 가장 결정적인 순간입니다.

Q1. 토스트 메시지를 AgreementUiState에 val toastMessage: String?으로 넣으면 안 되나요?
✅ 모범 답변: "상태(State)에 넣게 되면 '재구성(Recomposition)'이나 '화면 회전' 시 의도치 않게
*  토스트가 다시 뜨는 문제가 발생할 수 있습니다. 상태는 현재 화면의 모습을 유지하는 것이 목적이고,
* 토스트나 스낵바는 특정 시점에 **'한 번만 소비'**되어야 하는 이벤트이므로 SharedFlow나 Channel을
* 통한 Effect로 분리하는 것이 아키텍처적으로 더 견고합니다."

Q2. ShowError와 ShowToastMessage를 왜 굳이 나누셨나요?
✅ 모범 답변: "사용자 경험(UX) 관점에서 에러 피드백과 일반 알림은 처리 방식이 다를 수 있기 때문입니다.
* 예를 들어, 일반 토스트는 하단에 잠깐 띄우지만, ShowError는 스낵바를 띄우거나 진동을 주는 등
* 더 강력한 피드백을 줄 수 있도록 확장성을 고려한 설계입니다."

Q3. 이 이펙트를 처리하기 위해 ViewModel에서는 어떤 스트림을 쓰실 건가요?
✅ 모범 답변: "SharedFlow를 선호합니다. StateFlow와 달리 새로운 값이 구독자에게만 전달되고 상태를
*  보유하지 않기 때문에 일회성 이벤트를 처리하기에 가장 적합합니다. 만약 이벤트가 유실되지 않는 것이 매우 중요하다면
* Channel을 고려할 수도 있습니다."
*
*Q. 왜 이걸 UiState에 넣지 않고 Effect로 분리했나요?
A.
“Toast나 Snackbar는 화면이 다시 그려질 때마다
다시 실행되면 안 되는 ‘단발성 이벤트’입니다.
UiState에 포함시키면 recomposition 시 중복 실행 문제가 발생할 수 있어서,
Flow 기반 Effect로 분리했습니다.”

Q. sealed interface를 사용한 이유는 뭔가요?
A.
“Effect의 타입을 컴파일 타임에 제한하기 위해서입니다.
when 분기에서 누락되는 케이스를 방지할 수 있고,
새로운 Effect가 추가되더라도 안전하게 확장할 수 있습니다.”

Q. Channel이나 SharedFlow 대신 왜 이런 구조를 썼나요?
A.
“구현체에서는 SharedFlow를 사용하되,
UI 입장에서는 Effect 타입만 알도록 추상화했습니다.
이렇게 하면 UI는 구현 세부 사항에 의존하지 않고,
테스트와 유지보수가 쉬워집니다.”

Q. Effect가 많아지면 관리가 어려워지지 않나요?
A.
“그래서 Screen 단위로 Effect를 정의합니다.
해당 화면에서 필요한 Effect만 갖도록 범위를 제한해서
복잡도가 전파되지 않게 설계했습니다.”

Q. Dialog나 Navigation도 Effect로 처리할 수 있나요?
A.
“네, 가능합니다.
NavigateTo, ShowDialog 같은 Effect를 추가해서
Route 레벨에서 처리하면 화면 전환과 UI 이벤트를
일관된 방식으로 관리할 수 있습니다.”
*/

