package com.example.refluenceds.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refluenceds.domain.model.Campaign
import com.example.refluenceds.domain.model.Submission
import com.example.refluenceds.domain.model.Tutorial
import com.example.refluenceds.domain.model.Post
import com.example.refluenceds.domain.model.Earnings
import com.example.refluenceds.domain.repository.CampaignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampaignViewModel @Inject constructor(
    private val repository: CampaignRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            _isLoading.value = false
        }
    }

    val campaigns: StateFlow<List<Campaign>> = repository.getCampaigns()
        .combine(_selectedCategory) { campaigns, category ->
            if (category == "All") campaigns
            else campaigns.filter { it.category == category }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = listOf("All", "Fashion", "Lifestyle", "Events", "Gaming")

    val submissions: StateFlow<List<Submission>> = repository.getSubmissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tutorials: StateFlow<List<Tutorial>> = repository.getTutorials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val socialPosts: StateFlow<List<Post>> = repository.getSocialPosts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val earnings: StateFlow<Earnings?> = repository.getEarnings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) return
        
        viewModelScope.launch {
            _selectedCategory.value = category
            _isLoading.value = true
            kotlinx.coroutines.delay(1000) // Simulate loading
            _isLoading.value = false
        }
    }

    fun triggerLoading() {
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(1000) // Simulate loading
            _isLoading.value = false
        }
    }

    fun applyToCampaign(campaignId: String) {
        viewModelScope.launch {
            repository.submitContent(campaignId, "https://placeholder.content.url")
        }
    }
}
