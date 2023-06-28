import java.util.ArrayList;

/**
 * Java Class that represents the structure of the Sequential Radius Range Query.
 */
public class LinearRangeQueryRadius {
    private double pointRadius;
    private ArrayList<Double> searchPoint;
    private ArrayList<Long> qualifyingRecordIds;

    /**
     * The constructor of a point for which I run a range query withing a specific circle.
     * @param pointRadius the point's radius.
     * @param point search point's coordinates.
     */
    LinearRangeQueryRadius(double pointRadius, ArrayList<Double> point){
        this.pointRadius=pointRadius;
        searchPoint=point;
        qualifyingRecordIds=new ArrayList<>();
    }

    /**
     * Function which execute a range query withing a specific circle.
     * It examines all the blocks and all their records to find the included points.
     * With linear search.
     */
    public void search(){
        DataBlock block0 = DataFile.readDataFileBlock(0);
        int nofBlocks = (int) block0.records.get(2).id;
        int blockId = 1;
        while(blockId <= nofBlocks) {
            DataBlock block;
            block=DataFile.readDataFileBlock(blockId);
            ArrayList<Record> recordsInBlock;
            recordsInBlock= block.records;
            if (recordsInBlock != null) {
                for (Record record : recordsInBlock) {
                    double distance=calculateDistanceFromPoint(record,searchPoint);
                    if (distance<=pointRadius){
                        qualifyingRecordIds.add(record.getId());
                    }
                }
            }
            blockId++;
        }
    }

    /**
     * Prints the qualifying records.
     */
    public void print(){
        for (long a:qualifyingRecordIds){
            System.out.println("id="+a);
        }
    }

    /**
     * Prints the number of qualifying records.
     */
    public void printSize(){
        System.out.println("Returns "+qualifyingRecordIds.size()+ " records");

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
