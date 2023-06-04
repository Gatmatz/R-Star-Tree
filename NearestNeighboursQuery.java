import java.io.IOException;
import java.util.*;

public class NearestNeighboursQuery {
    public int k;
    public ArrayList<Double> searchPoint;
    public ArrayList<NearestNeighboursInfo> nearestNeighbours;

    /**
     * Τhe constructor of a point whose k nearest neighbors I want to find.
     * @param k int k nearest neighbours.
     * @param coordinates search point's coordinates.
     */
    NearestNeighboursQuery(int k, ArrayList<Double> coordinates){
        this.k=k;
        searchPoint=new ArrayList<>(coordinates);
        nearestNeighbours=new ArrayList<>();
    }

    public void nearestNeighborSearch(Node node) throws IOException, ClassNotFoundException {
        //if node is not a leaf.
        if (node.getLevel()!=RStarTree.getLeafLevel()) {
            ArrayList<NodeEntry> ABL = genBranchList(node);
            ABL = sortABL(ABL, searchPoint);
            ABL = downwardPruning(searchPoint, ABL);
            for (int i = 0; i < ABL.size(); i++) {
                Node newNode = IndexFile.readIndexBlock(ABL.get(i).getChildPtr());
                nearestNeighborSearch(newNode);
                ABL = downwardPruning(searchPoint,ABL);
            }
        }


        //if node is a leaf.
        else{
            for (NodeEntry entry: node.getEntries()){
                Double distance = entry.getMBR().minDist(searchPoint);
                if (nearestNeighbours.size()<k){
                    NearestNeighboursInfo neighbour= new NearestNeighboursInfo(getRecordId(entry),distance);
                    neighbour.minMaxDistance = entry.getMBR().minMaxDist(searchPoint);
                    nearestNeighbours.add(neighbour);
                    nearestNeighbours=sortNearestNeighbours(nearestNeighbours);
                }else{
                    if (sortNearestNeighbours(nearestNeighbours).get(k-1).getMinDistance()>distance) {
                        for (int i = 0; i < nearestNeighbours.size(); i++) {
                            if (nearestNeighbours.get(i).getMinDistance() == sortNearestNeighbours(nearestNeighbours).get(k-1).getMinDistance()) {
                                nearestNeighbours.remove(nearestNeighbours.get(i));
                            }
                        }
                        NearestNeighboursInfo neighbour= new NearestNeighboursInfo(getRecordId(entry),distance);
                        neighbour.minMaxDistance = entry.getMBR().minMaxDist(searchPoint);
                        nearestNeighbours.add(neighbour);
                        nearestNeighbours=sortNearestNeighbours(nearestNeighbours);
                    }
                }
            }
        }
    }

    public ArrayList<NodeEntry> genBranchList(Node node){
        ArrayList<NodeEntry> ABL=new ArrayList<>();
        ABL.addAll(node.getEntries());
        return ABL;
    }

    public ArrayList<NodeEntry> sortABL(ArrayList<NodeEntry> buffer, ArrayList<Double> searchPoint){
        HashMap<NodeEntry,Double> hashEntries = new HashMap<>();
        for (NodeEntry entry : buffer)
        {
            hashEntries.put(entry, entry.getMBR().minDist(searchPoint));
        }
        ArrayList<Double> sortedValues = new ArrayList<>();
        for (Map.Entry<NodeEntry,Double> entry: hashEntries.entrySet())
            sortedValues.add(entry.getValue());
        sortedValues.sort(Comparator.naturalOrder());
        LinkedHashMap<NodeEntry,Double> sortedMap = new LinkedHashMap<>();
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

    public ArrayList<NearestNeighboursInfo> sortNearestNeighbours(ArrayList<NearestNeighboursInfo> nearestNeighbours){
        HashMap<NearestNeighboursInfo,Double> hashEntries = new HashMap<>();
        for (NearestNeighboursInfo n : nearestNeighbours)
        {
            hashEntries.put(n, n.getMinDistance());
        }
        ArrayList<Double> sortedValues = new ArrayList<>();
        for (Map.Entry<NearestNeighboursInfo,Double> entry: hashEntries.entrySet())
            sortedValues.add(entry.getValue());
        sortedValues.sort(Comparator.naturalOrder());
        LinkedHashMap<NearestNeighboursInfo,Double> sortedMap = new LinkedHashMap<>();
        for (Double value: sortedValues)
        {
            for (Map.Entry<NearestNeighboursInfo,Double> n : hashEntries.entrySet())
            {
                if (n.getValue().equals(value))
                    sortedMap.put(n.getKey(),value);
            }
        }
        ArrayList<NearestNeighboursInfo> result = new ArrayList<>();
        for(Map.Entry<NearestNeighboursInfo,Double> n :sortedMap.entrySet())
            result.add(n.getKey());
        return result;
    }

    public ArrayList<NodeEntry> downwardPruning(ArrayList<Double> searchPoint, ArrayList<NodeEntry> ABL){
        if (nearestNeighbours.size()==0)
            return ABL;
        NearestNeighboursInfo last= sortNearestNeighbours(nearestNeighbours).get(nearestNeighbours.size()-1);
        for(int i=ABL.size()-1;i>=0;i--){
            NodeEntry e=ABL.get(i);
            if (e.getMBR().minDist(searchPoint)>last.minMaxDistance)
            {
                ABL.remove(e);
            }
        }
        return ABL;
    }

    public long getRecordId(NodeEntry entry){
        if (entry instanceof Leaf){
            Leaf leafEntry=(Leaf) entry;
            return ((Leaf) entry).getRecordID();
        }
        return 0;
    }

    /**
     * Prints k nearest neighbors.
     */
    public void print(){
        int i=0;
        for (NearestNeighboursInfo a:nearestNeighbours){
            //System.out.println("id="+a.getRecordId()+" distance="+ a.getMinDistance());
            i++;
        }
        System.out.println(i);
    }
}
