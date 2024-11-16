package com.mksolution.newsshort.Models

data class Poll(
    val pollId: String = "",
    val pollTitle: String = "",
    val pollQuestion: String = "",
    val pollUrl: String = "",
    val timestamp: Long = 0
)
