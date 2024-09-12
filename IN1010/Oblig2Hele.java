
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;

public class Oblig2Hele {
    private static final int MAX_TRAADER = 8;
    static int antTrueFiler;
    static int antFalseFiler;
    public static void main(String[] args)  {
        if (args.length == 0) {
            System.out.println("Vennligst angi mappestien der datafilene ligger som et programargument.");
            return;
        }

        String mappeSti = args[0];
        Monitor2 smittet = new Monitor2();
        Monitor2 ikkeSmittet = new Monitor2();

        // Leser inn filnavn fra metadata.csv
        ArrayList<String> filnavn = new ArrayList<>(); //Arraylist som man kan legge filer fra metadata i
        try (Scanner scanner = new Scanner(new File(mappeSti + "/metadata.csv"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                filnavn.add(line.trim()); // Legger til filnavnet i listen
            }
        } catch (FileNotFoundException e) {
            System.out.println("Fant ikke filen...");
        }

        ArrayList<Thread> lesetrader = new ArrayList<>();
        // Leser hver fil fra listen
        for (String fil : filnavn) {
            String[] trueEllerFalse = fil.split(",");

            //Hvis filen er True legges den inn i monitor for smittede
            if (trueEllerFalse[1].equals("True")) {
                String file = mappeSti + "/" + trueEllerFalse[0]; //filen
                //Leser filene
                LeseTrad lesSmittet = new LeseTrad(file, smittet);
                Thread smittetTrad = new Thread(lesSmittet);
                lesetrader.add(smittetTrad);
                antTrueFiler++;
                smittetTrad.start();
                //Hvis filen er False legges den i monitor for ikke smittede

            } else if (trueEllerFalse[1].equals("False")) {
                String file = mappeSti + "/" + trueEllerFalse[0]; //filen
                LeseTrad lesIkkeSmittet = new LeseTrad(file, ikkeSmittet);
                Thread ikkeSmittetTrad = new Thread(lesIkkeSmittet);
                antFalseFiler++;
                lesetrader.add(ikkeSmittetTrad);
                ikkeSmittetTrad.start();

            }

        }

        //Joiner lestråder
        for (Thread trad : lesetrader){
            try{
                trad.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        smittet.settAntallFlettetrader(antTrueFiler);
        ikkeSmittet.settAntallFlettetrader(antFalseFiler);
        //smittet.ferdigLest(); //Setter ferdigLest til true
        //ikkeSmittet.ferdigLest(); //Setter ferdig lest til true

        int smittede = (antTrueFiler - 1)/8; //Setter antall flettinger til antall hashmaps (filer) lest inn -1
        // og deler arbeidsmengden på antall tråder
        int restSmittede = (antTrueFiler-1) % 8; //PAsser på og også få med resten hvis det ikke går opp i 8
        //Setter i gang tråder for registeret for smittede
        ArrayList<Thread> fletteTrader = new ArrayList<>();
        for (int i = 0; i < MAX_TRAADER; i++) {
            if (i == 0) {
                FletteTrad forsteSmittet = new FletteTrad(smittet, smittede + restSmittede);
                Thread forste = new Thread(forsteSmittet);
                fletteTrader.add(forste);
                forste.start();
            } else {
                FletteTrad smittetTrad = new FletteTrad(smittet,smittede);
                Thread trad = new Thread(smittetTrad);
                fletteTrader.add(trad);
                trad.start();
            }
        }

        //joiner trådene
        for (Thread trad : fletteTrader) {
            try {
                trad.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //Setter i gang tråder for ikke smittede på samme måte
        int ikkeSmittede = (antFalseFiler - 1) / 8;
        int restIkkeSmittede = (antFalseFiler -1 ) % 8;
        ArrayList<Thread> fletteTrader2 = new ArrayList<>();
        for (int i = 0; i < MAX_TRAADER; i++) {
            if (i == 0) {
                FletteTrad forste = new FletteTrad(ikkeSmittet, ikkeSmittede + restIkkeSmittede);
                Thread forsteTrad = new Thread(forste);
                fletteTrader2.add(forsteTrad);
                forsteTrad.start();
            } else {
                FletteTrad ikkeSmittetTrad = new FletteTrad(ikkeSmittet, ikkeSmittede);
                Thread trad = new Thread(ikkeSmittetTrad);
                fletteTrader2.add(trad);
                trad.start();
            }
        }

        //Joiner trådene
        for (Thread trad : fletteTrader2) {
            try {
                trad.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        //Finner dominante subsekvenser (differanse på 7 eller mer)
        HashMap<String, Subsekvens> sisteSmittet = smittet.taUt(0); //Tar ut resultat av fletting
        HashMap<String, Subsekvens> sisteIkkeSmittet = ikkeSmittet.taUt(0); //Tar ut resultat av fletting

        for (String key : sisteIkkeSmittet.keySet()) {
            if (sisteSmittet.containsKey(key)) { //Sjekker om subsekvensen også finnes i de smittede
                Subsekvens smittetSub = sisteSmittet.get(key);
                Subsekvens ikkeSmittetSub = sisteIkkeSmittet.get(key);
                int diff = smittetSub.hentAntall() - ikkeSmittetSub.hentAntall(); //Hvis ja, så finner jeg differansen

                if (diff >= 7) { //Hvis differanse mellom smittet og ikkesmittet er 7 eller mer så printes det ut som dominant
                    System.out.println("Dominant subsekvens: ");
                    System.out.println(smittetSub.subsekvens + " med " + smittetSub.hentAntall() + " forekomster");
                    System.out.println("Det er " + diff + " flere forekomster enn " + ikkeSmittetSub.subsekvens + " hos ikke smittede med " + ikkeSmittetSub.hentAntall() + " forekomster");
                    System.out.println();
                }
            }
        }


    }
}
