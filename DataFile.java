import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataFile {

    DataFile(){
    }

    void readCSV(String path) throws FileNotFoundException {
        //Open file
        File data = new File(path);
        //Initialize scanner for reading the CSV file
        Scanner parser = new Scanner(data);
        //Set delimiter to suit CSD fornm
        String delimiter = ",|\n";
        //Parse through the lines
        while (parser.hasNextLine())
        {
            //Get a specific node
            String node = parser.nextLine();
            //Break node into the id and coordinates
            String[] nodeArray = node.split(delimiter);
            //Get node id
            String id = nodeArray[0];
            //Get coordinates
            for (String c : nodeArray)
            {

            }

        }

        //Close file after finishing reading
        parser.close();
    }

    public static void main(String[] args) throws FileNotFoundException {
        String path = "data/small_processed.csv";
        DataFile df= new DataFile();
        df.readCSV(path);

    }

}
