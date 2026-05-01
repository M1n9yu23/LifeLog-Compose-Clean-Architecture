package com.bossmg.android.sync

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SyncPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("sync_preferences") },
    )

    suspend fun getLastSyncTime(uid: String): Long =
        dataStore.data.first()[longPreferencesKey(lastSyncKey(uid))] ?: 0L

    suspend fun setLastSyncTime(uid: String, time: Long) {
        dataStore.edit { it[longPreferencesKey(lastSyncKey(uid))] = time }
    }

    private fun lastSyncKey(uid: String) = "last_sync_time_$uid"
}
