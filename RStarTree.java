import java.io.IOException;
import java.util.*;

/**
 * Java Class that represents the structure of the RStarTree.
 */
public class RStarTree
{
    private static int TOTAL_LEVELS; //Total levels of RStar-tree
    private static boolean[] reInsertedLevels; //Auxiliary boolean array to indicate which levels have been called by reInsert function.
    public static int LEAF_LEVEL = 1; //Leaf level is always 1
    public static final int P = 32; //Pre
    private static final int REINSERT_ENTRIES = (int) (0.30 * Node.maxEntries); // Setting p to 30% of max entries

    RStarTree() throws IOException, ClassNotFoundException {
        TOTAL_LEVELS = 1;
        reInsertedLevels = new boolean[100];
        insertLinear();
    }

    public static int getRootLevel() throws IOException, ClassNotFoundException {
        Node root = IndexFile.readIndexBlock(1);
        return root.getLevel();
    }
    /**
     * Function that creates the RStar-tree by reading one-by-one blocks from the datafile
     * and inserting them to the tree.
     */
    void insertLinear() throws IOException, ClassNotFoundException {
        //Initialize root node
        Node root = new Node(LEAF_LEVEL,new ArrayList<>());
        root.setBlockID(1);
        IndexFile.createIndexFileBlock(root);
        for (int i=1;i<=DataFile.getNofBlocks();i++)
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
     * ChooseSubtree implementation of RStar-Tree paper that picks the best fit for a NodeEntry in the current Node.
     * There is not a recursion, because the recursion is on recursiveInsert function.
     * @param N The current Node N
     * @param data the new NodeEntry to be inserted
     * @return the best NodeEntry to insert the new NodeEntry.
     */
    public NodeEntry chooseSubtreeInALevel(Node N, NodeEntry data) {
        NodeEntry bestPick;
        //If the child pointers in N point to leaves.
        if (N.getLevel() - 1 == LEAF_LEVEL)
        {
            //[determine the nearly minimum overlap cost]
            if (N.getEntries().size() > P && Node.maxEntries > (P*2)/3 )
            {
                //Sort the rectangles in N in increasing order of their area enlargement needed to include the new data rectangle
                HashMap<Double,NodeEntry> areaEnlargements = NodeEntry.getAreaEnlargement(N.getEntries(),data);
                TreeMap<Double, NodeEntry> sorted = new TreeMap<>(areaEnlargements); //Using TreeMap structure to sort easily by key
                //Let A be the group of the first p entries
                ArrayList<NodeEntry> A = new ArrayList<>();
                int count=0;
                for (Map.Entry<Double,NodeEntry> entry : sorted.entrySet())
                {
                    if (count<P) {
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

    /**
     * Split implementation of RStar-Tree paper.
     * A function to find good splits.
     * @param n the node to be split.
     * @return an ArrayList with the parts of the split node.
     */
    public static ArrayList<Node> split(Node n)
    {
        //Invoke ChooseSplitAxis to determine the axis, perpendicular to which the split is performed
        ArrayList<Distribution> differentAxisDistribution = chooseSplitAxis(n.getEntries());
        //Invoke ChooseSplitIndex to determine the best distribution into two groups along that axis
        int bestSplitIndex = chooseSplitIndex(differentAxisDistribution);
        //Distribute the entries into two groups
        ArrayList<Node> bestSplit = new ArrayList<>();
        NodeGroup groupA = differentAxisDistribution.get(bestSplitIndex).getGroupA();
        NodeGroup groupB = differentAxisDistribution.get(bestSplitIndex).getGroupB();
        bestSplit.add(new Node(n.getLevel(),groupA.getGroup()));
        bestSplit.add(new Node(n.getLevel(),groupB.getGroup()));
        return bestSplit;
    }

    /**
     * Auxiliary function to be used in split.
     * The function sorts an ArrayList of NodeEntries by lower or upper bound in a given dimension.
     * @param entries the ArrayList of entries to be sorted
     * @param dimension the dimension that the entries will be sorted
     * @param choice option to sort by upper or lower bound
     * @return the sorted ArrayList of the entries.
     */
    private static ArrayList<NodeEntry> sortByDimension(ArrayList<NodeEntry> entries, int dimension, String choice)
    {
        if (choice.compareTo("lower") == 0)
        {
            HashMap<NodeEntry,Double> hashEntries = new HashMap<>();
            for (NodeEntry entry : entries)
            {
                hashEntries.put(entry,entry.getMBR().getBounds().get(dimension).getLower());
            }
            ArrayList<Double> sortedValues = new ArrayList<>();
            for (Map.Entry<NodeEntry,Double> entry: hashEntries.entrySet())
                sortedValues.add(entry.getValue());
            sortedValues.sort(Double::compareTo);
            HashMap<NodeEntry,Double> sortedMap = new HashMap<>();
            for (Double value: sortedValues)
            {
                for (Map.Entry<NodeEntry,Double> entry : hashEntries.entrySet())
                {
                    if (entry.getValue().equals(value))
                        sortedMap.put(entry.getKey(),value);
                }
            }
            ArrayList<NodeEntry> result = new ArrayList<>();
            for(Map.Entry<NodeEntry, Double> entry :sortedMap.entrySet())
                result.add(entry.getKey());
            return result;
        }
        else if (choice.compareTo("upper") == 0)
        {
            HashMap<NodeEntry,Double> hashEntries = new HashMap<>();
            for (NodeEntry entry : entries)
            {
                hashEntries.put(entry,entry.getMBR().getBounds().get(dimension).getUpper());
            }
            ArrayList<Double> sortedValues = new ArrayList<>();
            for (Map.Entry<NodeEntry,Double> entry: hashEntries.entrySet())
                sortedValues.add(entry.getValue());
            sortedValues.sort(Comparator.naturalOrder());
            HashMap<NodeEntry,Double> sortedMap = new HashMap<>();
            for (Double value: sortedValues)
            {
                for (Map.Entry<NodeEntry,Double> entry : hashEntries.entrySet())
                {
                    if (entry.getValue().equals(value))
                        sortedMap.put(entry.getKey(),value);
                }
            }
            ArrayList<NodeEntry> result = new ArrayList<>();
            for(Map.Entry<NodeEntry, Double> entry :sortedMap.entrySet())
                result.add(entry.getKey());
            return result;
        }
        return null;
    }

    /**
     * ChooseSplitAxis implementation of RStar-tree paper.
     * @param entries the ArrayList of entries to be split
     * @return an ArrayList of different Distribution over an axis.
     */
    public static ArrayList<Distribution> chooseSplitAxis(ArrayList<NodeEntry> entries)
    {
        ArrayList<Distribution> bestDistributions = new ArrayList<>();
        double minS=Double.MAX_VALUE;
        //CSA1: For each axis
        for (int i=0;i<DataFile.getNofCoordinates();i++)
        {
            //Sort the entries by the lower then by the upper value of their rectangles
            ArrayList<NodeEntry> sortLower = sortByDimension(entries,i,"lower");
            ArrayList<NodeEntry> sortUpper = sortByDimension(entries,i,"upper");

            //Compute S, the sum of all margin-values of the different distributions
            double S = 0.0;
            ArrayList<Distribution>  distributions = new ArrayList<>();
            //For each sort M-2m+2 distributions of the M+1 entries into two groups are determined
            //Determine the distributions
            for (int k=1;k<=Node.maxEntries-2*Node.minEntries+2;k++)
            {
                ArrayList<NodeEntry> firstGroup = new ArrayList<>();
                ArrayList<NodeEntry> secondGroup = new ArrayList<>();
                // The first group contains the first (m-l)+k entries, the second group contains the remaining entries
                for (int j = 0; j < (Node.minEntries -1)+k; j++)
                    firstGroup.add(sortLower.get(j));
                for (int j = (Node.minEntries -1)+k; j < entries.size(); j++)
                    secondGroup.add(sortLower.get(j));

                MBR firstGroupMBR = new MBR(NodeEntry.findMinBounds(firstGroup));
                MBR secondGroupMBR = new MBR(NodeEntry.findMinBounds(secondGroup));
                Distribution currentDistribution = new Distribution(k,new NodeGroup(firstGroup,firstGroupMBR),new NodeGroup(secondGroup,secondGroupMBR));
                distributions.add(currentDistribution);
                S = S + (firstGroupMBR.perimeter + secondGroupMBR.perimeter);
            }

            //CSA2: Choose the axis with the minimum S as split axis
            if (minS > S)
            {
                minS = S;
                bestDistributions = distributions;
            }

            //For each sort M-2m+2 distributions of the M+1 entries into two groups are determined
            //Determine the distributions
            for (int k=1;k<=Node.maxEntries-2*Node.minEntries+2;k++)
            {
                ArrayList<NodeEntry> firstGroup = new ArrayList<>();
                ArrayList<NodeEntry> secondGroup = new ArrayList<>();
                // The first group contains the first (m-l)+k entries, the second group contains the remaining entries
                for (int j = 0; j < (Node.minEntries -1)+k; j++)
                    firstGroup.add(sortUpper.get(j));
                for (int j = (Node.minEntries -1)+k; j < entries.size(); j++)
                    secondGroup.add(sortUpper.get(j));

                MBR firstGroupMBR = new MBR(NodeEntry.findMinBounds(firstGroup));
                MBR secondGroupMBR = new MBR(NodeEntry.findMinBounds(secondGroup));
                Distribution currentDistribution = new Distribution(k,new NodeGroup(firstGroup,firstGroupMBR),new NodeGroup(secondGroup,secondGroupMBR));
                distributions.add(currentDistribution);
                S = S + (firstGroupMBR.perimeter + secondGroupMBR.perimeter);
            }

            //CSA2: Choose the axis with the minimum S as split axis
            if (minS > S)
            {
                minS = S;
                bestDistributions = distributions;
            }
        }
        return bestDistributions;
    }

    /**
     * ChooseSplitIndex implementation of RStar-tree.
     * @param distributions An ArrayList of Distributions over an axis.
     * @return the best index to perform the split
     */
    public static int chooseSplitIndex(ArrayList<Distribution> distributions)
    {
        //CSI1: Along the chosen split axis, choose the distribution with the minimum overlap-value
        double minOverlap = Double.MAX_VALUE;
        double minArea = Double.MAX_VALUE;
        int bestDistributionIndex = 0;
        for (Distribution distribution : distributions)
        {
            double overlap = NodeEntry.getOverlap(distribution.groupA.boxGroup,distribution.groupB.boxGroup);
            double area = distribution.groupA.boxGroup.area + distribution.groupB.boxGroup.area;
            if (minOverlap> overlap)
            {
                minOverlap = overlap;
                bestDistributionIndex = distributions.indexOf(distribution);
                minArea = area;
            }
            else if (minOverlap == overlap)
            {
                if (minArea>area)
                {
                    minArea = area;
                    bestDistributionIndex = distributions.indexOf(distribution);
                }
            }
        }
        return bestDistributionIndex;
    }

    /**
     * InsertData implementation of RStar-tree.
     * @param r Record to be inserted
     * @param dataBlockID the index of dataBlock that belongs to record to be inserted.
     */
    void insertData(Record r, int dataBlockID) throws IOException, ClassNotFoundException {
        ArrayList<Bounds> totalBounds = new ArrayList<>();
        for (int i=0;i<DataFile.nofCoordinates;i++)
        {
            Bounds boundsOneD = new Bounds(r.coordinates.get(i),r.coordinates.get(i));
            totalBounds.add(boundsOneD);
        }
        Leaf dummy = new Leaf(dataBlockID,r.getId(),new MBR(totalBounds));
        //ID1: Invoke Insert starting with the leaf level as a parameter, to insert a new data rectangle
        recursiveInsert(null,null,dummy,LEAF_LEVEL);
    }

    /**
     * OverFlowTreatment implementation of RStar-tree.
     * There are two auxiliary arguments parentOfN and parentEntryOfN to help with the recursion and keep track of the insertion path during the insertion.
     * @param parentOfN the parent Node of N
     * @param parentEntryOfN the parent NodeEntry of N
     * @param N the current Node N
     * @return a NodeEntry if a split in the tree was performed, null otherwise (root-split, reinsertion)
     */
    NodeEntry overFlowTreatment(Node parentOfN, NodeEntry parentEntryOfN, Node N) throws IOException, ClassNotFoundException {
        // If the level is not the root level and this is the first call of OverflowTreatment
        // in the given level during the insertion of one data rectangle, then reinsert
        if (N.getBlockID() != RStarTree.getRootLevel() && !reInsertedLevels[N.getLevel()-1])
        {
            reInsertedLevels[N.getLevel()-1] = true; // Mark level as already inserted
            reInsert(parentOfN,parentEntryOfN,N);
            return null;
        }
        else
        {
            ArrayList<Node> split = split(N);
            if (N.getBlockID() == 1)
            {
                //If OverFlowTreatment caused a split of the root, create a new root.
                N.setEntries(split.get(0).getEntries());
                N.setBlockID(IndexFile.nofBlocks);
                IndexFile.createIndexFileBlock(N);
                Node otherNode = split.get(1);
                otherNode.setBlockID(IndexFile.nofBlocks);
                IndexFile.createIndexFileBlock(otherNode);
                ArrayList<NodeEntry> newRootEntries = new ArrayList<>();
                newRootEntries.add(new NodeEntry(N));
                newRootEntries.add(new NodeEntry(otherNode));
                Node newRoot = new Node(++TOTAL_LEVELS,newRootEntries);
                newRoot.setBlockID(1);
                IndexFile.updateIndexBlock(1,newRoot);
                return null;
            }
            else
            {
                N.setEntries(split.get(0).getEntries());
                IndexFile.updateIndexBlock(N.getBlockID(),N);
                Node otherNode = split.get(1);
                otherNode.setBlockID(IndexFile.nofBlocks);
                IndexFile.createIndexFileBlock(otherNode);
                parentEntryOfN.fitEntries(N.getEntries());
                IndexFile.updateIndexBlock(parentOfN.getBlockID(),parentOfN);
                return new NodeEntry(otherNode);
            }
        }
    }

    /**
     * Insert implementation of RStar-tree at the most.
     * There is a small difference because recursiveInsert executes the recursion of ChooseSubTree and reInsertion from the root to the leaves.
     * The change was made because recursiveInsert can keep track of insertion path and can propagate OverflowTreatment upwards if necessary.
     * There are two auxiliary arguments parent and parentEntry to help with the recursion.
     * @param parent parent Node of current Node
     * @param parentEntry parent Entry of current Node
     * @param E the NodeEntry to be inserted
     * @return a NodeEntry to inform previous calls about a split, null otherwise.
     */
    NodeEntry recursiveInsert(Node parent, NodeEntry parentEntry, NodeEntry E, int level) throws IOException, ClassNotFoundException {
        Node recursiveNode;
        long recursive;
        //Start from the root
        if (parentEntry==null)
        {
            recursive= 1;
        }
        else
        {
            parentEntry.fitAnotherEntry(E);
            IndexFile.updateIndexBlock(parent.getBlockID(),parent);
            recursive = parentEntry.getChildPtr();
        }
        recursiveNode = IndexFile.readIndexBlock(recursive);
        if (recursiveNode.getLevel() != level)
        {
            NodeEntry bestFit = chooseSubtreeInALevel(recursiveNode,E);
            NodeEntry newRecursion = recursiveInsert(recursiveNode,bestFit,E,level);
            if (newRecursion != null)
            {
                recursiveNode.insertEntry(newRecursion);
                IndexFile.updateIndexBlock(recursiveNode.getBlockID(),recursiveNode);
            }
            else
            {
                IndexFile.updateIndexBlock(recursiveNode.getBlockID(),recursiveNode);
                return null;
            }
        }
        else
        {
            recursiveNode.insertEntry(E);
            IndexFile.updateIndexBlock(recursiveNode.getBlockID(),recursiveNode);
        }
        if (recursiveNode.getEntries().size() == Node.maxEntries)
            return overFlowTreatment(parent,parentEntry,recursiveNode);

        return null;
    }

    /**
     * Reinsert implementation of RStar-tree.
     * There are two auxiliary arguments parent and parentEntry to help with the recursion and keep track of the insertion path during the insertion.
     * @param parent the parent of Node N
     * @param parentEntry the parent NodeEntry of Node N
     * @param N the current Node N
     */
    public void reInsert(Node parent, NodeEntry parentEntry, Node N) throws IOException, ClassNotFoundException {
        //RI1: For all M+1 entries of a node N, compute the distance between the centers of their rectangles and the center of the bounding rectangle N
        //RI2: Sort the entries in decreasing order of their distances computed in RI1
        ArrayList<NodeEntry> sorted = sortbyCenterDistance(N.getEntries(),parentEntry);
        //RI3: Remove the first p entries from N and adjust the bounding rectangle of N
        ArrayList<NodeEntry> removed = new ArrayList<>();
        for (int i=sorted.size()-REINSERT_ENTRIES;i<sorted.size();i++)
        {
            removed.add(sorted.get(i));
        }
        for(int i = 0; i < REINSERT_ENTRIES; i++)
            N.getEntries().remove(N.getEntries().size()-1);

        // Updating bounding box of node and to the parent entry
        parentEntry.fitEntries(N.getEntries());
        IndexFile.updateIndexBlock(parent.getBlockID(),parent);
        IndexFile.updateIndexBlock(N.getBlockID(),N);


        for (NodeEntry nodeEntry : removed)
        {
            recursiveInsert(null,null,nodeEntry,N.getLevel());
        }
    }

    /**
     * Auxiliary function that sortes the entries of a Node base on the distance between their MBRs and the MBR of the parent Node.
     * The function sorts in increasing order and the distance is Euclidean.
     * @param entries the entries of the Node.
     * @param parent the parent Node
     * @return an ArrayList with the entries sorted by the distance.
     */
    private static ArrayList<NodeEntry> sortbyCenterDistance(ArrayList<NodeEntry> entries, NodeEntry parent)
    {
        HashMap<Double,NodeEntry> hashEntries = new HashMap<>();
        for (NodeEntry entry : entries)
        {
            hashEntries.put(NodeEntry.getDistanceBetweenCenters(entry.getMBR(),parent.getMBR()),entry);
        }
        TreeMap<Double, NodeEntry> sorted = new TreeMap<>(hashEntries);
        ArrayList<NodeEntry> result = new ArrayList<>();
        for (Map.Entry<Double,NodeEntry> entry : sorted.entrySet())
        {
            result.add(entry.getValue());
        }
        return result;
    }
}
