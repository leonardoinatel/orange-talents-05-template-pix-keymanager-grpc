package br.zup.ot5.pix.registra

import br.zup.ot5.RegistraChavePixRequest
import br.zup.ot5.pix.grpcenum.TipoChaveEnum
import br.zup.ot5.TipoChave
import br.zup.ot5.TipoConta
import br.zup.ot5.pix.grpcenum.TipoContaEnum

fun RegistraChavePixRequest.toModel() : NovaChavePix {
    return NovaChavePix(
        idCliente,
        when(tipoChave) {
            TipoChave.CHAVE_DESCONHECIDA -> null
            else -> TipoChaveEnum.valueOf(tipoChave.name)
        },
        chave,
        when(tipoConta) {
            TipoConta.CONTA_DESCONHECIDA -> null
            else -> TipoContaEnum.valueOf(tipoConta.name)
        }
    )
}