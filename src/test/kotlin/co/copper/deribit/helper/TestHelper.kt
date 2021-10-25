package co.copper.deribit.helper

import java.nio.charset.StandardCharsets

object TestHelper {

    fun apiResponseResourceAsText(fileName: String): String {
        return javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")!!
            .reader(StandardCharsets.UTF_8)
            .readText()
    }

}