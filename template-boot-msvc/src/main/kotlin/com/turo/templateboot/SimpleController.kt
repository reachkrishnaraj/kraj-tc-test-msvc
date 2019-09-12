package com.turo.app.base.package.name

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import java.util.concurrent.CompletableFuture

@RestController
class SimpleController(private val service: SimpleService) {

    @ApiOperation(
        value = "Simple get of configuration values",
        notes = "These are notes. The can be long.  \nI guess they can have newlines",
        tags = [ "api", "config" ])
    @GetMapping("/sample")
    fun doGet(): SimpleDto {
        return service.get()
    }

    @ApiOperation(
        value = "Async get of configuration values",
        notes = "These are notes. The can be long.  \nI guess they can have newlines",
        tags = [ "api", "config", "async" ])
    @GetMapping("/sample-async")
    fun doGetAsync(
        @ApiParam("Specify the asynchronous delay in seconds")
        @RequestParam(defaultValue = "5")
        delaySeconds: Long
    ): CompletableFuture<SimpleDto> {
        return service.getAsync(delaySeconds)
    }
}
