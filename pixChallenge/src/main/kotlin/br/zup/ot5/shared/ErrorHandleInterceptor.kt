package br.zup.ot5.shared

import br.zup.ot5.shared.anotations.ErrorHandle
import br.zup.ot5.shared.exception.ChavePixExistenteException
import br.zup.ot5.shared.exception.ChavePixNaoEncontradaException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandle::class)
class ErrorHandleInterceptor: MethodInterceptor<Any, Any>{

    override fun intercept(context: MethodInvocationContext<Any, Any>?): Any? {
        try {
            context?.proceed()
        } catch (e: Exception) {
            val responseObserver = context!!.parameterValues[1] as StreamObserver<*>

            val status = when(e) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message)
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
                is ChavePixExistenteException -> Status.ALREADY_EXISTS.withDescription(e.message)
                is ChavePixNaoEncontradaException -> Status.NOT_FOUND.withDescription(e.message)
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withDescription(e.message)

                else -> Status.UNKNOWN.withCause(e).withDescription("Erro inesperado")
            }

            responseObserver.onError(status.asRuntimeException())
        }

        return null
    }

}