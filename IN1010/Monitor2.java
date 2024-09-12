import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor2 {
    private SubsekvensRegister register;
    private Lock laas;
    private Condition ikkeTom; //Condition ikkeTom sjekker at registeret ikke er tomt
    private Condition ikkeFerdigLest; //sjekker om tråder er ferdig å lese fra fil
    private boolean ferdigLest = false; //Satt false til og begynne med
    private  int ANT_LESETRADER;



    public Monitor2 (){
        register = new SubsekvensRegister(); //Lager nytt register
        laas = new ReentrantLock(); //Lager ny renentrantlock
        ikkeTom = laas.newCondition(); //Sjekker om det er tomt for hashmaps
        ikkeFerdigLest = laas.newCondition();
        ANT_LESETRADER = 0;
    }

    //Les fil
    public HashMap<String,Subsekvens> lesImmunRepertoar(String fil){
        laas.lock(); //Låser slik at bare en tråd får tilgang av gangen
        try{
            return SubsekvensRegister.lesImmunRepertoar(fil); //Prøver å utføre metodekall
        } finally {
            laas.unlock(); //Låser opp metoden for neste tråd uansett om tråden bryter eller ikke
        }
    }

    //Flettemetoden
    public HashMap <String, Subsekvens> fletting(HashMap<String, Subsekvens> hashMap1, HashMap<String, Subsekvens> hashMap2) {
        laas.lock();
        try {
            return SubsekvensRegister.fletting(hashMap1, hashMap2);
        } finally {
            laas.unlock();
        }
    }

    // Hente subsekvensregister
    public ArrayList<HashMap<String, Subsekvens>> hentSubsekvensRegister(){
        laas.lock();
        try {
            return register.hentSubsekvensRegister();
        } finally {
            laas.unlock();
        }
    }

    //Sette inn subsekvensregister
    public void settInnSubsekvensRegister(HashMap<String, Subsekvens> hashMap){
        laas.lock();
        try{
            register.settInnSubsekvenser(hashMap);
        } finally {
            laas.unlock();
        }
    }

    //Antall hashmaps
    public int antallHashmaps(){
        laas.lock();
        try {
            return register.antallHashmaps();
        } finally {
            laas.unlock();
        }
    }

    // Ta ut gitt hashmap
    public HashMap<String, Subsekvens> taUt(int i){
        laas.lock();
        try {
            return register.taUt(i);
        } finally {
            laas.unlock();
        }
    }

    //Setter inn flettet
    public void settInnFlettet(HashMap<String, Subsekvens> flettet){
        laas.lock();
        try {
            register.settInnSubsekvenser(flettet);
            //FEIL HER NÅR JEG SKAL FJERNE TROR JEG
            //hentSubsekvensRegister().remove(1);
            //hentSubsekvensRegister().remove(0); // Fjern to første elementer

            if (antallHashmaps() >= 2) { //Sier i fra til tråder at det ikke er tomt lenger i hentTo()
                ikkeTom.signal();
            }
        } finally {
            laas.unlock();
        }

    }

    //Henter to hashmaps
    public ArrayList<HashMap<String, Subsekvens>> hentTo(){
        laas.lock();
        try{
            /*if (!ferdigLest){
                ikkeFerdigLest.await();
            }*/

            if (antallHashmaps() < 2) {
                ikkeTom.await();
            }


            ArrayList<HashMap<String , Subsekvens>> toHashMaps = new ArrayList<>();
            HashMap<String, Subsekvens> en = taUt(0);
            HashMap<String, Subsekvens> to = taUt(1);
            toHashMaps.add(en);
            toHashMaps.add(to);

            hentSubsekvensRegister().remove(1);
            hentSubsekvensRegister().remove(0); //Når jeg prøver å fjerne disse to så fungerer ikke koden i det hele tatt:(

            return toHashMaps;

        } catch (InterruptedException e){
            System.out.println("Traden ble avbrutt under kjoring av metoden hentTo().");
            e.printStackTrace();
            return null;
        } finally {
            laas.unlock();
        }
    }

    //Metoden for å finne flest forekomster
    public void mestForekomst(){
        laas.lock();
        try {

            Subsekvens mest = null;
            int forekomst = 0;

            HashMap<String, Subsekvens> siste = hentSubsekvensRegister().get(0);
            forekomst = 0;
            for (String key : siste.keySet()) {
                Subsekvens subsekvens = siste.get(key);
                if (subsekvens.hentAntall() > forekomst) {
                    mest = subsekvens;
                    forekomst = subsekvens.hentAntall();
                }

            }
            //hentSubsekvensRegister().remove(siste);


            System.out.println("Subsekvensen med flest forekomster er " + mest);
        }finally {
            laas.unlock();
        }
    }
    /*public void ferdigLest(){
        ferdigLest = true;
    }

    public int hentAntallFlettinger(){
        laas.lock();
        try{
            return ANT_LESETRADER;
        } finally {
            laas.unlock();
        }
    }

    public void minkAntallFlettinger(){
        laas.lock();
        try {
           ANT_LESETRADER--;
            System.out.println(ANT_LESETRADER);
        } finally {
            laas.unlock();
        }
    }*/

    public void settAntallFlettetrader(int antall){
        laas.lock();
        try {
            ANT_LESETRADER = antall;
        } finally {
            laas.unlock();
        }

    }
}
