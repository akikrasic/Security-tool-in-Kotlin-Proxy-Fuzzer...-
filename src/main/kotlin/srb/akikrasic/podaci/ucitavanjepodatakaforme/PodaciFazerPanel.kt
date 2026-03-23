package srb.akikrasic.podaci.ucitavanjepodatakaforme

import srb.akikrasic.podaci.HederIVrednost

data class PodaciFazerPanel(val url:String="", val metoda:String="", val hederi:MutableList<HederIVrednost> =mutableListOf(HederIVrednost(),HederIVrednost(),HederIVrednost()), val telo:String="",val brojNiti:Int = 20,val uspesanString:String="", val ponavljanjeString:String="", val brojacZaPocetak:Int = 0 )
