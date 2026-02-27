package srb.akikrasic.forma.paneli

import srb.akikrasic.forma.modelitabele.ModelTabeleHederi
import srb.akikrasic.korisno.GuiKorisno
import srb.akikrasic.podaci.HederIVrednost
import srb.akikrasic.podaci.ucitavanjepodatakaforme.PodaciPojedinacniPanel
import srb.akikrasic.podaci.ucitavanjepodatakaforme.PodaciZaUcitavanjeNaPanele
import srb.akikrasic.slanjehttpzahteva.SlanjeHttpZahteva
import srb.akikrasic.ucitavanjezahtevaiodgovora.Zahtev
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener


open class MojeSlanjeZahtevaPanel(val host:String = "", val zahtev: Zahtev = Zahtev()) : MojPanel(){


    val metodaKombo = JComboBox<String>()
    val urlPolje = JTextField()
    val tabelaHederi = JTable()
    val zahtevTeloArea = JTextArea()
    val odgovorSveArea = JTextArea()
    val dugmePosaljite = JButton("Пошаљите")

    val modelTabeleHederi = ModelTabeleHederi()


     fun izvucitePodatke() = PodaciPojedinacniPanel(
        vratiteUrl(),
        metodaKombo.selectedItem?.toString() ?: "",
        vratiteHedere(),
        vratiteTeloZahteva()
    )

    val slanjeZahteva = SlanjeHttpZahteva()

    init{
        rasporedjivanjePanela()
        dodajteListenerZaUklanjanjePanela()
        ucitajtePodatkeUFormu()
    }

    fun dodajteListenerZaUklanjanjePanela(){
        this.addAncestorListener(object: AncestorListener{
            override fun ancestorAdded(event: AncestorEvent?) {
            }
            override fun ancestorRemoved(event: AncestorEvent?) {
                sacuvajtePodatke()
            }

            override fun ancestorMoved(event: AncestorEvent?) {
            }
        })
    }

    open fun rasporedjivanjePanela(){
        listOf("GET", "POST", "PUT", "DELETE").forEach {
            metodaKombo.addItem(it)
        }

        layout = GridBagLayout()
        val c = GuiKorisno.napraviteGridBagConstraints()

        c.weightx = 1.0
        c.weighty =0.1

        val panelGore = napravitePanelGore()
        add(panelGore, c)


        c.gridx = 0
        c.gridy = 1
        c.weightx = 1.0
        c.weighty = 0.45
        c.gridheight = 1
        add(JScrollPane(tabelaHederi), c)
        c.gridy = 2
        c.weighty =0.45
        add(JScrollPane(zahtevTeloArea), c)

        c.gridx = 1
        c.gridy = 0
        c.weighty = 1.0
        c.gridheight = 3
        add(JScrollPane(odgovorSveArea), c)

        dugmePosaljite.addActionListener {
            posaljiteDugmeAkcija()
        }
        tabelaHederi.model = modelTabeleHederi

        tabelaHederi.addKeyListener( object: KeyListener {
            override fun keyTyped(e: KeyEvent?) {
                println(tabelaHederi.selectedRow)
            }

            override fun keyPressed(e: KeyEvent?) {

            }

            override fun keyReleased(e: KeyEvent?) {

            }

        })
        urlPolje.text = "https://"
        if( zahtev.url !=""){
            urlPolje.text += host+zahtev.url
        }

        metodaKombo.selectedItem = zahtev.metoda
        modelTabeleHederi.lista.clear()
        modelTabeleHederi.lista.addAll(zahtev.hederi.mapaOriginalnihHedera.map{HederIVrednost(it.key, it.value)})
        modelTabeleHederi.lista.add(HederIVrednost())
        zahtevTeloArea.text = String(zahtev.telo)
    }

    fun napravitePanelGore():JPanel{
        val panelGore = JPanel()
        panelGore.layout= GridBagLayout()

        val ogranicenjaZaPanel = GuiKorisno.napraviteGridBagConstraints()
        ogranicenjaZaPanel.fill = GridBagConstraints.HORIZONTAL
        ogranicenjaZaPanel.weightx = 0.3
        panelGore.add(metodaKombo, ogranicenjaZaPanel)
        ogranicenjaZaPanel.gridx =1
        ogranicenjaZaPanel.weightx = 0.7
        panelGore.add(urlPolje,ogranicenjaZaPanel)
        panelGoreDonjiDeo(panelGore,ogranicenjaZaPanel)
        return panelGore
    }

    open fun panelGoreDonjiDeo(panelGore:JPanel, ogranicenjaZaPanel: GridBagConstraints){
        ogranicenjaZaPanel.gridy = 1
        ogranicenjaZaPanel.fill = GridBagConstraints.NONE
        panelGore.add(dugmePosaljite, ogranicenjaZaPanel)
    }

    open fun posaljiteDugmeAkcija(){
            posaljiteZahtev()
    }

    open fun posaljiteZahtev(){
        odgovorSveArea.text = slanjeZahteva.slanjeZahteva(vratiteUrl(), metodaKombo.selectedItem?.toString()?:"", vratiteHedere(),vratiteTeloZahteva())
    }



    fun vratiteTeloZahteva()=obradaStringa( obradaStringa(zahtevTeloArea.text))
    fun vratiteUrl() = obradaStringa(urlPolje.text)
    fun vratiteHedere() = modelTabeleHederi.lista.map{ HederIVrednost(obradaStringa(it.headerNaziv), obradaStringa(it.headerVrednost)) }.toMutableList()
    open fun obradaStringa(s:String) = s.trim()


    override fun sacuvajtePodatke() {
        PodaciZaUcitavanjeNaPanele.postaviteMojeSlanjeZahtevaPanel(izvucitePodatke())
    }

    override fun ucitajtePodatkeUFormu() {
        val podaci = vratitePodatkeZaUcitavanjeUFormu()
        SwingUtilities.invokeLater {
            this.modelTabeleHederi.lista.clear()
            modelTabeleHederi.lista.addAll(podaci.hederi)
            //TODO dodaj jos jedan heder ako treba
            urlPolje.text = podaci.url
            zahtevTeloArea.text = podaci.telo
            metodaKombo.selectedItem = podaci.metoda
        }
    }

}