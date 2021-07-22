package br.zup.ot5.pix.remove

import br.zup.ot5.KeymanagerRemoveGrpcServiceGrpc
import br.zup.ot5.RemoveChavePixRequest
import br.zup.ot5.RemoveChavePixResponse
import br.zup.ot5.shared.anotations.ErrorHandle
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandle
@Singleton
class RemoveChaveEndpoint(@Inject private val service: RemoveChaveService) : KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceImplBase(){

    override fun remove(
        request: RemoveChavePixRequest?,
        responseObserver: StreamObserver<RemoveChavePixResponse>?
    ) {

        service.remove(request?.idCliente, request?.idPix)

        responseObserver!!.onNext(RemoveChavePixResponse.newBuilder()
            .setIdCliente(request!!.idCliente)
            .setIdPix(request.idPix)
            .setResultado("Sua chave pix foi deletada com sucesso")
            .build()
        )

        responseObserver?.onCompleted()
    }
}