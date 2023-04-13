import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Record implements Serializable{
    public long id;
    public ArrayList<Double> coordinates;

    public Record(long id, ArrayList<Double> cor){
        this.id=id;
        coordinates=new ArrayList<>();
        coordinates.addAll(cor);
    }

    public void printIt(){
        System.out.println(id);
        for (Double c: coordinates){
            System.out.println(c);
        }
        System.out.println("");
    }


    public static void main(String[] args) {

    }

}
