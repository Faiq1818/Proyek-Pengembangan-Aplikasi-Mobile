package com.example.mybawanggacha.domain.settings.repository

import com.example.mybawanggacha.domain.settings.model.JikanRequestUsage
import kotlinx.coroutines.flow.Flow

interface JikanRequestUsageRepository {
    val usage: Flow<JikanRequestUsage>
}
