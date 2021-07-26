package br.zup.ot5.externo

import br.zup.ot5.pix.grpcenum.TipoChaveEnum
import br.zup.ot5.pix.registra.ChavePix
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

import java.time.LocalDateTime

@Client("http://localhost:8082")
interface DadosDeClienteNoBancoCentral {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun cadastrar(@Body createdPixRequest: CreatedPixRequest) : HttpResponse<CreatedPixResponse>

    @Delete("/api/v1/pix/keys/{key}")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun deletar(@PathVariable key: String?, @Body delePixRequest: DeletePixRequest) : HttpResponse<DeletePixResponse>
}

data class CreatedPixRequest(
    val keyType: TipoChaveEnum,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    companion object {
        fun of(chave: ChavePix): CreatedPixRequest {
            return CreatedPixRequest(
                keyType = TipoChaveEnum.valueOf(chave.tipoChave.toString()),
                key = chave.chave,
                bankAccount = BankAccount(
                    participant = "60701190",
                    branch = chave.conta.agencia,
                    accountNumber = chave.conta.numero,
                    accountType = PixAccountType.by(chave.tipoConta)
                ),
                owner = Owner(
                    type = "LEGAL_PERSON",
                    name = chave.conta.TitularNome,
                    taxIdNumber = chave.conta.CpfTitular
                )
                )
        }
    }
}

data class CreatedPixResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime
)

data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: PixAccountType
)

data class Owner(
    val type: String,
    val name: String,
    val taxIdNumber: String
)


