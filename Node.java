import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java Class that represents a Node in the R*-tree.
 */
public class Node implements Serializable
{
    private int blockID;
    private ArrayList<NodeEntry> entries;

    /**
     * Getter function that returns the block ID of current node in RStarTree.
     * @return integer value of block ID
     */
    public int getBlockID()
    {
        return blockID;
    }

    /**
     * Getter function that returns the entries of current node in RStarTree.
     * @return an ArrayList that contains all NodeEntries of current Node.
     */
    public ArrayList<NodeEntry> getEntries()
    {
        return entries;
    }
}
