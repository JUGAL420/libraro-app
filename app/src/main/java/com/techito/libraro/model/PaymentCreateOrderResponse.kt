package com.techito.libraro.model
import com.google.gson.annotations.SerializedName
data class PaymentCreateOrderResponse(
    @SerializedName("data")
    var `data`: PaymentCreateOrderData?,
    @SerializedName("message")
    var message: String?,
    @SerializedName("status")
    var status: Boolean?
)

data class PaymentCreateOrderData(
    @SerializedName("is_paid")
    var isPaid: Boolean?,
    @SerializedName("amount")
    var amount: String?,
    @SerializedName("currency")
    var currency: String?,
    @SerializedName("key_id")
    var keyId: String?,
    @SerializedName("order_id")
    var orderId: String?,
    @SerializedName("transaction_id")
    var transactionId: String?
)


