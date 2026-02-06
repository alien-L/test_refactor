package com.rainist.banksalad.interview.data.remote

import kotlinx.coroutines.delay

// 라이브 코테용 Mock API (서버가 없으므로 인터페이스를 구현한 가짜 객체를 만듭니다)
class FakeTermsApi : TermsApi {
    override suspend fun getTerms(): List<TermDto> {
        delay(800) // 네트워크 지연 시뮬레이션
        return listOf(
            TermDto(1, "서비스 이용약관(필수)", true),
            TermDto(2, "개인정보 처리방침(필수)", true),
            TermDto(3, "마케팅 정보 수신(선택)", false)
        )
    }
}