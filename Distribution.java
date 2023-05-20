/**
 * Java Class that represents a distribution of a Node into two Groups(A and B) after a split.
 */
public class Distribution
{
    int axis;
    NodeGroup groupA;
    NodeGroup groupB;

    Distribution(int axis, NodeGroup groupA, NodeGroup groupB)
    {
        this.axis = axis;
        this.groupA = groupA;
        this.groupB = groupB;
    }

    /**
     * Getter that returns the first group of distribution
     * @return first NodeGroup
     */
    public NodeGroup getGroupA()
    {
        return groupA;
    }

    /**
     * Getter that returns the second group of distribution
     * @return second NodeGroup
     */
    public NodeGroup getGroupB()
    {
        return groupB;
    }

    /**
     * Getter that returns the axis of distribution
     * @return axis
     */
    public int getAxis()
    {
        return axis;
    }
}
