import java.util.ArrayList;

/**
 * Java Class that represents a non-Leaf Node of R*-Tree.
 * Based on the R*-tree implementation a non-leaf node consists of the child Node that points to lower level Node and its MBR,
 * which is the MBR that contains all child entries MBRs.
 */
public class NodeEntry
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

    NodeEntry()
    {

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
    private double findMinBound(ArrayList<NodeEntry> entries, int dimension)
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
    private double findMaxBound(ArrayList<NodeEntry> entries, int dimension)
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
    public ArrayList<Bounds> findMinBounds(ArrayList<NodeEntry> entries)
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
    public ArrayList<Bounds> mergeMBR(MBR A, MBR B) {
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

}
