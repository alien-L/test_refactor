package com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi

import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem

data class AgreementUiState(
    val isLoading: Boolean = false,
    val agreementItems: List<AgreementItem> = emptyList(),
    val logs: List<String> = emptyList(), // 로그뷰
    val isHistoryAvailable: Boolean = false
) {
    // [전체 동의] 모든 항목이 체크됨
    val isAllAgree get() = agreementItems.isNotEmpty() && agreementItems.all { it.isChecked }

    // [일부 동의] 필수 항목만 체크됨 (유저님이 정의하신 로직)
    val isPartiallyAgreed get() = agreementItems.isNotEmpty() &&
            agreementItems.all { if (it.isRequired) it.isChecked else !it.isChecked }

    // [미동의] 모든 항목이 체크 해제됨 (이게 필요합니다!)
    val isNoneAgreed get() = agreementItems.isNotEmpty() && agreementItems.all { !it.isChecked }

    // 이것도 state 도 관리하고 싶긴해
}
