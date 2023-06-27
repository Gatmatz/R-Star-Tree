import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Make datafile
        String csvPath = "data/map_processed.csv";
        String dataFilePath = "files/datafile.txt";
        DataFile df= new DataFile(csvPath,dataFilePath);
        df.createDatafile();
        //Make RStar-Tree
        String indexFilePath = "files/indexfile.txt";
        IndexFile di = new IndexFile(indexFilePath);
        RStarTree tree = new RStarTree();
//        RStarTree.bulkLoad();
        tree.insertLinear();
    }
    public static void testNoEntries() throws IOException, ClassNotFoundException {
        System.out.println(DataFile.getNofBlocks());
        System.out.println("Actual number of records: "+ DataFile.nofRecords);
        ArrayList<DataBlock> blocks = DataFile.getBlocks();
        int counter = 0;
        for (DataBlock block : blocks)
        {
            counter+=block.records.size();
        }
        System.out.println("Number of records in dataBlocks: "+counter);
        int count = 0;
        for (int i=1;i<IndexFile.nofBlocks;i++)
        {
            Node nodeN = IndexFile.readIndexBlock(i);
            if (nodeN.getLevel() == 1)
            {
                count += nodeN.getEntries().size();
            }
        }
        System.out.println("Number of records in indexFile: "+ count);
    }


}