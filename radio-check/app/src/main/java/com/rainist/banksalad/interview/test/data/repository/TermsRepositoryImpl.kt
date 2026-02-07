package com.rainist.banksalad.interview.test.data.repository

import com.rainist.banksalad.interview.test.data.remote.TermDto
import com.rainist.banksalad.interview.test.data.remote.TermsApi
import com.rainist.banksalad.interview.test.data.domain.model.TermItem
import com.rainist.banksalad.interview.test.data.domain.repository.TermsRepository
import kotlinx.coroutines.delay

class TermsRepositoryImpl(
    private val api: TermsApi // 실무에선 주입받음
) : TermsRepository {

    override suspend fun fetchTerms(): Result<List<TermItem>> = runCatching {
        // 실제 API가 없으므로 1초 대기 후 Mock 데이터 반환 (라이브 코테 필살기)
        delay(1000)
        listOf(
            TermDto(1, "서비스 이용약관(필수)", true),
            TermDto(2, "개인정보 처리방침(필수)", true),
            TermDto(3, "마케팅 수신 동의(선택)", false)
        ).map { it.toDomain() }
    }
}