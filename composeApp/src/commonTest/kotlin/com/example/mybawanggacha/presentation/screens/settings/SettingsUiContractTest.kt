package com.example.mybawanggacha.presentation.screens.settings

import com.example.mybawanggacha.domain.settings.model.NetworkMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsUiContractTest {
    @Test
    fun networkMode_fromString_shouldFallbackToAuto() {
        assertEquals(NetworkMode.Auto, NetworkMode.fromString(null))
        assertEquals(NetworkMode.Auto, NetworkMode.fromString("broken"))
        assertEquals(NetworkMode.OfflineOnly, NetworkMode.fromString("offlineonly"))
    }

    @Test
    fun networkMode_allowsNetwork_shouldMatchMode() {
        assertTrue(NetworkMode.Auto.allowsNetwork)
        assertFalse(NetworkMode.OfflineOnly.allowsNetwork)
    }
}
