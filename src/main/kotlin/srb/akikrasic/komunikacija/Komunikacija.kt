package srb.akikrasic.komunikacija

import kotlinx.coroutines.channels.Channel
import srb.akikrasic.ucitavanjeWebSocketa.WebSoketPoruka

object Komunikacija {

    val kanalZaKomunikaciju=Channel<KomunikacijaPodaci>(20)
    val kanalZaKomunikacijuWebSoket = Channel<WebSoketPoruka>(20)
    val kanalZaKomunikacijuMojPonavljac =  Channel<String>(10000)
}