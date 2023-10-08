package src;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Java Class that represents the structure of the Radius Range Query withing the use of R* tree.
 */
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

    /**
     * Function used for executing a range query withing a specific circle with the use of the R* Tree.
     * Searches for records within searchPoint's radius.
     * @param node the R* tree' s root.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void search(Node node) throws IOException, ClassNotFoundException {
        if (node.getLevel()!=RStarTree.getLeafLevel()) {
            for (NodeEntry entry : node.getEntries()) {
                if (entry.getMBR().checkOverlapFromPoint(searchPoint, pointRadius)) {
                    if (IndexFile.readIndexBlock(entry.getChildPtr())!=null)
                        search(IndexFile.readIndexBlock(entry.getChildPtr()));
                }
            }
        }else{
            for (NodeEntry entry: node.getEntries()){
                if (entry.getMBR().checkOverlapFromPoint(searchPoint,pointRadius)) {
                    Leaf leafEntry = (Leaf) entry;
                    qualifyingRecordIds.add(((Leaf) entry).getData().id);
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
