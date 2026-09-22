package com.example.prstamolabctma.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesRepository(private val context: Context) {
    private val CATEGORIA_KEY = stringPreferencesKey("categoria_filtro")

    val categoriaFiltro: Flow<String?> =
        context.dataStore.data.map { it[CATEGORIA_KEY] }

    suspend fun guardarCategoria(categoria: String?) {
        context.dataStore.edit {
            if (categoria == null) it.remove(CATEGORIA_KEY) else it[CATEGORIA_KEY] = categoria
        }
    }
}