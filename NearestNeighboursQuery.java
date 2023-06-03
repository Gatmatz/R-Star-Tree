import java.io.IOException;
import java.util.ArrayList;

public class NearestNeighboursQuery {
    public int k;
    public ArrayList<Double> searchPoint;
    public double pointRadius;
    public ArrayList<LinearNeighboursInfo> nearestNeighbours;

    /**
     * Τhe constructor of a point whose k nearest neighbors I want to find.
     * @param k int k nearest neighbours.
     * @param coordinates search point's coordinates.
     */
    NearestNeighboursQuery(int k, ArrayList<Double> coordinates){
        this.k=k;
        searchPoint=new ArrayList<>(coordinates);
        nearestNeighbours=new ArrayList<>();
        this.pointRadius=0;
    }

    public void create(Node node) throws IOException, ClassNotFoundException {
        //if node is not a leaf.
        if (node.getLevel()!=RStarTree.getLeafLevel()) {
            ArrayList<Double> distances = new ArrayList<>(arrayListOfDistancesOfANode(node));
            double d=minDistance(distances);
            for (NodeEntry entry: node.getEntries()){
                if (d==entry.getMBR().calculateDistanceFromPoint(searchPoint)){
                    if (IndexFile.readIndexBlock(entry.getChildPtr())!=null)
                        create(IndexFile.readIndexBlock(entry.getChildPtr()));
                }
            }
        }
        //if node is a leaf.
        else{
            for (NodeEntry entry: node.getEntries()){
                Double distance = entry.getMBR().calculateDistanceFromPoint(searchPoint);
                if (nearestNeighbours.size()<k){
                    LinearNeighboursInfo neighbour= new LinearNeighboursInfo(getRecordId(entry),distance);
                    nearestNeighbours.add(neighbour);
                }else{
                    if (maxDistance(nearestNeighbours)>distance) {
                        for (int i = 0; i < nearestNeighbours.size(); i++) {
                            if (nearestNeighbours.get(i).getDistance() == maxDistance(nearestNeighbours)) {
                                nearestNeighbours.remove(nearestNeighbours.get(i));
                            }
                        }
                        LinearNeighboursInfo neighbour= new LinearNeighboursInfo(getRecordId(entry),distance);
                        nearestNeighbours.add(neighbour);
                    }
                }
                pointRadius=maxDistance(nearestNeighbours);
            }

        }
    }

    public long getRecordId(NodeEntry entry){
        if (entry instanceof Leaf){
            Leaf leafEntry=(Leaf) entry;
            return ((Leaf) entry).getRecordID();
        }
        return 0;
    }

    public Double maxDistance(ArrayList<LinearNeighboursInfo> nearestNeighbours){
        Double max=nearestNeighbours.get(0).getDistance();
        for (int i=1;i<nearestNeighbours.size();i++){
            if (max<nearestNeighbours.get(i).getDistance())
                max=nearestNeighbours.get(i).getDistance();
        }
        return max;
    }

    public Double minDistance(ArrayList<Double> dis){
        Double min=dis.get(0);
        for (int i=1;i<dis.size();i++){
            if (min>dis.get(i))
                min=dis.get(i);
        }
        return min;
    }


    public ArrayList<Double> arrayListOfDistancesOfANode(Node node){
        ArrayList<Double> distances=new ArrayList<>();
        for (NodeEntry entry: node.getEntries()){
            Double d=entry.getMBR().calculateDistanceFromPoint(searchPoint);
            distances.add(d);
        }
        return distances;
    }


    /**
     * Prints k nearest neighbors.
     */
    public void print(){
        int i=0;
        for (LinearNeighboursInfo a:nearestNeighbours){
            //System.out.println("id="+a.getRecordId()+" distance="+ a.getDistance());
            i++;
        }
        System.out.println(i);
    }
}
