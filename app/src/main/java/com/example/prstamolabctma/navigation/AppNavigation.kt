package com.example.prstamolabctma.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prstamolabctma.uii.CatalogoScreen
import com.example.prstamolabctma.uii.EquipoDetalleScreen
import com.example.prstamolabctma.uii.SolicitudScreen
import com.example.prstamolabctma.uii.MisSolicitudesScreen
import com.example.prstamolabctma.uii.SolicitudDetalleScreen
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: PrestamoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaPrincipal(
                onCatalogo = { navController.navigate("catalogo") },
                onSolicitudes = { navController.navigate("misSolicitudes") }
            )
        }
        composable("catalogo") {
            CatalogoScreen(navController, viewModel)
        }
        composable(
            route = "equipoDetalle/{equipoId}",
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            EquipoDetalleScreen(navController, equipoId, viewModel)
        }
        composable(
            route = "solicitar/{equipoId}",
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            SolicitudScreen(navController, equipoId, viewModel)
        }
        composable("misSolicitudes") {
            MisSolicitudesScreen(navController, viewModel)
        }
        composable(
            route = "solicitudDetalle/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
        ) { backStackEntry ->
            val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: -1
            SolicitudDetalleScreen(navController, solicitudId, viewModel)
        }
    }
}

@Composable
fun PantallaPrincipal(
    onCatalogo: () -> Unit,
    onSolicitudes: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido al sistema de préstamos",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCatalogo, modifier = Modifier.fillMaxWidth()) {
            Text("Ver catálogo de equipos")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSolicitudes, modifier = Modifier.fillMaxWidth()) {
            Text("Mis solicitudes")
        }
    }
}