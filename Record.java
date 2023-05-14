import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java Class that represents a record.
 */
public class Record implements Serializable{
    public long id;
    public ArrayList<Double> coordinates;

    /**
     *A constructor for a record that takes as parameters the id of the record and its coordinates.
     * @param id record id.
     * @param cor the coordinates.
     */
    public Record(long id, ArrayList<Double> cor)
    {
        this.id=id;
        coordinates=new ArrayList<>();
        coordinates.addAll(cor);
    }

    public void printIt()
    {
        System.out.println(id);
        for (Double c: coordinates){
            System.out.println(c);
        }
        System.out.println("");
    }

    /**
     * Gets the id of a specific record.
     * @return long record ID.
     */
    public long getId() {return id;}

}
