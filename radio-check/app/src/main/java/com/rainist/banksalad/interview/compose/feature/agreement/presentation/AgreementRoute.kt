package com.rainist.banksalad.interview.compose.feature.agreement.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rainist.banksalad.interview.compose.feature.agreement.data.repository.AgreementRepositoryImpl
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementEffect

@Composable
@Preview
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun AgreementRoute(
    // - 테스트 / Hilt 주입 / Preview 대응이 쉬워짐
    // - Route가 "상태 연결 책임"을 가지도록 하기 위함

    // 멘트: "ViewModel을 외부에서 주입받을 수 있도록 파라미터로 노출했습니다.
    // 현재는 테스트 환경이므로 직접 Repository를 주입하는 팩토리를 사용하지만,
    // 실무라면 Hilt의 hiltViewModel()을 사용하여 의존성을 관리할 것입니다."
    viewModel: AgreementViewModel =
        viewModel { AgreementViewModel(AgreementRepositoryImpl()) },
) {

    /*
     * [라이브 멘트]
     * "Route는 화면의 진입점 역할을 하며,
     *  ViewModel과 UI를 연결하는 책임만 가집니다."
     *
     * "Screen은 순수 UI, Route는 상태와 사이드 이펙트를 담당하도록
     *  역할을 분리했습니다."
     */

    // 멘트: "ViewModel의 상태(State)를 관찰(Observe)하여 UI에 반영하겠습니다.
    // State가 변경될 때마다 하위 컴포저블이 효율적으로 재구성(Recomposition)됩니다."
    val state by viewModel.agreementState.collectAsState()

    //"현재는 collectAsState()를 사용했지만,
    // 실제 운영 환경이라면 안드로이드 생명주기에 더 안전한
    // collectAsStateWithLifecycle()을 적용하여 자원 소모를 최소화했을 것입니다.
    // 또한 에러 처리(ShowError) 발생 시 단순히 토스트를 띄우는 것 외에,
    // 스낵바나 에러 전용 UI 피드백으로 확장할 수 있도록 설계했습니다."

    val context = LocalContext.current

    /**
     * [이펙트 처리]
     * 멘트: "토스트나 에러 메시지 같은  한 번만 실행되어야 하는 이벤트,
     * '일회성 이벤트'는 LaunchedEffect 내에서 처리하겠습니다.
     * 이를 통해 화면 회전이나 재구성이 일어나더라도 동일한 메시지가 중복으로 뜨는 현상을 방지합니다."
     * "그래서 StateFlow와 SharedFlow를 분리해서 관리합니다."
     */
    LaunchedEffect(viewModel.agreementEffect) {
        viewModel.agreementEffect.collect { effect ->
            when (effect) {
                // 에러 노출 (SnackBar / Dialog로도 확장 가능)
                is AgreementEffect.ShowError -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()

                is AgreementEffect.ShowToastMessage -> Toast.makeText(
                    context,
                    effect.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // 멘트: "실제 UI를 담당하는 Screen에는 상태(State)와
    // 사용자 의도를 전달할 통로(Intent Handler)만 넘겨주어
    // Screen이 철저하게 UI에만 집중하는 Stateless한 상태를 유지하게 합니다."


    /*
     * Screen에는 상태와 이벤트 콜백만 전달
     *
     * [라이브 멘트]
     * "AgreementScreen은 ViewModel, Context를 전혀 모르는
     *  Stateless Composable입니다."
     *
     * "이렇게 하면 UI 테스트가 쉬워지고 재사용성이 좋아집니다."
     */

    AgreementScreen(
        state = state,
        onIntent = viewModel::handleIntent
    )
}

/**
 * Q1. "왜 Screen에서 직접 ViewModel을 참조하지 않고 Route를 거치나요?"
 * 답변: "Route는 ViewModel 연결과 Side Effect 처리를 담당하고,
 *  Screen은 순수 UI만 담당하게 하기 위해서입니다.
 *  이렇게 분리하면 상태 호이스팅이 명확해지고 유지보수가 쉬워집니다."
 *
 * Q2. "LaunchedEffect의 키값으로 왜 viewModel.agreementEffect를 넣었나요?"
 * 답변: "ViewModel의 Effect 스트림(Flow) 자체가 변경되지 않는 한,
 * 이 수집(Collect) 로직이 불필요하게 재시작되는 것을 방지하기 위해서입니다.
 * 이는 리소스를 효율적으로 관리하고 안정적인 이벤트 수집을 보장하기 위한 선택입니다."
 *
 * Q3. "collectAsState() 대신 collectAsStateWithLifecycle()을 쓰면 어떤 장점이 있나요?"
 * 답변: (2026년 기준 시니어급 답변) "일반 collectAsState는 앱이 백그라운드에 있어도
 * 수집을 계속할 수 있지만, collectAsStateWithLifecycle은
 * Lifecycle이 STARTED 미만으로 내려가면 수집을 중단합니다.
 * 이를 통해 불필요한 리소스를 아끼고 배터리 효율을 높일 수 있습니다."
 *
 * 팁: 실제 라이브 코딩에서 이걸 쓰면 면접관이 "최신 트렌드와 성능 최적화를 잘 아는구나"라고 생각합니다.
 *
 * Q4. "onIntent 파라미터로 viewModel::handleIntent를 넘기는 이유는 무엇인가요?"
 * 답변: "람다를 직접 생성하지 않고 **함수 참조(Function Reference)**를 전달함으로써 코드를 간결하게 유지하고,
 * UI 계층에서 비즈니스 로직에 관여하지 않고 오직 '사용자의 의도(Intent)'만 ViewModel로 전달하기 위해서입니다."
 *
 * Q. Effect를 State에 넣지 않은 이유는?
 * A.
 * "Effect는 일회성 이벤트이기 때문에 State에 포함하면
 *  화면 회전이나 재구성 시 중복 실행될 위험이 있습니다.
 *  그래서 SharedFlow로 분리했습니다."
 *
 *  Q. LaunchedEffect를 사용하는 이유는?
 * A.
 * "Composable 생명주기에 맞춰 코루틴을 안전하게 관리하기 위함입니다.
 *  화면이 사라지면 자동으로 취소되어 메모리 누수를 방지합니다."
 *
 *  Q. Toast를 ViewModel에서 처리하면 안 되나요?
 * A.
 * "ViewModel은 UI 프레임워크에 의존하면 안 되기 때문에
 *  어떤 이벤트가 발생해야 하는지만 Effect로 전달하고,
 *  실제 UI 표현은 Composable에서 처리합니다."
 *
 * */