package com.rainist.banksalad.interview.compose.feture.model

sealed interface AgreementIntent {
    object  Load : AgreementIntent
    data class  SetAllAgree(val isChecked : Boolean) :  AgreementIntent
    object  SetRequiredAgree : AgreementIntent
    data class  ClickTermAgreement(val id : Int) : AgreementIntent

    object  Play : AgreementIntent

    object  Rewind : AgreementIntent
}