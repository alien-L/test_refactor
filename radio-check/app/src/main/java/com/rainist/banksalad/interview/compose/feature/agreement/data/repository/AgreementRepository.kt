package com.rainist.banksalad.interview.compose.feature.agreement.data.repository

import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem

interface AgreementRepository {

    suspend fun fetchAgreementItems() : Result<List<AgreementItem>>
}