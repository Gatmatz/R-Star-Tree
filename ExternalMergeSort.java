import java.io.*;
import java.util.*;

/**
 * Java Class that contains all the tools to sort a DataFile using the HilbertCurve.
 */
public class ExternalMergeSort {
    public static long noOfBlocks=DataFile.getNofBlocks();
    public static String outputFile = "files/externalSort.txt";
    public static String tempFile = "files/externalSort.tmp";

    /**
     *  Function that serializes an object.
     * @param nblock Object class to be serialized
     * @return byte array of the serialized object
     */
    private static byte[] serializeObject(Object nblock) throws IOException
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
     * Function that reads a Node/indexFile Block from the indexFile.
     * Reads the Node with the given blockID.
     * @param blockID the index of the Node we want to read.
     * @return the Node with the given blockID
     */
    public static DataBlock readBufferBlock(long blockID, String file) throws IOException, ClassNotFoundException {
        RandomAccessFile raf = new RandomAccessFile(new File(file), "rw");
        FileInputStream fis = new FileInputStream(raf.getFD());
        BufferedInputStream bis = new BufferedInputStream(fis);

        raf.seek((blockID) *DataFile.BLOCK_SIZE);
        byte[] block = new byte[DataFile.BLOCK_SIZE];
        bis.read(block,0,DataFile.BLOCK_SIZE);

        return (DataBlock) deserializeObject(block);
    }

    /**
     * Function that writes a new indexFile-block in indexFile.
     * Writes a Node in memory.
     * @param n the given new Node to be written
     */
    public static void createBufferBlock(DataBlock n, String file)
    {
        try {
            byte[] buffer = serializeObject(n);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            System.arraycopy(buffer, 0, block, 0, buffer.length);

            FileOutputStream fos = new FileOutputStream(file,true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Startup function that executes an external merge sort in the leaves.
     */
    public static void externalSort() throws IOException, ClassNotFoundException {
        //Delete previous sorting
        RandomAccessFile bufferFile = new RandomAccessFile(outputFile,"rw");
        bufferFile.setLength(0);
        //Initialize new external Merge-Sort
        for (int i=0;i<=DataFile.getNofBlocks();i++)
        {
            DataBlock blockn = DataFile.readDataFileBlock(i);
            sortAndWriteBuffer(blockn.records,i);
        }
        // Merge sorted temporary blocks
        mergeBlocks();
        //Delete temporary file
        try
        {
            File file = new File(tempFile);
            file.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Function that executes a sorting of all the records of all DataBlocks in-memory.
     * The records are sorted based on their position in the Hilbert Curve.
     */
    public static void internalSort() throws IOException, ClassNotFoundException {
        RandomAccessFile bufferFile = new RandomAccessFile(outputFile,"rw");
        bufferFile.setLength(0);
        //Fetch all records into memory
        ArrayList<Record> records = new ArrayList<>();
        int nofRecords = DataFile.readDataFileBlock(1).records.size();
        for (int i=1;i<=DataFile.getNofBlocks();i++)
        {
            DataBlock blockn = DataFile.readDataFileBlock(i);
            records.addAll(blockn.records);
        }
        //Sort the records by their Hilbert Curve Value
        ArrayList<Record> sortedBuffer = sortRecords(records);
        //Copy the metaData block to outputFile
        DataBlock block0 = DataFile.readMetaDataBlock();
        createBufferBlock(block0,outputFile);
        //Distribute the records to blocks
        int i = 0;
        int blockCounter = 1;
        DataBlock blockn = new DataBlock(blockCounter);
        while (i < sortedBuffer.size())
        {
            if (blockn.records.size()>=nofRecords)
            {
                createBufferBlock(blockn,outputFile);
                blockCounter++;
                blockn = new DataBlock(blockCounter);
            }
            blockn.addRecord(sortedBuffer.get(i));
            i++;
        }
        createBufferBlock(blockn,outputFile);
        DataFile.dataFilePath = outputFile;
    }

    /**
     * Auxiliary Function that sorts the records of a given DataBlock(its entries).
     * The function sorts the records based on their position on the Hilbert Curve.
     * @param buffer the given records of a DataBlock.
     * @return the records sorted.
     */
    private static ArrayList<Record> sortRecords(ArrayList<Record> buffer)
    {
        HashMap<Record,Long> hashEntries = new HashMap<>();
        for (Record record : buffer)
        {
            hashEntries.put(record, record.getHilbertValue());
        }
        ArrayList<Long> sortedValues = new ArrayList<>();
        for (Map.Entry<Record,Long> entry: hashEntries.entrySet())
            sortedValues.add(entry.getValue());
        sortedValues.sort(Comparator.naturalOrder());
        LinkedHashMap<Record,Long> sortedMap = new LinkedHashMap<>();
        for (Long value: sortedValues)
        {
            for (Map.Entry<Record,Long> entry : hashEntries.entrySet())
            {
                if (entry.getValue().equals(value))
                    sortedMap.put(entry.getKey(),value);
            }
        }
        ArrayList<Record> result = new ArrayList<>();
        for(Map.Entry<Record, Long> entry :sortedMap.entrySet())
            result.add(entry.getKey());
        return result;
    }

    /**
     * Auxiliary function that sorts an ArrayList of Records and then put records in a DataBlock.
     * Finally, it stores the DataBlock to a temporary file.
     * @param buffer the ArrayList of records to be sorted and saved.
     * @param counter the blockID of the block to be saved.
     */
    private static void sortAndWriteBuffer(ArrayList<Record> buffer, int counter) {
        ArrayList<Record> sortedBuffer = buffer;
        //If block is referred to block0 then don't sort
        if (counter !=0 )
        {
            sortedBuffer = sortRecords(buffer);
        }
        //Create a new DataBlock with the sorted Records
        DataBlock blockn = new DataBlock(counter);
        blockn.records = new ArrayList<>(sortedBuffer);
        createBufferBlock(blockn,tempFile);
    }

    /**
     * Auxiliary function that performs a k-way merge, where k is number of blocks-1.
     */
    private static void mergeBlocks() throws IOException, ClassNotFoundException {
        //if DataFile is empty
        if (noOfBlocks == 0)
            return;
        //if DataFile has only one block
        else if (noOfBlocks == 1)
        {
            //Copy the metaData block to outputFile
            DataBlock block0 = readBufferBlock(0,tempFile);
            createBufferBlock(block0,outputFile);
            //Write the one block to the outputFile
            DataBlock outputBlock = readBufferBlock(1,tempFile);
            createBufferBlock(outputBlock,outputFile);
            return;
        }
        int nofRecords = DataFile.readDataFileBlock(1).records.size();
        //Initialize a priority heap that keeps the minimum record from each DataBlock
        PriorityQueue<RecordInfo> minHeap = new PriorityQueue<>();
        //Insert into the heap the minimum record from each DataBlock
        for (int i=1;i<=noOfBlocks;i++) {
            DataBlock block = ExternalMergeSort.readBufferBlock(i,tempFile);
            Record minRecord = block.records.get(0);
            RecordInfo p = new RecordInfo(minRecord,i,0);
            minHeap.offer(p);
        }
        //Copy the block0 to the outputFile.
        DataBlock block0 = readBufferBlock(0,tempFile);
        createBufferBlock(block0,outputFile);
        //Execute the k-way merge
        int counter = 1;
        DataBlock block=new DataBlock(counter);
        while (!minHeap.isEmpty())
        {
            //Take out the minimum record
            RecordInfo rp = minHeap.poll();
            //Check if current block is full
            if (block.records.size()>=nofRecords)
            {
                //Write the full block to memory and initialize a new one
                createBufferBlock(block,outputFile);
                counter++;
                //and initialize a new one
                block = new DataBlock(counter);
            }
            //Add the record to the block
            block.addRecord(rp.record);
            //Fetch the record's block
            DataBlock current = ExternalMergeSort.readBufferBlock(rp.blockID,tempFile);
            //Find the index of the record and move it to the next one
            int index = rp.index + 1;
            //If the index is in the bounds of the block
            if (index<current.records.size())
            {
                //Insert into the heap the next minimum record of current block
                Record minRecord = current.records.get(index);
                RecordInfo p = new RecordInfo(minRecord,rp.blockID, index);
                minHeap.offer(p);
            }
        }
        //Save the last DataBlock to the outputFile
        createBufferBlock(block,outputFile);
    }

    /**
     * Auxiliary function that retrieves all sorted DataBlocks and saves them into an ArrayList.
     * @return the ArrayList of the sorted DataBlocks.
     */
    public static ArrayList<DataBlock> getBlocks() throws IOException, ClassNotFoundException {
        long nofBlocks = DataFile.getNofBlocks();
        ArrayList<DataBlock> blocks = new ArrayList<>();
        for (int i=1;i<=nofBlocks;i++)
        {
            //Fetch from DataFile each block one by one
            DataBlock dummy = readBufferBlock(i,outputFile);
            blocks.add(dummy);
        }
        return blocks;
    }
}

/**
 * Auxiliary class that represents a record with additional information about its HilbertCurve value, its DataBlock
 * and its index in the DataBlock.
 */
class RecordInfo implements Comparable<RecordInfo>{
    public Record record;
    public double hValue; //The HilbertCurve of the Record
    public int blockID; //The DataBlock index in which the record is saved
    public int index; //The index of the DataBlock array in which the record is saved

    /**
     * Constructor that takes a record, its blockID and its index in the block,
     * calculates its HilbertCurve value and saves the information to the class.
     * @param record the record to be represented
     * @param blockID the block that the record is saved
     * @param index the index in the block that the record is saved
     */
    public RecordInfo(Record record, int blockID, int index) {
        this.record=record;
        this.hValue = record.getHilbertValue();
        this.blockID = blockID;
        this.index = index;
    }

    /**
     * Getter that returns the HilbertCurve value of the record
     * @return the HilbertCurve value of the record
     */
    public double gethValue() {
        return hValue;
    }

    /**
     * Getter that returns the record
     * @return the record
     */
    public Record getRecord() {
        return record;
    }

    /**
     * Getter that returns the blockID of the record
     * @return the blockID of the record
     */
    public int getBlockID()
    {
        return blockID;
    }

    @Override
    public int compareTo(RecordInfo o) {
        return Double.compare(this.hValue,o.hValue);
    }
}
