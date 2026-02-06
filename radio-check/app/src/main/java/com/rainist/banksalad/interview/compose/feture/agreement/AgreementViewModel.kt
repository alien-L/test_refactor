package com.rainist.banksalad.interview.compose.feture.agreement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rainist.banksalad.interview.compose.feture.data.AgreementItem
import com.rainist.banksalad.interview.compose.feture.data.AgreementRepository
import com.rainist.banksalad.interview.compose.feture.model.AgreementEffect
import com.rainist.banksalad.interview.compose.feture.model.AgreementIntent
import com.rainist.banksalad.interview.compose.feture.model.AgreementUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AgreementViewModel(private val repo: AgreementRepository) : ViewModel() {

    private val _agreementState = MutableStateFlow(AgreementUiState())

    val agreementState = _agreementState.asStateFlow()


    private val _agreementEffect = MutableSharedFlow<AgreementEffect>()

    val agreementEffect = _agreementEffect.asSharedFlow()

    private val historyStack = mutableListOf<List<AgreementItem>>()

    init {
        loadItem()
    }

    fun handleIntent(intent: AgreementIntent) {
        when (intent) {
            is AgreementIntent.SetAllAgree -> setAllAgree(intent.isChecked)
            is AgreementIntent.Load -> loadItem()
            is AgreementIntent.ClickTermAgreement -> clickTermAgreement(intent.id)
            is AgreementIntent.SetRequiredAgree -> setRequiredAgree()
            is AgreementIntent.Play -> play()
            is AgreementIntent.Rewind -> rewind()
        }
    }


    private fun loadItem() = viewModelScope.launch {
        // todo 로딩바 필요?
        _agreementState.update { it.copy(isLoading = true) }
        repo.fetchAgreementItems().onSuccess {
            // ui 보여줌
                data ->
            _agreementState.update { it.copy(isLoading = false, terms = data) }
        }.onFailure {
            _agreementEffect.emit(AgreementEffect.Error)
            //실패에 대한 처리도 있을까?
        }
    }

   private fun setAllAgree(isChecked: Boolean) {
       saveHistory()
        _agreementState.update { s ->
            s.copy(
                terms = s.terms.map {
                    it.copy(
                        isChecked = isChecked
                    )
                }
            )
        }
    }

    private fun clickTermAgreement(id: Int) {
        saveHistory()
        _agreementState.update { s ->
            s.copy(
                terms = s.terms.map {
                    if (it.id == id) it.copy(isChecked = !it.isChecked) else it
                }
            )
        }
    }

    private fun setRequiredAgree() {
        saveHistory()
        _agreementState.update { s ->
            s.copy(
                terms = s.terms.map {it.copy(isChecked = it.isRequired)
                })

        }
    }

    private fun play (){
        // 플레이 버튼 클릭 이벤트를 위한 함수
        val message = "[PLAY] : ${_agreementState.value.terms.filter {
            it.isChecked }.map { it.id }}"
        _agreementState.update {
            it.copy(logs = it.logs + "$message")
        }
    }

    private fun  rewind(){
        // 되감기 버튼 클릭 이벤트를 위한 함수

        if (historyStack.isNotEmpty()) {
            val prev = historyStack.removeAt(historyStack.lastIndex)
            _agreementState.update {
                it.copy(terms = prev,
                    isHistoryAvailable = historyStack.isNotEmpty(),
                    logs = it.logs + "[REWIND] 복구") }
        }
    }

    private fun saveHistory(){
        historyStack.add(_agreementState.value.terms)
        _agreementState.update { it.copy(isHistoryAvailable = true) }
    }
}