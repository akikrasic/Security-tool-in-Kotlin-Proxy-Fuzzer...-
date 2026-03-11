package srb.akikrasic.forma.paneli

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import srb.akikrasic.forma.Forma
import srb.akikrasic.komunikacija.Komunikacija
import srb.akikrasic.podaci.HederIVrednost
import srb.akikrasic.podaci.ucitavanjepodatakaforme.PodaciZaUcitavanjeNaPanele
import srb.akikrasic.ucitavanjezahtevaiodgovora.konstante.HederiNazivi
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.JButton
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel

class MojPonavljacPanel(val forma: Forma): MojeSlanjeZahtevaPanel() {
    var dugmeUcitajteFajl = JButton("Учитајте фајл")
    var odabraniFajl = File("")
    val nisteOdabraliFajlNatpis = "Нисте одабрали фајл"
    val labelaOdabraniFajl = JLabel(nisteOdabraliFajlNatpis)
    val dispatcher = Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()
    var kontekst = CoroutineScope(dispatcher)
    var pokrenutKontekst = false
    val param = "#param"


    fun fajlNijePrazan () = odabraniFajl.path !=""
    val brojac = AtomicInteger(0)
    val brojac503 = AtomicInteger(0)
    override fun rasporedjivanjePanela() {

    }

    override fun panelGoreDonjiDeo(panelGore: JPanel, ogranicenjaZaPanel: GridBagConstraints) {
        ogranicenjaZaPanel.gridx = 0
        ogranicenjaZaPanel.gridy= 1
        dugmeUcitajteFajl.addActionListener {
            val fileDialog = JFileChooser()
            fileDialog.currentDirectory = File("/home/aki/Downloads")
            fileDialog.fileSelectionMode = JFileChooser.FILES_ONLY
            val result = fileDialog.showOpenDialog(forma)
            if( result == JFileChooser.APPROVE_OPTION) {
                odabraniFajl = fileDialog.selectedFile
                labelaOdabraniFajl.text="Одабрали сте фајл: ${odabraniFajl.path}"
            }

        }
        ogranicenjaZaPanel.fill = GridBagConstraints.NONE
        val manjiGornjiPanel = JPanel(GridBagLayout())
        val cc = GridBagConstraints()
        manjiGornjiPanel.add(dugmeUcitajteFajl,cc)
        cc.gridy = 1
        cc.insets = Insets(5, 5, 5, 5)
        manjiGornjiPanel.add(labelaOdabraniFajl,cc)
        panelGore.add(manjiGornjiPanel, ogranicenjaZaPanel)
        ogranicenjaZaPanel.gridx = 1

        super.panelGoreDonjiDeo(panelGore, ogranicenjaZaPanel)
        //ucitajtePodatkeUFormu()
    }

    init{
        super.rasporedjivanjePanela()
    }

    val brojNiti = 20
    var executor1 = Executors.newFixedThreadPool(brojNiti)//.newVirtualThreadPerTaskExecutor()//Executors.newVirtualThreadPerTaskExecutor()// Executors.newFixedThreadPool(20)
    var executor2 = Executors.newFixedThreadPool(brojNiti)//Executors.newVirtualThreadPerTaskExecutor()
    var korutineOpseg = executor1.asCoroutineDispatcher()
    var korutineOpseg2 = executor2.asCoroutineDispatcher()
    var ioOpseg = CoroutineScope(korutineOpseg)
    var drugiOpseg = CoroutineScope(Dispatchers.Default)
    var treciOpseg = CoroutineScope(korutineOpseg2)

    override fun posaljiteDugmeAkcija() {
        if(pokrenutKontekst){
            zaustaviteSlanje()

        }
        else{
            zapocniteSlanje()
        }
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

        pokrenutKontekst = false
        drugiOpseg = CoroutineScope(Dispatchers.Default)
        ioOpseg = CoroutineScope(korutineOpseg)
        treciOpseg = CoroutineScope(korutineOpseg2)
    }
    fun zaustaviteSlanje(){
       ugasiteSveIInicijalizujte()

        dugmePosaljite.text="Покрените"
        odabraniFajl = File(odabraniFajl.absolutePath)
        println()
        println("ZAUSTAVITE")
        println()
    }

    fun zapocniteSlanje(){

        if(fajlNijePrazan()) {
            zapocniteSlanjeOsveziteFormu()
            pokrenutKontekst = true
            pokreniteUcitavanjeIzFajla()
            pokreniteCitanjeIzRedaiSlanje()

        }
    }
    fun zapocniteSlanjeOsveziteFormu(){
        odgovorSveArea.text=""
        dugmePosaljite.text = "Зауставите"
    }

    fun pokreniteCitanjeIzRedaiSlanje(){
        val url = vratiteUrl()
        val metoda = metodaKombo.selectedItem?.toString()?:""
        val hederi = vratiteHedere()
        val telo = vratiteTeloZahteva()
        drugiOpseg.launch {
            while (true) {
                ensureActive()
                val poruka = Komunikacija.kanalZaKomunikacijuMojPonavljac.receive()
                 posaljitePorukuPutemHttpaIObraditeRezultat(url, metoda, hederi, telo, poruka)
                //}

            }
        }
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
            proveraDaLiJeNeuspesan(urlZamenjeni, hederiZamenjeni, teloZamenjeno, rezultat)
            proveraDaLiJeUspesan(rezultat, poruka)

        }
    }

    fun proveraDaLiJeNeuspesan(urlZamenjeni: String, hederiZamenjeni: List<HederIVrednost>, teloZamenjeno: String, rezultat:String){
        if( rezultat.contains("401")) {
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
    }
    fun proveraDaLiJeUspesan(rezultat:String, poruka:String){
        if( rezultat.contains("200")||rezultat.contains("201")|| rezultat.contains("FLAG")) {
            println("PRONADJEN JE ${poruka}")
            drugiOpseg.launch{
                odgovorSveArea.append("успешно је прошао: $poruka  а резултат је: \n   $rezultat\n")
                println(rezultat)
                println("Шифра је ${poruka}")
                //System.exit(0)
                ugasiteSveIInicijalizujte()

            }}
    }
    fun pokreniteUcitavanjeIzFajla(){
        treciOpseg.launch {
            ensureActive()
            val brojacZaPocetak = AtomicInteger()
            odabraniFajl.forEachLine {
                ensureActive()
                //86000 za 100k
                //363000 za veliki
                    if(brojacZaPocetak.get()>8000) {//86000
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

    override fun sacuvajtePodatke() {
        PodaciZaUcitavanjeNaPanele.postaviteMojPonavljacPanel(izvucitePodatke())
    }

    override fun vratitePodatkeZaUcitavanjeUFormu() = PodaciZaUcitavanjeNaPanele.podaciSviPaneli.mojPonavljacPanel

     fun posaljiteZahtevIVratiteRezultat(urlZamenjeni:String, metoda:String, hederiZamenjeni: List<HederIVrednost>, teloZamenjeno:String, poruka:String):String{
        var rezultat = "503"
        while( rezultat.contains("503")){
            if(rezultat!="503"){
                println("503 rezultat po ${brojac503.incrementAndGet()}. put  idemo da ponovimo poruku $poruka")

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


}