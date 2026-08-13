package com.example.refluenceds.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.refluenceds.domain.model.Campaign

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey val id: String,
    val title: String,
    val brandName: String,
    val description: String,
    val reward: String,
    val imageUrl: String,
    val status: String,
    val deadline: Long,
    val category: String
)

fun CampaignEntity.toDomain() = Campaign(
    id = id,
    title = title,
    brandName = brandName,
    description = description,
    reward = reward,
    imageUrl = imageUrl,
    status = status,
    deadline = deadline,
    category = category
)

fun Campaign.toEntity() = CampaignEntity(
    id = id,
    title = title,
    brandName = brandName,
    description = description,
    reward = reward,
    imageUrl = imageUrl,
    status = status,
    deadline = deadline,
    category = category
)
