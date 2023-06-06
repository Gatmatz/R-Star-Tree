import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DataFile {
    public final String csvPath;
    public static String dataFilePath;
    public static int nofCoordinates;
    public static ArrayList<Record> records = new ArrayList<>();
    public static final int BLOCK_SIZE = 32 * 1024;
    private static int RECORD_SIZE;
    private static int nofBlocks;
    public static long nofRecords;
    RandomAccessFile dataFile;

    DataFile(String csvPath, String dataFilePath) throws IOException {
        this.csvPath = csvPath;
        DataFile.dataFilePath = dataFilePath;
    }

    public void readDatafile() throws FileNotFoundException {
        dataFile = new RandomAccessFile(dataFilePath, "rw"); //Create a new RAF
        DataBlock block0 = readMetaDataBlock();
        RECORD_SIZE = (int)block0.records.get(0).id;
        nofRecords = (int)block0.records.get(1).id;
        nofBlocks = (int) block0.records.get(2).id;
    }
    public void createDatafile() throws IOException {
        this.readRecords(); //Read Records from CSV
        dataFile = new RandomAccessFile(dataFilePath, "rw"); //Create a new RAF
        dataFile.setLength(0); //Clear the datafile from previous data
        RECORD_SIZE = calculateObjectSize(records.get(0)); //Inform that the record_size is not found yet and find it
        nofBlocks = 0;
        this.createBlocks(); //Create datafile and return the real size of block in memory
        nofRecords = records.size();
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
    private static Object deserializeObject(byte[] nblock) throws IOException, ClassNotFoundException {
        ByteArrayInputStream objectStream = new ByteArrayInputStream(nblock);
        ObjectInputStream object = new ObjectInputStream(objectStream);
        return object.readObject();
    }

    /**
     * Function that creates or overwrites the current metadata block.
     * The MetaData block is the first block in the dataFile, and it stores statistics about the data.
     */
    public void updateMetaDataBlock()
    {
        DataBlock info = new DataBlock(0);
        info.addRecord(new Record(RECORD_SIZE,new ArrayList<>()));
        info.addRecord(new Record(records.size(),new ArrayList<>()));
        info.addRecord(new Record(getNofBlocks(),new ArrayList<>()));
        try {
            byte[] nodeInBytes = serializeObject(info);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            System.arraycopy(nodeInBytes, 0, block, 0, nodeInBytes.length);

            RandomAccessFile f = new RandomAccessFile(new File(dataFilePath), "rw");
            f.seek(0);
            f.write(block);
            f.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter function that return the number of blocks in DataFile
     * @return the number of blocks in Datafile.
     */
    public static int getNofBlocks()
    {
        return nofBlocks;
    }
    /**
     * Basic function that parses through the records Arraylist and create blocks that writes on datafile.
     */
    private void createBlocks() {
        updateMetaDataBlock(); //Create and write block0 to datafile
        int blockCounter = 1; //Start writing blocks from index 1
        DataBlock nblock = new DataBlock(blockCounter);
        for (Record record : records)
        {
            //If block can fit more records,add one more record to block
            nblock.addRecord(record);

            //If blockSize is passed
            if (RECORD_SIZE + RECORD_SIZE*nblock.getNofRecords() > BLOCK_SIZE)
            {
                writeDataBlockToFile(nblock); //Write block to datafile
                //Create next block
                blockCounter++;
                nblock = new DataBlock(blockCounter);
            }
        }
        writeDataBlockToFile(nblock); //Write the last block to datafile
        updateMetaDataBlock(); //Update block0 with the new statistics about number of blocks
    }

    /**
     * Function that writes a block to the datafile.
     * @param nblock Block to be written to datafile.
     */
    private void writeDataBlockToFile(DataBlock nblock) {
        try {
            byte[] node = serializeObject(nblock);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            System.arraycopy(node, 0, block, 0, node.length);

            FileOutputStream fos = new FileOutputStream(dataFilePath,true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(block);
            nofBlocks++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that calculates the number of bytes of an Object.
     * @param dummy a dummy record to examine its size
     * @return Number of bytes of an Object
     */
    private int calculateObjectSize(Object dummy) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(byteStream);
        objStream.writeObject(dummy);
        return byteStream.toByteArray().length;
    }

    /**
     * Basic function that read the first block from the datafile.
     * The first block of a datafile called block0 keeps statistics about the datafile.
     * Caution: if blockId does not exist returns null, so precaution should be taken.
     * @return block0 with information about the datafile.
     */
    public static DataBlock readMetaDataBlock()
    {
        try
        {
            RandomAccessFile raf = new RandomAccessFile(new File(dataFilePath), "rw");
            FileInputStream fis = new FileInputStream(raf.getFD());
            BufferedInputStream bis = new BufferedInputStream(fis);
            raf.seek(0);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            bis.read(block,0,DataFile.BLOCK_SIZE);

            return (DataBlock) deserializeObject(block);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Basic function that read a specific block from the datafile.
     * Caution: if blockId does not exist returns null, so precaution should be taken.
     * @param blockId number of block that will be fetched.
     * @return DataBlock Object with blockid the given argument.
     */
    public static DataBlock readDataFileBlock(int blockId)
    {
        //Check if block exists
        int numofBlocks = getNofBlocks();
        if (blockId>numofBlocks+1)
            return null;
        try
        {
            RandomAccessFile raf = new RandomAccessFile(new File(dataFilePath), "rw");
            FileInputStream fis = new FileInputStream(raf.getFD());
            BufferedInputStream bis = new BufferedInputStream(fis);

            raf.seek((blockId) *DataFile.BLOCK_SIZE);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            bis.read(block,0,DataFile.BLOCK_SIZE);

            return (DataBlock) deserializeObject(block);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Basic function that returns all Blocks of a Datafile as an ArrayList.
     * @return an ArrayList that contains all blocks of datafile
     */
    public static ArrayList<DataBlock> getBlocks()
    {
        //Calculate number of blocks to be fetched
        int nofBlocks = getNofBlocks();
        ArrayList<DataBlock> blocks = new ArrayList<>();
        for (int i=1;i<=nofBlocks;i++)
        {
            //Fetch from DataFile each block one by one
            blocks.add(readDataFileBlock(i));
        }
        return blocks;
    }
}
