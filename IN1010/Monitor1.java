import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor1 {
    private SubsekvensRegister register;
    private Lock laas;

    public Monitor1 (){
        register = new SubsekvensRegister(); //Lager nytt register
        laas = new ReentrantLock(); //Lager ny renentrantlock
    }

    public void lesImmunRepertoar(String fil){
        laas.lock(); //Låser slik at bare ne tråd får tilgang av gangen
        try{
            SubsekvensRegister.lesImmunRepertoar(fil); //Prøver å utføre metodekall
        } finally {
            laas.unlock(); //Låser opp metoden for neste tråd uansett om tråden bryter eller ikke
        }
    }

    public HashMap <String, Subsekvens> fletting(HashMap<String, Subsekvens> hashMap1, HashMap<String, Subsekvens> hashMap2) {
        laas.lock();
        try {
            return SubsekvensRegister.fletting(hashMap1, hashMap2);
        } finally {
            laas.unlock();
        }
    }

    public ArrayList<HashMap<String, Subsekvens>> hentSubsekvensRegister(){
        laas.lock();
        try {
            return register.hentSubsekvensRegister();
        } finally {
            laas.unlock();
        }
    }

    public void settInnSubsekvensRegister(HashMap<String, Subsekvens> hashMap){
        laas.lock();
        try{
            register.settInnSubsekvenser(hashMap);
        } finally {
           laas.unlock();
        }
    }

    public int antallHashmaps(){
        laas.lock();
        try {
            return register.antallHashmaps();
        } finally {
            laas.unlock();
        }
    }
}
