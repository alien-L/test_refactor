package com.rainist.banksalad.interview.domain.model

data class TermItem(
    val id: Int,
    val title: String,
    val isChecked: Boolean = false,
    val isRequired: Boolean = false
)