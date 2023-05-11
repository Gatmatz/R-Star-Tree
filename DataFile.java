import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class DataFile {
    public final String csvPath;
    public final String dataFilePath;
    public static int nofCoordinates;
    public ArrayList<Record> records=new ArrayList<>();
    public final int BLOCK_SIZE =  32 * 1024;
    private int actualBlockSize;
    private int RECORD_SIZE;
    private int offset;
    RandomAccessFile dataFile;
    DataFile(String csvPath, String dataFilePath) throws IOException
    {
        this.csvPath = csvPath;
        this.dataFilePath = dataFilePath;
    }

    public void createDatafile() throws IOException {
        this.readRecords(); //Read Records from CSV
        dataFile = new RandomAccessFile(dataFilePath, "rw"); //Create a new RAF
        dataFile.setLength(0); //Clear the datafile from previous data
        RECORD_SIZE = calculateObjectSize(records.get(0)); //Inform that the record_size is not found yet and find it
        actualBlockSize = calculateActualBlockSize(); //Find the block size
        this.createBlocks(); //Create datafile and return the real size of block in memory
    }

    /**
     * Getter function that returns the number of parameters used in program.
     * @return number of parameters.
     */
    public static int getNofCoordinates()
    {
        return nofCoordinates;
    }
    /**
     * Basic function that reads the CSV file and keeps the records to an ArrayList.
     */
    private void readRecords() throws IOException
    {
        File data = new File(csvPath); //Open file
        Scanner parser = new Scanner(data); //Initialize scanner for reading the CSV file
        String delimiter = ",|\n";  //Set delimiter to suit CSV form
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
                records.add(nRec);
                //Set number of coordinates
                nofCoordinates = coordinates.size();
            }
            counter++;
        }
        parser.close(); //Close CSV file after finishing reading
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
     *  Function that deserializes an object.
     * @param nblock Object class to be deserialized
     * @return byte array of the deserialized object
     */
    private Object deserializeObject(byte[] nblock) throws IOException, ClassNotFoundException {
        ByteArrayInputStream objectStream = new ByteArrayInputStream(nblock);
        ObjectInputStream object = new ObjectInputStream(objectStream);
        return object.readObject();
    }

    /**
     * Function that calculates the real size Java converts an Object to bytes.
     * @return number of bytes of Block
     */
    private int calculateActualBlockSize() throws IOException {
        DataBlock dummy = new DataBlock(1);
        //Simulates writing the first data block (block0) to file to keep its size
        for (Record record : records)
        {
            if (RECORD_SIZE + RECORD_SIZE*dummy.getNofRecords() > BLOCK_SIZE)
            {
                return calculateObjectSize(dummy);
            } else
            {
                dummy.addRecord(record);
            }
        }
        return calculateObjectSize(dummy); //Return the size of data block
    }

    /*
    Function that creates the MetaDataBlock with the information we need as Record Size, number of Records,
    number of Blocks, Java's block size in bytes.
     */
    private void createMetaDataBlock() throws IOException {
        DataBlock block0 = new DataBlock(0);
        //Initialize arguments
        ArrayList<Double> dummy = new ArrayList<>();
        dummy.add(0.0);
        dummy.add(0.0);

        //Calculate arguments
        Record recSize = new Record(RECORD_SIZE,dummy);
        block0.addRecord(recSize);

        Record nofRecords = new Record(records.size(),dummy);
        block0.addRecord(nofRecords);

        int intofBlocks = (int)Math.ceil((float)(RECORD_SIZE*records.size())/BLOCK_SIZE); //Calculate number of Blocks needed
        Record nofBlocks = new Record(intofBlocks,dummy);
        block0.addRecord(nofBlocks);

        Record blockSize = new Record(actualBlockSize,dummy);
        block0.addRecord(blockSize);

        //Fill in block0 to match as much as possible the size of another block
        Record fill = new Record(0,dummy);
        while(calculateObjectSize(block0) < actualBlockSize)
        {
            block0.addRecord(fill);
        }
        offset = calculateObjectSize(block0)-actualBlockSize; //Keep the difference between block0 size and other blocks
        writeObjectToFile(block0); //Write block0 to datafile
    }
    /**
     * Basic function that parses through the records Arraylist and create blocks that writes on datafile.
     */
    private void createBlocks() throws IOException {
        createMetaDataBlock(); //Create and write block0 to datafile
        int blockCounter = 1; //Start writing blocks from index 1
        DataBlock nblock = new DataBlock(blockCounter);
        for (Record record : records)
        {
            //If blocksize is passed
            if (RECORD_SIZE + RECORD_SIZE*nblock.getNofRecords() > BLOCK_SIZE)
            {
                writeObjectToFile(nblock); //Write block to datafile
                //Create next block
                blockCounter++;
                nblock = new DataBlock(blockCounter);
            }
            else //If block can fit more records
            {
                //Add one more record to block
                nblock.addRecord(record);
            }
        }
        writeObjectToFile(nblock); //Write the last block to datafile
    }
    /**
     * Function that writes a block to the datafile.
     * @param nblock Block to be written to datafile.
     */
    private void writeObjectToFile(Object nblock) throws IOException {
        dataFile.seek(dataFile.length());
        dataFile.write(serializeObject(nblock));
    }

    /**
     * Function that calculates the number of bytes of an Object.
     * @param dummy dummy record to examine its size
     * @return Number of bytes of an Object
     */
    private int calculateObjectSize(Object dummy) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(dummy);
        return byteStream.toByteArray().length;
    }

    /**
     * Basic function that read a specific block from the datafile.
     * Caution: if blockId does not exist returns null, so precaution should be taken.
     * @param blockId number of block that will be fetched.
     * @return DataBlock Object with blockid the given argument.
     */
    private DataBlock readDataFileBlock(int blockId)
    {
        //Check if block exists
        int numofBlocks = (int)Math.ceil((float)(RECORD_SIZE*records.size())/BLOCK_SIZE);
        if (blockId>numofBlocks)
            return null;
        try {
            //Read DataFile
            RandomAccessFile raf = new RandomAccessFile(new File(dataFilePath), "rw");
            FileInputStream fis = new FileInputStream(raf.getFD());
            BufferedInputStream bis = new BufferedInputStream(fis);
            //If block0 is demanded
            if (blockId == 0)
            {
                byte[] block = new byte[actualBlockSize+offset]; //block0 size is blockSize plus an offset due to Java
                bis.read(block,0,actualBlockSize+offset); //Read specific bytes
                return (DataBlock) deserializeObject(block); //Deserialize those bytes to make an DataBlock Object
            }
            else //if other block is demanded
            {
                //Skip block0 and all previous blocks
                int size = (actualBlockSize+offset) + (blockId-1)*actualBlockSize;
                raf.seek(size);
                //Read specific block
                byte[] block = new byte[actualBlockSize];
                bis.read(block,0,actualBlockSize);
                return (DataBlock) deserializeObject(block); //Deserialize those bytes to make an DataBlock Object
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Basic function that returns all Blocks of a Datafile as an ArrayList.
     * @return an ArrayList that contains all blocks of datafile
     */
    public ArrayList<DataBlock> getBlocks()
    {
        //Calculate number of blocks to be fetched
        int nofBlocks = (int)Math.ceil((float)(RECORD_SIZE*records.size())/BLOCK_SIZE);
        ArrayList<DataBlock> blocks = new ArrayList<>();
        for (int i=1;i<=nofBlocks;i++)
        {
            //Fetch from DataFile each block one by one
            blocks.add(readDataFileBlock(i));
        }
        return blocks;
    }
}
