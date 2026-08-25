package com.example.prstamolabctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import com.example.prstamolabctma.data.repository.InMemoryPrestamoRepository
import com.example.prstamolabctma.navigation.AppNavigation
import com.example.prstamolabctma.ui.theme.PréstamoLabCTMATheme
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PréstamoLabCTMATheme {

                val viewModel = remember {
                    PrestamoViewModel(
                        InMemoryPrestamoRepository()
                    )
                }

                AppNavigation(
                    viewModel = viewModel
                )
            }
        }
    }
}