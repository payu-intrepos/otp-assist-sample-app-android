package com.payu.otp_assist_sample

interface CardSelected {
    fun onCardSelected(storedCard: StoredCard?)
    fun onCVVEntered(cvv: String)
}