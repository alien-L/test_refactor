
💡 2/13 라이브 코딩테스트를 위한 '초단기 집중' 코스
시간이 없으니 위 로드맵 중 이번 시험(뱅샐)에 합격하기 위해 당장 1주일간 파야 할 것은 다음과 같습니다.(State Hoisting)
1. Compose 상태 관리 (2단계): 체크박스 여러 개의 상태를 어떻게 List로 관리하고, "전체 동의"와 동기화할 것인가? 
2. MVI/MVVM 설계 (3단계): Sealed Class로 UI 상태를 정의하고, ViewModel에서 상태를 업데이트하는 구조.
3. 코틀린 컬렉션 함수 (1단계): 리스트 데이터를 filter, any, all 등으로 자유자재로 가공하기.
4. Rewind 로직 연습: Stack 자료구조나 List.take() 함수를 이용해 이전 상태로 되돌리는 로직을 별도 클래스로 분리해 보기.
5. LIFO(후입선출) 구조와 히스토리 관리: * MutableList를 Stack처럼 활용하여 List<List<Term>> 저장하는 법.
    * Rewind 클릭 시 마지막 요소를 제거(removeLast)하고 현재 상태로 복구하는 로직.
6. Sealed Class를 활용한 UI State 설계: * 단순 리스트가 아니라 Loading, Success, Error 상태를 정의
7. 예외 상황 방어 (Edge Cases): * 히스토리가 비었을 때 Rewind 버튼 비활성화, 체크박스가 0개일 때의 처리 등.

"구현에 앞서 제가 설계한 구조의 핵심 가이드라인을 먼저 말씀드리겠습니다."

MVI 아키텍처: "데이터의 흐름을 한눈에 파악하고 예측 가능한 상태 관리를 위해 MVI(Model-View-Intent) 패턴을 채택했습니다.
모든 유저 액션은 단방향 데이터 흐름(UDF)에 따라 처리됩니다."

관심사 분리: "UI 레이어는 Stateless하게 유지하여 테스트와 재사용성을 높이고, 비즈니스 로직은 ViewModel에,
데이터 수집은 Repository에 철저히 격리하겠습니다."

데이터 무결성: "전체 동의나 미동의 같은 상태는 별도의 변수로 관리하지 않고, 
리스트 원본 데이터에서 유도하는 파생 상태(Derived State) 방식을 사용해 싱크 버그를 원천 차단하겠습니다."

사용자 경험(UX): "과제의 핵심인 Undo(되감기) 기능을 위해 상태 스냅샷 스택을 구현하고
, 로그 콘솔을 통해 실시간 피드백을 제공하겠습니다."

🏗️ 2. 실전 개발 순서 (Top-Down vs Bottom-Up)
라이브 코딩에서는 "작은 부품을 먼저 만들고, 마지막에 큰 그림을 완성하는 Bottom-Up" 방식이 오류를 줄이고 
흐름을 타기에 가장 좋습니다. 아래 순서대로 진행하세요.

Phase 1: 데이터 모델 및 인터페이스 (기초 공사)
AgreementItem: 가장 기본이 되는 데이터 모델 정의.

AgreementRepository: 데이터 소스에 대한 인터페이스 정의 (추상화).

AgreementRepositoryImpl: Mock 데이터를 이용한 가짜 구현체 작성 (delay 포함).

Phase 2: MVI 계약 (설계 도면)
AgreementUiState: 화면의 모든 상태 정의 (isAllAgree 등 파생 변수 포함).

AgreementIntent: 유저의 행동(Intent) 명세화.

AgreementEffect: 일회성 이벤트(Toast, Error) 정의.

Phase 3: ViewModel (두뇌 구현)
handleIntent 작성: 유저 행동에 따른 분기 처리.

비즈니스 로직 작성: loadAgreements, saveHistory, rewind 등 핵심 로직 구현.

중요: saveHistory에서 .toList() 스냅샷 처리를 이때 가장 강조하며 코딩하세요.

Phase 4: UI Components (조립식 부품)
하위 컴포저블부터 작성: LoadingBox, RadioButtonWithText, CheckboxWithText.

기능별 컴포저블: AgreementTypeSelectors, AgreementItemList, LogConsole, ActionButtons.

멘트: "하위 컴포넌트를 Stateless하게 쪼개서 가독성을 높이겠습니다."

Phase 5: 완성 및 연결 (진입점)
AgreementScreen: Scaffold와 하위 부품 조립.

AgreementRoute: ViewModel과 Screen을 최종 연결 (Side Effect 구독 포함).

MainActivity: setContent { AgreementRoute() } 호출.
//========================================================================

“바로 코드부터 치기 전에, 전체 구조와 책임 분리를 먼저 설명하겠습니다.

이 화면은 약관 동의라는 명확한 상태 전환 문제이기 때문에
MVVM 기반에 MVI 패턴을 섞어서 설계했습니다.

UI는 상태를 그리기만 하고,
모든 판단과 상태 변경은 ViewModel에서만 일어나도록 구성했습니다.

또한 체크박스, 라디오 버튼 선택에 따라
여러 상태가 파생되기 때문에
단일 UiState를 기준으로 파생 상태를 계산하도록 했습니다.”

“feature 단위로 패키징했고,
agreement 기능 안에서 presentation, data를 나눴습니다.

Screen은 순수 UI,
Route는 ViewModel과 Effect 연결,
ViewModel은 Intent를 받아 State와 Effect를 생성합니다.”

✅ 1단계: 모델부터 정의 (State / Intent / Effect)
먼저 치는 이유 멘트
“UI를 그리기 전에
이 화면에서 발생할 수 있는 상태와 이벤트를 먼저 정의하겠습니다.
이렇게 하면 UI와 로직이 흔들리지 않습니다.”

실제 순서

AgreementItem

AgreementUiState

AgreementIntent

AgreementEffect


✅ 2단계: Repository (데이터 소스 고정)
멘트
“데이터 소스를 인터페이스로 먼저 분리해
ViewModel이 구현체에 의존하지 않도록 했습니다.”


✅ 3단계: ViewModel (핵심)
시작 멘트
“이제 이 화면의 핵심인 ViewModel을 구현하겠습니다.
모든 상태 변경은 Intent를 통해서만 일어납니다.”

✅ 4단계: Route (상태 연결)
멘트
“Screen은 순수 UI로 유지하기 위해
Route에서 ViewModel과 Effect를 연결합니다.”

✅ 5단계: Screen (UI는 마지막)
멘트
“UI는 최대한 단순하게,
State를 그대로 그리도록 구성했습니다.”

❓ “왜 UI부터 안 만들었나요?”
“UI는 언제든 바뀔 수 있지만,
상태와 로직은 구조의 뼈대이기 때문입니다.”

❓ “이렇게 하면 코드가 많아지지 않나요?”
“초기엔 코드가 많아 보이지만,
요구사항 변경 시 수정 범위가 줄어듭니다.”



