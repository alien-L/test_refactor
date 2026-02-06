package com.rainist.banksalad.interview.compose.feture.agreement

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rainist.banksalad.interview.compose.feture.model.AgreementIntent
import com.rainist.banksalad.interview.compose.ui.component.CheckboxWithText
import com.rainist.banksalad.interview.compose.ui.component.RadioButtonWithText


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AgreementScreen (viewModel: AgreementViewModel){
   val state by viewModel.agreementState.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title =  {
                    Text( text =  "테스트 화면")
                }
            )
        }
    ){ paddingValues ->

        if(state.isLoading){
            Box (Modifier
                .fillMaxWidth()
                .fillMaxHeight() , contentAlignment = Alignment.Center){
                CircularProgressIndicator()
            }

        }else{
            Column(modifier = Modifier.padding(paddingValues)) {
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween){
                    Column(modifier = Modifier.selectableGroup()) {
                        RadioButtonWithText("모든 약관 동의",state.isAllAgree , {
                            viewModel.handleIntent(intent = AgreementIntent.SetAllAgree(true))
                        })
                        RadioButtonWithText("일부 약관 동의",state.isPartiallyAgreed , {
                                    viewModel.handleIntent(intent = AgreementIntent.SetRequiredAgree)
                        })
                        RadioButtonWithText("모든 약관 미동의",state.terms.none { it.isChecked }, {
                            viewModel.handleIntent(intent = AgreementIntent.SetAllAgree(false))
                        })
                    }
                    Column() {
                        state.terms.forEach { term ->

                            CheckboxWithText(
                                text = term.title, term.isChecked,
                                onCheckedChange = {
                                    viewModel.handleIntent(AgreementIntent.ClickTermAgreement(term.id))
                                })
                        }
                    }
                }

                Row(  modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceEvenly){
                    Button({
                        viewModel.handleIntent(AgreementIntent.Play)
                    }, Modifier.padding(5.dp)) {
                        Text("PLAY")
                    }
                    Button({
                        viewModel.handleIntent(AgreementIntent.Rewind)
                    },Modifier.padding(5.dp)) {
                        Text("REWIND")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Box (modifier = Modifier.fillMaxWidth()){
                    Text(text = state.logs.joinToString("\n"), modifier = Modifier
                        .padding(5.dp)
                        .verticalScroll(rememberScrollState()))

                }

            }
        }

    }

}