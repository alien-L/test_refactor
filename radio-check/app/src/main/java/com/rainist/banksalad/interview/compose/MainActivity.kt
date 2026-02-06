package com.rainist.banksalad.interview.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rainist.banksalad.interview.data.remote.FakeTermsApi
import com.rainist.banksalad.interview.data.repository.TermsRepositoryImpl
import com.rainist.banksalad.interview.testui.terms.TermsScreen
import com.rainist.banksalad.interview.testui.terms.TermsViewModel


/**
 * 유선 면접 테스트의 구현을 진행할 기본 액티비티 입니다. (컴포즈 세팅)
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 필요한 객체들을 순서대로 생성 (의존성 주입의 원리)
        val api = FakeTermsApi()
        val repository = TermsRepositoryImpl(api)

        // 2. ViewModel 생성 시 Repository 전달
        // (실제론 ViewModelFactory를 써야 하지만, 라이브 코테용 간단 버전)
        val viewModel = TermsViewModel(repository)
        // TermsScreen(viewModel)
        setContent {
            TermsScreen(viewModel)
        }
    }
}