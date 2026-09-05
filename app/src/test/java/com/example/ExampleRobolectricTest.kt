package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    assertEquals("Emoji Battery Themes", appName)
  }

  @Test
  fun `verify emoji theme catalog categories and defaults`() {
    val categories = com.example.model.EmojiThemeCatalog.CATEGORIES
    org.junit.Assert.assertTrue(categories.contains("Football"))
    org.junit.Assert.assertTrue(categories.contains("Cute Animals"))
    org.junit.Assert.assertTrue(categories.contains("Valentine"))

    val defaultTheme = com.example.model.EmojiThemeCatalog.getThemeById("cute_bunny_love")
    assertEquals("Bunny Love", defaultTheme.title)
    assertEquals("🐰", defaultTheme.mascotEmoji)
  }
}
