package srb.akikrasic.podaci.ucitavanjepodatakaforme

import srb.akikrasic.podaci.HederIVrednost

data class PodaciPojedinacniPanel(val url:String="", val metoda:String="", val hederi:MutableList<HederIVrednost> =mutableListOf(HederIVrednost(),HederIVrednost(),HederIVrednost()), val telo:String="")