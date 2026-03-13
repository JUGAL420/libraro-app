package com.techito.libraro.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/**
 * PreferenceManager handles all local data storage using Jetpack DataStore.
 * Sensitive data is manually encrypted using Android KeyStore (MasterKey) before storage.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "libraro_secure_prefs")

@Suppress("DEPRECATION")
class PreferenceManager private constructor(context: Context) {

    // Store the dataStore instance directly to avoid holding onto the Context object
    private val dataStore = context.dataStore
    private val masterKeyAlias = "_libraro_master_key_2026"

    // Initialize MasterKey
    init {
        MasterKey.Builder(context, masterKeyAlias)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val HAS_ASKED_NOTIF_PERMISSION = booleanPreferencesKey("has_asked_notification_permission")
    }

    companion object {
        private const val AES_GCM_NO_PADDING = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val TAG_SIZE = 128

        @Volatile
        private var INSTANCE: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    /**
     * Helper to get the key from KeyStore safely.
     */
    private fun getSecretKey() = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }.getKey(masterKeyAlias, null)

    /**
     * Helper to encrypt a string using the MasterKey.
     */
    private fun encrypt(value: String?): String? {
        if (value.isNullOrEmpty()) return null
        return try {
            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
            
            val combined = ByteArray(iv.size + encryptedBytes.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
            
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Helper to decrypt a string using the MasterKey.
     */
    private fun decrypt(encryptedValue: String?): String? {
        if (encryptedValue.isNullOrEmpty()) return null
        return try {
            val combined = Base64.decode(encryptedValue, Base64.DEFAULT)
            if (combined.size < IV_SIZE) return null
            
            val iv = combined.sliceArray(0 until IV_SIZE)
            val encryptedBytes = combined.sliceArray(IV_SIZE until combined.size)

            val cipher = Cipher.getInstance(AES_GCM_NO_PADDING)
            val spec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)
            
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Auth Token (Encrypted)
     */
    val authToken: Flow<String?> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences -> decrypt(preferences[Keys.AUTH_TOKEN]) }

    suspend fun saveAuthToken(token: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.AUTH_TOKEN] = encrypt(token) ?: ""
        }
    }

    /**
     * User Login State
     */
    val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[Keys.IS_LOGGED_IN] ?: false }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = loggedIn
        }
    }

    /**
     * User ID (Encrypted)
     */
    val userId: Flow<String?> = dataStore.data
        .map { preferences -> decrypt(preferences[Keys.USER_ID]) }

    suspend fun saveUserId(id: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_ID] = encrypt(id) ?: ""
        }
    }

    /**
     * User Name
     */
    val userName: Flow<String?> = dataStore.data
        .map { preferences -> preferences[Keys.USER_NAME] }

    suspend fun saveUserName(name: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_NAME] = name ?: ""
        }
    }

    /**
     * App States
     */
    val isFirstTimeLaunch: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[Keys.IS_FIRST_TIME] ?: true }

    suspend fun setFirstTimeLaunch(isFirstTime: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_FIRST_TIME] = isFirstTime
        }
    }

    val fcmToken: Flow<String?> = dataStore.data
        .map { preferences -> preferences[Keys.FCM_TOKEN] }

    suspend fun saveFcmToken(token: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.FCM_TOKEN] = token ?: ""
        }
    }

    val deviceId: Flow<String?> = dataStore.data
        .map { preferences -> preferences[Keys.DEVICE_ID] }

    suspend fun saveDeviceId(id: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.DEVICE_ID] = id ?: ""
        }
    }

    val hasAskedNotificationPermission: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[Keys.HAS_ASKED_NOTIF_PERMISSION] ?: false }

    suspend fun setHasAskedNotificationPermission(asked: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.HAS_ASKED_NOTIF_PERMISSION] = asked
        }
    }

    /**
     * Clears all stored preferences (usually on Logout) except for FCM_TOKEN and DEVICE_ID.
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            val fcmToken = preferences[Keys.FCM_TOKEN]
            val deviceId = preferences[Keys.DEVICE_ID]
            
            preferences.clear()
            
            // Restore FCM_TOKEN and DEVICE_ID if they existed
            fcmToken?.let { preferences[Keys.FCM_TOKEN] = it }
            deviceId?.let { preferences[Keys.DEVICE_ID] = it }
        }
    }
}
