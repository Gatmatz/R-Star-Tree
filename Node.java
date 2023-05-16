import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java Class that represents a Node in the R*-tree.
 */
public class Node implements Serializable
{
    public static int maxEntries = IndexFile.calculateMaxEntries(); //Max number of nodeEntries a Node can fit based on BLOCK_SIZE
    public static int minEntries; //Min number of nodeEntries a Node can have based on a percentage of maxEntries
    private int level; //The tree level of current node in the RStarTree
    private int blockID; //the block index in the indexFile that this node refers to
    private ArrayList<NodeEntry> entries; //the nodeEntries of current Node
    public Node()
    {

    }
    public Node(int level, ArrayList<NodeEntry> entries)
    {
        minEntries = (int)(0.4 * maxEntries); //40% of maxEntries
        this.level = level;
        this.entries = new ArrayList<>(entries);
    }

    /**
     * Getter that returns the level of current node in the R*-tree
     */
    public int getLevel()
    {
        return level;
    }

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

    public void setEntries(ArrayList<NodeEntry> entries) {
        this.entries = entries;
    }

    public void setBlockID(int blockID)
    {
        this.blockID = blockID;
    }

}
