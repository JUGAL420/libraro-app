package com.techito.libraro.repository

import android.util.Log
import com.google.gson.Gson
import com.techito.libraro.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * BaseRepository provides a robust way to execute API calls and map them to NetworkResult.
 * It handles standard HTTP errors, network timeouts, and parses JSON error bodies.
 */
abstract class BaseRepository {

    /**
     * Executes an API call safely, catching exceptions and wrapping the result.
     */
    protected suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error("Something went wrong. Please try again.")
                }

            } else {
                handleErrorResponse(response)
            }

        } catch (e: SocketTimeoutException) {
            NetworkResult.Error("Request timeout. Please try again.")
        } catch (e: IOException) {
            NetworkResult.Error("No internet connection.")
        } catch (e: Exception) {
            Log.e("BaseRepository", "Unexpected error", e)
            NetworkResult.Error("Something went wrong. Please try again later.")
        }
    }

    /**
     * Helper to handle non-successful HTTP responses and parse their error bodies.
     */
    private fun <T> handleErrorResponse(response: Response<T>): NetworkResult<T> {
        // Log server error for debugging
        val errorBody = response.errorBody()?.string()
        Log.e("API_ERROR", "Code: ${response.code()} Body: $errorBody")

        return when (response.code()) {

            401, 403 -> {
                NetworkResult.Unauthorized("Session expired. Please login again.")
            }

            in 400..499 -> {
                NetworkResult.Error("Something went wrong. Please try again.")
            }

            in 500..599 -> {
                NetworkResult.Error("Server error. Please try again later.")
            }

            else -> {
                NetworkResult.Error("Something went wrong. Please try again.")
            }
        }
    }
}
