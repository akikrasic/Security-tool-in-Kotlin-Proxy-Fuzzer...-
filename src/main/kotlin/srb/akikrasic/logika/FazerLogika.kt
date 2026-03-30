package srb.akikrasic.logika

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import srb.akikrasic.forma.paneli.MojFazerPanel
import srb.akikrasic.komunikacija.Komunikacija
import srb.akikrasic.podaci.HederIVrednost
import srb.akikrasic.slanjehttpzahteva.SlanjeHttpZahteva
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class FazerLogika(val url:String,
                  val metoda:String,
                  val hederi:List<HederIVrednost>,
                  val telo:String,
                  val odabraniFajl: File,
                  val brojNiti :Int,
                  val brojacZaPocetakBrojanjaIzFajla:Int,
                  val uspesniStringovi:String,
                  val ponavljanjeStringovi :String,
                  val fazerPanel: MojFazerPanel
                  ) {

    var executor1 = Executors.newFixedThreadPool(brojNiti)//.newVirtualThreadPerTaskExecutor()//Executors.newVirtualThreadPerTaskExecutor()// Executors.newFixedThreadPool(20)
    var executor2 = Executors.newFixedThreadPool(brojNiti)//Executors.newVirtualThreadPerTaskExecutor()
    var korutineOpseg = executor1.asCoroutineDispatcher()
    var korutineOpseg2 = executor2.asCoroutineDispatcher()
    var ioOpseg = CoroutineScope(korutineOpseg)
    var drugiOpseg = CoroutineScope(Dispatchers.Default)
    var treciOpseg = CoroutineScope(korutineOpseg2)

    val param = "#param"

    val slanjeZahteva = SlanjeHttpZahteva()

    val brojac = AtomicInteger(0)
    val brojacZahtevaKojiSuPonovljeni = AtomicInteger(0)

    fun napraviteListuOdStringa(s:String) = s.split(",")

    val uspesniStringoviLista = napraviteListuOdStringa(uspesniStringovi)
    val ponavljanjeStringoviLista = napraviteListuOdStringa(ponavljanjeStringovi)

    fun proveriteDaLiRezultatSadrzi(rezultat:String, listaZaProveru:List<String>): Boolean{
        for(zaProveru in listaZaProveru){
            if( rezultat.contains(zaProveru)){
                return true
            }
        }
        return false
    }

    fun ugasiteSveIInicijalizujte(){
        treciOpseg.cancel()
        drugiOpseg.cancel()
        korutineOpseg.cancel()
        korutineOpseg2.cancel()
        ioOpseg.cancel()

        executor1 = Executors.newFixedThreadPool(brojNiti)//.newVirtualThreadPerTaskExecutor()//Executors.newVirtualThreadPerTaskExecutor()// Executors.newFixedThreadPool(20)
        executor2  = Executors.newFixedThreadPool(brojNiti)//newVirtualThreadPerTaskExecutor()
        korutineOpseg = executor1.asCoroutineDispatcher()
        korutineOpseg2 = executor2.asCoroutineDispatcher()

        drugiOpseg = CoroutineScope(Dispatchers.Default)
        ioOpseg = CoroutineScope(korutineOpseg)
        treciOpseg = CoroutineScope(korutineOpseg2)
    }

    fun pokreniteCitanjeIzRedaiSlanje(){

        drugiOpseg.launch {
            while (true) {
                ensureActive()
                val poruka = Komunikacija.kanalZaKomunikacijuMojPonavljac.receive()
                posaljitePorukuPutemHttpaIObraditeRezultat(url, metoda, hederi, telo, poruka)
                //}

            }
        }
    }
    fun pokreniteUcitavanjeIzFajla(){
        treciOpseg.launch {
            ensureActive()
            val brojacZaPocetak = AtomicInteger()
            odabraniFajl.forEachLine {
                ensureActive()
                //86000 za 100k
                //363000 za veliki
                if(brojacZaPocetak.get()>brojacZaPocetakBrojanjaIzFajla){//15000) {//86000
//                        println("Upaljen ti je brojac i krece od : ${brojacZaPocetak.get()}")
                    ioOpseg.launch { posaljitePorukuNaRedZaCitanje(it) }
                }
                brojacZaPocetak.incrementAndGet()

            }
        }
    }

    fun zamenite(s:String, zaZamenu:String) = s.replace(param,zaZamenu)
    suspend fun posaljitePorukuNaRedZaCitanje(poruka:String){
        Komunikacija.kanalZaKomunikacijuMojPonavljac.send(poruka.trim())
    }





    fun posaljitePorukuPutemHttpaIObraditeRezultat(url:String, metoda:String, hederi:List<HederIVrednost>, telo:String, poruka:String){
        ioOpseg.launch { //radelo je s treciOpseg
            println("${brojac.addAndGet(1)}. ${poruka}")
            val urlZamenjeni = zamenite(url, poruka)
            val hederiZamenjeni =  hederi.filter{it.headerNaziv!=""}
                .map { HederIVrednost(
                    zamenite(it.headerNaziv, poruka),
                    zamenite(it.headerVrednost, poruka)
                )

                }
            val teloZamenjeno = zamenite(telo, poruka)
            val rezultat = posaljiteZahtevIVratiteRezultat(urlZamenjeni, metoda, hederiZamenjeni, teloZamenjeno, poruka)
            if(!proveraDaLiJeUspesan(rezultat, poruka)){
                stampajteNeuspesan(urlZamenjeni, hederiZamenjeni, teloZamenjeno, rezultat)
            }

        }
    }

    fun posaljiteZahtevIVratiteRezultat(urlZamenjeni:String, metoda:String, hederiZamenjeni: List<HederIVrednost>, teloZamenjeno:String, poruka:String):String{
        var rezultat = ponavljanjeStringoviLista[0]// "503"
        while(proveriteDaLiRezultatSadrzi(rezultat, ponavljanjeStringoviLista)){// rezultat.contains("503")){
            if(rezultat!=ponavljanjeStringoviLista[0]){//"503"){
                println("503 rezultat po ${brojacZahtevaKojiSuPonovljeni.incrementAndGet()}. put  idemo da ponovimo poruku $poruka")

            }
            rezultat = slanjeZahteva.slanjeZahteva(
                urlZamenjeni,
                metoda,
                hederiZamenjeni,
                teloZamenjeno
            )
        }
        return rezultat
    }

    fun stampajteNeuspesan(urlZamenjeni: String, hederiZamenjeni: List<HederIVrednost>, teloZamenjeno: String, rezultat:String){
            println(
                """
                            ${urlZamenjeni}
                            ${hederiZamenjeni}
                            ${teloZamenjeno}
                           
                            Rezultat: 
                            $rezultat
                           
                        """.trimIndent()
            )
    }
    fun proveraDaLiJeUspesan(rezultat:String, poruka:String):Boolean{
        if( proveriteDaLiRezultatSadrzi(rezultat, uspesniStringoviLista)){// rezultat.contains("200")||rezultat.contains("201")|| rezultat.contains("FLAG")) {
            println("PRONADJEN JE ${poruka}")
            drugiOpseg.launch{
                fazerPanel.postaviteTekstUOdgovorSveAreu("успешно је прошао: $poruka  а резултат је: \n   $rezultat\n")
                println(rezultat)
                println("Шифра је ${poruka}")
                //System.exit(0)
                //ugasiteSveIInicijalizujte()

            }
            return true
        }
        return false
    }

}