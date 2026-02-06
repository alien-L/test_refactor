package com.rainist.banksalad.interview.testui.terms

import com.rainist.banksalad.interview.domain.model.TermItem


// 1. 상태: UI가 그릴 모든 데이터
data class TermsState(
    val isLoading: Boolean = false,
    val terms: List<TermItem> = emptyList(),
    val logs: List<String> = emptyList(),
    val isHistoryAvailable: Boolean = false
) {
    val isAllAgreed get() = terms.isNotEmpty() && terms.all { it.isChecked }

    // 필수 약관만 모두 동의했는지 여부 (일부 동의 라디오 버튼용)
    val isMandatoryOnlyAgreed get() =
        terms.filter { it.isRequired }.all { it.isChecked } && !isAllAgreed

    // PLAY 버튼 활성화 조건: 필수(isRequired)가 모두 체크(isChecked)되었는가?
    val isPlayEnabled get() = terms.filter { it.isRequired }.all { it.isChecked }
}

// 2. 의도: 사용자가 할 수 있는 모든 액션
sealed class TermsIntent {
    object LoadTerms : TermsIntent()
    data class ToggleTerm(val id: Int) : TermsIntent()
    data class SetAll(val checked: Boolean) : TermsIntent()
    object Play : TermsIntent()
    object Rewind : TermsIntent()

 //   object Reset : TermsIntent() // 모든 데이터 초기화 의도

    object SetMandatoryOnly : TermsIntent() // 일부 동의 클릭 시 필수만 체크
}
//
//sealed interface TermsIntent {
//    data object LoadTerms : TermsIntent
//    data class ToggleTerm(val id: Int) : TermsIntent
//    data class SetAll(val checked: Boolean) : TermsIntent
//    data object Play : TermsIntent
//    data object Rewind : TermsIntent
//    data object SetMandatoryOnly : TermsIntent
//}



// 3. 효과: 토스트, 스크롤 등 일회성 이벤트
sealed class TermsEffect {
    data class Error(val message: String) : TermsEffect()
    object ScrollToBottom : TermsEffect()
}