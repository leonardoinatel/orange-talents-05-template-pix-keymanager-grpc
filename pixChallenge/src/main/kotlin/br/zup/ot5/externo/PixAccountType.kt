package br.zup.ot5.externo

import br.zup.ot5.pix.grpcenum.TipoContaEnum

enum class PixAccountType {
    CACC,
    SVGS;
    // Tipo de conta (CACC=Conta Corrente; SVGS=Conta Poupança)
    companion object{
        fun by(domainType: TipoContaEnum):PixAccountType{
            return when(domainType) {
                TipoContaEnum.CONTA_CORRENTE -> CACC
                TipoContaEnum.CONTA_POUPANCA -> SVGS
            }
        }
    }
}