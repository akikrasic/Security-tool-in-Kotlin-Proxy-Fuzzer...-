package srb.akikrasic.forma.paneli

import srb.akikrasic.forma.Forma
import srb.akikrasic.logika.FazerLogika
import srb.akikrasic.podaci.ucitavanjepodatakaforme.PodaciFazerPanel
import srb.akikrasic.podaci.ucitavanjepodatakaforme.PodaciZaUcitavanjeNaPanele
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.io.File
import javax.swing.*

class MojFazerPanel(val forma: Forma): MojeSlanjeZahtevaPanel() {
    var dugmeUcitajteFajl = JButton("Учитајте фајл")
    var odabraniFajl = File("")
    val nisteOdabraliFajlNatpis = "Нисте одабрали фајл"
    val labelaOdabraniFajl = JLabel(nisteOdabraliFajlNatpis)

    val fazerOpcijePanel = FazerOpcijePanel(odgovorSveArea)

    var pokrenutKontekst = false
    var fazerLogika: FazerLogika? = null


    fun fajlNijePrazan () = odabraniFajl.path !=""


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

    override fun posaljiteDugmeAkcija() {
        if(pokrenutKontekst){
            zaustaviteSlanje()
            pokrenutKontekst = false
        }
        else{
            zapocniteSlanje()
            pokrenutKontekst = true
        }
    }
    fun zaustaviteSlanje(){
        dugmePosaljite.text="Покрените"
        odabraniFajl = File(odabraniFajl.absolutePath)
        fazerLogika?.ugasiteSveIInicijalizujte()
        println()
        println("ZAUSTAVITE")
        println()
    }

    fun zapocniteSlanje(){

        if(fajlNijePrazan()) {
            zapocniteSlanjeOsveziteFormu()
            fazerLogika = FazerLogika(vratiteUrl(),
                metodaKombo.selectedItem?.toString()?:"",
             vratiteHedere(),
            vratiteTeloZahteva(),
                odabraniFajl,
                fazerOpcijePanel.brojNitiVratiteVrednost(),
                 fazerOpcijePanel.brojacZaPocetakVratiteVrednost(),
                fazerOpcijePanel.uspesniStringoviVratiteString(),
                fazerOpcijePanel.ponavljanjeStringVratiteString(),
                this)

            fazerLogika?.pokreniteUcitavanjeIzFajla()
            fazerLogika?.pokreniteCitanjeIzRedaiSlanje()

        }
    }
    fun zapocniteSlanjeOsveziteFormu(){
        odgovorSveArea.text=""
        dugmePosaljite.text = "Зауставите"
    }

    override fun sacuvajtePodatke() {
        PodaciZaUcitavanjeNaPanele.postaviteMojFazerPanel(izvucitePodatkeZaFazer())
    }

    fun izvucitePodatkeZaFazer() = PodaciFazerPanel(
        vratiteUrl(),
        metodaKombo.selectedItem?.toString() ?: "",
        vratiteHedere(),
        vratiteTeloZahteva(),
        fazerOpcijePanel.brojNitiVratiteVrednost(),
        fazerOpcijePanel.uspesniStringoviVratiteString(),
        fazerOpcijePanel.ponavljanjeStringVratiteString(),
        fazerOpcijePanel.brojacZaPocetakVratiteVrednost()
    )

    override fun ucitajtePodatkeUFormu() {
        val podaci = PodaciZaUcitavanjeNaPanele.podaciSviPaneli.mojFazerPanel
        SwingUtilities.invokeLater {
            this.modelTabeleHederi.lista.clear()
            modelTabeleHederi.lista.addAll(podaci.hederi)
            //TODO dodaj jos jedan heder ako treba
            urlPolje.text = podaci.url
            zahtevTeloArea.text = podaci.telo
            metodaKombo.selectedItem = podaci.metoda
            fazerOpcijePanel.postaviteBrojNiti(podaci.brojNiti)
            fazerOpcijePanel.postaviteUspesanString(podaci.uspesanString)
            fazerOpcijePanel.postaviteZaPonavljanjeString(podaci.ponavljanjeString)
            fazerOpcijePanel.postaviteBrojacZaPocetak(podaci.brojacZaPocetak)
        }
    }


    fun postaviteTekstUOdgovorSveAreu(tekst:String){
        SwingUtilities.invokeLater {
            odgovorSveArea.append(tekst)
        }
    }


    override fun napraviteDeoDesno(): JComponent  = fazerOpcijePanel


}