package src;

import java.util.Comparator;

public class Pair implements Comparable<Pair>{
    public double minDist;
    public NodeEntry entry;

    public Pair(NodeEntry entry,double minDist) {
        this.entry=entry;
        this.minDist=minDist;
    }

    public double getMinDist() {
        return minDist;
    }

    public NodeEntry getEntry() {
        return entry;
    }

    @Override
    public int compareTo(Pair o) {
        return Double.compare(this.minDist,o.minDist);
    }
}
