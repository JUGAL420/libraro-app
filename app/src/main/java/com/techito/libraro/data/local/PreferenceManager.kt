package com.techito.libraro.data.local

import android.content.Context
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.techito.libraro.model.AppSettingData
import com.techito.libraro.model.LibraryDetail
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
        val APP_SETTINGS = stringPreferencesKey("app_settings")
        val LIBRARY_DETAILS = stringPreferencesKey("library_details")
        val LIBRARY_ID = stringPreferencesKey("library_id")
        val USER_TYPE = stringPreferencesKey("user_type")
        
        // Remember Login Details
        val REMEMBER_EMAIL = stringPreferencesKey("remember_email")
        val REMEMBER_PASSWORD = stringPreferencesKey("remember_password")
        val IS_REMEMBER_ME = booleanPreferencesKey("is_remember_me")
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
     * Library ID (Encrypted)
     */
    val libraryId: Flow<String?> = dataStore.data
        .map { preferences -> decrypt(preferences[Keys.LIBRARY_ID]) }

    suspend fun saveLibraryId(id: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.LIBRARY_ID] = encrypt(id) ?: ""
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
     * User Type
     */
    val userType: Flow<String?> = dataStore.data
        .map { preferences -> preferences[Keys.USER_TYPE] }

    suspend fun saveUserType(type: String?) {
        dataStore.edit { preferences ->
            preferences[Keys.USER_TYPE] = type ?: ""
        }
    }

    /**
     * Remember Me Details (Encrypted)
     */
    val rememberEmail: Flow<String?> = dataStore.data
        .map { preferences -> decrypt(preferences[Keys.REMEMBER_EMAIL]) }

    val rememberPassword: Flow<String?> = dataStore.data
        .map { preferences -> decrypt(preferences[Keys.REMEMBER_PASSWORD]) }

    val isRememberMeChecked: Flow<Boolean> = dataStore.data
        .map { preferences -> preferences[Keys.IS_REMEMBER_ME] ?: false }

    suspend fun saveRememberMeDetails(email: String?, password: String?, isChecked: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.REMEMBER_EMAIL] = if (isChecked) encrypt(email) ?: "" else ""
            preferences[Keys.REMEMBER_PASSWORD] = if (isChecked) encrypt(password) ?: "" else ""
            preferences[Keys.IS_REMEMBER_ME] = isChecked
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
     * App Settings
     */
    val appSettings: Flow<AppSettingData?> = dataStore.data
        .map { preferences ->
            val json = preferences[Keys.APP_SETTINGS]
            if (json.isNullOrEmpty()) null else Gson().fromJson(json, AppSettingData::class.java)
        }

    suspend fun saveAppSettings(settings: AppSettingData?) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_SETTINGS] = Gson().toJson(settings)
        }
    }

    /**
     * Library Details
     */
    val libraryDetails: Flow<LibraryDetail?> = dataStore.data
        .map { preferences ->
            val json = preferences[Keys.LIBRARY_DETAILS]
            if (json.isNullOrEmpty()) null else Gson().fromJson(json, LibraryDetail::class.java)
        }

    suspend fun saveLibraryDetails(details: LibraryDetail?) {
        dataStore.edit { preferences ->
            preferences[Keys.LIBRARY_DETAILS] = Gson().toJson(details)
        }
    }

    /**
     * Clears all stored preferences (usually on Logout) except for FCM_TOKEN and DEVICE_ID.
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            val fcmToken = preferences[Keys.FCM_TOKEN]
            val deviceId = preferences[Keys.DEVICE_ID]
            val isFirstTime = preferences[Keys.IS_FIRST_TIME]

            // Keep remember me details if that's what user meant by "restore when clear" 
            // but usually clearAll means logout. 
            // I will keep remember details for now as per point 3.
            val remEmail = preferences[Keys.REMEMBER_EMAIL]
            val remPass = preferences[Keys.REMEMBER_PASSWORD]
            val remChecked = preferences[Keys.IS_REMEMBER_ME]
            
            preferences.clear()
            
            // Restore kept values
            fcmToken?.let { preferences[Keys.FCM_TOKEN] = it }
            deviceId?.let { preferences[Keys.DEVICE_ID] = it }
            isFirstTime?.let { preferences[Keys.IS_FIRST_TIME] = it }
            remEmail?.let { preferences[Keys.REMEMBER_EMAIL] = it }
            remPass?.let { preferences[Keys.REMEMBER_PASSWORD] = it }
            remChecked?.let { preferences[Keys.IS_REMEMBER_ME] = it }
        }
    }
}
