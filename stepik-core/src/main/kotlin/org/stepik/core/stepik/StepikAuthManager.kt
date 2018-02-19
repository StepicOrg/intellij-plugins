package org.stepik.core.stepik

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.util.net.HttpConfigurable
import org.stepik.api.client.HttpTransportClient
import org.stepik.api.client.StepikApiClient
import org.stepik.api.exceptions.StepikClientException
import org.stepik.api.objects.auth.TokenInfo
import org.stepik.api.objects.users.User
import org.stepik.core.ProjectManager
import org.stepik.core.auth.ui.AuthDialog
import org.stepik.core.common.Loggable
import org.stepik.core.metrics.Metrics
import org.stepik.core.stepik.StepikAuthState.AUTH
import org.stepik.core.stepik.StepikAuthState.NOT_AUTH
import org.stepik.core.stepik.StepikAuthState.SHOW_DIALOG
import org.stepik.core.stepik.StepikAuthState.UNKNOWN
import org.stepik.core.utils.PluginUtils
import org.stepik.core.utils.PluginUtils.getCurrentProduct
import org.stepik.core.utils.PluginUtils.getCurrentProductVersion
import org.stepik.core.utils.PluginUtils.version
import org.stepik.core.utils.ProductGroup
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import javax.swing.SwingUtilities
import java.lang.System.getProperty as getSysProperty

object StepikAuthManager : Loggable {
    private const val CLIENT_ID = "vV8giW7KTPMOTriOUBwyGLvXbKV0Cc4GPBnyCJPd"
    private const val CLIENT_ID_PASSWORD_BASED = "MVo2c17mATXWfBUdmP5oCsFrUB7RtCYqOvUmng90"
    private const val LAST_USER_PROPERTY_NAME = "${PluginUtils.PLUGIN_ID}.LAST_USER"
    val userAgent = "Stepik Union/$version (${getSysProperty("os.name")}) " +
            "StepikApiClient/${StepikApiClient.getVersion()} " +
            "${getCurrentProduct()}/${getCurrentProductVersion()} " +
            "JRE/${getSysProperty("java.version")}"
    val stepikApiClient = initStepikApiClient()
    const val implicitGrantUrl = "https://stepik.org/oauth2/authorize/?client_id=$CLIENT_ID&response_type=token"
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

            if (stepikApiClient.tokenInfo.accessToken != null) {
                val user = getCurrentUser(true)
                return !user.isGuest
            }

            return false
        }

    val currentUser: User
        get() = getCurrentUser(false)

    val currentUserFullName: String
        get() {
            val user = currentUser
            return (user.firstName + " " + user.lastName).trim { it <= ' ' }
        }

    @Synchronized
    private fun initStepikApiClient(): StepikApiClient {
        logger.info(userAgent)

        val instance = HttpConfigurable.getInstance()
        val client: StepikApiClient
        client = if (instance.USE_HTTP_PROXY) {
            logger.info(String.format("Uses proxy: Host = %s, Port = %s", instance.PROXY_HOST, instance.PROXY_PORT))
            val transportClient: HttpTransportClient =
                    HttpTransportClient.getInstance(instance.PROXY_HOST, instance.PROXY_PORT, userAgent)
            StepikApiClient(transportClient)
        } else {
            StepikApiClient(userAgent)
        }

        val lastUserId = lastUser

        val tokenInfo = getTokenInfo(lastUserId, client)

        client.setTokenInfo(tokenInfo)

        return client
    }

    /**
     * Authentication is in the following order:
     *
     *  * Check a current authentication.
     *  * Try refresh a token.
     *  * Try authentication with a stored password.
     *  * Show a browser for authentication or registration
     *
     */
    @Synchronized
    fun authentication(showDialog: Boolean): StepikAuthState {
        var value = minorLogin()
        if (value !== AUTH && showDialog) {
            setState(SHOW_DIALOG)
            value = showAuthDialog()
        }
        setState(value)
        return value
    }

    private fun showAuthDialog(): StepikAuthState {
        val application = ApplicationManager.getApplication()
        val isDispatchThread = application.isDispatchThread || SwingUtilities.isEventDispatchThread()

        val authenticated = arrayOf(state)

        val showDialog = { authenticated[0] = showDialog() }

        if (!isDispatchThread) {
            try {
                if (PluginUtils.isCurrent(ProductGroup.PYCHARM)) {
                    /* TODO Check it what it's actual for supported versions today.
                     * For some reason PyCharm run in Swing Event Dispatch Thread
                     */
                    SwingUtilities.invokeAndWait(showDialog)
                } else {
                    application.invokeAndWait(showDialog, ModalityState.defaultModalityState())
                }
            } catch (e: InterruptedException) {
                logger.warn(e)
            } catch (e: InvocationTargetException) {
                logger.warn(e)
            } catch (e: ProcessCanceledException) {
                logger.warn(e)
            }

        } else {
            showDialog.invoke()
        }

        return authenticated[0]
    }

    private fun showDialog(): StepikAuthState {
        val map = AuthDialog.showAuthForm()
        var newState = NOT_AUTH
        val tokenInfo = TokenInfo()
        if (!map.isEmpty() && !map.containsKey("error")) {
            newState = AUTH
            tokenInfo.accessToken = map["access_token"]
            tokenInfo.expiresIn = map.getOrDefault("expires_in", "0").toInt()
            tokenInfo.scope = map["scope"]
            tokenInfo.tokenType = map["token_type"]
            tokenInfo.refreshToken = map["refresh_token"]
        }

        stepikApiClient.setTokenInfo(tokenInfo)
        if (newState === AUTH && tokenInfo.accessToken != null) {
            val user = getCurrentUser(true)
            if (!user.isGuest) {
                setTokenInfo(user.id, tokenInfo)
            } else {
                newState = NOT_AUTH
            }
        }

        logger.info("Show the authentication dialog with result: $newState")

        return newState
    }

    private fun setState(value: StepikAuthState) {
        val oldState = state
        state = value

        if (state === NOT_AUTH) {
            stepikApiClient.setTokenInfo(null)
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

        var refreshToken = stepikApiClient.tokenInfo.refreshToken
        if (refreshToken == null) {
            val tokenInfo = getTokenInfo(lastUser)
            refreshToken = tokenInfo.refreshToken
        }

        if (refreshToken != null) {
            try {
                stepikApiClient.oauth2()
                        .userAuthenticationRefresh(CLIENT_ID, refreshToken)
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
        val credentials = PasswordSafe.getInstance().get(attributes)
        var authInfo: TokenInfo? = null
        if (credentials != null) {
            val password = credentials.getPasswordAsString()
            authInfo = client.jsonConverter.fromJson(password, TokenInfo::class.java)
        }
        return if (authInfo == null) {
            TokenInfo()
        } else authInfo
    }

    private fun setTokenInfo(userId: Long, tokenInfo: TokenInfo) {
        val serviceName = ProjectManager::class.java.name
        val userName = userId.toString()
        val attributes: CredentialAttributes
        attributes = CredentialAttributes(serviceName, userName, ProjectManager::class.java, false)
        val serializedAuthInfo = stepikApiClient.jsonConverter.toJson(tokenInfo)
        val credentials = Credentials(attributes.userName, serializedAuthInfo)
        PasswordSafe.getInstance().set(attributes, credentials)
        lastUser = userId
    }

    private fun getCurrentUser(request: Boolean): User {
        if (user == null || user!!.id == 0L || request) {
            user = try {
                stepikApiClient.stepiks().currentUser
            } catch (e: StepikClientException) {
                logger.warn("Get current user failed", e)
                User()
            }

        }

        return user!!
    }

    @JvmOverloads
    fun authAndGetStepikApiClient(showDialog: Boolean = false): StepikApiClient {
        StepikAuthManager.authentication(showDialog)
        return stepikApiClient
    }

    @Synchronized
    fun logout() {
        setState(NOT_AUTH)
        logger.info("Logout successfully")
    }

    @Synchronized
    fun relogin() {
        logout()
        setState(showAuthDialog())
    }

    fun addListener(listener: StepikAuthManagerListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: StepikAuthManagerListener) {
        listeners.remove(listener)
    }

    @Synchronized
    fun authentication(
            email: String,
            password: String): CompletableFuture<StepikAuthState> {
        return stepikApiClient.oauth2()
                .userAuthenticationPassword(CLIENT_ID_PASSWORD_BASED, email, password)
                .executeAsync()
                .thenApplyAsync<StepikAuthState> { tokenInfo ->
                    synchronized(StepikAuthManager::class.java) {
                        stepikApiClient.setTokenInfo(tokenInfo)
                        val user = getCurrentUser(true)
                        setTokenInfo(user.id, tokenInfo)
                        setState(AUTH)
                        return@thenApplyAsync state
                    }
                }.exceptionally { e ->
                    synchronized(StepikAuthManager::class.java) {
                        logger.warn(e)
                        setState(NOT_AUTH)
                        return@exceptionally state
                    }
                }
    }
}
