package com.rainist.banksalad.interview.data.remote

import com.rainist.banksalad.interview.domain.model.TermItem
import retrofit2.http.GET

// 서버에서 내려주는 데이터 형식
data class TermDto(
    val termId: Int,
    val termTitle: String,
    val mandatory: Boolean
) {
    // DTO를 Domain 모델로 변환 (Mapped)
    fun toDomain() = TermItem(id = termId, title = termTitle, isRequired = mandatory)
}

// Retrofit 인터페이스
interface TermsApi {
    @GET("v1/terms")
    suspend fun getTerms(): List<TermDto>
}