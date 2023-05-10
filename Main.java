import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        //Make datafile
        String csvPath = "data/map_processed.csv";
        String dataFilePath = "files/datafile.txt";
        DataFile df= new DataFile(csvPath,dataFilePath);
        df.createDatafile();
    }
}
