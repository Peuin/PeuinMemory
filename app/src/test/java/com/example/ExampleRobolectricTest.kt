package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.datasource.SampleData
import com.example.data.local.PeuinDatabase
import com.example.data.model.PlaceCategory
import com.example.data.remote.GeminiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Peuin", appName)
    }

    @Test
    fun `verify sample trip and places data structure`() {
        val trip = SampleData.sampleTrip
        assertEquals("trip_dalat_01", trip.id)
        assertEquals("Đà Lạt", trip.destination)
        assertEquals(3, trip.days.size)
        assertTrue(trip.days[0].items.isNotEmpty())

        val places = SampleData.samplePlaces
        assertTrue(places.size >= 6)
        assertTrue(places.any { it.category == PlaceCategory.CAFE })
        assertTrue(places.any { it.category == PlaceCategory.FOOD })
    }

    @Test
    fun `verify smart assistant response for weather alert`() = runBlocking {
        val geminiService = GeminiService()
        val response = geminiService.askPeuin("Chiều nay trời mưa thì nên đi đâu?", "Đà Lạt 3N2Đ")
        assertNotNull(response)
        assertEquals("peuin", response.sender)
        assertTrue(response.text.contains("mưa") || response.text.contains("Dinh III") || response.text.contains("Workshop"))
    }
}
