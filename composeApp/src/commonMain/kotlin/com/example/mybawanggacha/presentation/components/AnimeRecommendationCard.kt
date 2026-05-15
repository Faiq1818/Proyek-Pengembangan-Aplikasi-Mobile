package com.example.mybawanggacha.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.mybawanggacha.data.remote.dto.AnimeEntry

@Composable
fun AnimeRecommendationCard(
    anime: AnimeEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            AsyncImage(
                model = anime.images.jpg.large_image_url,
                contentDescription = anime.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
