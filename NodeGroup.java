import java.util.ArrayList;

/**
 * Java Class that represents a group of nodes after a distribution(nodeSplit).
 */
public class NodeGroup
{
    ArrayList<NodeEntry> group;
    MBR boxGroup;

    NodeGroup(ArrayList<NodeEntry> group, MBR boxGroup)
    {
        this.group = group;
        this.boxGroup = boxGroup;
    }

    /**
     * Getter that returns the nodeEntries of distribution group
     * @return ArrayList of nodeEntries of distribution group
     */
    public ArrayList<NodeEntry> getGroup()
    {
        return group;
    }

    /**
     * Getter that returns the MBR of the distribution group
     * @return MBR of group
     */
    public MBR getBoxGroup()
    {
        return boxGroup;
    }

}
