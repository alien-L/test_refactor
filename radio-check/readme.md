1. API 연동을 위한 안드로이드 필수 커리큘럼 (우선순위 순)
   API 연동은 단순히 데이터를 가져오는 게 아니라 **'예외 처리와 상태 관리'**가 핵심입니다.
   단계	핵심 학습 내용	라이브 코테 적용 포인트
   초급	HTTP 기본 (GET/POST), JSON 구조 이해	API 문서를 보고 데이터 클래스(Dto) 설계 능력
   중급	Retrofit2 & OkHttp 기초 사용법	Interface 정의 및 ConverterFactory 설정
   중급+	Repository Pattern	ViewModel이 API를 직접 호출하지 않고 저장소를 거치게 설계
   고급	Coroutines Exception Handling	try-catch 또는 Result 객체를 이용한 에러 대응
   심화	UDF(StateFlow)와 API 결합	로딩 중(Loading), 결과(Success), 실패(Error) 상태 분리


🟢 1단계: 초급 (Foundation & Core)
목표: 안드로이드 앱의 동작 원리를 이해하고 기본적인 화면을 구성할 수 있음
1. Kotlin 언어 정복 (★)
    * 기본 문법: val/var, 제어문(when, if as expression), Null Safety(?., !!, ?:).
    * 함수형: 람다 식, 고차 함수, 확장 함수(Extension Functions).
    * 클래스: Data Class, Sealed Class(상태 정의 핵심), Enum, Object(싱글톤).
    * 컬렉션: List, Set, Map 및 가공 함수(filter, map, flatMap, any, all).
2. 안드로이드 4대 컴포넌트 & 생명주기
    * Activity & Fragment: 생명주기(onCreate ~ onDestroy)의 정확한 호출 시점과 역할.
    * Intent: 컴포넌트 간 데이터 전달 및 화면 전환.
    * Service & Broadcast Receiver & Content Provider: 개념과 기초 사용법.
    * Manifest & Permissions: 권한 요청 프로세스.
3. 현대적 UI 구성 (Jetpack Compose 기초) (★)
    * 기본 Layout: Box, Column, Row, Scaffold, Surface.
    * 기본 Component: Text, TextField, Button, Image, Icon, Checkbox, RadioButton.
    * 상태 관리: remember, mutableStateOf, State Hoisting(상태 끌어올리기).
    * Modifier: 크기, 여백, 클릭 이벤트, 테두리 설정 순서의 중요성.

🟡 2단계: 중급 (Architecture & Efficiency)
목표: 유지보수가 쉬운 구조로 앱을 설계하고 데이터를 효율적으로 관리함
1. 비동기 프로그래밍 (Coroutines) (★)
    * suspend 함수, CoroutineScope, Dispatcher(Main, IO, Default).
    * Kotlin Flow: flow, collect, StateFlow vs SharedFlow.
2. AAC (Android Architecture Components)
    * ViewModel: 화면 회전에도 데이터 유지, UI 로직 분리.
    * LiveData / Flow: 데이터 관찰 및 UI 업데이트.
    * Lifecycle-aware components: 생명주기에 안전한 데이터 처리.
3. 데이터 및 네트워크
    * Retrofit2 & OkHttp: API 연동, Interceptor(로깅, 헤더 추가).
    * Room: 로컬 DB 설계 및 Query 작성.
    * DataStore: 간단한 설정 값 저장 (SharedPreferences 대체).
4. 의존성 주입 (DI)
    * Hilt: 안드로이드 표준 DI 프레임워크 사용법 (기초).
5. Jetpack Compose 심화
    * Lazy Layouts: LazyColumn, LazyRow, LazyVerticalGrid.
    * Navigation: Compose Navigation을 이용한 화면 이동 및 인자 전달.
    * Side Effects: LaunchedEffect, SideEffect, DisposableEffect.

🟠 3단계: 고급 (Expertise & Quality)
목표: 성능 최적화, 테스트 코드 작성, 복잡한 아키텍처 설계 가능
1. 고급 아키텍처 (★)
    * MVI 패턴: Intent(의도) -> State(상태) -> UI 흐름 구현 (이번 코테의 'Rewind' 구현에 유리).
    * Clean Architecture: Data / Domain / Presentation 레이어 분리.
2. 테스트 자동화
    * Unit Test: JUnit5, Mockk를 이용한 ViewModel 로직 검증.
    * UI Test: Compose Test Rule을 이용한 컴포넌트 동작 확인.
3. 성능 최적화 및 디버깅
    * Memory Leak: LeakCanary 사용 및 Context 참조 주의.
    * App Profiling: CPU, 메모리, 네트워크 사용량 분석.
    * Compose 최적화: Stability, derivedStateOf, 재구성(Recomposition) 횟수 줄이기.
4. 고급 UI 및 애니메이션
    * Canvas API: 커스텀 그래픽 드로잉.
    * Compose Animation: animate*AsState, Transition, AnimatedVisibility.
5. Gradle & 빌드 시스템
    * Build Variants(Debug/Release) 설정, Gradle Kotlin DSL, Version Catalog.

🔴 4단계: 전문 엔지니어 (System & Infrastructure)
목표: 대규모 앱의 인프라 설계 및 최신 기술 트렌드 리딩
1. 멀티 모듈 (Multi-module): 앱을 기능별/레이어별 모듈로 분리하여 빌드 속도 개선.
2. CI/CD: GitHub Actions / Bitrise를 이용한 빌드 및 배포 자동화.
3. KMP (Kotlin Multiplatform): Android와 iOS 비즈니스 로직 공유.
4. Design System 구축: 전사적으로 공통 사용되는 UI 컴포넌트 라이브러리 제작.
5. OS Deep Dive: Binder 통신, Looper & Handler 원리, 가상 머신(ART) 이해.

💡 2/13 라이브 코딩테스트를 위한 '초단기 집중' 코스
시간이 없으니 위 로드맵 중 이번 시험(뱅샐)에 합격하기 위해 당장 1주일간 파야 할 것은 다음과 같습니다.
1. Compose 상태 관리 (2단계): 체크박스 여러 개의 상태를 어떻게 List로 관리하고, "전체 동의"와 동기화할 것인가? (State Hoisting)
2. MVI/MVVM 설계 (3단계): Sealed Class로 UI 상태를 정의하고, ViewModel에서 상태를 업데이트하는 구조.
3. 코틀린 컬렉션 함수 (1단계): 리스트 데이터를 filter, any, all 등으로 자유자재로 가공하기.
4. Rewind 로직 연습: Stack 자료구조나 List.take() 함수를 이용해 이전 상태로 되돌리는 로직을 별도 클래스로 분리해 보기.



🚨 1순위: 필수 중의 필수 (이것만 해도 70%는 합격)
"상태(State)를 자유자재로 다루고 동기화할 수 있는가?"
1. Compose State Hoisting (상태 끌어올리기): * 체크박스 개별 상태를 하위 컴포저블이 갖지 않고, ViewModel(또는 부모)로 올리는 법.
2. Kotlin Collection 함수 (★가장 중요): * all { it.checked }: 모든 약관이 동의됐는지 확인 (전체 동의 버튼 연동).
    * any { it.checked }: 하나라도 동의됐는지 확인.
    * map { it.copy(checked = true) }: 전체 동의 클릭 시 리스트 전체 변경.
3. ViewModel + StateFlow: * UI 상태를 StateFlow로 들고 있고, UI(Compose)에서 collectAsStateWithLifecycle()로 구독하는 구조.
4. Data Class Immutability (불변성): * 상태를 바꿀 때 list[0].checked = true (X) -> list.map { it.copy(...) } (O) 처럼 객체 복사를 통해 새로운 리스트를 만드는 법. (그래야 Compose가 인지하고 화면을 다시 그립니다.)

🥈 2순위: 구현 완성도 (변별력이 생기는 지점)
"복잡한 기능(Rewind)을 구조적으로 짤 수 있는가?"
1. LIFO(후입선출) 구조와 히스토리 관리: * MutableList를 Stack처럼 활용하여 List<List<Term>> (상태들의 리스트) 저장하는 법.
    * Rewind 클릭 시 마지막 요소를 제거(removeLast)하고 현재 상태로 복구하는 로직.
2. Sealed Class를 활용한 UI State 설계: * 단순 리스트가 아니라 Loading, Success, Error 상태를 정의해 코드의 격을 높이기.
3. Side Effects (Compose): * 로그가 추가될 때 자동으로 스크롤을 내리기 위한 LaunchedEffect 사용법.
4. Component Interaction: * 라디오 버튼(단일 선택)과 체크박스(다중 선택)의 비즈니스 로직 연결 (예: 라디오 1번 선택 시 체크박스 모두 해제 등).

🥉 3순위: 디테일 및 가산점 (고급 역량)
"실수 없이 깔끔한 코드를 짜는가?"
1. DerivedStateOf 최적화: * 전체 동의 여부처럼 다른 상태에 의존하는 상태를 계산할 때 Recomposition을 줄이는 법.
2. Coroutine 비동기 처리: * Play 버튼 클릭 시 네트워크 통신을 가정하여 delay(1000)를 주고 로딩 바 보여주기.
3. String Formatting: * 로그를 찍을 때 joinToString이나 buildString을 사용하여 가독성 있게 만들기.
4. 예외 상황 방어 (Edge Cases): * 히스토리가 비었을 때 Rewind 버튼 비활성화, 체크박스가 0개일 때의 처리 등.


