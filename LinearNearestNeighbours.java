import java.io.Serializable;
import java.util.*;

/**
 * Java Class that represents the structure of the Sequential Nearest Neighbours Query.
 */
public class LinearNearestNeighbours implements Serializable {
    public int k;
    public ArrayList<Double> searchPoint;
    public ArrayList<NearestNeighboursInfo> nearestNeighbours;

    /**
     * Τhe constructor of a point whose k nearest neighbors I want to find.
     * @param k int k nearest neighbours.
     * @param coordinates search point's coordinates.
     */
    LinearNearestNeighbours(int k, ArrayList<Double> coordinates){
        this.k=k;
        searchPoint=new ArrayList<>(coordinates);
        nearestNeighbours=new ArrayList<>();
    }

    /**
     * Function which finds the k nearest neighbors of the search point.
     * It examines all the blocks and all their records to find the closest points.
     * With linear search.
     */
    public void create(){
        DataBlock block0 = DataFile.readDataFileBlock(0);
        int nofBlocks = (int) block0.records.get(2).id;
        //System.out.println(nofBlocks);
        int blockId = 1;
        int counter=0;
        while(blockId <= nofBlocks) {
            DataBlock block;
            block=DataFile.readDataFileBlock(blockId);
            ArrayList<Record> recordsInBlock;
            recordsInBlock= block.records;
            if (recordsInBlock != null) {
                for (Record record : recordsInBlock) {
                    double distance = calculateDistanceFromPoint(record, searchPoint);
                    NearestNeighboursInfo neighbour = new NearestNeighboursInfo(record.id, distance);
                    counter++;
                    if (counter <= k) {
                        nearestNeighbours.add(neighbour);
                    } else {
                        MaxHeapForLinearNearestNeighbours max = new MaxHeapForLinearNearestNeighbours(nearestNeighbours, k);
                        nearestNeighbours = max.findMax(neighbour);
                    }
                }
            }
            blockId++;
        }
    }

    /**
     * Sorts the nearest neighbors in ascending order.
     * @param nearestNeighbours the ArrayList of the nearest neighbors.
     * @return the sorted ArrayList of the nearest neighbors in ascending order.
     */
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

    /**
     * Prints k nearest neighbors.
     */
    public void print(){
        nearestNeighbours=sortNearestNeighbours(nearestNeighbours);
        int i=0;
        for (NearestNeighboursInfo a:nearestNeighbours){
            System.out.println("id="+a.getRecordId()+" distance="+ a.getMinDistance());
            i++;
        }
        System.out.println(i);
    }

    /**
     * Finds the distance between search point and record point.
     * @param a record point.
     * @param p search point.
     * @return double distance between search point and record point.
     */
    public double calculateDistanceFromPoint(Record a, ArrayList<Double> p){
        double dist=0;
        int size=DataFile.getNofCoordinates();
        for (int i=0;i<size;i++){
            dist+=(p.get(i)-a.coordinates.get(i))*(p.get(i)-a.coordinates.get(i));
        }
        return Math.sqrt(dist);
    }
}
