package br.zup.ot5.pix.remove

import br.zup.ot5.pix.registra.ChavePixRepository
import br.zup.ot5.shared.anotations.ValidUUID
import br.zup.ot5.shared.exception.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChaveService(@Inject val chavePixRepository: ChavePixRepository) {

    @Transactional
    fun remove(
        @NotBlank @ValidUUID idCliente: String?,
        @NotBlank @ValidUUID idPix: String?
    ) {
        chavePixRepository.findByIdAndClienteId(idPix, idCliente)
            .map { chavePixRepository.deleteById(idPix) }
            .orElseThrow {
                ChavePixNaoEncontradaException("Chave pix não encontrada ou nao pertence ao cliente")
            }
        }
}