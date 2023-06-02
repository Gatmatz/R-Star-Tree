import java.io.IOException;
import java.util.ArrayList;

public class RangeRadiusQuery {
    private double pointRadius;
    private ArrayList<Double> searchPoint;
    private ArrayList<Long> qualifyingRecordIds;

    /**
     * The constructor of a point for which I run a range query withing a specific circle.
     * @param pointRadius the point's radius.
     * @param point search point's coordinates.
     */
    RangeRadiusQuery(double pointRadius, ArrayList<Double> point){
        this.pointRadius=pointRadius;
        searchPoint=point;
        qualifyingRecordIds=new ArrayList<>();
    }

    public void create(Node node) throws IOException, ClassNotFoundException {
        if (node.getLevel()!=RStarTree.getLeafLevel()) {
            for (NodeEntry entry : node.getEntries()) {
                if (entry.getMBR().checkOverLapWithPoint(searchPoint, pointRadius)) {
                    if (IndexFile.readIndexBlock(entry.getChildPtr())!=null)
                        create(IndexFile.readIndexBlock(entry.getChildPtr()));
                }
            }
        }else{
            for (NodeEntry entry: node.getEntries()){
                if (entry.getMBR().checkOverLapWithPoint(searchPoint,pointRadius))
                {
                    Leaf leafEntry = (Leaf) entry;
                    qualifyingRecordIds.add(leafEntry.getRecordID());
                }
            }
        }
    }

    /**
     * Prints the qualifying records.
     */
    public void print() {
        for (long a : qualifyingRecordIds) {
            System.out.println("id=" + a);
        }
    }

    /**
     * Prints the number of qualifying records.
     */
    public void printSize() {
        System.out.println("Returns " + qualifyingRecordIds.size() + " records");
    }
}
