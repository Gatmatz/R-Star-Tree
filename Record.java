import java.io.Serializable;
import java.util.ArrayList;

public class Record implements Serializable{
    public long id;
    public String name;
    public ArrayList<Double> coordinates;

    public Record(long id, String name){
        this.id=id;
        this.name=name;
        coordinates= new ArrayList<>();
    }


}
