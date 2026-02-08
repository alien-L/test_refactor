package com.rainist.banksalad.interview.compose.feature.agreement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem
import com.rainist.banksalad.interview.compose.feature.agreement.data.repository.AgreementRepository
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementEffect
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementIntent
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/*
[라이브 코딩 멘트 - ViewModel 시작]

“AgreementViewModel은 이 화면의 모든 비즈니스 로직과 상태를 책임집니다.
UI는 상태를 그리기만 하고, 모든 판단은 ViewModel에서 수행합니다.”

“MVI 패턴을 사용해서
Intent → State 변경 → Effect 발생 흐름을 명확히 분리했습니다.”


*/
class AgreementViewModel(
    private val repo: AgreementRepository
) : ViewModel() {

    /*
    “UI의 단일 진실 공급원(Single Source of Truth)으로
    MutableStateFlow를 사용했습니다.”
    */
    private val _agreementState = MutableStateFlow(AgreementUiState())
    val agreementState = _agreementState.asStateFlow()

    /*
    “토스트, 에러 메시지 같은 일회성 이벤트는
    State가 아니라 Effect로 분리했습니다.”
    */
    private val _agreementEffect = MutableSharedFlow<AgreementEffect>()
    val agreementEffect = _agreementEffect.asSharedFlow()

    /*
    “되돌리기(Rewind) 기능을 위해
    이전 상태 스냅샷을 저장하는 히스토리 스택을 두었습니다.”
    */
    private val historyStack = mutableListOf<List<AgreementItem>>()

    init {
        /*
        “ViewModel 생성 시점에 최초 데이터를 로드합니다.
        화면 진입 시 항상 동일한 초기 상태를 보장하기 위함입니다.”
        */
        loadAgreements()
    }

    /*
    “UI에서 발생한 모든 이벤트는
    Intent 형태로 이 함수 하나로만 들어옵니다.”
    */
    fun handleIntent(intent: AgreementIntent) {
        when (intent) {
            is AgreementIntent.OnSelectAll -> setAllAgree(intent.isChecked)
            is AgreementIntent.OnInitialLoad -> loadAgreements()
            is AgreementIntent.OnAgreementChecked -> clickTermAgreement(intent.id)
            is AgreementIntent.OnSelectRequiredOnly -> setRequiredAgree()
            is AgreementIntent.Play -> play()
            is AgreementIntent.Rewind -> rewind()
        }
    }

    /*
    =========================
    데이터 로딩
    =========================
    */
    private fun loadAgreements() = viewModelScope.launch {
        /*
        “비동기 작업 시작 시 로딩 상태를 먼저 true로 변경합니다.”
        */
        _agreementState.update { it.copy(isLoading = true) }

        repo.fetchAgreementItems()
            .onSuccess { data ->
                /*
                “성공 시 데이터와 로딩 상태를 함께 갱신합니다.”
                */
                _agreementState.update {
                    it.copy(
                        isLoading = false,
                        agreementItems = data
                    )
                }
            }
            .onFailure {
                /*
                “실패 시 UI 상태와 Effect를 분리해서 처리합니다.”
                */
                _agreementState.update { it.copy(isLoading = false) }
                _agreementEffect.emit(
                    AgreementEffect.ShowError("실패 했습니다.")
                )
            }
    }

    /*
    =========================
    전체 동의 / 해제
    =========================
    */
    private fun setAllAgree(isChecked: Boolean) {
        /*
        “상태 변경 전에 반드시 히스토리를 저장합니다.”
        */
        saveHistory()

        _agreementState.update { s ->
            s.copy(
                agreementItems = s.agreementItems.map {
                    it.copy(isChecked = isChecked)
                }
            )
        }
    }

    /*
    =========================
    개별 약관 토글
    =========================
    */
    private fun clickTermAgreement(id: Int) {
        saveHistory()

        _agreementState.update { s ->
            s.copy(
                agreementItems = s.agreementItems.map {
                    if (it.id == id) {
                        it.copy(isChecked = !it.isChecked)
                    } else {
                        it
                    }
                }
            )
        }
    }

    /*
    =========================
    필수 약관만 동의
    =========================
    */
    private fun setRequiredAgree() {
        saveHistory()

        _agreementState.update { s ->
            s.copy(
                agreementItems = s.agreementItems.map {
                    it.copy(isChecked = it.isRequired)
                }
            )
        }
    }

    /*
    =========================
    PLAY 버튼
    =========================
    */
    private fun play() {
        /*
        “현재 선택된 항목을 로그로 남깁니다.
        실제 서비스라면 서버 전송이나 다음 화면 이동이 이 위치에 들어갑니다.”
        */
        val message = "[PLAY] : ${
            _agreementState.value.agreementItems
                .filter { it.isChecked }
                .map { it.id }
        }"

        _agreementState.update {
            it.copy(logs = it.logs + message)
        }

        /*
        “사용자 피드백을 위해 Effect로 토스트를 발생시킵니다.”
        */
        viewModelScope.launch {
            _agreementEffect.emit(
                AgreementEffect.ShowToastMessage("TEST")
            )
        }
    }

    /*
    =========================
    REWIND 버튼
    =========================
    */
    private fun rewind() {
        /*
        “히스토리가 존재할 때만 이전 상태로 복구합니다.”
        */
        if (historyStack.isNotEmpty()) {
            val prev = historyStack.removeAt(historyStack.lastIndex)

            _agreementState.update {
                it.copy(
                    agreementItems = prev,
                    isHistoryAvailable = historyStack.isNotEmpty(),
                    logs = it.logs + "[REWIND] : ${
                        _agreementState.value.agreementItems
                            .filter { it.isChecked }
                            .map { it.id }
                    }"
                )
            }
        }
    }

    /*
    =========================
    히스토리 저장
    =========================
    */
    private fun saveHistory() {
        /*
        [면접 핵심 포인트]
        “리스트는 참조 타입이기 때문에
        그대로 저장하면 이후 상태 변경 시 히스토리까지 오염됩니다.”

        “.toList()를 사용해 해당 시점의 스냅샷을 저장했습니다.”

        “AgreementItem은 불변 객체이므로
        map { it.copy() }까지는 필요 없다고 판단했습니다.”

        // **"왜 굳이 .toList()를 붙이셨나요?"**라고 물으면 이렇게 대답하세요.
         //"코틀린에서 리스트는 참조 타입입니다. 단순히 add()를 하면 리스트의 주소값이 저장되어,
         // 나중에 상태가 업데이트될 때 히스토리 데이터까지 오염될 수 있습니다.
         따라서 .toList()를 통해 해당 시점의 리스트 // 스냅샷을 생성하여 데이터의 독립성을 보장했습니다.
        // AgreementItem은 이미 불변 객체이므로 깊은 복사(map { it.copy() })까지는
        필요 없다고 판단하여 메모리 효율을 챙겼습니다."
        */
        val snapshot = _agreementState.value.agreementItems.toList()
        historyStack.add(snapshot)

        _agreementState.update {
            it.copy(isHistoryAvailable = true)
        }
    }
}
/*
*
* Q. 왜 StateFlow + SharedFlow를 같이 썼나요?

A.

“지속 상태는 StateFlow, 일회성 이벤트는 SharedFlow로 분리했습니다.
토스트나 네비게이션 같은 이벤트를 State로 관리하면 재구성 시 중복 실행 위험이 있기 때문입니다.”

Q. 왜 MVI를 썼나요?

A.

“상태 전환이 많은 화면이라 단일 UiState로 관리하는 MVI가 적합했습니다.
특히 Rewind 기능은 상태 스냅샷을 저장·복구하는 구조와 매우 잘 맞습니다.”

Q. historyStack을 ViewModel에 둔 이유는?

A.

“되돌리기는 UI 관심사가 아니라 상태 관리 문제이기 때문에
ViewModel에서 책임지는 것이 맞다고 판단했습니다.”

Q. saveHistory를 모든 함수에서 호출하는 이유는?

A.

“사용자 액션으로 상태가 변경되기 직전의 시점을 기준으로
항상 되돌릴 수 있도록 하기 위함입니다.”

Q. 이 구조의 개선 포인트는?

A.

“히스토리 개수 제한, 메모리 관리,
또는 sealed class 기반 LoadState 분리 정도를 추가할 수 있습니다.”
*
*
* */

/**
 * [라이브 코딩 가이드: AgreementViewModel]
 * * 1. 구현 시나리오 (입으로 내뱉을 멘트)
 * - "비즈니스 로직 담당 ViewModel입니다. MVI 아키텍처를 채택해 handleIntent로 상태 변화를 집중 관리합니다."
 * - "loadAgreements: Repository 결과에 따라 isLoading 상태를 제어하고, 실패 시 Effect로 에러를 전파합니다."
 * - "상태 변경: 불변성 유지를 위해 copy와 map을 사용하며, 변경 직전 saveHistory()를 호출해 Undo를 지원합니다."
 * - "saveHistory/rewind: .toList()를 사용하여 주소값이 아닌 스냅샷을 저장해 데이터 오염을 방지하는 것이 핵심입니다."
 */

/**
 * 2. 예상 질문 및 방어 기제 (Q&A)
 * * Q: 왜 _agreementState.update 를 쓰나요? (value = ... 대신)
 * A: "동시성(Concurrency) 문제를 방지하고 원자적(Atomic) 연산을 보장하여 최신 상태를 안전하게 반영하기 위함입니다."
 * * Q: historyStack의 메모리 문제는?
 * A: "실제 서비스라면 최대 개수 제한(LIFO)을 두거나 SavedStateHandle을 활용해 프로세스 중단에 대비했을 것입니다."
 * * Q: play()에서 viewModelScope.launch로 이펙트를 쏘는 이유는?
 * A: "SharedFlow는 구독자가 없으면 유실될 수 있고, 비동기 처리를 통해 UI 스레드 차단을 방지하며 안정성을 높이기 위해서
 * * Q: rewind() 로그에서 _agreementState.value를 다시 참조하는 이유는?
 * A: "복구 직후의 실제 반영된 ID 리스트를 로그로 기록하여 디버깅 및 추적 가능성을 극대화하기 위해서입니다."
 */

/**
 * 3. 시니어급 코드 품질 개선 포인트 (가산점 필살기)
 * * - 중복 로직 제거: "현재 map { it.copy(...) } 로직이 반복되는데, 실무라면 고차 함수를 통해 중복을 제거했을 것입니다.
 * - 데이터 무결성: "saveHistory를 상태 변경 '직전'에 호출하여 유효한 상태를 백업하는 정석적인 Undo 로직을 따랐습니다."
 * - 로그 디테일: "[PLAY], [REWIND] 태그와 상세 ID를 포함해 콘솔만으로 유저 플로우 재구성이 가능하게 설계했습니다."
 * - 참조 vs 복사: "참조 타입인 리스트의 오염을 막기 위해 .toList()로 독립적인 스냅샷을 생성했습니다."
 */

/* 실제 뷰모델 구현 시작 */
// class AgreementViewModel(private val repo: AgreementRepository) : ViewModel() { ... }
