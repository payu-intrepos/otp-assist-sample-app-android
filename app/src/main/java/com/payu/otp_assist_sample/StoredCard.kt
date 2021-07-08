package com.payu.otp_assist_sample

data class StoredCard(var nameOnCard: String?) {
     var cardName: String? = null
     var expiryYear: String? = null
     var expiryMonth: String? = null
     var cardType: String? = null
     var cardToken: String? = null
     var isExpired: Boolean? = null
     var cardMode: String? = null
     var maskedCardNumber: String? = null
     var cardBrand: String? = null
     var cardBin: String? = null
     var isDomestic: String? = null
     var cvv: String? = null
     var issuingBank: String? = null
     var duplicateCardCount = 0
}