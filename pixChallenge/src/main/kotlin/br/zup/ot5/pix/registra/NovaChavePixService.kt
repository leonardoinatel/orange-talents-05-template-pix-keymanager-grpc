package br.zup.ot5.pix.registra

import br.zup.ot5.externo.ContasDeClientesNoItauClient
import br.zup.ot5.shared.exception.ChavePixExistenteException
import br.zup.ot5.shared.exception.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class NovaChavePixService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val itauClient: ContasDeClientesNoItauClient
) {

    @Transactional
    fun cadastra(@Valid novaChave : NovaChavePix) : ChavePix {
        val response = itauClient.buscaContaPorTipo(novaChave.clienteId!!, novaChave.tipoConta!!.name)
        val conta = response.body()?.toModel() ?: throw ChavePixNaoEncontradaException("Cliente não encontrado")

        if(chavePixRepository.existsByChave(novaChave.chave))
        throw ChavePixExistenteException("Chave Pix - ${novaChave.chave} - já cadastrada no sistema")

        val chave = novaChave.toModel(conta)

        chavePixRepository.save(chave)

        return chave
    }
}