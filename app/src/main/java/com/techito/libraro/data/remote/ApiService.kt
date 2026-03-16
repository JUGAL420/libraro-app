package com.techito.libraro.data.remote

import com.techito.libraro.model.AppSettingResponse
import com.techito.libraro.model.BasicResponse
import com.techito.libraro.model.LoginResponse
import com.techito.libraro.model.OtpVerificationResponse
import com.techito.libraro.model.PaymentCreateOrderResponse
import com.techito.libraro.model.RegisterResponse
import com.techito.libraro.model.SubscriptionPlanResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @GET(ApiConstants.SUB_URL + ApiConstants.LIBRARY_APP_SETTINGS)
    suspend fun getAppSettings(): Response<AppSettingResponse>

    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_REGISTER)
    suspend fun adminRegister(
        @Field("library_name") libraryName: String,
        @Field("email") email: String,
        @Field("library_mobile") libraryMobile: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_VERIFY_EMAIL)
    suspend fun verifyOtp(
        @Field("library_id") libraryId: String,
        @Field("otp") otp: String
    ): Response<OtpVerificationResponse>

    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_RESEND_OTP)
    suspend fun resendOtp(
        @Field("library_id") libraryId: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_LOGIN)
    suspend fun adminLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_FORGOT_PASSWORD)
    suspend fun forgotPassword(
        @Field("email") email: String
    ): Response<BasicResponse>

    @GET(ApiConstants.SUB_URL + ApiConstants.LIBRARY_SUBSCRIPTION_PLAN)
    suspend fun getSubscriptionPlans(): Response<SubscriptionPlanResponse>

    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_CREATE_PAYMENT_ORDER)
    suspend fun createPaymentOrder(
        @Field("plan_mode") planTypeId: String,
        @Field("subscription_id") planModeId: String
    ): Response<PaymentCreateOrderResponse>


    @FormUrlEncoded
    @POST(ApiConstants.SUB_URL + ApiConstants.LIBRARY_PAYMENT_VERIFY)
    suspend fun verifyPayment(
        @Field("transaction_id") transactionId: String,
        @Field("razorpay_payment_id") razorpayPaymentId: String,
        @Field("razorpay_order_id") razorpayOrderId: String,
        @Field("razorpay_signature") razorpaySignature: String,
        @Field("payment_status") paymentStatus: String,
        @Field("payment_response") paymentResponse: String
    ): Response<BasicResponse>

}