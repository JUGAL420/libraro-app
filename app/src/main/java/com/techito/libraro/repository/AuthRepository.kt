package com.techito.libraro.repository

import com.techito.libraro.data.remote.ApiService
import com.techito.libraro.model.BasicResponse
import com.techito.libraro.model.BranchConfigurationRequest
import com.techito.libraro.model.PaymentCreateOrderResponse
import com.techito.libraro.utils.NetworkResult

class AuthRepository(private val apiService: ApiService) : BaseRepository() {

    suspend fun createPaymentOrder(
        planTypeId: String,
        planModeId: String
    ): NetworkResult<PaymentCreateOrderResponse> {
        return safeApiCall {
            apiService.createPaymentOrder(planTypeId, planModeId)
        }
    }

    suspend fun verifyPayment(
        transactionId: String,
        razorpayPaymentId: String,
        razorpayOrderId: String,
        razorpaySignature: String,
        paymentStatus: String,
        paymentResponse: String
    ): NetworkResult<BasicResponse> {
        return safeApiCall {
            apiService.verifyPayment(
                transactionId,
                razorpayPaymentId,
                razorpayOrderId,
                razorpaySignature,
                paymentStatus,
                paymentResponse
            )
        }
    }

    suspend fun saveBranchConfiguration(request: BranchConfigurationRequest): NetworkResult<BasicResponse> {
        return safeApiCall {
            apiService.saveBranchConfiguration(request)
        }
    }
}