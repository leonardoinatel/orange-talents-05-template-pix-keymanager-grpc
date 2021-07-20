package br.zup.ot5.externo

import br.zup.ot5.pix.registra.ContaAssociada as ContaAssociada

data class DadosdaContaResponse(
    val tipo : String,
    val instituicao : Instituicao,
    val agencia : String,
    val numero : String,
    val titular : Titular
) {
    fun toModel(): ContaAssociada {
        return ContaAssociada(
            this.instituicao.nome,
            this.titular.nome,
            this.titular.cpf,
            this.agencia,
            this.numero
        )
    }
}

data class Titular (val id : String, val nome : String, val cpf : String)
data class Instituicao(val nome : String, val ispb : String)
