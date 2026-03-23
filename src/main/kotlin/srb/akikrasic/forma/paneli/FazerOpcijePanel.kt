package srb.akikrasic.forma.paneli

import srb.akikrasic.korisno.GuiKorisno
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField

class FazerOpcijePanel(val odgovorSveArea: JTextArea): JPanel() {



    fun postaviteUspesanString(uspesanString:String){
        uspesanStringTextField.text=uspesanString
    }

    fun postaviteZaPonavljanjeString(ponavljanjeString:String){
        zaPonavljanjeStringTextField.text=ponavljanjeString
    }

    fun postaviteBrojNiti(brojNiti:Int){
        brojNitiTextField.text=brojNiti.toString()
    }

    fun postaviteBrojacZaPocetak(brojacZaPocetak:Int){
        brojacZaPocetakTextField.text=brojacZaPocetak.toString()
    }

    private fun pretvoriteStringUListu(s:String) = s.trim().split(",").toList()

    fun uspesniStringoviVratiteListu() = pretvoriteStringUListu(uspesanStringTextField.text)
    fun ponavljanjeStringoviVratiteListu() = pretvoriteStringUListu(zaPonavljanjeStringTextField.text)
    fun brojNitiVratiteVrednost() = brojNitiTextField.text.trim().toInt()
    fun brojacZaPocetakVratiteVrednost() = brojacZaPocetakTextField.text.trim().toInt()
    fun uspesniStringoviVratiteString() = uspesanStringTextField.text.trim()
    fun ponavljanjeStringVratiteString() = zaPonavljanjeStringTextField.text.trim()



    val brojNitiLabela = JLabel("Број нити")
    val brojNitiTextField = JTextField("20")


    val brojacZaPocetakLabela = JLabel("Број редова за прескакање (старт)")
    val brojacZaPocetakTextField = JTextField("0")

    val uspesanStringLabela = JLabel("Успешно је пронађен ако одговор или статус садрже, одвојите запетом више вредности")
    val zaPonavljanjeStringLabela = JLabel("Понављајте ако одговор и статус садрже, одвојите запетом више вредности")

    val uspesanStringTextField = JTextField()
    val zaPonavljanjeStringTextField = JTextField()



    init{
        layout = GridBagLayout()

        val c = GuiKorisno.napraviteGridBagConstraints()
        c.weightx=1.0
        c.weighty=0.01
        val lista = listOf(brojNitiLabela, brojNitiTextField,
            uspesanStringLabela, uspesanStringTextField,
            zaPonavljanjeStringLabela, zaPonavljanjeStringTextField,
            brojacZaPocetakLabela, brojacZaPocetakTextField)
        for ( el in lista){
            add(el, c)
            c.gridy++
        }
        c.weighty = 0.92
        add(JScrollPane(odgovorSveArea), c)

    }
}