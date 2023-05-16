import com.sun.source.tree.Tree;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Java Class that represents the structure of the RStarTree.
 */
public class RStarTree
{
    private int nofLevels;
    public static int leafLevel;
    public static final int p = 32;
    RStarTree()
    {
        this.nofLevels = IndexFile.nofLevels;
        insertLinear();
    }

    /**
     * Function that creates the RStar-tree by reading one-by-one blocks from the datafile
     * and inserting them to the tree.
     */
    void insertLinear()
    {
        //Initialize root node
        Node root = new Node();
        root.setBlockID(1);
        IndexFile.createIndexFileBlock(root);

        for (int i=1;i<DataFile.getNofBlocks();i++)
        {
            DataBlock blockn = DataFile.readDataFileBlock(i);
            for (Record record : blockn.records)
            {
                insertData(record,i);
            }
        }
    }
    /**
     * Getter function that return the root Node
     * @return root Node object
     */
    Node getRoot() throws IOException, ClassNotFoundException {
        return IndexFile.readIndexBlock(1);
    }

    /**
     * ChooseSubtree function based on RStar-tree paper.
     * Includes the optimization for big node sizes.
     * @param N the current Node
     * @param data the NodeEntry to be added
     * @param leafLevel the level of leafs (insert begins with leafLevel in chooseSubtree)
     * @return the best NodeEntry to add the new data.
     */
    public NodeEntry chooseSubtree(Node N, NodeEntry data, int leafLevel)
    {
        NodeEntry bestPick = null;
        //If the child pointers in N point to leaves.
        if (N.getLevel() - 1 == leafLevel)
        {
            //[determine the nearly minimum overlap cost]
            if (N.getEntries().size() > p && Node.maxEntries > (p*2)/3 )
            {
                //Sort the rectangles in N in increasing order of their area enlargement needed to include the new data rectangle
                HashMap<Double,NodeEntry> areaEnlargements = NodeEntry.getAreaEnlargement(N.getEntries(),data);
                TreeMap<Double, NodeEntry> sorted = new TreeMap<>(areaEnlargements); //Using TreeMap structure to sort easily by key
                //Let A be the group of the first p entries
                ArrayList<NodeEntry> A = new ArrayList<>();
                int count=0;
                for (Map.Entry<Double,NodeEntry> entry : sorted.entrySet())
                {
                    if (count<p) {
                        A.add(entry.getValue());
                        count++;
                    }
                    else
                        break;
                }
                //From the entries in A, considering all entries in N, choose the entry whose rectangle needs least overlap enlargement
                bestPick = NodeEntry.findMinOverlap(A,data);
                return bestPick;
            }
            //Determine the minimum overlap cost.
            //Choose the entry in N whose rectangle needs the least overlap enlargement to include the new data rectangle.
            //Resolve ties by choosing the entry whose rectangle needs the least area enlargement.
            bestPick = NodeEntry.findMinOverlap(N.getEntries(),data);
            return bestPick;
        }
        //if the child pointers in N do not point to leaves.
        else
        {
            //Determine the minimum area cost.
            //Choose the entry in N whose rectangle needs the least area enlargement to include new data rectangle.
            bestPick = NodeEntry.findMinArea(N.getEntries(),data);
            return bestPick;
        }
    }

    public ArrayList<Node> split()
    {
        //Invoke ChooseSplitAxis to determine the axis, perpendicular to which the split is performed
        ArrayList<Distribution> differentAxisDistribution = chooseSplitAxis();
        //Invoke ChooseSplitIndex to determinte the best distribution into two groups along that axis
        int bestSplitIndex = chooseSplitIndex();
        //Distribute the entries into two groups
        ArrayList<Node> bestSplit = new ArrayList<>();
        return bestSplit;
    }

    public ArrayList<Distribution> chooseSplitAxis()
    {
        return null;
    }

    public int chooseSplitIndex()
    {
        return 0;
    }

    void insertData(Record r, int dataBlockID)
    {
        ArrayList<Bounds> totalBounds = new ArrayList<>();
        for (int i=0;i<DataFile.nofCoordinates;i++)
        {
            Bounds boundsOneD = new Bounds(r.coordinates.get(i),r.coordinates.get(i));
            totalBounds.add(boundsOneD);
        }
        Leaf dummy = new Leaf(dataBlockID,r.getId(),new MBR(totalBounds));
        //ID1: Invoke Insert starting with the leaf level as a parameter, to insert a new data rectangle
        insert(dummy,RStarTree.leafLevel);
    }

    void insert(NodeEntry n, int insertLevel)
    {

    }

}
