import javax.xml.crypto.Data;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Java Class that represents the services of an indexfile.
 */
public class IndexFile
{
    public static String indexFilePath;
    public static int nofLevels;
    public static int nofBlocks;
    static RandomAccessFile indexFile;

    public IndexFile(String indexFilePath) throws IOException {
        IndexFile.indexFilePath = indexFilePath;
        this.createIndexFile();
    }

    public void createIndexFile() throws IOException {
        indexFile = new RandomAccessFile(indexFilePath,"rw");
        indexFile.setLength(0);
        IndexFile.nofBlocks = 0;
        IndexFile.nofLevels = 1;
    }

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


    public static Node readIndexBlock(int blockID) throws IOException, ClassNotFoundException {
        RandomAccessFile raf = new RandomAccessFile(new File(indexFilePath), "rw");
        FileInputStream fis = new FileInputStream(raf.getFD());
        BufferedInputStream bis = new BufferedInputStream(fis);

        raf.seek((blockID-1) *DataFile.BLOCK_SIZE);
        byte[] block = new byte[DataFile.BLOCK_SIZE];
        bis.read(block,0,DataFile.BLOCK_SIZE);

        return (Node) deserializeObject(block);
    }

    public static void createIndexFileBlock(Node n)
    {
        try {
            byte[] node = serializeObject(n);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            System.arraycopy(node, 0, block, 0, node.length);

            FileOutputStream fos = new FileOutputStream(indexFilePath,true);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateIndexBlock(int blockID, Node newNode)
    {
        try {
            byte[] nodeInBytes = serializeObject(newNode);
            byte[] block = new byte[DataFile.BLOCK_SIZE];
            System.arraycopy(nodeInBytes, 0, block, 0, nodeInBytes.length);

            RandomAccessFile f = new RandomAccessFile(new File(indexFilePath), "rw");
            f.seek((blockID-1)*DataFile.BLOCK_SIZE);
            f.write(block);
            f.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Auxiliary function that calculates the number of nodeEntries a Node can fit by simulating the addition of nodeEntries in a Node.
     * @return an Integer that represents the number of nodeEntries a Node can fit.
     */
    static public int calculateMaxEntries(){
        int nofEntries=0;
        byte[] node = new byte[0];
        ArrayList<NodeEntry> entries = new ArrayList<>();
        while (node.length < DataFile.BLOCK_SIZE)
        {
            nofEntries++;
            ArrayList<Bounds> bounds = new ArrayList<>();
            for (int d = 0; d < DataFile.nofCoordinates; d++)
                bounds.add(new Bounds(new Random().nextDouble(), new Random().nextDouble()));
            NodeEntry entry = new Leaf(new Random().nextInt(),new Random().nextInt(), new MBR(bounds));
            entry.setChildPtr(new Random().nextLong());
            entries.add(entry);
            try {
                node = serializeObject(new Node(new Random().nextInt(), entries));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return nofEntries;
    }

}
