import java.io.Serializable;
import java.util.ArrayList;

public class MBR implements Serializable {
    public ArrayList<Bounds> bounds;
    public Double area;
    public Double perimeter;
    public ArrayList<Double> center;

    MBR(ArrayList<Bounds> bounds) {
        this.bounds = bounds;
        this.area = calculateArea();
        this.perimeter = calculatePerimeter();
        this.center =  calculateCenter();
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
     * The perimeter of an MBR is the sum of lengths of the edges.
     * @return the perimeter of the MBR
     */
    public Double calculatePerimeter() {
        double perimeter = 0;
        for (int i = 0; i < DataFile.getNofCoordinates(); i++) {
            perimeter = perimeter + this.bounds.get(i).getLength();
        }
        return perimeter;
    }

    /**
     * Function that calculates the center point of MBR.
     * @return the perimeter of the MBR
     */
    public ArrayList<Double> calculateCenter()
    {
        ArrayList<Double> center = new ArrayList<>();
        for (int i=0;i<DataFile.getNofCoordinates();i++)
        {
            Double middle = bounds.get(i).getLower() + ((bounds.get(i).getUpper() - bounds.get(i).getLower())/2);
            center.add(middle);
        }
        return center;
    }

    /**
     * Checks if
     * @param searchPoint
     * @param pointRadius
     * @return
     */
    public boolean checkOverLapWithPoint(ArrayList<Double> searchPoint, double pointRadius) {
        double dist=0, point;
        int size=DataFile.getNofCoordinates();
        for (int i=0;i<size;i++) {
            if (getBounds().get(i).getLower() > searchPoint.get(i))
                point = getBounds().get(i).getLower();
            else if (getBounds().get(i).getUpper() < searchPoint.get(i))
                point = getBounds().get(i).getUpper();
            else
                point = searchPoint.get(i);
            dist += (searchPoint.get(i) - point) * (searchPoint.get(i) - point);
        }
        dist=Math.sqrt(dist);
        if (dist<= pointRadius)
            return true;
        else
            return false;
    }
}
