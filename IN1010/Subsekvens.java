public class Subsekvens {
    public final String subsekvens; //Tar vare på en subsekvens
    private int antall; //Antall foreomster av subsekvensen hos flere personer

    public Subsekvens(String subsekvens, int antall){
        this.subsekvens = subsekvens;
        this.antall = antall;
    }

    public int hentAntall(){
        return antall;
    }

    public void oekAntall(int ant){
        //antall = ant;
        antall += ant;
    }

    @Override
    public String toString(){
        return subsekvens + ", " + hentAntall();
    }

}
