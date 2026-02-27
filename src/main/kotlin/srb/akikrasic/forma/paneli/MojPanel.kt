package srb.akikrasic.forma.paneli

import srb.akikrasic.podaci.ucitavanjepodatakaforme.PodaciZaUcitavanjeNaPanele
import javax.swing.JPanel

open class MojPanel : JPanel() {
    open fun ugasilaSeForma(){
        sacuvajtePodatke()
    }

    open fun sacuvajtePodatke(){

    }

    open fun ucitajtePodatkeUFormu(){

    }

    open fun vratitePodatkeZaUcitavanjeUFormu() = PodaciZaUcitavanjeNaPanele.podaciSviPaneli.mojeSlanjeZahtevaPanel

}