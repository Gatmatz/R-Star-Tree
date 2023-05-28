import java.io.*;
import java.util.*;

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
     * Function that reads a Node/indexfile Block from the indexFile.
     * Reads the Node with the given blockID.
     * @param blockID the index of the Node we want to read.
     * @return the Node with the given blockID
     */
    public static DataBlock readBufferBlock(long blockID, String file) throws IOException, ClassNotFoundException {
        RandomAccessFile raf = new RandomAccessFile(new File(file), "rw");
        FileInputStream fis = new FileInputStream(raf.getFD());
        BufferedInputStream bis = new BufferedInputStream(fis);

        raf.seek((blockID-1) *DataFile.BLOCK_SIZE);
        byte[] block = new byte[DataFile.BLOCK_SIZE];
        bis.read(block,0,DataFile.BLOCK_SIZE);

        return (DataBlock) deserializeObject(block);
    }

    /**
     * Function that writes a new indexfile-block in indexFile.
     * Writes a Node in memory.
     * @param n the given new Node to be writter
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
    public static void externalSort() throws IOException, ClassNotFoundException {
        //Delete previous sorting
        RandomAccessFile bufferFile = new RandomAccessFile(outputFile,"rw");
        bufferFile.setLength(0);
        //Initialize new external Merge-Sort
        for (int i=1;i<=DataFile.getNofBlocks();i++)
        {
            DataBlock blockn = DataFile.readDataFileBlock(i);
            sortAndWriteBuffer(blockn.records,i);
        }
        // Merge sorted temporary files
        mergeBlocks();
        //Delete temporary files
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
    public static void internalSort() throws IOException {
        RandomAccessFile bufferFile = new RandomAccessFile(outputFile,"rw");
        bufferFile.setLength(0);
        //Fetch all records into memory
        ArrayList<Record> records = new ArrayList<>();
        int nofRecords = DataFile.readDataFileBlock(2).records.size();
        for (int i=1;i<=DataFile.getNofBlocks();i++)
        {
            DataBlock blockn = DataFile.readDataFileBlock(i);
            records.addAll(blockn.records);
        }
        //Sort the records by their Hilbert Curve Value
        ArrayList<Record> sortedBuffer = sortRecords(records);
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
        return;
    }
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
    private static void sortAndWriteBuffer(ArrayList<Record> buffer, int counter) throws IOException {
        ArrayList<Record> sortedBuffer = sortRecords(buffer);
        DataBlock blockn = new DataBlock(counter);
        blockn.records = new ArrayList<>(sortedBuffer);
        createBufferBlock(blockn,tempFile);
    }

    private static void mergeBlocks() throws IOException, ClassNotFoundException {
        if (noOfBlocks == 0)
            return;
        else if (noOfBlocks == 1)
        {
            DataBlock outputBlock = readBufferBlock(1,tempFile);
            createBufferBlock(outputBlock,outputFile);
            return;
        }
        int counterA = 1;
        int counterB = 2;
        int counterC = 1;
        DataBlock blockA = readBufferBlock(counterA,tempFile);
        DataBlock blockB = readBufferBlock(counterB,tempFile);
        DataBlock outputBlock = new DataBlock(counterC);
        int indexA = 0;
        int indexB = 0;
        long maxRecords = blockA.getNofRecords();
        while (counterA <= noOfBlocks && counterB <= noOfBlocks)
        {
            while (indexA < blockA.getNofRecords() && indexB < blockB.getNofRecords()) {
                Record record1 = blockA.records.get(indexA);
                Record record2 = blockB.records.get(indexB);

                if (record1.getHilbertValue() < record2.getHilbertValue())
                {
                    outputBlock.addRecord(record1);
                    indexA++;
                } else {
                    outputBlock.addRecord(record2);
                    indexB++;
                }

                if (outputBlock.getNofRecords() >= maxRecords)
                {
                    createBufferBlock(outputBlock,outputFile);
                    counterC++;
                    outputBlock = new DataBlock(counterC);
                }
            }
            if (indexA>=blockA.getNofRecords())
            {
                counterA = Math.max(counterA,counterB) + 1;
                if (counterA > noOfBlocks)
                    break;
                blockA = readBufferBlock(counterA,tempFile);
                indexA = 0;
            }
            else if (indexB>=blockB.getNofRecords())
            {
                counterB = Math.max(counterA,counterB) + 1;
                if (counterB > noOfBlocks)
                    break;
                blockB = readBufferBlock(counterB,tempFile);
                indexB = 0;
            }
        }
        if (indexA < blockA.getNofRecords())
        {
            while (indexA < blockA.getNofRecords())
            {
                if (outputBlock.getNofRecords() >= maxRecords)
                {
                    createBufferBlock(outputBlock,outputFile);
                    counterC++;
                    outputBlock = new DataBlock(counterC);
                }
                Record record1 = blockA.records.get(indexA);
                outputBlock.addRecord(record1);
                indexA++;
            }
        }
        else if (indexB < blockB.getNofRecords())
        {
            while (indexB < blockB.getNofRecords())
            {
                if (outputBlock.getNofRecords() >= maxRecords)
                {
                    createBufferBlock(outputBlock,outputFile);
                    counterC++;
                    outputBlock = new DataBlock(counterC);
                }
                Record record2 = blockB.records.get(indexB);
                outputBlock.addRecord(record2);
                indexB++;
            }
        }
        createBufferBlock(outputBlock,outputFile);
    }
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
