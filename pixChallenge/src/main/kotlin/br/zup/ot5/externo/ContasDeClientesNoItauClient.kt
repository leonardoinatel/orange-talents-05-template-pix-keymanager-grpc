package br.zup.ot5.externo

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:9091")
interface ContasDeClientesNoItauClient {

    @Get("/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun buscaContaPorTipo(@PathVariable idCliente: String, @QueryValue tipo: String) : HttpResponse<DadosdaContaResponse>
}