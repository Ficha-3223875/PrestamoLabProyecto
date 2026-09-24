package com.example.prstamolabctma.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.prstamolabctma.model.CategoriaEquipo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val CATEGORIA_FILTRO = stringPreferencesKey("categoria_filtro")
    }

    val categoriaFiltroFlow: Flow<CategoriaEquipo?> = context.dataStore.data.map { preferences ->
        val categoriaString = preferences[PreferencesKeys.CATEGORIA_FILTRO]
        if (!categoriaString.isNullOrEmpty()) {
            runCatching { CategoriaEquipo.valueOf(categoriaString) }.getOrNull()
        } else {
            null
        }
    }

    suspend fun guardarCategoriaFiltro(categoria: CategoriaEquipo?) {
        context.dataStore.edit { preferences ->
            if (categoria != null) {
                preferences[PreferencesKeys.CATEGORIA_FILTRO] = categoria.name
            } else {
                preferences.remove(PreferencesKeys.CATEGORIA_FILTRO)
            }
        }
    }
}
