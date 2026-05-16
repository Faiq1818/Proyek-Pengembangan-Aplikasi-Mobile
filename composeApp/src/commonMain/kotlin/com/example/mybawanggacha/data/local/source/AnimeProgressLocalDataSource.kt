package com.example.mybawanggacha.data.local.source

import com.example.mybawanggacha.data.local.NoteDatabase
import kotlin.time.Clock

class AnimeProgressLocalDataSource(
    database: NoteDatabase
) {
    private val queries = database.animeQueries

    fun getWatchedEpisodeNumbers(animeId: Int): Set<Int> {
        return queries.getWatchedEpisodeNumbers(animeId.toLong())
            .executeAsList()
            .map { it.toInt() }
            .toSet()
    }

    fun setEpisodeWatched(
        animeId: Int,
        episodeNumber: Int,
        watched: Boolean
    ) {
        queries.upsertEpisodeProgress(
            anime_id = animeId.toLong(),
            episode_number = episodeNumber.toLong(),
            watched = if (watched) 1L else 0L,
            updated_at = Clock.System.now().toEpochMilliseconds()
        )
    }
}
