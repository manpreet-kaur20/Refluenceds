package com.example.refluenceds.data.repository

import com.example.refluenceds.data.remote.datasource.RemoteDataSource
import com.example.refluenceds.data.remote.dto.ProfileResponseDto
import com.example.refluenceds.domain.model.AppNotification
import com.example.refluenceds.domain.model.EarningsSummary
import com.example.refluenceds.domain.model.HomeData
import com.example.refluenceds.domain.model.Product
import com.example.refluenceds.domain.model.ReferralInfo
import com.example.refluenceds.domain.model.UserProfile
import com.example.refluenceds.domain.repository.AppRepository
import com.example.refluenceds.utils.NetworkResult
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : AppRepository {

    override suspend fun login(email: String, pass: String): Result<String> {
        return when (val res = remoteDataSource.login(email, pass)) {
            is NetworkResult.Success -> Result.success(res.data.token)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun signup(email: String, pass: String): Result<String> {
        return when (val res = remoteDataSource.signup(email, pass)) {
            is NetworkResult.Success -> Result.success(res.data.token)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun forgotPassword(email: String): Result<String> {
        return when (val res = remoteDataSource.forgotPassword(email)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun verifyEmail(email: String, code: String): Result<String> {
        return when (val res = remoteDataSource.verifyEmail(email, code)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun verifySocial(platform: String, username: String): Result<String> {
        return when (val res = remoteDataSource.verifySocial(platform, username)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun submitOnboarding(
        firstName: String,
        lastName: String,
        gender: String,
        country: String,
        interests: List<String>
    ): Result<String> {
        return when (val res = remoteDataSource.submitOnboarding(firstName, lastName, gender, country, interests)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun logout(): Result<String> {
        return when (val res = remoteDataSource.logout()) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun deleteAccount(): Result<String> {
        return when (val res = remoteDataSource.deleteAccount()) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getProducts(): Result<List<Product>> {
        return when (val result = remoteDataSource.getProducts()) {
            is NetworkResult.Success -> Result.success(result.data.products.map { it.toDomain() })
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getHomeData(): Result<HomeData> {
        return when (val result = remoteDataSource.getHomeData()) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getProfile(userId: String): Result<UserProfile> {
        return when (val result = remoteDataSource.getProfile(userId)) {
            is NetworkResult.Success -> Result.success(result.data.toDomain())
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun editProfile(profile: UserProfile): Result<UserProfile> {
        val dto = ProfileResponseDto(profile.id, profile.name, profile.email, profile.avatarUrl, profile.followersCount)
        return when (val res = remoteDataSource.editProfile(dto)) {
            is NetworkResult.Success -> Result.success(res.data.toDomain())
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun reportCampaign(campaignId: String, reasons: List<String>, comment: String?): Result<String> {
        return when (val res = remoteDataSource.reportCampaign(campaignId, reasons, comment)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun toggleFavorite(campaignId: String, isFav: Boolean): Result<String> {
        return when (val res = remoteDataSource.toggleFavorite(campaignId, isFav)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun followBrand(brandId: String, follow: Boolean): Result<String> {
        return when (val res = remoteDataSource.followBrand(brandId, follow)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getReferrals(): Result<ReferralInfo> {
        return when (val res = remoteDataSource.getReferrals()) {
            is NetworkResult.Success -> Result.success(res.data.toDomain())
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getEarnings(): Result<EarningsSummary> {
        return when (val res = remoteDataSource.getEarnings()) {
            is NetworkResult.Success -> Result.success(res.data.toDomain())
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun withdrawPayout(amount: Double, iban: String): Result<String> {
        return when (val res = remoteDataSource.withdrawPayout(amount, iban)) {
            is NetworkResult.Success -> Result.success(res.data.message)
            is NetworkResult.Error -> Result.failure(res.exception ?: Exception(res.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }

    override suspend fun getNotifications(userId: String): Result<List<AppNotification>> {
        return when (val result = remoteDataSource.getNotifications(userId)) {
            is NetworkResult.Success -> Result.success(result.data.notifications.map { it.toDomain() })
            is NetworkResult.Error -> Result.failure(result.exception ?: Exception(result.message))
            is NetworkResult.Loading -> Result.failure(Exception("Loading"))
        }
    }
}
