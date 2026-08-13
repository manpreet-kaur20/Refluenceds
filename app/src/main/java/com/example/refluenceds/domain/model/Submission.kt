package com.example.refluenceds.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Submission(
    val id: String,
    val campaignId: String,
    val creatorId: String,
    val contentUrl: String,
    val status: SubmissionStatus,
    val submittedAt: Long
)

enum class SubmissionStatus {
    PENDING, APPROVED, REJECTED
}
