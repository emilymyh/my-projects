import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SubsekvensRegister {
    private ArrayList<HashMap<String, Subsekvens>> subsekvensRegister; //Arraylist som tar inn hashmaps
    // av subsekvenser

    public SubsekvensRegister() {
        subsekvensRegister = new ArrayList<>();

    }

    //Leser immunrepertoar
   public static HashMap<String, Subsekvens> lesImmunRepertoar(String fil) {
        HashMap<String, Subsekvens> subsekvensHashMap = new HashMap<>();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fil));
        } catch (FileNotFoundException e) {
            System.out.println("Fant ikke filen...");
            System.exit(-1);
        }

        while (sc.hasNextLine()) {
            String[] data = sc.nextLine().split("");
            for (int i = 0; i < data.length - 2; i++) { //Er alltid to mindre subsekvenser enn lengden på lista
                if (data.length < 3) { //Hvis mindre enn 3 stopper systemet
                    System.exit(-1);
                } else {
                    String sub = data[i] + data[i + 1] + data[i + 2]; //Lager sekvenser på 3 bokstaver

                    Subsekvens subsekvens = new Subsekvens(sub, 1);
                    subsekvensHashMap.put(sub, subsekvens); //Legger til subsekvens i hashmappet

                }
            }
        }

        sc.close();
        return subsekvensHashMap;
    }


    public static HashMap<String, Subsekvens> fletting(HashMap<String, Subsekvens> hashMap1,
                                                       HashMap<String, Subsekvens> hashMap2) {
        HashMap<String, Subsekvens> flettetHashMap = new HashMap<>();
        for (String key : hashMap1.keySet()){
            flettetHashMap.put(key, hashMap1.get(key));
        }

        for (String key : hashMap2.keySet()) {
            String nokkel = key;
            Subsekvens subsekvens = hashMap2.get(key);
            if (flettetHashMap.containsKey(nokkel)) {
                Subsekvens eksisterende = flettetHashMap.get(nokkel);
                int antall = subsekvens.hentAntall();
                eksisterende.oekAntall(antall);
            } else {
                flettetHashMap.put(nokkel, subsekvens);
            }
        }

        //subsekvensRegister.remove(hashMap2);
        return flettetHashMap;
    }

    public ArrayList<HashMap<String, Subsekvens>> hentSubsekvensRegister() {
        return subsekvensRegister;
    }

    //Setter inn ny hashmap
    public void settInnSubsekvenser(HashMap<String, Subsekvens> subsekvenser) {
        subsekvensRegister.add(subsekvenser);
    }

    //returner antall hasmaps i subsekvensregisteret
    public int antallHashmaps() {
        return subsekvensRegister.size();
    }

    public HashMap<String, Subsekvens> taUt(int i){
        if (subsekvensRegister.isEmpty()){
            throw new IllegalStateException("Ingen hashmaps i beholderen");
        }

        return subsekvensRegister.get(i);
    }

    public void fjern(int i){
        subsekvensRegister.remove(i);
    }

}

