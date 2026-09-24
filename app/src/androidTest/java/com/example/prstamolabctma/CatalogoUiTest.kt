package com.example.prstamolabctma

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class CatalogoUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verificar_tituloCatalogo_seMuestraEnPantalla() {
        composeTestRule.onNodeWithText("PréstamoLab CTMA", substring = true)
            .assertIsDisplayed()
    }
}