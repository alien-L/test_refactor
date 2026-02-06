package com.rainist.banksalad.interview.compose.feture.model

sealed  interface AgreementEffect {
    object  Error : AgreementEffect
    object  ShowToastMessage : AgreementEffect
    object  ShowErrorPopup  : AgreementEffect
}