package br.zup.ot5.externo

import java.time.LocalDateTime

data class DeletePixResponse(
    val key: String?,
    val participant: String,
    val deletedAt: LocalDateTime
)