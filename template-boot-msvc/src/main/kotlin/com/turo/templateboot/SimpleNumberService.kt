package com.turo.app.base.package.name

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

import java.time.Instant
import java.util.concurrent.CompletableFuture

@Service
@Profile("number")
class SimpleNumberService @Autowired
constructor(private val config: SimpleNumberConfig) : SimpleService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun get(): SimpleDto {
        val simpleDto = SimpleDto("Var:" + config.variable, "Secret:" + config.secret, Instant.now())
        logger.info("complete request: {}", simpleDto)
        return simpleDto
    }

    @Async
    override fun getAsync(delaySeconds: Long): CompletableFuture<SimpleDto> {
        return CompletableFuture.supplyAsync {
            logger.info("starting number request: {}s delay", delaySeconds)
            try {
                Thread.sleep(delaySeconds * 1000)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }

            get()
        }
    }
}
