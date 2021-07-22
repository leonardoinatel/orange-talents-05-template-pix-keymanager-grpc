package br.zup.ot5.pix.remove

import br.zup.ot5.KeymanagerRegistraGrpcServiceGrpc
import br.zup.ot5.KeymanagerRemoveGrpcServiceGrpc
import br.zup.ot5.RemoveChavePixRequest
import br.zup.ot5.pix.grpcenum.TipoChaveEnum
import br.zup.ot5.pix.grpcenum.TipoContaEnum
import br.zup.ot5.pix.registra.ChavePix
import br.zup.ot5.pix.registra.ChavePixRepository
import br.zup.ot5.pix.registra.ContaAssociada
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RemoveChaveEndpointTest(@Inject val chavePixRepository: ChavePixRepository,
@Inject val grpcClient: KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub){

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    internal fun setUp() {
        CHAVE_EXISTENTE = chavePixRepository.save(
            chave(
                TipoChaveEnum.CPF,
                "09157454612",
                CLIENTE_ID.toString()
            ))
    }

    @AfterEach
    internal fun tearDown() {
        chavePixRepository.deleteAll()
    }

    companion object {
        val CLIENTE_ID: UUID = UUID.randomUUID()
    }

    @Test
    fun `deve conseguir excluir uma chave`() {
        //acao
        val response = grpcClient.remove(RemoveChavePixRequest.newBuilder()
            .setIdCliente(CHAVE_EXISTENTE.clienteId)
            .setIdPix(CHAVE_EXISTENTE.id)
            .build())
        //validacao
        with(response) {
            assertEquals(CHAVE_EXISTENTE.id, response.idPix)
            assertEquals(CHAVE_EXISTENTE.clienteId, response.idCliente)
        }
    }

    @Test
    fun `nao deve remover chave de um cliente diferente`() {
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.remove(RemoveChavePixRequest.newBuilder()
                .setIdCliente("e2b55b2e-f130-4a62-b085-a0c501baeb03")
                .setIdPix(CHAVE_EXISTENTE.id)
                .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou nao pertence ao cliente", status.description)
        }
    }

    @Test
    fun `nao deve remover chave inexistente`(){
        val thrown = assertThrows<StatusRuntimeException>{
            grpcClient.remove(RemoveChavePixRequest.newBuilder()
                .setIdCliente(CLIENTE_ID.toString())
                .setIdPix("e2b55b2e-f130-4a62-b085-a0c501baeb03")
                .build()
            )
        }

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave pix não encontrada ou nao pertence ao cliente", status.description)
        }
    }


    private fun chave(tipoChave: TipoChaveEnum, chave: String = UUID.randomUUID().toString(), clienteId: String): ChavePix {
        return ChavePix(clienteId, tipoChave, chave, TipoContaEnum.CONTA_CORRENTE, ContaAssociada(
            "UNIBANCO ITAU",
            "Rafael Ponte",
            "63657520325",
            "1218",
            "291900"
        )
        )
    }

    @Factory
    class ClientRemoveChavePix  {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRemoveGrpcServiceGrpc.KeymanagerRemoveGrpcServiceBlockingStub? {
            return KeymanagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }
}