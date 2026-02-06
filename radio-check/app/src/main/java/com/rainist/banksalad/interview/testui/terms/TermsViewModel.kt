package com.rainist.banksalad.interview.testui.terms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rainist.banksalad.interview.domain.model.TermItem
import com.rainist.banksalad.interview.domain.repository.TermsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TermsViewModel(
    private val repository: TermsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TermsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TermsEffect>()
    val effect = _effect.asSharedFlow()

    private val historyStack = mutableListOf<List<TermItem>>()

    init { handleIntent(TermsIntent.LoadTerms) }

    fun handleIntent(intent: TermsIntent) {
        when (intent) {
            is TermsIntent.LoadTerms -> loadTerms()
            is TermsIntent.ToggleTerm -> toggleTerm(intent.id)
            is TermsIntent.SetAll -> setAll(intent.checked)
            is TermsIntent.Play -> play()
            is TermsIntent.Rewind -> rewind()
            is TermsIntent.SetMandatoryOnly -> setMandatoryOnly()
        }
    }

    private fun loadTerms() = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        repository.fetchTerms()
            .onSuccess { data -> _state.update { it.copy(isLoading = false, terms = data) } }
            .onFailure { _effect.emit(TermsEffect.Error("데이터 로드 실패")) }
    }

    private fun toggleTerm(id: Int) {
        saveHistory()
        _state.update { s ->
            s.copy(terms = s.terms.map { if (it.id == id) it.copy(isChecked = !it.isChecked) else it })
        }
    }

    private fun setAll(checked: Boolean) {
        saveHistory()
        _state.update { s -> s.copy(terms = s.terms.map { it.copy(isChecked = checked) }) }
    }

    private fun play() {
        val message = "현재 동의: ${_state.value.terms.filter { it.isChecked }.map { it.id }}"
        _state.update { it.copy(logs = it.logs + "[PLAY] $message") }
        viewModelScope.launch { _effect.emit(TermsEffect.ScrollToBottom) }
    }

    private fun rewind() {
        if (historyStack.isNotEmpty()) {
            val prev = historyStack.removeAt(historyStack.lastIndex)
            _state.update { it.copy(terms = prev, isHistoryAvailable = historyStack.isNotEmpty(), logs = it.logs + "[REWIND] 복구") }
        }
    }

    private fun saveHistory() {
        historyStack.add(_state.value.terms)
        _state.update { it.copy(isHistoryAvailable = true) }
    }

    private fun setMandatoryOnly() {
        saveHistory()
        _state.update { currentState ->
            val newTerms = currentState.terms.map {
                it.copy(isChecked = it.isRequired) // 필수면 true, 선택이면 false
            }
            currentState.copy(terms = newTerms)
        }
    }
}