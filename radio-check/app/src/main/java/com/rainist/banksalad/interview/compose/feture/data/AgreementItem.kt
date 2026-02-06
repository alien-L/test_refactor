package com.rainist.banksalad.interview.compose.feture.data

data class AgreementItem(
    val id : Int, // 각 항목 (라디오 버튼 , 선택박스) 에 대한 구분값
    val title : String, // 제목
    val isChecked : Boolean,  // 체크유무 확인
    val isRequired: Boolean // 필수적으로 체크를 해야되나? 필수 동의 / 선택적 동의 선택 박스를 위한 값
)