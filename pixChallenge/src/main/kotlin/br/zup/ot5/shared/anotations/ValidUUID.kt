package br.zup.ot5.shared.anotations

import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

@ReportAsSingleViolation
@MustBeDocumented
@Retention(RUNTIME)
@Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$", flags = [Pattern.Flag.CASE_INSENSITIVE])
@Target(FIELD)
annotation class ValidUUID(
    val message:String ="não é um formato válido de UUID",
    val groups:Array<KClass<Any>> =[],
    val payload:Array<KClass<Any>> = [],
)
