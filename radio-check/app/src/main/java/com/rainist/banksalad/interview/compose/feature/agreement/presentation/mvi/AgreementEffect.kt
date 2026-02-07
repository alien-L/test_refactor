package com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi

sealed  interface AgreementEffect {
    object  Error : AgreementEffect
    data class ShowToastMessage(val message: String) : AgreementEffect
}

//ShowError