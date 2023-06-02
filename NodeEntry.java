import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Java Class that represents a non-Leaf Node entry of R*-Tree.
 * Based on the R*-tree implementation a non-leaf node consists of the child Node that points to lower level Node and its MBR,
 * which is the MBR that contains all child entries MBRs.
 */
public class NodeEntry implements Serializable
{
    private long childPtr;
    private MBR minimumBoundingRectangle;
    NodeEntry(Node child)
    {
        childPtr = child.getBlockID();
        fitEntries(child.getEntries());
    }

    NodeEntry(MBR mbr)
    {
        this.minimumBoundingRectangle = mbr;
    }

    /**
     * Setter that sets the blockID of lower Node(child Node).
     * @param childPtr long value that points to a chile Node.
     */
    public void setChildPtr(long childPtr)
    {
        this.childPtr = childPtr;
    }

    /**
     * Getter that returns the Minimum Bounding Rectangle of current Node.
     * @return the Minimum Bounding Rectangle of current NodeEntry.
     */
    public MBR getMBR()
    {
        return minimumBoundingRectangle;
    }

    /**
     * Getter function that returns the blockID of the lower Node.
     * @return long value that points to blockID of the lower Node.
     */
    public long getChildPtr()
    {
        return childPtr;
    }

    /**
     * Auxiliary function that calculates the minimum bound in one dimension by finding the minimum value of lower bound in all entries.
     * @param entries ArrayList containing all child of a node.
     * @param dimension integer value of the current dimension.
     * @return the upper bound of all entries.
     */
    private static double findMinBound(ArrayList<NodeEntry> entries, int dimension)
    {
        double min=Double.MAX_VALUE;
        for (NodeEntry entry : entries) {
            double current = entry.minimumBoundingRectangle.getBounds().get(dimension).getLower();
            if (current<min)
                min = current;
        }
        return min;
    }
    /**
     * Auxiliary function that calculates the maximum bound in one dimension by finding the maximum value of upper bound in all entries.
     * @param entries ArrayList containing all child of a node.
     * @param dimension integer value of the current dimension.
     * @return the upper bound of all entries.
     */
    private static double findMaxBound(ArrayList<NodeEntry> entries, int dimension)
    {
        double max=Double.MIN_VALUE;
        for (NodeEntry entry : entries) {
            double current = entry.minimumBoundingRectangle.getBounds().get(dimension).getUpper();
            if (current>max)
                max = current;
        }
        return max;
    }

    /**
     * Function that calculates the Minimum Bound Rectangle of all entries(child) in current Node.
     * The function parses through the dimensions and calculates the lower and upper bound for each dimension and stores them into an ArrayList.
     * @param entries an ArrayList containing all entries(child) of current node.
     * @return an ArrayList representing the Minimum Bounding Rectangle.
     */
    public static ArrayList<Bounds> findMinBounds(ArrayList<NodeEntry> entries)
    {
        ArrayList<Bounds> minBounds = new ArrayList<>();
        for (int i=0;i<DataFile.getNofCoordinates();i++)
        {
            double min = findMinBound(entries,i);
            double max = findMaxBound(entries,i);
            Bounds oneDimension = new Bounds(min,max);
            minBounds.add(oneDimension);
        }
        return minBounds;
    }

    /**
     * Function that finds the Minimum Bounding Rectangle of two MBRs.
     * The function fits two MBRs.
     * For every dimension the function calculates the minimum value between lower bounds and the maximum value
     * between upper bounds to create a new MBR with minimum-maximum interval.
     * @param A a Minimum Bounding Rectangle
     * @param B a Minimum Bounding Rectangle
     * @return the bounds of a new MBR that fits MBR A and B.
     */
    public static ArrayList<Bounds> mergeMBR(MBR A, MBR B) {
        ArrayList<Bounds> minBounds = new ArrayList<>();
        for (int i = 0; i < DataFile.getNofCoordinates(); i++)
        {
            //Calculate lower bound by finding minimum value between two MBRs
            double min;
            if (A.getBounds().get(i).getLower() < B.getBounds().get(i).getLower())
            {
                min=A.getBounds().get(i).getLower();
            }
            else
            {
                min=B.getBounds().get(i).getLower();
            }

            //Calculate upper bound by finding maximum value between two MBRs
            double max;
            if (A.getBounds().get(i).getUpper() > B.getBounds().get(i).getUpper())
            {
                max=A.getBounds().get(i).getUpper();
            }
            else
            {
                max=B.getBounds().get(i).getUpper();
            }
            Bounds oneDimension = new Bounds(min,max);
            minBounds.add(oneDimension);
        }
        return minBounds;
    }

    /**
     * Function that sets the MBR of current node to a MBR that fits all entries.
     * @param entries an ArrayList that contains all NodeEntries of current Node.
     */
    public void fitEntries(ArrayList<NodeEntry> entries)
    {
        minimumBoundingRectangle = new MBR(findMinBounds(entries));
    }

    /**
     * Function that includes/fits the MBR of a new NodeEntry into the MBR of current NodeEntry.
     * @param n the new NodeEntry to be fitted.
     */
    public void fitAnotherEntry(NodeEntry n)
    {
        minimumBoundingRectangle = new MBR(mergeMBR(minimumBoundingRectangle,n.getMBR()));
    }

    /**
     * Auxiliary function that parses through the node entries of a node and finds the node with the smallest plus area for the addition on new Data
     * @param entries an ArrayList with the current entries in the node
     * @param newData a new node entry to be inserted
     * @return the node entry with the smallest plus area
     */
    public static NodeEntry findMinArea(ArrayList<NodeEntry> entries,NodeEntry newData)
    {
        double min = Double.MAX_VALUE;
        NodeEntry minEntry = null;
        for (NodeEntry entry : entries)
        {
            //Calculate the merged MBR with the addition of new Data and compute the plus area that will be needed.
            MBR newMBR = new MBR(NodeEntry.mergeMBR(entry.getMBR(), newData.getMBR()));
            double areaPlus = newMBR.area - entry.getMBR().area;
            if (areaPlus<min) {
                minEntry = entry;
                min = areaPlus;
            }
            else if (areaPlus == min)
            {
                //Resolve ties by choosing the leaf with the rectangle of the smallest area
                if (entry.getMBR().area < minEntry.getMBR().area)
                    minEntry = entry;
            }
        }
        return minEntry;
    }

    /**
     * Computes the areaEnlargement of each entry in a ArrayList of NodeEntries after the addition of a new NodeEntry newData.
     * @param entries the ArrayList of current NodeEntries
     * @param newData the new NodeEntry to be inserted.
     * @return a HashMap with the area enlargement
     */
    public static HashMap<Double,NodeEntry> getAreaEnlargement(ArrayList<NodeEntry> entries, NodeEntry newData)
    {
        HashMap<Double,NodeEntry> areaEnlargements = new HashMap<>();
        for (NodeEntry entry : entries)
        {
            MBR newMBR = new MBR(NodeEntry.mergeMBR(entry.getMBR(), newData.getMBR()));
            double areaPlus = newMBR.area - entry.getMBR().area;
            areaEnlargements.put(areaPlus,entry);
        }
        return areaEnlargements;
    }

    /**
     * Auxiliary function that computes the sum of overlap of an entry Ek with the ArrayList entries.
     * k index shows the index of Ek in the entries ArrayList, so it can be excluded from calculation.
     * @param Ek the entry we want to calculate the overlap
     * @param entries the entries in a Node
     * @param k the index of Ek in the ArrayList entries
     * @return the sum of overlaps of entry Ek
     */
    public static double findSumOverlap(NodeEntry Ek, ArrayList<NodeEntry> entries, int k)
    {
        double sum = 0;
        for (int i=0;i<entries.size();i++)
        {
            if (i != k)
            {
                sum += getOverlap(Ek.getMBR(),entries.get(i).getMBR());
            }
        }
        return sum;
    }
    /**
     * Auxiliary function that parses through the node entries of a node and finds the node with the smallest plus overlap for the addition on new Data.
     * @param entries an ArrayList with the current entries in the node
     * @param newData a new node entry to be inserted
     * @return the node entry with the smallest plus overlap
     */
    public static NodeEntry findMinOverlap(ArrayList<NodeEntry> entries,NodeEntry newData)
    {
        double min = Double.MAX_VALUE;
        NodeEntry minEntry = null;
        for (NodeEntry entry : entries)
        {
            //Add the data to be inserted to current entry
            MBR merged = new MBR(mergeMBR(entry.getMBR(),newData.getMBR()));
            NodeEntry entryMerged = new NodeEntry(merged);

            //Calculate the merged MBR with the addition of new Data and compute the plus area that will be needed.
            double overlap = findSumOverlap(entryMerged,entries,entries.indexOf(entry));
            if (overlap<min) {
                minEntry = entry;
                min = overlap;
            }
            else if (overlap == min)
            {
                //Resolve ties by choosing the entry whose rectangle needs the least area enlargement.
                ArrayList<NodeEntry> resolveTies = new ArrayList<>();
                resolveTies.add(entry);
                resolveTies.add(minEntry);
                minEntry = findMinArea(resolveTies,newData);
            }
        }
        return minEntry;
    }
    /**
     * Calculates the overlap between two MBRs along all axes.
     * @param currentMBR one MBR
     * @param newMBR another MBR
     * @return the total overlap along all axes between two MBRs.
     */
    public static Double getOverlap(MBR currentMBR, MBR newMBR)
    {
        double overlap = 1.0;
        for(int i=0;i<DataFile.getNofCoordinates();i++)
        {
            overlap = overlap * Bounds.calculateOverlapAxis(currentMBR.getBounds().get(i),newMBR.getBounds().get(i));
        }
        return overlap;
    }

    /**
     * Function that checks of two MBRs overlap each other.
     * The function uses the overlap calculation function to determine if overlap exists.
     * @param currentMBR one MBR
     * @param newMBR another MBR
     * @return true if the two MBRs overlap each other, false otherwise.
     */
    public static boolean getOverlapBoolean(MBR currentMBR, MBR newMBR)
    {
        if (getOverlap(currentMBR, newMBR)==0) {
            return false;
        }
        return true;
    }

    /**
     * Function that computes the Euclidean distance between the centers of 2 MBRs.
     * @param A one MBR
     * @param B second MBR
     * @return the distance between the MBRs
     */
    public static Double getDistanceBetweenCenters(MBR A, MBR B)
    {
        double distance = 0;
        for (int i=0;i<DataFile.getNofCoordinates();i++)
        {
         distance += Math.pow(A.center.get(i)-B.center.get(i),2);
        }
        return Math.sqrt(distance);
    }
}
