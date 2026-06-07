package com.example.mybawanggacha.data.repository.jikan

import com.example.mybawanggacha.data.remote.jikan.api.JikanRateLimiter
import com.example.mybawanggacha.data.remote.jikan.api.JikanRequestUsageSnapshot
import com.example.mybawanggacha.domain.settings.model.JikanRequestUsage
import com.example.mybawanggacha.domain.settings.repository.JikanRequestUsageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JikanRequestUsageRepositoryImpl : JikanRequestUsageRepository {
    override val usage: Flow<JikanRequestUsage> = flow {
        while (true) {
            emit(JikanRateLimiter.snapshot().toDomain())
            delay(1_000L)
        }
    }

    private fun JikanRequestUsageSnapshot.toDomain(): JikanRequestUsage {
        return JikanRequestUsage(
            usedLastSecond = usedLastSecond,
            secondLimit = secondLimit,
            usedLastMinute = usedLastMinute,
            minuteLimit = minuteLimit,
            remainingThisMinute = remainingThisMinute,
            msUntilNextRequest = msUntilNextRequest
        )
    }
}
