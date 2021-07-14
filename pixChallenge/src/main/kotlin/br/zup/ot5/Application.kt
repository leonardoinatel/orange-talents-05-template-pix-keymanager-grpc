package br.zup.ot5

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.zup.ot5")
		.start()
}

