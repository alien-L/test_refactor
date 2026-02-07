package com.rainist.banksalad.interview.test.data.domain.model

data class TermItem(
    val id: Int,
    val title: String,
    val isChecked: Boolean = false,
    val isRequired: Boolean = false
)