package com.example.refluenceds.utils

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorHandler {

    fun parseException(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> {
                when (throwable.code()) {
                    400 -> "Bad Request. Please check your parameters."
                    401 -> "Unauthorized. Please log in again."
                    403 -> "Forbidden access."
                    404 -> "Resource not found on server."
                    500 -> "Internal server error. Please try again later."
                    else -> "HTTP Error: ${throwable.code()} ${throwable.message()}"
                }
            }
            is SocketTimeoutException -> "Connection timed out. Please check your internet connection."
            is UnknownHostException -> "Server host could not be resolved."
            is IOException -> "No internet connection available."
            else -> throwable.localizedMessage ?: "An unknown error occurred."
        }
    }
}
