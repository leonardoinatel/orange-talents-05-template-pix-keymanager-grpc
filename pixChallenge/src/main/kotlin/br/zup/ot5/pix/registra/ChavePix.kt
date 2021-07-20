package br.zup.ot5.pix.registra

import br.zup.ot5.pix.grpcenum.TipoChaveEnum
import br.zup.ot5.pix.grpcenum.TipoContaEnum
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class ChavePix(
    @Column(nullable = false)
    val clienteId: String?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoChave: TipoChaveEnum,

    @Column(nullable = false, unique = true)
    val chave: String?,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoContaEnum,

    @Embedded
    @Column(nullable = false)
    val conta: ContaAssociada
) {

    @Id
    lateinit var id: String

    @Column(nullable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()

    @PrePersist
    fun prePersiste() {
        this.id = UUID.randomUUID().toString()
    }
}
