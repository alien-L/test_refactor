package com.rainist.banksalad.interview.compose.feture.model

import com.rainist.banksalad.interview.compose.feture.data.AgreementItem

data class AgreementUiState(
    val isLoading: Boolean = false,
    val terms: List<AgreementItem> = emptyList(),
    val logs: List<String> = emptyList(),
    val isHistoryAvailable: Boolean = false
) {

    val isAllAgree get() = terms.isNotEmpty() && terms.all { it.isChecked }

    //val isPartiallyAgreed get() = terms.isNotEmpty() && !terms.all { it.isChecked }&& terms.any{it.isChecked}
    val isPartiallyAgreed: Boolean
        get() = terms.any { it.isChecked } && !isAllAgree
    // 이것도 state 도 관리하고 싶긴해
}


//sealed interface GameCandidateListUIModel {
//
//    val index: Int
//
//    data class Shimmer(override val index: Int) :
//        io.celebe.ui.v2.gamev2.candidatelist.model.GameCandidateListUIModel
//
//    data class CandidateItem(
//        override val index: Int,
//        val candidate: Candidate,
//        val isVoted: Boolean = false,
//        val isSelected: Boolean = false
//    ) : io.celebe.ui.v2.gamev2.candidatelist.model.GameCandidateListUIModel
//}