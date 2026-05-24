package com.example.mybawanggacha.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshContainer(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()
    val pulledOffset = (pullRefreshState.distanceFraction.coerceIn(0f, 1f) * 28f).dp
    val contentOffset by animateDpAsState(
        targetValue = if (isRefreshing) 10.dp else pulledOffset,
        label = "pull_refresh_content_offset"
    )

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            if (!isRefreshing) {
                onRefresh()
            }
        },
        modifier = modifier,
        state = pullRefreshState,
        indicator = {}
    ) {
        Box(
            modifier = Modifier.offset(y = contentOffset)
        ) {
            content()
        }
    }
}