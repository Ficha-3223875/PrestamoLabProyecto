package com.example.prstamolabctma.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prstamolabctma.ui.catalogo.CatalogoScreen
import com.example.prstamolabctma.ui.equipo.EquipoDetalleScreen
import com.example.prstamolabctma.ui.solicitud.SolicitudScreen
import com.example.prstamolabctma.ui.misprestamos.MisSolicitudesScreen
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@Composable
fun NavGraph(navController: NavHostController, viewModel: PrestamoViewModel) {
    NavHost(navController = navController, startDestination = Destino.Catalogo.ruta) {

        // Catálogo de equipos
        composable(Destino.Catalogo.ruta) {
            CatalogoScreen(
                viewModel,
                navController,
                onEquipoClick = { equipoId ->
                    navController.navigate(Destino.Equipo.crearRuta(equipoId))
                }
            )
        }

        // Detalle de equipo
        composable(Destino.Equipo.ruta) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getString("equipoId")?.toIntOrNull()
            if (equipoId != null) {
                EquipoDetalleScreen(viewModel, equipoId, navController)
            }
        }

        // Solicitud de préstamo
        composable(Destino.Solicitud.ruta) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getString("equipoId")?.toIntOrNull()
            if (equipoId != null) {
                SolicitudScreen(viewModel, equipoId, navController)
            }
        }

        // Lista de solicitudes
        composable(Destino.MisSolicitudes.ruta) {
            MisSolicitudesScreen(viewModel, navController)
        }
    }
}
