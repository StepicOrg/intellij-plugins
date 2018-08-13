package org.stepik.core.auth

import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.openapi.util.io.BufferExposingByteArrayOutputStream
import com.intellij.openapi.util.io.StreamUtil
import com.intellij.openapi.wm.IdeFrame
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.AppIcon
import io.netty.buffer.Unpooled.wrappedBuffer
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.HttpMethod.GET
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.QueryStringDecoder
import org.jetbrains.ide.BuiltInServerManager
import org.jetbrains.ide.RestService
import org.jetbrains.io.addNoCache
import org.jetbrains.io.response
import org.jetbrains.io.send
import org.stepik.api.exceptions.StepikClientException
import org.stepik.core.auth.StepikAuthManager.authorizationCodeUrl
import org.stepik.core.auth.StepikAuthManager.stepikApiClient
import org.stepik.core.clientId
import org.stepik.core.common.Loggable
import org.stepik.core.templates.Templater
import java.io.ByteArrayInputStream

class StepikRestService : RestService(), Loggable {
    
    override fun getServiceName() = SERVICE_NAME
    
    override fun isMethodSupported(method: HttpMethod) = method === GET
    
    override fun isPrefixlessAllowed() = true
    
    override fun isHostTrusted(request: FullHttpRequest): Boolean {
        val codeMatcher = OAUTH_URI_PATTERN.matchEntire(request.uri())
        return if (request.method() === GET && codeMatcher != null) {
            true
        } else {
            super.isHostTrusted(request)
        }
    }
    
    override fun execute(urlDecoder: QueryStringDecoder, request: FullHttpRequest,
                         context: ChannelHandlerContext): String? {
        val uri = urlDecoder.uri()
        logger.info("Process the request $uri")
        
        val codeMatcher = OAUTH_URI_PATTERN.matchEntire(uri)
        if (codeMatcher != null) {
            val code = getStringParameter("code", urlDecoder)
            val error = getStringParameter("error", urlDecoder)
            when {
                error != null -> {
                    logger.warn(error)
                    sendHTMLResponse(request, context, "auth_failure",
                            mapOf(
                                    "link" to authorizationCodeUrl,
                                    "error" to error
                            )
                    )
                    return error
                }
                code != null  -> {
                    var newState = StepikAuthState.NOT_AUTH
                    try {
                        val tokenInfo = stepikApiClient.oauth2()
                                .userAuthenticationCode(clientId, redirectUri, code)
                                .execute()
                        if (tokenInfo.accessToken != null) {
                            newState = StepikAuthState.AUTH
                            
                            stepikApiClient.tokenInfo = tokenInfo
                            val user = StepikAuthManager.getCurrentUser(true)
                            if (!user.isGuest) {
                                StepikAuthManager.setTokenInfo(user.id, tokenInfo)
                            } else {
                                newState = StepikAuthState.NOT_AUTH
                            }
                            
                            logger.info("Open a user browser with result: $newState")
                        }
                    } catch (e: StepikClientException) {
                        logger.warn(e)
                        sendHTMLResponse(request, context, "auth_failure",
                                mapOf(
                                        "link" to authorizationCodeUrl,
                                        "error" to e.message
                                )
                        )
                        return e.message
                    }
                    StepikAuthManager.setState(newState)
                    if (newState === StepikAuthState.AUTH) {
                        sendHTMLResponse(request, context, "auth_successfully")
                        val frame = WindowManager.getInstance()
                                .findVisibleFrame()
                        getApplication().invokeLater {
                            AppIcon.getInstance()
                                    .requestFocus(frame as IdeFrame)
                            frame.toFront()
                        }
                        return null
                    }
                }
                else          -> {
                    sendHTMLResponse(request, context, "auth_failure",
                            mapOf(
                                    "link" to authorizationCodeUrl,
                                    "error" to "Unknown error"
                            )
                    )
                    return null
                }
            }
        }
        
        sendStatus(HttpResponseStatus.BAD_REQUEST, false, context.channel())
        val message = "Unknown command $uri"
        logger.info(message)
        return message
    }
    
    companion object {
        const val SERVICE_NAME = "$PREFIX/stepik"
        val port = BuiltInServerManager.getInstance()
                .port
        val redirectUri = "http://localhost:$port/$SERVICE_NAME/oauth"
        val OAUTH_URI_PATTERN = "/$SERVICE_NAME/oauth[^/]*".toRegex()
    }
}

private fun sendHTMLResponse(request: HttpRequest, context: ChannelHandlerContext, template: String,
                             templateContext: Map<String, Any?> = emptyMap()) {
    val pageTemplate = Templater.processTemplate(template, templateContext)
    BufferExposingByteArrayOutputStream().use {
        it.write(StreamUtil.loadFromStream(ByteArrayInputStream(pageTemplate.toByteArray())))
        val response = response("text/html", wrappedBuffer(it.internalBuffer, 0, it.size()))
        response.addNoCache()
        response.headers()
                .set("X-Frame-Options", "Deny")
        response.send(context.channel(), request)
    }
    
}
