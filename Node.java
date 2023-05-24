import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java Class that represents a Node in the R*-tree.
 */
public class Node implements Serializable
{
    public static int maxEntries = IndexFile.calculateMaxEntries() -1 ; //Max number of nodeEntries a Node can fit based on BLOCK_SIZE
    public static int minEntries = (int)(0.4 * maxEntries); //Min number of nodeEntries a Node can have based on a percentage of maxEntries(40% of maxEntries)
    private int level; //The tree level of current node in the RStarTree
    private int blockID; //the block index in the indexFile that this node refers to
    private ArrayList<NodeEntry> entries; //the nodeEntries of current Node
    public Node(int level, ArrayList<NodeEntry> entries)
    {
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

    /**
     * Setter function that sets the nodeEntries the current Node has.
     * @param entries the new entries of the Node.
     */
    public void setEntries(ArrayList<NodeEntry> entries) {
        this.entries = entries;
    }

    /**
     * Setter function that sets the blockID of current Node.
     * @param blockID index of block in the IndexFile
     */
    public void setBlockID(int blockID)
    {
        this.blockID = blockID;
    }

    /**
     * Setter function that sets the level in the tree of current Node.
     * @param level the current level of the Node
     */
    public void setLevel(int level)
    {
        this.level = level;
    }

    /**
     * Function that inserts a new nodeEntry in the entries of current Node.
     * @param newEntry the new NodeEntry to be inserted.
     */
    public void insertEntry(NodeEntry newEntry)
    {
        entries.add(newEntry);
    }

}
