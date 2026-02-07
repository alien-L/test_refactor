package com.rainist.banksalad.interview.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.AgreementRoute


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgreementRoute()
        }
    }
}