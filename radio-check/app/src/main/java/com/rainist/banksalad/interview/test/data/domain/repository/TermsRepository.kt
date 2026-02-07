package com.rainist.banksalad.interview.test.data.domain.repository

import com.rainist.banksalad.interview.test.data.domain.model.TermItem

// domain/repository/TermsRepository.kt
interface TermsRepository {
    /**
     * 약관 목록을 서버에서 가져옵니다.
     * Result 타입을 사용하여 성공/실패를 명확히 처리합니다.
     */
    suspend fun fetchTerms(): Result<List<TermItem>>
}