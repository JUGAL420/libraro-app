package com.techito.libraro.repository

import com.techito.libraro.data.remote.ApiService
import com.techito.libraro.model.*
import com.techito.libraro.utils.NetworkResult
import retrofit2.Response

class MainRepository(private val apiService: ApiService): BaseRepository() {
    
    suspend fun getAppSettings(): Response<AppSettingResponse> {
        return apiService.getAppSettings()
    }

    suspend fun adminRegister(
        libraryName: String,
        email: String,
        libraryMobile: String,
        password: String
    ): NetworkResult<RegisterResponse> {
        return safeApiCall {
            apiService.adminRegister(libraryName, email, libraryMobile, password)
        }
    }

    suspend fun verifyOtp(
        libraryId: String,
        otp: String
    ): NetworkResult<OtpVerificationResponse> {
        return safeApiCall {
            apiService.verifyOtp(libraryId, otp)
        }
    }

    suspend fun resendOtp(
        libraryId: String
    ): NetworkResult<RegisterResponse> {
        return safeApiCall {
            apiService.resendOtp(libraryId)
        }
    }

    suspend fun adminLogin(
        email: String,
        password: String
    ): NetworkResult<LoginResponse> {
        return safeApiCall {
            apiService.adminLogin(email, password)
        }
    }

    suspend fun forgotPassword(
        email: String
    ): NetworkResult<BasicResponse> {
        return safeApiCall {
            apiService.forgotPassword(email)
        }
    }

    suspend fun getSubscriptionPlans(): NetworkResult<SubscriptionPlanResponse> {
        return safeApiCall {
            apiService.getSubscriptionPlans()
        }
    }

    suspend fun getMasterStaticDataList(): NetworkResult<StaticDataListResponse> {
        return safeApiCall {
            apiService.getMasterStaticDataList()
        }
    }

}