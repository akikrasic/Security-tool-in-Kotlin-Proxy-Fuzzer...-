package srb.akikrasic.slanjehttpzahteva

import srb.akikrasic.podaci.HederIVrednost
import srb.akikrasic.ucitavanjezahtevaiodgovora.konstante.HederiNazivi
import srb.akikrasic.ucitavanjezahtevaiodgovora.konstante.HederiVrednosti
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class SlanjeHttpZahteva {
    val zabranjeniHederi = setOf("Host", "Content-Length", "Connection", "")

    fun slanjeZahteva(url:String, metoda:String, hederi: List<HederIVrednost>, body:String ):String{
        try {
        val client = HttpClient.newHttpClient()

        val zahtevIzgradjivac = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method(metoda, HttpRequest.BodyPublishers.ofString(body))

        hederi.filter{!(it.headerNaziv in zabranjeniHederi)}.forEach {
            zahtevIzgradjivac.header(it.headerNaziv, it.headerVrednost)
        }

            val odgovor = client.send(zahtevIzgradjivac.build(), HttpResponse.BodyHandlers.ofString())
            client.close()
            return pretvaranjeOdgovoraUString(odgovor)
        }
        catch(e: Exception){
            e.printStackTrace()
            return "Дошло је до грешке"
        }
    }

    fun pretvaranjeOdgovoraUString(response:HttpResponse<String>) = """
        ${response.headers().map().map {
        " ${it.key}: ${it.value}"
    }.joinToString("\n")}
        
        ${response.body()}
        
        """.trimIndent()

}