package com.rainist.banksalad.interview.compose.feture.data

import kotlinx.coroutines.delay

class AgreementRepositoryImpl : AgreementRepository {

    override suspend fun fetchAgreementItems(): Result<List<AgreementItem>> = runCatching {
        delay(2000)
        listOf(
            AgreementItem(id = 1, title = "약관1", isChecked = false, isRequired = true),
            AgreementItem(id = 2, title = "약관2", isChecked = false, isRequired = true),
            AgreementItem(id = 3, title = "약관3", isChecked = false, isRequired = false),
        )
    }

}