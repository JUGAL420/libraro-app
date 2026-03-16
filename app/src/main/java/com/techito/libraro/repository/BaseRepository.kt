package com.techito.libraro.repository

import android.util.Log
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.utils.NetworkResult
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

/**
 * BaseRepository provides a robust way to execute API calls and map them to NetworkResult.
 * It handles standard HTTP errors, network timeouts, and parses JSON error bodies.
 */
abstract class BaseRepository {

    /**
     * Helper function to get string resources from the Application context.
     */
    private fun getString(resId: Int): String {
        return LibraroApp.instance.getString(resId)
    }

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
                    NetworkResult.Error(getString(R.string.error_something_went_wrong))
                }

            } else {
                handleErrorResponse(response)
            }

        } catch (e: SocketTimeoutException) {
            NetworkResult.Error(getString(R.string.error_request_timeout))
        } catch (e: IOException) {
            NetworkResult.Error(getString(R.string.error_no_internet))
        } catch (e: Exception) {
            Log.e("BaseRepository", "Unexpected error", e)
            NetworkResult.Error(getString(R.string.error_something_went_wrong_later))
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
                NetworkResult.Unauthorized(getString(R.string.error_session_expired))
            }

            in 400..499 -> {
                NetworkResult.Error(getString(R.string.error_something_went_wrong))
            }

            in 500..599 -> {
                NetworkResult.Error(getString(R.string.error_server))
            }

            else -> {
                NetworkResult.Error(getString(R.string.error_something_went_wrong))
            }
        }
    }
}
