package com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi

sealed interface AgreementIntent {

    // 1. 데이터 로딩
    object  OnInitialLoad : AgreementIntent

    // 2. 전체 동의/해제 (파라미터가 있으므로 data class)
    data class  OnSelectAll(val isChecked : Boolean) :  AgreementIntent

    // 3. 필수 항목만 동의
    object  OnSelectRequiredOnly : AgreementIntent

    // 4. 개별 항목 토글 (id 전달)
    data class  OnAgreementChecked(val id : Int) : AgreementIntent

    // 5. 플레이 (로그 기록 및 확정)
    object  Play : AgreementIntent //로그뷰이 입력

    // 6. 되감기 (히스토리 복구)
    object  Rewind : AgreementIntent // 로그뷰 상태 되돌리기
}