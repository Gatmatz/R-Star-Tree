import java.io.Serializable;
import java.util.ArrayList;

public class LinearNearestNeighbours implements Serializable {
    public int k;
    public ArrayList<Double> searchPoint;
    public ArrayList<NeighboursInfo> nearestNeighbours;

    /**
     * Τhe constructor of a point whose k nearest neighbors I want to find.
     * @param k int k nearest neighbours.
     * @param coordinates search point's coordinates
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
                    double distance=calculateDistanceFromPoint(record,searchPoint);
                    NeighboursInfo neighbour=new NeighboursInfo(record.id, distance);
                    counter++;
                    if (counter<=k){
                        nearestNeighbours.add(neighbour);
                    }else{
                        MaxHeap max= new MaxHeap(nearestNeighbours,k);
                        nearestNeighbours= max.findMax(neighbour);
                    }
                }
            }else
                throw new IllegalStateException("Could not read records properly from the datafile");
            blockId++;
        }
    }

    /**
     * Prints k nearest neighbors.
     */
    public void print(){
        for (NeighboursInfo a:nearestNeighbours){
            System.out.println("id="+a.getRecordId()+" distance="+ a.getDistance());
        }
    }

    /**
     * Finds the distance between search point and record point.
     * @param a record point.
     * @param p search point.
     * @return double distance between search point and record point.
     */
    public double calculateDistanceFromPoint(Record a, ArrayList<Double> p){
        int i=0;
        double dist=0;
        for (Double coordinate: p){
            dist+=(p.get(i)-a.coordinates.get(i))*(p.get(i)-a.coordinates.get(i));
            i++;
        }
        //System.out.println("id="+a.id+" distance="+dist);
        return Math.sqrt(dist);
    }

}
