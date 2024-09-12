import javax.management.monitor.Monitor;
import java.util.HashMap;

public class LeseTrad implements Runnable {
    private String fil;
    Monitor2 monitor;
    public LeseTrad(String fil, Monitor2 monitor){
        this.fil = fil;
        this.monitor = monitor;
    }

    public void run(){
        HashMap<String, Subsekvens> lest = monitor.lesImmunRepertoar(fil);
        monitor.settInnSubsekvensRegister(lest);
    }
}
