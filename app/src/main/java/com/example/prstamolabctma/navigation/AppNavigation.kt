package com.example.prstamolabctma.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prstamolabctma.ui.catalogo.CatalogoScreen
import com.example.prstamolabctma.ui.equipo.EquipoDetalleScreen
import com.example.prstamolabctma.ui.solicitud.SolicitudScreen
import com.example.prstamolabctma.ui.misprestamos.MisSolicitudesScreen
import com.example.prstamolabctma.ui.misprestamos.SolicitudDetalleScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {
        composable("catalogo") {
            CatalogoScreen(navController)
        }
        composable(
            route = "equipoDetalle/{equipoId}",
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            EquipoDetalleScreen(navController, equipoId)
        }
        composable(
            route = "solicitar/{equipoId}",
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            SolicitudScreen(navController, equipoId)
        }
        composable("misSolicitudes") {
            MisSolicitudesScreen(navController)
        }
        composable(
            route = "solicitudDetalle/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
        ) { backStackEntry ->
            val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: -1
            SolicitudDetalleScreen(navController, solicitudId)
        }
    }
}
