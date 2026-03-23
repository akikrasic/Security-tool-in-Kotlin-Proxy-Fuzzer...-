package srb.akikrasic.forma

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import srb.akikrasic.forma.paneli.HttpKomunikacijaPanel
import srb.akikrasic.forma.paneli.MojPanel
import srb.akikrasic.forma.paneli.MojFazerPanel
import srb.akikrasic.forma.paneli.MojeSlanjeZahtevaPanel
import srb.akikrasic.forma.paneli.PrenosInformacijaZaPretragu
import srb.akikrasic.forma.paneli.WSPanel
import srb.akikrasic.komunikacija.Komunikacija
import srb.akikrasic.komunikacija.KomunikacijaPodaci
import srb.akikrasic.ucitavanjeWebSocketa.WebSoketPoruka
import srb.akikrasic.ucitavanjezahtevaiodgovora.Zahtev
import java.awt.GridBagLayout
import java.awt.event.WindowEvent
import java.awt.event.WindowListener
import java.util.concurrent.Executors
import javax.swing.*

class Forma : JFrame() {

    var httpKomunikacijaPanel = HttpKomunikacijaPanel(this)

    var wsPanel = WSPanel()
    var mojTrenutniPanel = MojPanel()

    fun osnovneOperacije() {
        this.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        val velicinaEkrana = toolkit.screenSize
        setSize(velicinaEkrana.width, velicinaEkrana.height)
        isVisible = true
    }

    init {
        GlobalScope.launch(Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()) {
            while (true) {
                dodajteUFormu(Komunikacija.kanalZaKomunikaciju.receive())
            }
        }
        GlobalScope.launch(Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher()) {
            while (true) {
                dodajteUFormuWebSoket(Komunikacija.kanalZaKomunikacijuWebSoket.receive())
            }
        }
        osnovneOperacije()
        val layout = GridBagLayout()
        this.contentPane.layout = layout
        this.contentPane = httpKomunikacijaPanel

        val bar = JMenuBar()
        val glavniMeni = JMenu("Мени")
        val httpMeni = JMenuItem("Хттп саобраћај")
        val wsMeni = JMenuItem("Веб сокит")
        val mojeSlanjeZahteva = JMenuItem("Моје слање захтева")
        val mojPonavljac  = JMenuItem("Мој понављач")
        bar.add(glavniMeni)
        dodajteActionListenerNaMenuItemiNamestiteContentPanel(glavniMeni, httpMeni){
            HttpKomunikacijaPanel(this)
        }
        dodajteActionListenerNaMenuItemiNamestiteContentPanel(glavniMeni, wsMeni){
            WSPanel()
        }

        dodajteActionListenerNaMenuItemiNamestiteContentPanel(glavniMeni, mojeSlanjeZahteva){
            MojeSlanjeZahtevaPanel()
        }
        dodajteActionListenerNaMenuItemiNamestiteContentPanel(glavniMeni, mojPonavljac){
            MojFazerPanel(this)
        }
        this.jMenuBar = bar

        this.addWindowListener(object: WindowListener {
            override fun windowOpened(e: WindowEvent?) {

            }

            override fun windowClosing(e: WindowEvent?) {
                mojTrenutniPanel.ugasilaSeForma()
            }
            override fun windowClosed(e: WindowEvent?) {

            }

            override fun windowIconified(e: WindowEvent?) {

            }

            override fun windowDeiconified(e: WindowEvent?) {

            }

            override fun windowActivated(e: WindowEvent?) {

            }

            override fun windowDeactivated(e: WindowEvent?) {

            }
        })

    }
    fun dodajteActionListenerNaMenuItemiNamestiteContentPanel(glavniMeni:JMenu, menuItem: JMenuItem, fja:()-> MojPanel){
        glavniMeni.add(menuItem)
        menuItem.addActionListener {
            mojTrenutniPanel = fja()
            this.contentPane = mojTrenutniPanel
            osveziteFormu()
        }

    }
    fun osveziteFormu(){
        this.revalidate()
        this.repaint()
    }

    fun dodajteUFormu(k: KomunikacijaPodaci) {
        httpKomunikacijaPanel.dodajteUFormu(k)
    }


    fun pretraga(zaPretragu: PrenosInformacijaZaPretragu) {
        httpKomunikacijaPanel.pretraga(zaPretragu)

    }

    suspend fun dodajteUFormuWebSoket(wsPoruka:WebSoketPoruka){
        wsPanel.dodajteUFormu(wsPoruka)
    }

    fun ucitajteZahtevUMojeSlanjeZahtevaPanel(host:String, zahtev:Zahtev){
        this.contentPane = MojeSlanjeZahtevaPanel(host, zahtev)
        osveziteFormu()
    }



}