package com.example.mybawanggacha.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {
    private val _filters = MutableStateFlow(MediaSearchFilters())
    val filters: StateFlow<MediaSearchFilters> = _filters.asStateFlow()

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null

    fun updateFilters(filters: MediaSearchFilters) {
        _filters.value = filters
    }

    fun resetFilters() {
        _filters.value = MediaSearchFilters(mediaType = _filters.value.mediaType)
        _uiState.value = SearchUiState.Idle
    }

    fun submitSearch() {
        search(page = 1, append = false)
    }

    fun refresh() {
        if (_uiState.value is SearchUiState.Success) {
            _isRefreshing.value = true
        }
        search(page = 1, append = false)
    }

    fun loadNextPage() {
        val current = _uiState.value as? SearchUiState.Success ?: return
        val nextPage = current.nextPage ?: return
        if (current.isLoadingMore) return

        _uiState.value = current.copy(isLoadingMore = true)
        search(page = nextPage, append = true)
    }

    private fun search(page: Int, append: Boolean) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            val currentFilters = _filters.value
            val previousItems = (_uiState.value as? SearchUiState.Success)?.items.orEmpty()

            if (!append) {
                _uiState.value = SearchUiState.Loading
            }

            runCatching {
                repository.search(filters = currentFilters, page = page)
            }.onSuccess { result ->
                val mergedItems = if (append) {
                    (previousItems + result.items).distinctBy { item ->
                        "${item.mediaType}:${item.malId}"
                    }
                } else {
                    result.items
                }

                _uiState.value = SearchUiState.Success(
                    items = mergedItems,
                    nextPage = result.nextPage,
                    isLoadingMore = false
                )
            }.onFailure { error ->
                if (append && previousItems.isNotEmpty()) {
                    _uiState.value = SearchUiState.Success(
                        items = previousItems,
                        nextPage = null,
                        isLoadingMore = false
                    )
                } else {
                    _uiState.value = SearchUiState.Error(
                        message = error.message ?: "Gagal mencari data dari Jikan"
                    )
                }
            }

            _isRefreshing.value = false
        }
    }
}
