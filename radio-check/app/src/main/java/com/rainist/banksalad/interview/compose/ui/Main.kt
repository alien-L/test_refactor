package com.rainist.banksalad.interview.compose.ui

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.rainist.banksalad.interview.compose.feture.agreement.AgreementScreen
import com.rainist.banksalad.interview.compose.feture.agreement.AgreementViewModel
import com.rainist.banksalad.interview.compose.feture.data.AgreementRepositoryImpl

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun Main() {
    val repo = AgreementRepositoryImpl()
    val viewModel = AgreementViewModel(repo)
    AgreementScreen(viewModel)
}
