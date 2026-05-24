package com.example.mybawanggacha.presentation.screens.search

import com.example.mybawanggacha.domain.search.model.MediaSearchFilters
import com.example.mybawanggacha.domain.search.model.MediaSearchItem
import com.example.mybawanggacha.domain.search.model.MediaSearchPage
import com.example.mybawanggacha.domain.search.model.SearchMediaType
import com.example.mybawanggacha.domain.search.repository.SearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun refresh_whenSuccessAlreadyExists_shouldKeepContentAndUseRefreshingFlag() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Old Result")),
            nextPage = 2,
            hasNextPage = true
        )
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()

        val initialState = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Old Result", initialState.items.first().title)

        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 2, title = "Fresh Result")),
            nextPage = null,
            hasNextPage = false
        )

        viewModel.refresh()

        assertTrue(viewModel.isRefreshing.value)
        val stateWhileRefreshing = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Old Result", stateWhileRefreshing.items.first().title)

        advanceUntilIdle()

        assertFalse(viewModel.isRefreshing.value)
        val refreshedState = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals("Fresh Result", refreshedState.items.first().title)
    }

    @Test
    fun loadNextPage_whenCalledTwiceBeforeCompletion_shouldRequestNextPageOnlyOnce() = runTest {
        val repository = FakeSearchRepository()
        repository.pages[1] = MediaSearchPage(
            items = listOf(createItem(id = 1, title = "Page 1")),
            nextPage = 2,
            hasNextPage = true
        )
        repository.pages[2] = MediaSearchPage(
            items = listOf(createItem(id = 2, title = "Page 2")),
            nextPage = 3,
            hasNextPage = true
        )
        val viewModel = SearchViewModel(repository)

        viewModel.submitSearch()
        advanceUntilIdle()

        viewModel.loadNextPage()
        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(listOf(1, 2), repository.requestedPages)
        val state = assertIs<SearchUiState.Success>(viewModel.uiState.value)
        assertEquals(listOf(1, 2), state.items.map { it.malId })
        assertFalse(state.isLoadingMore)
    }

    private class FakeSearchRepository : SearchRepository {
        val pages = mutableMapOf<Int, MediaSearchPage>()
        val requestedPages = mutableListOf<Int>()

        override suspend fun search(
            filters: MediaSearchFilters,
            page: Int
        ): MediaSearchPage {
            requestedPages += page
            return pages[page] ?: MediaSearchPage(
                items = emptyList(),
                nextPage = null,
                hasNextPage = false
            )
        }
    }

    private fun createItem(
        id: Int,
        title: String
    ): MediaSearchItem {
        return MediaSearchItem(
            malId = id,
            mediaType = SearchMediaType.Anime,
            title = title,
            imageUrl = null,
            type = "TV",
            status = "Airing",
            score = 8.0,
            rank = null,
            episodes = 12,
            chapters = null,
            volumes = null
        )
    }
}
