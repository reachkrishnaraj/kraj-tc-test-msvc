package com.turo.app.base.package.name

import java.time.Instant

data class SimpleDto(
    var variable: String? = null,
    var secret: String? = null,
    var timestamp: Instant? = null
)
