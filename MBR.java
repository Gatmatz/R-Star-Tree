import java.io.Serializable;
import java.util.ArrayList;

public class MBR implements Serializable
{
    private ArrayList<Bounds> bounds;

    MBR(ArrayList<Bounds> bounds)
    {
        this.bounds = bounds;
    }

    /**
     * Getter function that return an ArrayList of bounds of each dimension.
     * @return an ArrayList containing the interval of MBR for each dimension.
     */
    ArrayList<Bounds> getBounds()
    {
        return bounds;
    }
}
