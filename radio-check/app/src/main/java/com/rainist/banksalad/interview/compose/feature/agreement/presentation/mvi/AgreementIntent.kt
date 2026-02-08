package com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi

/*
라이브 멘트 시나리오

“AgreementIntent는 사용자의 모든 ‘의도’를 명시적으로 표현하는 계층입니다.
버튼 클릭, 체크 상태 변경, 화면 진입 같은 사용자 액션을
UI 이벤트가 아니라 ‘의미 있는 행동’ 단위로 모델링했습니다.”

“Composable에서는 단순히 Intent만 발생시키고,
어떤 로직이 실행될지는 ViewModel이 결정하도록 책임을 분리했습니다.”

“이 구조는 MVI 패턴에서 말하는 Intent 역할을 하며,
이벤트 흐름을 추적하거나 로그를 남기기에도 유리합니다.”
*/
sealed interface AgreementIntent {

    /*
    “화면 최초 진입 시 호출되는 Intent입니다.
    약관 목록을 로딩하거나 초기 상태를 구성하는 트리거 역할을 합니다.”
    */
    object OnInitialLoad : AgreementIntent

    /*
    “전체 동의 / 전체 해제 액션입니다.
    체크 여부를 파라미터로 명시해서
    ViewModel이 현재 상태를 다시 계산하지 않도록 했습니다.”
    */
    data class OnSelectAll(
        val isChecked: Boolean
    ) : AgreementIntent

    /*
    “필수 약관만 빠르게 동의하는 UX를 위한 Intent입니다.
    비즈니스 규칙을 UI가 아니라 ViewModel 쪽으로 위임하기 위해
    별도의 Intent로 분리했습니다.”
    */
    object OnSelectRequiredOnly : AgreementIntent

    /*
    “개별 약관 항목을 토글할 때 사용하는 Intent입니다.
    UI는 index가 아니라 id만 전달하고,
    실제 데이터 탐색은 ViewModel에서 수행합니다.”
    */
    data class OnAgreementChecked(
        val id: Int
    ) : AgreementIntent

    /*
    “약관 동의가 완료된 후 다음 단계로 진행할 때의 Intent입니다.
    로그 기록, 유효성 검사, 서버 전송 같은 후처리를
    한 곳에서 관리하기 위한 진입점입니다.”
    */
    object Play : AgreementIntent

    /*
    “사용자가 이전 상태로 되돌리고 싶을 때의 Intent입니다.
    로그 히스토리나 스냅샷을 기반으로
    상태를 복구하는 시나리오를 염두에 두고 설계했습니다.”
    */
    object Rewind : AgreementIntent
}

/*
========================
예상 질문 & 모범 답변
========================

Q. 왜 sealed interface를 사용했나요?
A.
“Intent의 종류를 컴파일 타임에 제한하기 위해서입니다.
ViewModel에서 when 처리 시 누락 케이스를 방지할 수 있고,
새로운 Intent가 추가돼도 안전하게 확장할 수 있습니다.”

Q. UI에서 바로 함수 호출하면 더 간단하지 않나요?
A.
“단기적으로는 간단해 보이지만,
이벤트 흐름이 분산되면 디버깅과 테스트가 어려워집니다.
Intent로 추상화하면 ‘무슨 행동이 발생했는지’를
한 눈에 파악할 수 있습니다.”

Q. OnSelectAll에서 Boolean을 넘기는 이유는 뭔가요?
A.
“UI는 사용자의 ‘의도’만 전달하고,
현재 상태를 해석하는 책임은 ViewModel에 두기 위해서입니다.
이렇게 하면 상태 계산 로직이 UI에 스며들지 않습니다.”

Q. 필수 동의 로직을 UI에서 처리하면 안 되나요?
A.
“비즈니스 규칙은 UI에 두지 않는 게 원칙입니다.
UI는 단순히 Intent를 발생시키고,
규칙은 ViewModel에서 일관되게 처리해야
테스트와 유지보수가 쉬워집니다.”

Q. Play / Rewind 같은 Intent는 오버설계 아닌가요?
A.
“단순 화면이라면 과할 수 있지만,
로그, 히스토리 복구, 실험 기능이 붙는 순간
Intent 단위 설계가 큰 장점이 됩니다.
확장 가능성을 고려한 설계입니다.”

Q1. 왜 인터페이스(함수 호출)가 아니라 굳이 Intent라는 객체로 감싸서 보내나요?
✅ 모범 답변: "두 가지 이유가 있습니다. 첫째는 **'추적 가능성(Traceability)'**입니다.
모든 유저 액션이 하나의 handleIntent 함수를 거치게 되어 로그를
 찍거나 디버깅할 때 흐름을 파악하기 매우 유리합니다.
 둘째는 **'테스트 용이성'**입니다. 특정 Intent 객체만 ViewModel에
 던져주면 되기 때문에 UI 없이도 비즈니스 로직을 완벽하게 검증할 수 있습니다."

Q2. sealed interface를 사용한 특별한 이유가 있나요?
✅ 모범 답변: "sealed 키워드를 사용하면 컴파일러가 모든 하위 타입을 알고 있기 때문에,
ViewModel의 when 절에서 else 문 없이 모든 케이스를 처리하도록 강제할 수 있습니다.
이는 새로운 Intent가 추가되었을 때 처리를 누락하는 실수를 컴파일 단계에서 방지해 줍니다."

Q3. OnSelectAll은 data class인데, 왜 다른 것들은 object인가요?
✅ 모범 답변: "isChecked와 같이 외부에서 전달받아야 할 데이터가 있는 경우는 data class가 필요하지만,
데이터 전달이 필요 없는 단순 명령형 액션들은 싱글톤인 object로 선언하는 것이 메모리 효율 측면에서 더 낫기 때문입니다."
*/

