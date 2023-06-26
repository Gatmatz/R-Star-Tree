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
    public void skylineSearch(Node node) throws IOException, ClassNotFoundException {
        for (NodeEntry entry: node.getEntries()){
            double dist=minDist(entry.getMBR());
            Pair p=new Pair(entry,dist);
            pQueue.offer(p);
        }
        while (!pQueue.isEmpty()){
            Pair pair=pQueue.peek();
            if (isDominated(pair.getEntry())){
                pQueue.poll();
            }else{
                if (pQueue.peek().getEntry().getChildPtr()!=0){
                    Node nod=IndexFile.readIndexBlock(pQueue.peek().getEntry().getChildPtr());
                    for (NodeEntry entry : nod.getEntries()) {
                        if (!isDominated(entry)){
                            double dist=minDist(entry.getMBR());
                            Pair p=new Pair(entry,dist);
                            pQueue.offer(p);
                        }
                    }
                }else{
                    qualifyingRecordIds.add(pQueue.poll());
                }
            }
        }
    }

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

    public double minDist(MBR m){
        double dist = 0;
        int size = DataFile.getNofCoordinates();
        for (int i = 0; i < size; i++) {
            dist+=m.getBounds().get(i).getLower();
        }
        return dist;
    }

    public void print(){
        int i=0;
        for (Pair a:qualifyingRecordIds){
            Leaf leafEntry=(Leaf) a.getEntry() ;
            System.out.println("id="+leafEntry.getRecordID());
            i++;
        }
        System.out.println(i);
    }
}

