package src;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Java Class that represents the structure of the MBR Range Query withing the use of R* tree.
 */
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

    /**
     * Function used for executing a range query withing a specific MBR with the use of the R* Tree
     * Searches for records within that searchMBR.
     * @param node the R* tree' s root.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void search(Node node) throws IOException, ClassNotFoundException {
        //if node is not a leaf.
        if (node.getLevel()!=RStarTree.getLeafLevel()){
            for (NodeEntry entry: node.getEntries()){
                if (NodeEntry.getOverlapBoolean(entry.getMBR(),searchMBR))
                    search(IndexFile.readIndexBlock(entry.getChildPtr()));

            }
            //if node is a leaf.
        }else
            for (NodeEntry entry: node.getEntries()) {
                if (NodeEntry.getOverlapBoolean(entry.getMBR(), searchMBR)) {
                    Leaf leafEntry=(Leaf) entry ;
                    qualifyingRecordIds.add(leafEntry.getData().id);
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
