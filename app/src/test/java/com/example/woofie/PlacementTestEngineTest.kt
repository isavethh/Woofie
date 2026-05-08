package com.example.woofie

import com.example.woofie.ui.PlacementTestEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class PlacementTestEngineTest {

    @Test
    fun scoreToLevel_mapsScoresCorrectly() {
        assertEquals("A1", PlacementTestEngine.scoreToLevel(0))
        assertEquals("A1", PlacementTestEngine.scoreToLevel(1))
        assertEquals("A2", PlacementTestEngine.scoreToLevel(2))
        assertEquals("B1", PlacementTestEngine.scoreToLevel(3))
        assertEquals("B1", PlacementTestEngine.scoreToLevel(4))
    }
}

