package src;

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
        this.center = calculateCenter();
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

    /**
     * Function that calculates the center point of MBR.
     *
     * @return the perimeter of the MBR
     */
    public ArrayList<Double> calculateCenter() {
        ArrayList<Double> center = new ArrayList<>();
        for (int i = 0; i < DataFile.getNofCoordinates(); i++) {
            Double middle = bounds.get(i).getLower() + ((bounds.get(i).getUpper() - bounds.get(i).getLower()) / 2);
            center.add(middle);
        }
        return center;
    }

    /**
     * Calculates the distance between the search point and MBR for its dimension.
     * @param searchPoint the point for which I am performing a range query.
     * @return the double distance between MBR and search point.
     */
    public double calculateDistanceFromPoint(ArrayList<Double> searchPoint) {
        double dist = 0, point;
        int size = DataFile.getNofCoordinates();
        for (int i = 0; i < size; i++) {
            if (getBounds().get(i).getLower() > searchPoint.get(i))
                point = getBounds().get(i).getLower();
            else if (getBounds().get(i).getUpper() < searchPoint.get(i))
                point = getBounds().get(i).getUpper();
            else
                point = searchPoint.get(i);
            dist += (searchPoint.get(i) - point) * (searchPoint.get(i) - point);
        }
        dist = Math.sqrt(dist);
        return dist;
    }


    /**
     * Checks if an MBR belongs to circle.
     *
     * @param searchPoint the point for which I am performing a range query.
     * @param pointRadius the circle' s radius.
     * @return true if the circle with radius=pointRadius overlaps the MBR.
     */
    public boolean checkOverlapFromPoint(ArrayList<Double> searchPoint, double pointRadius) {
        if (calculateDistanceFromPoint(searchPoint) <= pointRadius)
            return true;
        else
            return false;
    }

    /**
     * Calculates the distance between the search point and MBR for its dimension without square.
     *
     * @param searchPoint the point for which I am performing a range query.
     * @return the double distance between MBR and search point.
     */
    public double minDist(ArrayList<Double> searchPoint) {
        double dist = 0, point;
        int size = DataFile.getNofCoordinates();
        for (int i = 0; i < size; i++) {
            if (getBounds().get(i).getLower() > searchPoint.get(i))
                point = getBounds().get(i).getLower();
            else if (getBounds().get(i).getUpper() < searchPoint.get(i))
                point = getBounds().get(i).getUpper();
            else
                point = searchPoint.get(i);
            dist += (searchPoint.get(i) - point) * (searchPoint.get(i) - point);
        }
        return dist;
    }

//    public double minMaxDist(ArrayList<Double> searchPoint) {
//        double rmk, rmi, a , b = 0, min = Double.MAX_VALUE;
//        int size = DataFile.getNofCoordinates();
//        for (int k = 0; k < size; k++) {
//            if (searchPoint.get(k) <= ((float)(getBounds().get(k).getLower() + getBounds().get(k).getUpper()) / 2))
//                rmk = getBounds().get(k).getLower();
//            else
//                rmk = getBounds().get(k).getUpper();
//            a = Math.pow(searchPoint.get(k) - rmk, 2);
//            for (int i = size - 1; i >= 0; i--) {
//                if (k != i) {
//                    if (searchPoint.get(i) >= ((float)(getBounds().get(i).getLower() + getBounds().get(i).getUpper()) / 2))
//                        rmi = getBounds().get(i).getLower();
//                    else
//                        rmi = getBounds().get(i).getUpper();
//                    b += Math.pow(searchPoint.get(i) - rmi, 2);
//                }
//            }
//            a=Math.sqrt(a);
//            b=Math.sqrt(b);
//            if (min > (a + b)) {
//                min = a + b;
//            }
//        }
//        return min;
//    }


    }
