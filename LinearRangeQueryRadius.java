import java.util.ArrayList;

public class LinearRangeQueryRadius {
    private double pointRadius;
    private ArrayList<Double> searchPoint;
    private ArrayList<Long> qualifyingRecordIds;

    /**
     * Τhe constructor of a point whose k nearest neighbors I want to find.
     * @param pointRadius int k nearest neighbours.
     * @param point search point's coordinates
     */
    LinearRangeQueryRadius(double pointRadius, ArrayList<Double> point){
        this.pointRadius=pointRadius;
        searchPoint=new ArrayList<>();
        searchPoint=point;
        qualifyingRecordIds=new ArrayList<>();
    }

    public void create(){
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
                    if (distance<=pointRadius)
                        qualifyingRecordIds.add(record.getId());
                }
            }else
                throw new IllegalStateException("Could not read records properly from the datafile");
            blockId++;
        }
    }

    /**
     *
     */
    public void print(){
        for (long a:qualifyingRecordIds){
            System.out.println("id="+a);
        }
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
