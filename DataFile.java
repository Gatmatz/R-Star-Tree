import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataFile {
    public ArrayList<Double> coordinates;
    public ArrayList<Record> records=new ArrayList<>();


    DataFile(){
    }

    void readCSV(String path) throws FileNotFoundException {
        //Open file
        File data = new File(path);
        //Initialize scanner for reading the CSV file
        Scanner parser = new Scanner(data);
        //Set delimiter to suit CSD form
        String delimiter = ",|\n";
        //Parse through the lines
        long counter=0;
        while (parser.hasNextLine()) {
            //Get a specific node
            String node = parser.nextLine();
            //Break node into the id and coordinates
            String[] nodeArray = node.split(delimiter);

            //Get node id and coordinates
            long id=0;
            if (counter>0){
                boolean flag=false;
                coordinates= new ArrayList<>();
                for (String c : nodeArray) {
                    if (!flag){
                        id = Long.parseLong(c);
                        flag=true;
                    }else{
                        Double coordinate=Double.parseDouble(c);
                        coordinates.add(coordinate);
                    }
                }
                Record a=new Record(id,coordinates);
                records.add(a);
            }
            counter++;
        }
        //Close file after finishing reading
        parser.close();
    }

    public void printRecords(){
        for (Record r:records){
            r.printIt();
        }
        System.out.println("I have " + records.size()+ " records.");
    }

    public static void main(String[] args) throws FileNotFoundException {
        String path = "data/small_processed.csv";
        DataFile df= new DataFile();
        df.readCSV(path);
        df.printRecords();
    }
}
