import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DataFile {
    public ArrayList<Record> records=new ArrayList<>();
    public final long BLOCK_SIZE =  32 * 1024;
    public long RECORD_SIZE;
    public final String dataPath = "data/small_processed.csv";
    public final String dataFilePath = "files/datafile";
    RandomAccessFile dataFile;
    DataFile() throws IOException {
        dataFile = new RandomAccessFile(dataFilePath, "rw"); //Create a new RAF
        dataFile.setLength(0); //Clear the datafile from previous data
        RECORD_SIZE = -1; //Inform that the record_size is not found yet
    }

    /**
     * Basic function that creates blocks consisting of records and writes them to datafile.
     */
    private void createBlocks() throws IOException
    {
        File data = new File(dataPath); //Open file
        Scanner parser = new Scanner(data); //Initialize scanner for reading the CSV file
        String delimiter = ",|\n";  //Set delimiter to suit CSV form
        MetaDataBlock block0 = new MetaDataBlock(); //Initialize block0
        long blockCounter = 1; //Initialize block counter to zero
        DataBlock currentBlock = new DataBlock(blockCounter); //Initialize helper block that keeps the current block
        //Parse through the lines
        long counter=0;
        while (parser.hasNextLine()) {
            //Get a specific node
            String node = parser.nextLine();
            //Break node into the id and coordinates
            String[] nodeArray = node.split(delimiter);
            //Get node id and coordinates
            long id=0;
            if (counter>0)
            {
                boolean flag=false;
                ArrayList<Double> coordinates= new ArrayList<>();
                for (String c : nodeArray)
                {
                    if (!flag)
                    {
                        id = Long.parseLong(c);
                        flag=true;
                    }
                    else
                    {
                        Double coordinate=Double.parseDouble(c);
                        coordinates.add(coordinate);
                    }
                }
                Record nRec=new Record(id,coordinates);
                //Find record size from first record read
                if (RECORD_SIZE == -1) {
                    RECORD_SIZE = calculateRecordSize(nRec);
                }

                if (RECORD_SIZE + currentBlock.getNofRecords()*RECORD_SIZE < BLOCK_SIZE)
                {
                    currentBlock.addRecord(nRec);
                    block0.increaseRecords();
                    writeBlockToFile(currentBlock);
                }
                else
                {
                    blockCounter++;
                    currentBlock = new DataBlock(blockCounter);
                    block0.increaseBlocks();
                    block0.increaseRecords();
                }
            }
            counter++;
        }
        updateMetaDataBlock(block0); //Append to the top of the file metaData block0
        parser.close(); //Close CSV file after finishing reading
        dataFile.close(); //Close datafile after finishing writing
    }

    /**
     *  Function that serializes an object.
     * @param nblock Object class to be serialized
     * @return byte array of the serialized object
     */
    private byte[] serializeObject(Object nblock) throws IOException
    {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
            objStream.writeObject(nblock);
            return byteStream.toByteArray();
    }

    /**
     * Function that writes a block to the datafile.
     * @param nblock Block to be written to datafile.
     */
    private void writeBlockToFile(DataBlock nblock) throws IOException {
        dataFile.seek(dataFile.length());
        dataFile.write(serializeObject(nblock));
    }

    /**
     * Function that calculates the number of bytes of a record.
     * @param dummy dummy record to examine its size
     * @return Number of bytes of a Record
     */
    private int calculateRecordSize(Record dummy) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(dummy);
        return byteStream.toByteArray().length;
    }

    /**
     * Function that appends block0 to the top of the datafile.
     * @param block0 first block that carries information.
     */
    private void updateMetaDataBlock(MetaDataBlock block0) throws IOException {
        File datafile = new File(dataFilePath);
        FileInputStream dataIS = new FileInputStream(datafile);
        BufferedReader br = new BufferedReader(new InputStreamReader(dataIS));
        StringBuilder result = new StringBuilder();
        String line = "";
        while( (line = br.readLine()) != null){
            result.append(line);
        }

        result.insert(0, serializeObject(block0));

        datafile.delete();
        FileOutputStream fos = new FileOutputStream(datafile);
        fos.write(result.toString().getBytes());
        fos.flush();
    }

    private void printRecords(){
        for (Record r:records){
            r.printIt();
        }
        System.out.println("I have " + records.size()+ " records.");
    }

    public static void main(String[] args) throws IOException {
        DataFile df= new DataFile();
        df.createBlocks();
    }
}
