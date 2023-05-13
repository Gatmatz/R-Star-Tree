import java.util.ArrayList;

public class DistanceForLinearNearestNeighbours {
    public ArrayList<Record> basicInfo;
    public ArrayList<Record> searchPoint;

    public DistanceForLinearNearestNeighbours(Record a, ArrayList<Double> searchPoint){

    }



    public double calculateDistanceFromPoint(Record a, ArrayList<Double> p){
        double dist=Math.sqrt((p.get(0)-a.coordinates.get(0))*(p.get(0)-a.coordinates.get(0)) + (p.get(1)-a.coordinates.get(1))*(p.get(1)-a.coordinates.get(1)));
        return dist;
    }


}
