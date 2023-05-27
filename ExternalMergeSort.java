import javax.xml.crypto.Data;
import java.io.*;
import java.util.*;

public class ExternalMergeSort {
    public static String outputFile = "files/externalSort.txt";
    public static int bufferSize = 1000;
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
    public static DataBlock readBufferBlock(long blockID) throws IOException, ClassNotFoundException {
        RandomAccessFile raf = new RandomAccessFile(new File(outputFile), "rw");
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
    public static void createBufferBlock(DataBlock n)
    {
        try {
            byte[] buffer = serializeObject(n);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            System.arraycopy(buffer, 0, block, 0, buffer.length);

            FileOutputStream fos = new FileOutputStream(outputFile,true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void externalSort() throws IOException
    {
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
        HashMap<Record,Long> sortedMap = new HashMap<>();
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
        createBufferBlock(blockn);
    }

    private static void mergeBlocks() throws IOException, ClassNotFoundException {
        int counterA = 1;
        int counterB = 2;
        int counterC = 1;
        while ()
        {
            DataBlock blockA = readBufferBlock(counterA);
            DataBlock blockB = readBufferBlock(counterB);
            DataBlock outputBlock = new DataBlock(counterC);
            int index1 = 0;
            int index2 = 0;

            while (index1 < blockA.getNofRecords() && index2 < blockB.getNofRecords()) {
                Record record1 = blockA.records.get(index1);
                Record record2 = blockB.records.get(index2);

                if (record1.getHilbertValue() <= record2.getHilbertValue()) {
                    outputBlock.addRecord(record1);
                    index1++;
                } else {
                    outputBlock.addRecord(record2);
                    index2++;
                }
            }

            // Add the remaining records from block1
            while (index1 < blockA.getNofRecords()) {
                Record record1 = blockA.records.get(index1);
                outputBlock.addRecord(record1);
                index1++;
            }

            // Add the remaining records from block2
            while (index2 < blockB.getNofRecords()) {
                Record record2 = blockB.records.get(index2);
                outputBlock.addRecord(record2);
                index2++;
            }
        }
    }
}
