import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.ArrayList;

public class MBR implements Serializable {
    public ArrayList<Bounds> bounds;
    public Double area;
    public Double perimeter;

    MBR(ArrayList<Bounds> bounds) {
        this.bounds = bounds;
        this.area = calculateArea();
        this.perimeter = calculatePerimeter();
    }

    /**
     * Getter function that return an ArrayList of bounds of each dimension.
     *
     * @return an ArrayList containing the interval of MBR for each dimension.
     */
    ArrayList<Bounds> getBounds() {
        return bounds;
    }

    /**
     * Function that calculates the area of MBR. The area of an MBR is the product of lengths of each dimension.
     *
     * @return the area of the MBR
     */
    public Double calculateArea() {
        double area = 1;
        for (int i = 0; i < DataFile.getNofCoordinates(); i++) {
            area = area * this.bounds.get(i).getLength();
        }
        return area;
    }

    /**
     * Function that calculates the perimeter of MBR.
     * The perimeter of an MBR is the sum of lengths of the edges..
     *
     * @return the perimeter of the MBR
     */
    public Double calculatePerimeter() {
        double perimeter = 0;
        for (int i = 0; i < DataFile.getNofCoordinates(); i++) {
            perimeter = perimeter + this.bounds.get(i).getLength();
        }
        return perimeter;
    }

}
