package srb.akikrasic.podaci.ucitavanjepodatakaforme

import org.apache.commons.codec.binary.Base64
import srb.akikrasic.podaci.HederIVrednost

data class PodaciPojedinacniPanel(val url:String="", val metoda:String="", val hederi:MutableList<HederIVrednost> =mutableListOf(HederIVrednost(),HederIVrednost(),HederIVrednost()), val telo:String="")