package com.dk.piley.util

import com.dk.piley.BuildConfig
import com.dk.piley.model.user.UserPrefsManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.URISyntaxException
import javax.inject.Singleton

/**
 * Base url interceptor for replacing base url
 *
 * @property userPrefsManager instance of user preferences manager to get base url
 */
@OptIn(DelicateCoroutinesApi::class)
@Singleton
class BaseUrlInterceptor(private val userPrefsManager: UserPrefsManager) : Interceptor {
    private var baseUrl = BuildConfig.LOCAL_API_BASE_URL.toHttpUrlOrNull()

    // observe changes in base url and set accordingly
    init {
        GlobalScope.launch {
            userPrefsManager.getBaseUrl().collectLatest {
                baseUrl = it.toHttpUrlOrNull()
            }
        }
    }

    /**
     * Intercept http requests and replace with custom url
     *
     * @param chain interceptor chain
     * @return http response
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        var newUrl: HttpUrl? = null
        try {
            newUrl = baseUrl?.let {
                request.url.newBuilder()
                    .scheme(it.scheme)
                    .host(it.toUrl().toURI().host)
                    .build()
            }
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        request = newUrl?.let {
            request.newBuilder()
                .url(it)
                .build()
        } ?: request
        return chain.proceed(request)
    }
}
