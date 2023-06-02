import java.io.IOException;
import java.util.ArrayList;

public class RangeMBRQuery {
    private MBR searchMBR;
    private ArrayList<Long> qualifyingRecordIds;

    /**
     * The constructor of a point for which I run a range query withing a specific bounding box.
     * @param bounds the bounds of the search Bounding Box.
     */
    RangeMBRQuery(MBR bounds) {
        searchMBR = bounds;
        qualifyingRecordIds = new ArrayList<>();
    }

    public void create(Node node) throws IOException, ClassNotFoundException {
        //if node is not a leaf.
        if (node.getLevel()!=RStarTree.getLeafLevel()){
            for (NodeEntry entry: node.getEntries()){
                if (NodeEntry.getOverlapBoolean(entry.getMBR(),searchMBR))
                    create(IndexFile.readIndexBlock(entry.getChildPtr()));

            }
            //if node is a leaf.
        }else
            for (NodeEntry entry: node.getEntries()) {
                if (NodeEntry.getOverlapBoolean(entry.getMBR(), searchMBR)) {
                    Leaf leafEntry=(Leaf) entry ;
                    qualifyingRecordIds.add(leafEntry.getRecordID());
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
