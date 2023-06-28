import java.io.IOException;
import java.util.*;

/**
 * Java Class that represents the structure of the Skyline Query withing the use of R* tree.
 */
public class Skyline {
    public ArrayList<Pair> qualifyingRecordIds;
    public PriorityQueue<Pair> pQueue;

    /**
     * Τhe constructor of qualifying records and of priority queue.
     */
    Skyline(){
        qualifyingRecordIds=new ArrayList<>();
        pQueue=new PriorityQueue<>();
    }

    /**
     * Function which finds the Skyline of records by using a branch and bound algorithm
     * with the help of the R* Tree.
     */
    public void search(Node node) throws IOException, ClassNotFoundException {
        //insert all entries of the root node in the heap
        for (NodeEntry entry: node.getEntries()){
            double dist=minDist(entry.getMBR());
            Pair p=new Pair(entry,dist);
            pQueue.offer(p);
        }
        while (!pQueue.isEmpty()){
            Pair pair=pQueue.peek();
            //pair is dominated by some point in pQueue
            if (isDominated(pair.getEntry())){
                pQueue.poll();
            }//pair is not dominated
            else{
                //entry is an intermediate entry
                if (pQueue.peek().getEntry().getChildPtr()!=0){
                    Node nod=IndexFile.readIndexBlock(pQueue.poll().getEntry().getChildPtr());
                    //for each child entry of nod
                    for (NodeEntry entry : nod.getEntries()) {
                        //if entry is not dominated by some point in pQueue
                        if (!isDominated(entry)){
                            double dist=minDist(entry.getMBR());
                            Pair p=new Pair(entry,dist);
                            pQueue.offer(p);
                        }
                    }
                }// entry is a data point
                else{
                    qualifyingRecordIds.add(pQueue.poll());
                }
            }
        }
    }

    /**
     * Checks if an entry is dominated by a point on the Skyline.
     * A point P1 is dominated by another point P2 if and only if:
     * -For all dimensions, P1's value is greater than or equal to P2's value (n), and
     * -For at least one dimension, P1's value is strictly greater than P2's value (m).
     * @param entry the entry for which I want to examine whether it is dominated by another element of the priority queue.
     * @return true if the entry is dominated, false if the entry isn't dominated by other point from the priority queue.
     */
    public boolean isDominated(NodeEntry entry){
        for (Pair p:qualifyingRecordIds){
            int n=0,m=0;
            for (int i=0;i< DataFile.getNofCoordinates(); i++) {
                if (p.getEntry().getMBR().getBounds().get(i).getLower()<=entry.getMBR().getBounds().get(i).getLower())
                    n++;
                if (p.getEntry().getMBR().getBounds().get(i).getLower()<entry.getMBR().getBounds().get(i).getLower())
                    m++;
            }
            if (n==DataFile.getNofCoordinates() && m>=1)
                return true;
        }
        return false;
    }

    /**
     * The mindist value of an entry e, is the distance of its MBR’s lower-left corner to the origin.
     * @param m the MBR of the entry for which I want to find the min distance.
     * @return the min distance.
     */
    public double minDist(MBR m){
        double dist = 0;
        int size = DataFile.getNofCoordinates();
        for (int i = 0; i < size; i++) {
            dist+=m.getBounds().get(i).getLower();
        }
        return dist;
    }

    /**
     * Prints the points which belong to the Skyline.
     */
    public void print(){
        int i=0;
        for (Pair a:qualifyingRecordIds){
            Leaf leafEntry=(Leaf) a.getEntry() ;
            System.out.println("id="+leafEntry.getData().id);
            i++;
        }
        System.out.println("The Skyline consists of "+i+" points.");
    }
}

