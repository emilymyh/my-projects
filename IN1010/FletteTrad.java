import java.util.ArrayList;
import java.util.HashMap;

public class FletteTrad implements Runnable {
    public static int MAX_TRAADER = 8;
    private Monitor2 monitor;
    private int antFlettinger;

    public FletteTrad(Monitor2 monitor, int antFlettinger){
        this.monitor = monitor;
        this.antFlettinger = antFlettinger;
    }

    @Override
    public void run(){
        while (antFlettinger > 0) {
            antFlettinger--;
            if (monitor.antallHashmaps() >= 2) {

                ArrayList<HashMap<String, Subsekvens>> forFletting = monitor.hentTo(); //Henter to hashmaps for fletting
                HashMap<String, Subsekvens> en = forFletting.get(0);
                HashMap<String, Subsekvens> to = forFletting.get(1);
                HashMap<String, Subsekvens> flettetTrad = monitor.fletting(en, to); //Fletter sammen

                monitor.settInnFlettet(flettetTrad); //Setter inn flettet hashmap


            } else {
                break;
            }

        }
    }
}
