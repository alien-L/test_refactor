package com.rainist.banksalad.interview.compose.feature.agreement.data.repository

import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem
import kotlinx.coroutines.delay

/*
라이브 멘트 시나리오

“AgreementRepositoryImpl은 Repository 인터페이스의 실제 구현체입니다.”

“이번 과제에서는 네트워크 API 대신
Fake 데이터 소스를 사용해 전체 아키텍처 흐름을 보여주는 데 집중했습니다.”

* 라이브 멘트: "앞서 정의한 인터페이스를 구현하는 구현체 클래스입니다.
* 현재는 실제 API가 없으므로 Mock 데이터를 반환하도록 작성하겠습니다."

“delay(500)을 둔 이유는
비동기 환경을 가정한 로딩 상태 테스트를 위함입니다.”
*/
class AgreementRepositoryImpl : AgreementRepository {

    override suspend fun fetchAgreementItems(): Result<List<AgreementItem>> = runCatching {
        /**
         * [딜레이와 네트워크 시뮬레이션]
         * 라이브 멘트: "실제 네트워크 통신 환경을 시뮬레이션하기 위해 delay(500)을 추가했습니다.
         * 이를 통해 UI에서 로딩 프로그레스 바가 정상적으로 동작하는지 확인할 수 있습니다."
         */
        /*
          “실제 네트워크 호출을 흉내 내기 위해
          인위적으로 지연 시간을 추가했습니다.
          이를 통해 Loading UI와 상태 전환을 검증할 수 있습니다.”
          */
        delay(500)
        /*
            “필수 약관과 선택 약관을 구분하기 위해
            isRequired 플래그를 함께 내려줍니다.
            이 값은 이후 ‘필수 약관만 동의’ 로직의 기준이 됩니다.”
            */
        listOf(
            AgreementItem(id = 1, title = "약관1", isChecked = false, isRequired = true),
            AgreementItem(id = 2, title = "약관2", isChecked = false, isRequired = true),
            AgreementItem(id = 3, title = "약관3", isChecked = false, isRequired = false),
        )
    }

}

/*
========================
예상 질문 & 모범 답변
========================

Q. 왜 runCatching을 사용했나요?
A.
“예외를 try-catch로 흩뿌리지 않고,
성공과 실패를 Result 타입으로 명확히 전달하기 위함입니다.
ViewModel에서 onSuccess / onFailure로 깔끔하게 분기할 수 있습니다.”

Q. delay는 Repository에서 써도 되나요?
A.
“실서비스에서는 네트워크 호출 자체가 지연을 포함하므로
명시적인 delay는 필요 없습니다.
이번 과제에서는 비동기 흐름과 로딩 UI 검증을 위해 사용했습니다.”

Q. 이 구현은 테스트하기 쉬운가요?
A.
“Repository가 인터페이스로 분리되어 있기 때문에
테스트에서는 FakeRepository나 Mock 구현으로 쉽게 대체할 수 있습니다.”

Q. Dispatcher는 왜 지정하지 않았나요?
A.
“Dispatcher 선택은 상위 계층(ViewModel / UseCase)의 책임이라고 생각합니다.
Repository는 데이터 접근 로직에만 집중하도록 설계했습니다.”

Q. 실제 API가 생기면 구조가 어떻게 바뀌나요?
A.
“AgreementApi → DTO → Mapper → AgreementItem 구조로 확장하고,
RepositoryImpl 내부 구현만 교체하면 됩니다.
ViewModel과 UI 코드는 그대로 유지됩니다.”

Q. isChecked 값을 서버에서 내려주는 게 맞나요?
A.
“실무에서는 서버 상태와 로컬 상태를 분리합니다.
이번 과제에서는 UI 흐름 단순화를 위해
초기값만 내려주고 이후 상태는 ViewModel에서 관리합니다.”

Q1. runCatching 블록을 사용했을 때의 장단점은 무엇인가요?
✅ 모범 답변: "장점은 블록 내에서 발생하는 모든 예외(Exception)를 잡아 Result.failure로 반환해준다는 점입니다.
덕분에 호출부(ViewModel)에서 try-catch 없이 깔끔하게 에러 처리를 할 수 있습니다.
 단, CancellationException까지 잡아버릴 수 있다는 단점이 있는데,
 코루틴의 취소 메커니즘을 방해하지 않으려면 실제 서비스에서는 runCatching 내부에서
 예외 타입을 체크하거나 코루틴 전용 예외 처리 방식을 병행하는 것이 좋습니다."

Q2. delay(500)을 넣은 특별한 이유가 있나요?
✅ 모범 답변: "단순히 데이터를 빨리 보여주는 것보다, 사용자 경험(UX) 관점에서 로딩 상태가
매끄럽게 전환되는지 검증하기 위해서입니다. 데이터가 즉시 나오면 로딩 화면이 깜빡거리며 사라질 수 있는데,
적절한 딜레이를 통해 LoadingBox에서 AgreementContent로 넘어가는 흐름이 자연스러운지 확인하고 싶었습니다."

Q3. 만약 서버 에러 상황을 테스트하고 싶다면 어떻게 하나요?
✅ 모범 답변: "runCatching 블록 내부 상단에 throw Exception("Network Error")를 한 줄 추가하기만 하면 됩니다.
그러면 즉시 Result.failure가 반환되어 ViewModel의 onFailure 로직과 UI의 에러 토스트(Effect)가
잘 동작하는지 바로 테스트할 수 있습니다."

1. Dispatcher 분리 언급
"현재는 간단한 리스트 반환이라 생략했지만, 실제 네트워크 통신이나 DB 작업이 포함된다면
withContext(Dispatchers.IO)를 사용하여 작업 스레드를 분리함으로써 메인 스레드 차단(Blocking)을 방지했을 것입니다."

2. DTO와 UI Model의 분리
"지금은 AgreementItem을 직접 반환하지만, 실제로는 서버 응답용 AgreementResponse 같은 DTO를 먼저 받고,
이를 도메인 모델이나 UI 모델로 변환(Mapping)하는 과정을 Repository에서 수행하는 것이 계층 간
독립성을 지키는 좋은 설계입니다."

3. 테스트 코드의 편의성
"이렇게 인터페이스와 구현체를 분리해 두었기 때문에, 추후 테스트 코드 작성 시 delay를
 없앤 TestRepository를 만들어 빠르고 안정적인 유닛 테스트를 수행할 수 있습니다."


*/
