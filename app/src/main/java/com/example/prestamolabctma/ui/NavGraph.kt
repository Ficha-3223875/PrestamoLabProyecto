package com.example.prestamolabctma.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prestamolabctma.ui.theme.PrestamoLabTheme
import com.example.prestamolabctma.viewmodel.PrestamoViewModel


// ==========================================================
// RUTAS DE NAVEGACIÓN
// ==========================================================

object Routes {

    const val CATALOGO = "catalogo"

    const val MIS_SOLICITUDES = "mis_solicitudes"

    const val EQUIPO = "equipo/{equipoId}"

    const val SOLICITAR = "solicitar/{equipoId}"

    const val SOLICITUD = "solicitud/{solicitudId}"


    // Crear ruta para un equipo específico
    fun equipo(id: Int): String {
        return "equipo/$id"
    }


    // Crear ruta para solicitar un equipo específico
    fun solicitar(id: Int): String {
        return "solicitar/$id"
    }


    // Crear ruta para una solicitud específica
    fun solicitud(id: Int): String {
        return "solicitud/$id"
    }
}


// ==========================================================
// NAVIGATION GRAPH
// ==========================================================

@Composable
fun NavGraph() {

    PrestamoLabTheme {

        // Controlador de navegación
        val navController = rememberNavController()

        val context = androidx.compose.ui.platform.LocalContext.current
        val databaseLocal = androidx.compose.runtime.remember { com.example.prestamolabctma.data.local.BaseDatosLocal(context.applicationContext) }
        val roomRepository = androidx.compose.runtime.remember { com.example.prestamolabctma.data.RoomPrestamoRepository(databaseLocal) }
        val dsPreferences = androidx.compose.runtime.remember { com.example.prestamolabctma.data.local.PreferenciaDataStore(context.applicationContext) }

        // ViewModel compartido por todas las pantallas con persistencia única en Room y DataStore
        val viewModel: PrestamoViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return PrestamoViewModel(roomRepository, dsPreferences) as T
                }
            }
        )


        // ==================================================
        // NAV HOST
        // ==================================================

        NavHost(

            navController = navController,

            startDestination = Routes.CATALOGO

        ) {


            // ==================================================
            // 1. CATÁLOGO
            // ==================================================

            composable(
                route = Routes.CATALOGO
            ) {

                CatalogoScreen(

                    viewModel = viewModel,

                    // Cuando se selecciona un equipo
                    onEquipoClick = { equipoId ->

                        navController.navigate(
                            Routes.equipo(equipoId)
                        )
                    },

                    // Ir a mis solicitudes
                    onMisSolicitudes = {

                        navController.navigate(
                            Routes.MIS_SOLICITUDES
                        )
                    }
                )
            }


            // ==================================================
            // 2. DETALLE DEL EQUIPO
            // ==================================================

            composable(

                route = Routes.EQUIPO,

                arguments = listOf(

                    navArgument("equipoId") {

                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->


                // Obtener ID del equipo
                val equipoId =
                    backStackEntry.arguments
                        ?.getInt("equipoId")
                        ?: -1


                // Mostrar detalle
                EquipoDetalleScreen(

                    equipo =
                        viewModel.equipo(equipoId),

                    // Regresar
                    onBack = {

                        navController.popBackStack()
                    },

                    // Ir al formulario de solicitud
                    onSolicitar = { id ->

                        navController.navigate(
                            Routes.solicitar(id)
                        )
                    }
                )
            }


            // ==================================================
            // 3. FORMULARIO DE SOLICITUD
            // ==================================================

            composable(

                route = Routes.SOLICITAR,

                arguments = listOf(

                    navArgument("equipoId") {

                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->


                // Obtener ID del equipo
                val equipoId =
                    backStackEntry.arguments
                        ?.getInt("equipoId")
                        ?: -1


                SolicitudScreen(

                    equipo =
                        viewModel.equipo(equipoId),

                    viewModel =
                        viewModel,

                    // Regresar
                    onBack = {

                        navController.popBackStack()
                    },

                    // Cuando se crea correctamente
                    onCreated = { solicitudId ->

                        navController.navigate(
                            Routes.solicitud(solicitudId)
                        ) {

                            // Evitar volver al formulario
                            popUpTo(
                                Routes.CATALOGO
                            )
                        }
                    }
                )
            }


            // ==================================================
            // 4. MIS SOLICITUDES
            // ==================================================

            composable(
                route = Routes.MIS_SOLICITUDES
            ) {

                MisSolicitudesScreen(

                    viewModel =
                        viewModel,

                    // Regresar al catálogo
                    onBack = {

                        navController.popBackStack()
                    },

                    // Ver detalle de una solicitud
                    onSolicitudClick = { solicitudId ->

                        navController.navigate(
                            Routes.solicitud(solicitudId)
                        )
                    }
                )
            }


            // ==================================================
            // 5. DETALLE DE LA SOLICITUD
            // ==================================================

            composable(

                route = Routes.SOLICITUD,

                arguments = listOf(

                    navArgument("solicitudId") {

                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->


                // Obtener ID de la solicitud
                val solicitudId =
                    backStackEntry.arguments
                        ?.getInt("solicitudId")
                        ?: -1


                // Buscar solicitud
                val solicitud =
                    viewModel.solicitud(solicitudId)


                // Buscar equipo asociado
                val equipo =
                    solicitud?.let {

                        viewModel.equipo(
                            it.equipoId
                        )
                    }


                // Mostrar detalle
                SolicitudDetalleScreen(

                    solicitud =
                        solicitud,

                    equipo =
                        equipo,

                    viewModel =
                        viewModel,

                    // Regresar
                    onBack = {

                        navController.popBackStack()
                    }
                )
            }
        }
    }
}