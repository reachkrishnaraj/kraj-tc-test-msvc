package com.turo.app.base.package.name.client

import com.turo.app.base.package.name.SimpleDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface testAppNameApi {
    @get:GET("/sample")
    val config: Call<SimpleDto>

    @GET("/sample-async")
    fun getConfigAsync(@Query("delaySeconds") delaySeconds: Long): Call<SimpleDto>
}
