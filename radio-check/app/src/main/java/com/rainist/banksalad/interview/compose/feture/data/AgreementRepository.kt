package com.rainist.banksalad.interview.compose.feture.data

interface AgreementRepository {

    suspend fun fetchAgreementItems() : Result<List<AgreementItem>>
}