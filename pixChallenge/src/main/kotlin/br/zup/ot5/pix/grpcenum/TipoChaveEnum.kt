package br.zup.ot5.pix.grpcenum

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChaveEnum {

    CPF {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false
            if(!chave.matches("[0-9]+".toRegex())) return false
            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },

    EMAIL {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },

    RANDOM {
        override fun valida(chave: String?) = chave.isNullOrBlank()
    },

    PHONE {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    };

    abstract fun valida(chave: String?) : Boolean
}