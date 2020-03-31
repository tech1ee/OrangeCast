package com.example.orangecast.entity

class ErrorResponse: Throwable() {
    val code: Int? = null
}