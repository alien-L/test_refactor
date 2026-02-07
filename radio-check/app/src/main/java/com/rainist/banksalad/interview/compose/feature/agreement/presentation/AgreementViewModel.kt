package com.rainist.banksalad.interview.compose.feature.agreement.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rainist.banksalad.interview.compose.feature.agreement.data.model.AgreementItem
import com.rainist.banksalad.interview.compose.feature.agreement.data.repository.AgreementRepository
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementEffect
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementIntent
import com.rainist.banksalad.interview.compose.feature.agreement.presentation.mvi.AgreementUiState
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


    // todo 이해 안감
    // [체크포인트] 복사본 저장을 위해 List<AgreementItem> 스택 유지
  //  private val historyStack = mutableListOf<List<AgreementItem>>()

    private val historyStack = mutableListOf<List<AgreementItem>>()

    init {
        loadAgreements()
    }

    fun handleIntent(intent: AgreementIntent) {
        when (intent) {
            is AgreementIntent.OnSelectAll -> setAllAgree(intent.isChecked)
            is AgreementIntent.OnInitialLoad -> loadAgreements()
            is AgreementIntent.OnAgreementChecked -> clickTermAgreement(intent.id)
            is AgreementIntent.OnSelectRequiredOnly -> setRequiredAgree()
            is AgreementIntent.Play -> play()
            is AgreementIntent.Rewind -> rewind()
        }
    }


    private fun loadAgreements() = viewModelScope.launch {
        // 로딩프로그레스 바 만듬?
        _agreementState.update { it.copy(isLoading = true) }
        repo.fetchAgreementItems().onSuccess {
                data ->
            _agreementState.update { it.copy(isLoading = false, agreementItems = data) }
        }.onFailure {
            _agreementState.update { it.copy(isLoading = false) }
            _agreementEffect.emit(AgreementEffect.Error)
            //실패에 대한 처리도 있을까?
        }
    }

   private fun setAllAgree(isChecked: Boolean) {
       saveHistory()
        _agreementState.update { s ->
            s.copy(
                agreementItems = s.agreementItems.map {
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
                agreementItems = s.agreementItems.map {
                    if (it.id == id) it.copy(isChecked = !it.isChecked) else it
                }
            )
        }
    }

    private fun setRequiredAgree() {
        saveHistory()
        _agreementState.update { s ->
            s.copy(
                agreementItems = s.agreementItems.map {it.copy(isChecked = it.isRequired)
                })

        }
    }

    private fun play (){
        // 플레이 버튼 클릭 이벤트를 위한 함수
        val message = "[PLAY] : ${_agreementState.value.agreementItems.filter {
            it.isChecked }.map { it.id }}"
        _agreementState.update {
            it.copy(logs = it.logs + "$message")
        }
        viewModelScope.launch { _agreementEffect.emit(AgreementEffect.ShowToastMessage("TEST"))}
    }

    private fun  rewind(){
        // 되감기 버튼 클릭 이벤트를 위한 함수

        if (historyStack.isNotEmpty()) {
            val prev = historyStack.removeAt(historyStack.lastIndex)
            _agreementState.update {
                it.copy(agreementItems = prev,
                    isHistoryAvailable = historyStack.isNotEmpty(),
                    logs = it.logs + "[REWIND] : ${_agreementState.value.agreementItems.filter {
                        it.isChecked }.map { it.id }}") }
        }
    }

    private fun saveHistory(){
        // todo 이해 안감
        // [중요] .toList()를 붙여서 현재 리스트의 스냅샷(복사본)을 저장해야 함
      //  historyStack.add(_agreementState.value.agreementItems.toList())
        historyStack.add(_agreementState.value.agreementItems)
        _agreementState.update { it.copy(isHistoryAvailable = true) }
    }
}