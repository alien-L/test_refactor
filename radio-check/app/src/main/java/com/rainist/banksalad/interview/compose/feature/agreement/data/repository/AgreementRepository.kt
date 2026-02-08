package com.rainist.banksalad.interview.compose.feature.agreement.data.repository

import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem

/*
라이브 멘트 시나리오

“AgreementRepository는 데이터 소스의 추상화 계층입니다.”

“ViewModel이 실제 데이터가
네트워크에서 오는지, 로컬에서 오는지 알 필요 없도록
인터페이스로 분리했습니다.”

“이번 과제에서는 API 연동은 하지 않지만,
실무 확장성을 고려해 Repository 계층을 먼저 정의했습니다.”

* 라이브 멘트: "데이터 소스의 구체적인 구현과 비즈니스 로직을 분리하기 위해
 * AgreementRepository 인터페이스를 정의하겠습니다.
 * 이를 통해 ViewModel은 데이터가 로컬 DB에서 오는지, 네트워크에서 오는지 알 필요 없이
 * 오직 필요한 기능에만 집중할 수 있게 됩니다."
*/
interface AgreementRepository {

    /*
    “약관 동의 항목을 비동기로 가져오는 함수입니다.
    suspend 함수로 정의해 코루틴 환경에서
    안전하게 호출할 수 있도록 했습니다.”*/

    /**
     * [suspend 함수와 Result 타입]
     * 라이브 멘트: "비동기 처리를 위해 Coroutine의 suspend 함수를 사용하고,
     * 에러 핸들링을 함수 시그니처에서 명시적으로 드러내기 위해
     * Kotlin의 Result 타입을 반환형으로 선택했습니다."
     */

    suspend fun fetchAgreementItems(): Result<List<AgreementItem>>
}

/*
========================
예상 질문 & 모범 답변
========================

Q. 지금은 API도 없는데 Repository를 왜 만들었나요?
A.
“ViewModel에서 데이터 생성 책임을 제거하고,
데이터 출처 변경에 대비하기 위함입니다.
과제에서는 FakeRepository로 시작하고,
실서비스에서는 API/DB로 쉽게 교체할 수 있습니다.”

Q. 왜 Result 타입을 사용했나요?
A.
“성공과 실패를 명시적으로 표현하기 위해서입니다.
try-catch를 ViewModel까지 끌고 오지 않고,
결과 분기를 한 눈에 파악할 수 있습니다.”

Q. sealed class LoadState를 쓰지 않은 이유는요?
A.
“과제 범위에서는 성공/실패만 필요하다고 판단했습니다.
확장 시에는 Result를 감싸는 UiState로
Loading / Success / Error를 분리할 수 있습니다.”

Q. Repository에서 Thread 처리는 안 하나요?
A.
“Dispatcher 선택은 ViewModel 또는 UseCase에서 담당합니다.
Repository는 데이터 접근 책임만 가지도록 설계했습니다.”

Q. AgreementItem을 Repository에서 바로 반환하는 게 괜찮나요?
A.
“이번 과제에서는 서버 모델이 존재하지 않기 때문에
Presentation Model을 직접 반환했습니다.
실무에서는 DTO → Mapper → UI Model 구조로 분리합니다.”

Q. Repository가 하나뿐인데 인터페이스가 과하지 않나요?
A.
“지금은 하나지만,
테스트와 확장성을 고려하면 인터페이스 분리는 오히려 비용이 낮습니다.”

Q1. 굳이 인터페이스를 만드는 이유가 무엇인가요? 바로 클래스로 만들면 안 되나요?
✅ 모범 답변: "가장 큰 이유는 **'의존성 역전 원칙(DIP)'**을 준수하고 **'테스트 용이성'**을 확보하기 위해서입니다.
 인터페이스를 두면 라이브 코딩 테스트처럼 실제 API가 없는 상황에서 가짜 데이터를 주는 MockRepository를 쉽게 주입할 수 있고,
 나중에 실제 서버 통신 코드로 교체할 때도 ViewModel의 수정 없이 구현체만 갈아 끼우면 되기 때문입니다."


Q2. Result<List<AgreementItem>> 대신 그냥 List<AgreementItem>을 반환하고 예외(Exception)를 던지면 안 되나요?
✅ 모범 답변: "예외를 던지는 방식은 호출하는 쪽에서 try-catch를 강제하지 않아 런타임 에러가 발생할 위험이 있습니다.
 반면 Result를 사용하면 함수 사용자에게 성공과 실패 케이스 처리를 강제할 수 있어
 훨씬 더 견고하고 함수형 프로그래밍 스타일의 코드를 작성할 수 있습니다."

Q3. 왜 RxJava가 아닌 Coroutine(suspend)을 선택하셨나요?
✅ 모범 답변: "Coroutine은 안드로이드 공식 권장 라이브러리이며, 비동기 코드를 마치 동기 코드처럼 가독성 있게
작성할 수 있게 해줍니다. 또한 RxJava에 비해 러닝 커브가
낮고 리소스 소모가 적어 최신 안드로이드 개발 환경에 더 적합하다고 판단했습니다."

1. runCatching의 활용 (구현체 작성 시)
"Repository 구현체에서는 runCatching { ... } 블록을 사용하여 내부에서 발생하는
예외를 Result.failure로 안전하게 캡슐화하겠습니다. 이는 예측 불가능한 네트워크 오류 등을 처리하는 가장 깔끔한 방식입니다."

2. 데이터 매핑 로직의 위치
"만약 서버에서 내려주는 DTO(Data Transfer Object)가 UI 모델과 다르다면,
이 Repository 계층에서 매핑(Mapping) 작업을 수행하여
ViewModel에게는 오직 '순수한 비즈니스 모델'인 AgreementItem만 전달하도록 설계할 것입니다."
*/


