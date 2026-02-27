package srb.akikrasic.podaci.ucitavanjepodatakaforme

import com.google.gson.Gson
import org.apache.commons.codec.binary.Base64
import srb.akikrasic.podaci.HederIVrednost
import java.io.File

object PodaciZaUcitavanjeNaPanele {
    val gson = Gson()
    val fajl = File("podaciZaUcitavanjeNaForme.json")
    var podaciSviPaneli = PodaciSviPaneli()

    init {
        ucitajtePodatkeIzFajla()
        podaciSviPaneli.mojPonavljacPanel.hederi.add(HederIVrednost())
        podaciSviPaneli.mojeSlanjeZahtevaPanel.hederi.add(HederIVrednost())
    }

    fun ucitajtePodatkeIzFajla() {
        if (fajl.exists()) {
            val podaciString = fajl.readText()
            val podaciEnkodovani = gson.fromJson(podaciString, PodaciSviPaneli::class.java)

            podaciSviPaneli = dekodujtePodaci(podaciEnkodovani)
        }
    }

    fun sacuvajtePodatkeUFajl() {
        val podaciZaSnimanje = enkodujtePodaci(podaciSviPaneli)
        fajl.writeText(gson.toJson(podaciZaSnimanje))

    }

    fun postaviteMojPonavljacPanel(podaciPojedinacniPanel: PodaciPojedinacniPanel) {
        podaciSviPaneli = PodaciSviPaneli(podaciSviPaneli.mojPonavljacPanel, podaciPojedinacniPanel)
        sacuvajtePodatkeUFajl()
    }

    fun postaviteMojeSlanjeZahtevaPanel(pojedinacniPanel: PodaciPojedinacniPanel) {
        podaciSviPaneli = PodaciSviPaneli(pojedinacniPanel, podaciSviPaneli.mojPonavljacPanel)
        sacuvajtePodatkeUFajl()
    }

    fun enkodujtePodaci(podaciSvi: PodaciSviPaneli) =
        PodaciSviPaneli(
            enkodujtePodaciPojedinacniPanel(podaciSvi.mojeSlanjeZahtevaPanel),
            enkodujtePodaciPojedinacniPanel(podaciSvi.mojPonavljacPanel)
        )

    fun dekodujtePodaci(podaciSvi: PodaciSviPaneli)=
        PodaciSviPaneli(
            dekodujtePodaciPojedinacniPanel(podaciSvi.mojeSlanjeZahtevaPanel),
            dekodujtePodaciPojedinacniPanel(podaciSvi.mojPonavljacPanel)
        )

    fun enkodujtePodaciPojedinacniPanel(podaci: PodaciPojedinacniPanel): PodaciPojedinacniPanel =
        PodaciPojedinacniPanel(
            enkodujte(podaci.url),
            podaci.metoda,
            podaci.hederi.map { HederIVrednost(enkodujte(it.headerNaziv), enkodujte(it.headerVrednost)) }
                .toMutableList(),
            enkodujte(podaci.telo)
        )

    fun dekodujtePodaciPojedinacniPanel(podaci: PodaciPojedinacniPanel): PodaciPojedinacniPanel =
        PodaciPojedinacniPanel(
            dekodujte(podaci.url),
            podaci.metoda,
            podaci.hederi.map { HederIVrednost(dekodujte(it.headerNaziv), dekodujte(it.headerVrednost)) }
                .toMutableList(),
            dekodujte(podaci.telo)
        )


    fun dekodujte(s: String) = String(Base64.decodeBase64(s.toByteArray()))
    fun enkodujte(s: String) = Base64.encodeBase64String(s.encodeToByteArray())


}