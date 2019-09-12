package com.turo.app.base.package.name

import java.util.concurrent.CompletableFuture

interface SimpleService {
    fun get(): SimpleDto
    fun getAsync(delaySeconds: Long): CompletableFuture<SimpleDto>
}
