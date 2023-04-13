import java.io.Serializable;
import java.util.ArrayList;

public class Record implements Serializable{
    public long id;
    public ArrayList<Double> coordinates;

    public Record(String id, String lat, String lon){
        coordinates= new ArrayList<>();
        this.id=Long.parseLong(id);
        Double Lat= Double.parseDouble(lat);
        Double Lon= Double.parseDouble(lon);
        coordinates.add(Lat);
        coordinates.add(Lon);
    }


    public static void main(String[] args) {

    }

}
