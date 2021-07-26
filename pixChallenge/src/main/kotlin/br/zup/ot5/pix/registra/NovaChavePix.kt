package br.zup.ot5.pix.registra

import br.zup.ot5.pix.grpcenum.TipoChaveEnum
import br.zup.ot5.pix.grpcenum.TipoContaEnum
import br.zup.ot5.shared.anotations.ValidUUID
import br.zup.ot5.shared.anotations.ValidaChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaChavePix
@Introspected
class NovaChavePix(
    @field:NotBlank @ValidUUID val clienteId:String?,
    @field:NotNull val tipoChave: TipoChaveEnum?,
    @field:Size(max = 77) val chave:String?,
    @field:NotNull val tipoConta: TipoContaEnum?
) {

    fun toModel(conta: ContaAssociada): ChavePix {
        return ChavePix(
            clienteId,
            TipoChaveEnum.valueOf(tipoChave!!.name),
            if (tipoChave == TipoChaveEnum.RANDOM)
                UUID.randomUUID().toString()
            else chave,
            TipoContaEnum.valueOf(tipoConta!!.name),
            conta
            )
    }
}
