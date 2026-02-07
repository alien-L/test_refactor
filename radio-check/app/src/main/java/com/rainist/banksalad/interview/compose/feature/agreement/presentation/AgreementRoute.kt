package com.rainist.banksalad.interview.compose.feature.agreement.presentation

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rainist.banksalad.interview.compose.feature.agreement.data.repository.AgreementRepositoryImpl
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementEffect

@Composable
@Preview
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun AgreementRoute(
    // 1. ViewModel을 파라미터로 넘기면 외부(Hilt 등)에서 주입받기 훨씬 쉬워집니다.
    viewModel: AgreementViewModel = viewModel { AgreementViewModel(AgreementRepositoryImpl()) }
) {

    val state by viewModel.agreementState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(viewModel.agreementEffect) {
        viewModel.agreementEffect.collect { effect ->
            when (effect) {
                AgreementEffect.Error -> TODO()
                is AgreementEffect.ShowToastMessage -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    // AgreementScreen에서 사용할 effect에 대한 처리 , 로그뷰에 대한 effect를 넣을까?

    AgreementScreen(
        state = state,
        onIntent = viewModel::handleIntent
    )
}