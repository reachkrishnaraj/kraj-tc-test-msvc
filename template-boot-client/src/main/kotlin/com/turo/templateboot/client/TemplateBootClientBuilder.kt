package com.turo.app.base.package.name.client

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

import java.util.concurrent.TimeUnit

class testAppNameClientBuilder {

    private var baseUrl: String? = null

    private var connectTimeout = DEFAULT_CONNECT_TIMEOUT_SECONDS
    private var connectTimeoutUnit = TimeUnit.SECONDS

    private var readTimeout = DEFAULT_READ_TIMEOUT_SECONDS
    private var readTimeoutUnit = TimeUnit.SECONDS

    private var writeTimeout = DEFAULT_WRITE_TIMEOUT_SECONDS
    private var writeTimeoutUnit = TimeUnit.SECONDS

    private var userAgent = String.format(USER_AGENT_FORMAT, "unknown", "unknown")

    fun setBaseUrl(baseUrl: String): testAppNameClientBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun setConnectTimeout(timeout: Long, unit: TimeUnit): testAppNameClientBuilder {
        this.connectTimeout = timeout
        this.connectTimeoutUnit = unit

        return this
    }

    fun setReadTimeout(timeout: Long, unit: TimeUnit): testAppNameClientBuilder {
        this.readTimeout = timeout
        this.readTimeoutUnit = unit

        return this
    }

    fun setWriteTimeout(timeout: Long, unit: TimeUnit): testAppNameClientBuilder {
        this.writeTimeout = timeout
        this.writeTimeoutUnit = unit

        return this
    }

    fun setUserAgent(clientService: String, clientVersion: String): testAppNameClientBuilder {
        this.userAgent = String.format(USER_AGENT_FORMAT, clientService, clientVersion)

        return this
    }

    fun build(): testAppNameApi {
        if (baseUrl == null) {
            throw IllegalStateException("Must set a base URL before constructing a client.")
        }

        val jacksonConverterFactory = JacksonConverterFactory.create(
            ObjectMapper()
                .registerModule(Jdk8Module())
                .registerModule(JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
        )

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(connectTimeout, connectTimeoutUnit)
            .readTimeout(readTimeout, readTimeoutUnit)
            .writeTimeout(writeTimeout, writeTimeoutUnit)
            .followRedirects(false)
            .followSslRedirects(true)
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", userAgent)
                        .build()
                )
            }
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl!!)
            .addConverterFactory(jacksonConverterFactory)
            .build()

        return retrofit.create(testAppNameApi::class.java)
    }

    companion object {

        private val DEFAULT_CONNECT_TIMEOUT_SECONDS: Long = 10
        private val DEFAULT_READ_TIMEOUT_SECONDS: Long = 10
        private val DEFAULT_WRITE_TIMEOUT_SECONDS: Long = 10

        private val USER_AGENT_FORMAT = "test-service-name-client/1.0 (Turo) %s/%s"

        fun start(): testAppNameClientBuilder {
            return testAppNameClientBuilder()
        }
    }
}
