package br.zup.ot5.pix.registra

import br.zup.ot5.externo.ContasDeClientesNoItauClient
import br.zup.ot5.externo.CreatedPixRequest
import br.zup.ot5.externo.DadosDeClienteNoBancoCentral
import br.zup.ot5.shared.exception.ChavePixExistenteException
import br.zup.ot5.shared.exception.ChavePixNaoEncontradaException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val itauClient: ContasDeClientesNoItauClient,
    @Inject val bcbClient: DadosDeClienteNoBancoCentral
) {

    private val Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun cadastra(@Valid novaChave : NovaChavePix) : ChavePix {
        if(chavePixRepository.existsByChave(novaChave.chave))
            throw ChavePixExistenteException("Chave Pix - ${novaChave.chave} - já cadastrada no sistema")

        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!, novaChave.tipoConta!!.name)
        val conta = response.body()?.toModel() ?: throw ChavePixNaoEncontradaException("Cliente não encontrado")

        val chave = novaChave.toModel(conta)

        chavePixRepository.save(chave)

        val bcbRequest = CreatedPixRequest.of(chave)

        val bcbResponse = bcbClient.cadastrar(bcbRequest)
        Logger.info("Status BCB ${bcbResponse.status.toString()}")
        if(bcbResponse.status != HttpStatus.CREATED)
            throw IllegalStateException("Erro ao registrar chave PIX no BCB")
        
        return chave
    }
}