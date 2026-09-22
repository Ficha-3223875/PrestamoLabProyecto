package com.example.prstamolabctma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prstamolabctma.navigation.AppNavigation
import com.example.prstamolabctma.ui.theme.PréstamoLabCTMATheme
import com.example.prstamolabctma.viewmodel.PrestamoViewModel
import com.example.prstamolabctma.viewmodel.PrestamoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PréstamoLabCTMATheme {
                val app = application as PrestamoLabApplication
                val viewModel: PrestamoViewModel = viewModel(
                    factory = PrestamoViewModelFactory(app.container.prestamoRepository)
                )

                AppNavigation(
                    viewModel = viewModel
                )
            }
        }
    }
}