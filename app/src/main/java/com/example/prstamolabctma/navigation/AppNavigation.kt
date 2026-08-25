package com.example.prstamolabctma.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prstamolabctma.ui.catalogo.CatalogoScreen
import com.example.prstamolabctma.ui.equipo.EquipoScreen
import com.example.prstamolabctma.ui.misprestamos.MisPrestamosScreen
import com.example.prstamolabctma.ui.misprestamos.SolicitudDetalleScreen
import com.example.prstamolabctma.ui.solicitud.SolicitudScreen
import com.example.prstamolabctma.viewmodel.PrestamoViewModel

@Composable
fun AppNavigation(
    viewModel: PrestamoViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "catalogo"
    ) {

        // -------------------------
        // CATÁLOGO
        // -------------------------
        composable("catalogo") {

            CatalogoScreen(
                viewModel = viewModel,

                onEquipoClick = { equipoId ->
                    navController.navigate(
                        "equipo/$equipoId"
                    )
                },

                onMisPrestamosClick = {
                    navController.navigate(
                        "misprestamos"
                    )
                }
            )
        }

        // -------------------------
        // DETALLE DEL EQUIPO
        // -------------------------
        composable(
            route = "equipo/{equipoId}",
            arguments = listOf(
                navArgument("equipoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val equipoId =
                backStackEntry.arguments?.getInt("equipoId")

            if (equipoId != null) {

                EquipoScreen(
                    equipoId = equipoId,
                    viewModel = viewModel,

                    onSolicitarClick = { id ->
                        navController.navigate(
                            "solicitud/$id"
                        )
                    },

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // -------------------------
        // SOLICITUD
        // -------------------------
        composable(
            route = "solicitud/{equipoId}",
            arguments = listOf(
                navArgument("equipoId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val equipoId =
                backStackEntry.arguments?.getInt("equipoId")

            if (equipoId != null) {

                SolicitudScreen(
                    equipoId = equipoId,
                    viewModel = viewModel,

                    onSolicitudCreada = {
                        navController.navigate(
                            "misprestamos"
                        ) {
                            popUpTo("catalogo")
                        }
                    },

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // -------------------------
        // MIS PRÉSTAMOS
        // -------------------------
        composable("misprestamos") {

            MisPrestamosScreen(
                viewModel = viewModel,

                onSolicitudClick = { solicitudId ->
                    navController.navigate(
                        "solicitudDetalle/$solicitudId"
                    )
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // -------------------------
        // DETALLE DE SOLICITUD
        // -------------------------
        composable(
            route = "solicitudDetalle/{solicitudId}",
            arguments = listOf(
                navArgument("solicitudId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val solicitudId =
                backStackEntry.arguments?.getInt("solicitudId")

            if (solicitudId != null) {

                SolicitudDetalleScreen(
                    solicitudId = solicitudId,
                    viewModel = viewModel,

                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}