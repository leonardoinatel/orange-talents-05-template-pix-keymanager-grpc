package br.zup.ot5.pix.registra

import javax.persistence.Column
import javax.persistence.Embeddable
import javax.validation.constraints.Size

@Embeddable
class ContaAssociada(
    @Column(nullable = false) val InstituicaoNome: String,
    @Column(nullable = false) val TitularNome: String,
    @field:Size(max = 11) @Column(nullable = false) val CpfTitular: String,
    @Column(nullable = false) val agencia: String,
    @Column(nullable = false) val numero: String
) {

    override fun toString(): String {
        return "ContaAssociada(InstituicaoNome='$InstituicaoNome', TitularNome='$TitularNome', CpfTitular='$CpfTitular', agencia='$agencia', numero='$numero')"
    }
}
