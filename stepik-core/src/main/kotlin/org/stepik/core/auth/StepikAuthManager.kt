package org.stepik.core.auth

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.BrowserUtil
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ide.util.PropertiesComponent
import com.intellij.util.net.HttpConfigurable
import org.stepik.api.client.HttpTransportClient
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.auth.TokenInfo
import org.stepik.api.objects.users.User
import org.stepik.core.ProjectManager
import org.stepik.core.auth.StepikAuthState.AUTH
import org.stepik.core.auth.StepikAuthState.NOT_AUTH
import org.stepik.core.auth.StepikAuthState.UNKNOWN
import org.stepik.core.auth.StepikRestService.Companion.redirectUri
import org.stepik.core.clientId
import org.stepik.core.common.Loggable
import org.stepik.core.host
import org.stepik.core.loadCurrentUser
import org.stepik.core.metrics.Metrics
import org.stepik.core.pluginId
import org.stepik.core.utils.getCurrentProduct
import org.stepik.core.utils.getCurrentProductVersion
import org.stepik.core.utils.version
import java.util.*
import java.util.concurrent.Executors
import java.lang.System.getProperty as getSysProperty

object StepikAuthManager : Loggable {
    val authorizationCodeUrl = "$host/oauth2/authorize/?" +
                               "client_id=$clientId&redirect_uri=$redirectUri&response_type=code"
    private val LAST_USER_PROPERTY_NAME = "$pluginId.LAST_USER"
    private val userAgent = "Stepik Union/${version(pluginId)} (${getSysProperty("os.name")}) " +
                            "StepikApiClient/${StepikApiClient.version} " +
                            "${getCurrentProduct()}/${getCurrentProductVersion()} " +
                            "JRE/${getSysProperty("java.version")}"
    val stepikApiClient = initStepikApiClient()
    private val listeners = ArrayList<StepikAuthManagerListener>()
    private val executor = Executors.newSingleThreadExecutor()
    @Volatile
    private var state = UNKNOWN
    private var user: User? = null
    
    private var lastUser: Long
        get() = PropertiesComponent.getInstance().getOrInitLong(LAST_USER_PROPERTY_NAME, 0)
        set(userId) = PropertiesComponent.getInstance().setValue(LAST_USER_PROPERTY_NAME, userId.toString())
    
    val isAuthenticated: Boolean
        get() {
            if (state === NOT_AUTH) {
                return false
            }
            
            stepikApiClient.tokenInfo?.accessToken ?: return false
            
            return !getCurrentUser(true).isGuest
        }
    
    val currentUser: User
        get() = getCurrentUser(false)
    
    val currentUserFullName: String
        get() {
            val user = currentUser
            return ("${user.firstName} ${user.lastName}").trim()
        }
    
    private fun initStepikApiClient(): StepikApiClient {
        synchronized(this) {
            logger.info(userAgent)
            
            val instance = HttpConfigurable.getInstance()
            val client: StepikApiClient
            client = if (instance.USE_HTTP_PROXY) {
                logger.info("Uses proxy: Host = ${instance.PROXY_HOST}, Port = ${instance.PROXY_PORT}")
                val transportClient: HttpTransportClient =
                        HttpTransportClient.getInstance(instance.PROXY_HOST, instance.PROXY_PORT, userAgent)
                StepikApiClient(transportClient, host)
            } else {
                StepikApiClient(userAgent, host)
            }
            
            val lastUserId = lastUser
            
            val tokenInfo = getTokenInfo(lastUserId, client)
            
            client.tokenInfo = tokenInfo
            
            return client
        }
    }
    
    /**
     * Authentication is in the following order:
     *
     *  * Check a current authentication.
     *  * Try refresh a token.
     *  * Show a browser for authentication or registration
     *
     */
    fun authentication(openBrowser: Boolean = true): StepikAuthState {
        synchronized(this) {
            val value = minorLogin()
            if (value !== AUTH && openBrowser) {
                authInUserBrowser()
            }
            setState(value)
            return value
        }
    }
    
    private fun authInUserBrowser() {
        BrowserUtil.browse(authorizationCodeUrl)
    }
    
    internal fun setState(value: StepikAuthState) {
        val oldState = state
        state = value
        
        if (state === NOT_AUTH) {
            stepikApiClient.tokenInfo = null
            user = null
            val userId = lastUser
            setTokenInfo(userId, TokenInfo())
            lastUser = 0
        }
        
        if (oldState !== state) {
            if (state === AUTH) {
                Metrics.authenticate()
            }
            executor.execute { listeners.forEach { listener -> listener.stateChanged(oldState, state) } }
        }
    }
    
    private fun minorLogin(): StepikAuthState {
        if (isAuthenticated) {
            return AUTH
        }
        
        val tokenInfo = stepikApiClient.tokenInfo ?: getTokenInfo(lastUser)
        
        val refreshToken = tokenInfo.refreshToken
        
        if (refreshToken != null) {
            try {
                stepikApiClient.oauth2()
                        .userAuthenticationRefresh(clientId, refreshToken)
                        .execute()
                logger.info("Refresh a token is successfully")
                return AUTH
            } catch (re: StepikClientException) {
                logger.info("Refresh a token failed: " + re.message)
            }
        }
        
        return NOT_AUTH
    }
    
    private fun getTokenInfo(userId: Long, client: StepikApiClient = stepikApiClient): TokenInfo {
        if (userId == 0L) {
            return TokenInfo()
        }
        val serviceName = ProjectManager::class.java.name
        val attributes = CredentialAttributes(serviceName,
                userId.toString(),
                ProjectManager::class.java,
                false)
        val credentials = PasswordSafe.getInstance()
                .get(attributes)
        var authInfo: TokenInfo? = null
        if (credentials != null) {
            val password = credentials.getPasswordAsString()
            authInfo = client.jsonConverter.fromJson(password, TokenInfo::class.java)
        }
        return authInfo ?: TokenInfo()
    }
    
    internal fun setTokenInfo(userId: Long, tokenInfo: TokenInfo) {
        val serviceName = ProjectManager::class.java.name
        val userName = userId.toString()
        val attributes: CredentialAttributes
        attributes = CredentialAttributes(serviceName, userName, ProjectManager::class.java, false)
        val serializedAuthInfo = stepikApiClient.jsonConverter.toJson(tokenInfo)
        val credentials = Credentials(attributes.userName, serializedAuthInfo)
        PasswordSafe.getInstance()
                .set(attributes, credentials)
        lastUser = userId
    }
    
    internal fun getCurrentUser(request: Boolean): User {
        if (user == null || user!!.id == 0L || request) {
            user = try {
                loadCurrentUser(stepikApiClient)
            } catch (e: StepikClientException) {
                logger.warn("Get current user failed", e)
                User()
            }
            
        }
        
        return user!!
    }
    
    fun authAndGetStepikApiClient(openBrowser: Boolean = false): StepikApiClient {
        authentication(openBrowser)
        return stepikApiClient
    }
    
    fun logout() {
        synchronized(this) {
            setState(NOT_AUTH)
            logger.info("Logout successfully")
        }
    }
    
    fun relogin() {
        synchronized(this) {
            logout()
            authInUserBrowser()
        }
    }
    
    fun addListener(listener: StepikAuthManagerListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: StepikAuthManagerListener) {
        listeners.remove(listener)
    }
    
}
