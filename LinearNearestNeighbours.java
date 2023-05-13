import java.io.Serializable;
import java.util.ArrayList;

public class LinearNearestNeighbours implements Serializable {
    public int k;
    public ArrayList<Double> searchPoint;
    public ArrayList<Record> nearestNeighbours;
    public ArrayList<Double> distancesAndInfo;


    LinearNearestNeighbours(int k, double x, double y){
        this.k=k;
        searchPoint=new ArrayList<>();
        searchPoint.add(x);
        searchPoint.add(y);
        distancesAndInfo=new ArrayList<>();
    }

    public void create(){
        DataBlock block0 = DataFile.readDataFileBlock(0);
        int nofBlocks = (int) block0.records.get(2).id;
        //System.out.println(nofBlocks);
        int blockId = 1;
        while(blockId < nofBlocks) {
            DataBlock block;
            block=DataFile.readDataFileBlock(blockId);
            ArrayList<Record> recordsInBlock;
            recordsInBlock= block.records;




            blockId++;
        }




    }

}
