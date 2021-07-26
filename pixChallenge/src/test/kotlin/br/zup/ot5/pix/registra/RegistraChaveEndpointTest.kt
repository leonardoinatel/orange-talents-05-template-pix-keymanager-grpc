package br.zup.ot5.pix.registra

import br.zup.ot5.KeymanagerRegistraGrpcServiceGrpc
import br.zup.ot5.RegistraChavePixRequest
import br.zup.ot5.TipoChave
import br.zup.ot5.TipoConta
import br.zup.ot5.externo.*
import br.zup.ot5.pix.grpcenum.TipoChaveEnum
import br.zup.ot5.pix.grpcenum.TipoContaEnum
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class RegistraChaveEndpointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val grpcClient: KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub
){

    @Inject
    lateinit var itauClient: ContasDeClientesNoItauClient

    @Inject
    lateinit var bcbClient: DadosDeClienteNoBancoCentral

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    companion object {
        val CLIENTE_ID: UUID = UUID.randomUUID()
    }

    @Test
    fun `nao deve registrar uma chave pix quando chave existente`() {
        //cenario
        chavePixRepository.save(chave(
            TipoChaveEnum.CPF,
            "09157454612",
            UUID.randomUUID().toString()
        ))

        //acao
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(RegistraChavePixRequest.newBuilder()
                .setIdCliente(UUID.randomUUID().toString())
                .setTipoChave(TipoChave.CPF)
                .setChave("09157454612")
                .setTipoConta(TipoConta.CONTA_CORRENTE)
                .build()
            )
        }

        //validacao
        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave Pix - 09157454612 - já cadastrada no sistema", status.description)
        }
    }

    @Test
    fun `deve registrar uma chave no banco`() {
        //cenario
        `when`(itauClient.buscaContaPorTipo(CLIENTE_ID.toString(), "CONTA_CORRENTE")).thenReturn(HttpResponse.ok(dadosDaContaResponse()))

        `when`(bcbClient.cadastrar(createdPixRequest())).thenReturn(HttpResponse.ok(createdPixResponse()))

        //acao
        val response = grpcClient.cadastrar(RegistraChavePixRequest.newBuilder()
            .setIdCliente(CLIENTE_ID.toString())
            .setTipoChave(TipoChave.EMAIL)
            .setChave("costaleo122@gmail.com")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()
        )

        //validacao
        with(response) {
            assertEquals(CLIENTE_ID.toString(), idCliente)
            assertNotNull(idPix)
        }
    }

    @Test
    fun `deve retornar erro quando nao achar a chave`() {
        //cenario
        `when`(itauClient.buscaContaPorTipo(CLIENTE_ID.toString(), "CONTA_CORRENTE")).thenReturn(HttpResponse.notFound())

        //acao
        val trown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(
                RegistraChavePixRequest.newBuilder()
                    .setIdCliente(CLIENTE_ID.toString())
                    .setTipoChave(TipoChave.RANDOM)
                    .setChave("")
                    .setTipoConta(TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //validacao
        with(trown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente não encontrado", status.description)
        }
    }

    @Test
    fun `nao deve registrar com atributos invalidos`() {
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.cadastrar(RegistraChavePixRequest.newBuilder().build())
        }

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
        }
    }

    private fun chave(tipoChave: TipoChaveEnum, chave: String = UUID.randomUUID().toString(), clienteId: String): ChavePix {
        return ChavePix(clienteId, tipoChave, chave, TipoContaEnum.CONTA_CORRENTE, ContaAssociada(
            "UNIBANCO ITAU",
            "Rafael Ponte",
            "63657520325",
            "1218",
            "291900"
        ))
    }

    private fun dadosDaContaResponse(): DadosdaContaResponse {
        return DadosdaContaResponse("CONTA_CORRENTE",
            Instituicao("UNIBANCO ITAU SA","60701190"),
            "1218",
            "291900",
            Titular("c56dfef4-7901-44fb-84e2-a2cefb157890",
                "Rafael M C Ponte",
                "02467781054"
            )
        )
    }

    private fun createdPixResponse(): CreatedPixResponse? {
        return CreatedPixResponse(
            TipoChaveEnum.EMAIL,
            "rafael.pontes@gmail.com",
            BankAccount("60701190", "1218", "291900", PixAccountType.CACC),
            Owner("NATURAL_PERSON", "Rafael Ponte", "63657520325"),
            LocalDateTime.now()
        )
    }

    private fun createdPixRequest(): CreatedPixRequest {
        return CreatedPixRequest(
            TipoChaveEnum.EMAIL,
            "rafael.pontes@gmail.com",
            BankAccount("60701190", "1218", "291900", PixAccountType.CACC),
            Owner("NATURAL_PERSON", "Rafael Ponte", "63657520325")
        )
    }

    @MockBean(ContasDeClientesNoItauClient::class)
    fun itauClient(): ContasDeClientesNoItauClient? {
        return Mockito.mock(ContasDeClientesNoItauClient::class.java)
    }

    @MockBean(DadosDeClienteNoBancoCentral::class)
    fun bcbClient(): DadosDeClienteNoBancoCentral? {
        return Mockito.mock(DadosDeClienteNoBancoCentral::class.java)
    }

    @Factory
    class ClientRegistrarChavePix  {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerRegistraGrpcServiceGrpc.KeymanagerRegistraGrpcServiceBlockingStub? {
            return KeymanagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

}
