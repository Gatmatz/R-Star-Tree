import java.util.ArrayList;

public class NearestNeighboursQuery {
    public int k;
    public ArrayList<Double> searchPoint;
    private double pointRadius;
    public ArrayList<LinearNeighboursInfo> nearestNeighbours;

    /**
     * Τhe constructor of a point whose k nearest neighbors I want to find.
     * @param k int k nearest neighbours.
     * @param coordinates search point's coordinates.
     */
    NearestNeighboursQuery(int k, ArrayList<Double> coordinates, Double pointRadius){
        this.k=k;
        searchPoint=new ArrayList<>(coordinates);
        nearestNeighbours=new ArrayList<>();
        this.pointRadius=pointRadius;
    }

    public void create(Node node){


    }


    /**
     * Prints k nearest neighbors.
     */
    public void print(){
        for (LinearNeighboursInfo a:nearestNeighbours){
            System.out.println("id="+a.getRecordId()+" distance="+ a.getDistance());
        }
    }
}
