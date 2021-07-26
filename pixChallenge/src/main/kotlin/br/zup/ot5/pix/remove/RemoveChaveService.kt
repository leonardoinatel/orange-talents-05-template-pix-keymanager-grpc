package br.zup.ot5.pix.remove

import br.zup.ot5.externo.DadosDeClienteNoBancoCentral
import br.zup.ot5.externo.DeletePixRequest
import br.zup.ot5.pix.registra.ChavePixRepository
import br.zup.ot5.shared.anotations.ValidUUID
import br.zup.ot5.shared.exception.ChavePixNaoEncontradaException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChaveService(@Inject val chavePixRepository: ChavePixRepository, @Inject val bcbClient: DadosDeClienteNoBancoCentral) {

    @Transactional
    fun remove(
        @NotBlank @ValidUUID idCliente: String?,
        @NotBlank @ValidUUID idPix: String?
    ) {
        val chave = chavePixRepository.findByIdAndClienteId(idPix, idCliente)
            .orElseThrow {
                ChavePixNaoEncontradaException("Chave pix não encontrada ou nao pertence ao cliente")
            }

        val deleteRequest = DeletePixRequest(chave.chave)
        val deleteResponse = bcbClient.deletar(chave.chave, deleteRequest)

        if (!deleteResponse.status.equals(HttpStatus.OK))
            throw IllegalStateException("Erro ao remover chave pix no BCB ${idPix} - ${idCliente}")

        chavePixRepository.delete(chave)
    }
}
