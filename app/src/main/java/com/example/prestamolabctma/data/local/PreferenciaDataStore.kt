package com.example.prestamolabctma.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "configuracion_prestamos")

class PreferenciaDataStore(private val context: Context) {

    companion object {
        val KEY_CATEGORIA_FILTRO = stringPreferencesKey("categoria_filtro")
    }

    val categoriaFiltroFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_CATEGORIA_FILTRO] ?: "TODOS"
        }

    suspend fun guardarCategoriaFiltro(categoria: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CATEGORIA_FILTRO] = categoria
        }
    }
}
