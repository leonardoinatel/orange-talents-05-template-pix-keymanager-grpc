package br.zup.ot5.shared.anotations

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS

@MustBeDocumented
@Retention(RUNTIME)
@Target(CLASS)
@Around
annotation class ErrorHandle
