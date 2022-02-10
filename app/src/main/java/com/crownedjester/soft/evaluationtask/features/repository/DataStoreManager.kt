package com.crownedjester.soft.evaluationtask.features.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.crownedjester.soft.evaluationtask.common.Constants
import com.crownedjester.soft.evaluationtask.common.DataStorePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences>
        by preferencesDataStore(name = Constants.DATASTORE_NAME)

class DataStoreManager @Inject constructor(@ApplicationContext context: Context) :
    DataStoreRepository {

    private val imagesDataStore = context.dataStore

    override val folderTitle: Flow<String>
        get() = imagesDataStore.getValueAsFlow(DataStorePreferences.FOLDER_TITLE_KEY, "Улицы")
    override val subFolderTitle: Flow<String>
        get() = imagesDataStore.getValueAsFlow(DataStorePreferences.SUBFOLDER_TITLE_KEY, "Подпапка")

    override suspend fun setFolderTitle(title: String) {
        imagesDataStore.setValue(DataStorePreferences.FOLDER_TITLE_KEY, title)
    }

    override suspend fun setSubFolderTitle(title: String) {
        imagesDataStore.setValue(DataStorePreferences.SUBFOLDER_TITLE_KEY, title)
    }

    private suspend fun <T> DataStore<Preferences>.setValue(
        key: Preferences.Key<T>,
        value: T
    ) {
        this.edit { prefs ->
            prefs[key] = value
        }

    }


    private fun <T> DataStore<Preferences>.getValueAsFlow(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> {
        return this.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { prefs ->
            prefs[key] ?: defaultValue
        }
    }
}