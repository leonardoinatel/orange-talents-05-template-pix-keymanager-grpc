package br.zup.ot5.pix.registra

import br.zup.ot5.KeymanagerRegistraGrpcServiceGrpc
import br.zup.ot5.RegistraChavePixRequest
import br.zup.ot5.RegistraChavePixResponse
import br.zup.ot5.shared.anotations.ErrorHandle
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandle
class RegistraChaveEndpoint(@Inject private val service: NovaChavePixService) : KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceImplBase() {

    override fun cadastrar(
        request: RegistraChavePixRequest?,
        responseObserver: StreamObserver<RegistraChavePixResponse>?
    ) {
        val novaChave = request!!.toModel()
        val chaveCriada = service.cadastra(novaChave)

        responseObserver?.onNext(RegistraChavePixResponse.newBuilder()
            .setIdCliente(chaveCriada.clienteId)
            .setIdPix(chaveCriada.id)
            .build()
        )

        responseObserver?.onCompleted()
    }

}