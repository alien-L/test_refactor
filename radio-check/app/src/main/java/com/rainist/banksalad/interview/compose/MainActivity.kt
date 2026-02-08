package com.rainist.banksalad.interview.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.AgreementRoute


// UI 진입점과 생명주기 관리 역할만 맡는 게 핵심
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* “Compose 기반 앱이라 XML을 사용하지 않고
        setContent 안에서 UI 진입점을 구성했습니다.” */

        setContent {
            AgreementRoute() //Route는 ViewModel과 상태 연결 , Screen은 순수 UI만 담당
        }
        //책임 분리가 중요 , 단위 나눔 , 화면 단위로 상태를 관리
        /*"여기서 Screen을 바로 부르지 않고 Route를 부르는 이유는
          프레임워크 의존성(ViewModel 등)과 순수 UI를 분리하기 위해서입니다."  */
    }
}

/*
*"Activity에 왜 로직이 하나도 없나요?"
* "Activity는 단순한 UI 컨테이너 역할만 수행해야 합니다. 비즈니스 로직은 ViewModel에,
* UI 로직은 Composable에 집중시켜 관심사를 분리하고 유지보수성을 높이기 위함입니다."
"화면 회전 시 데이터는 어떻게 보호되나요?"
* "MainActivity는 다시 생성되지만, AgreementRoute에서 사용하는
* ViewModel이 데이터를 유지하므로 상태는 안전하게 보존됩니다."
"왜 여기서 직접 Screen을 안 부르고 Route를 거치나요?"
* "상태 호이스팅(State Hoisting) 때문입니다. Route가 ViewModel 주입과
* Side Effect(토스트 등) 처리를 담당하게 함으로써,
* 실제 UI인 AgreementScreen을 어떤 의존성도 없는 Stateless한 컴포저블로 만들기 위해서입니다."
*
*   */