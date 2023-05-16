import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        //Make datafile
        String csvPath = "data/map_processed.csv";
        String dataFilePath = "files/datafile.txt";
        DataFile df= new DataFile(csvPath,dataFilePath);
        df.createDatafile();
        String indexFilePath = "files/indexfile.txt";
        IndexFile di = new IndexFile(indexFilePath);
        testNodeEntry();
    }

    public static void testNodeEntry() throws IOException, ClassNotFoundException {
        //Create box A
        Bounds a1 = new Bounds(1,2);
        Bounds a2 = new Bounds(1,5);
        ArrayList<Bounds> a = new ArrayList<>();
        a.add(a1);
        a.add(a2);
        MBR boxA = new MBR(a);

        //Create box B
        Bounds b1 = new Bounds(2,6);
        Bounds b2 = new Bounds(2,3);
        ArrayList<Bounds> b = new ArrayList<>();
        b.add(b1);
        b.add(b2);
        MBR boxB = new MBR(b);

        //Create box C
        Bounds c1 = new Bounds(4,5);
        Bounds c2 = new Bounds(4,4.5);
        ArrayList<Bounds> c = new ArrayList<>();
        c.add(c1);
        c.add(c2);
        MBR boxC = new MBR(c);

        //Create box E
        Bounds e1 = new Bounds(4,6);
        Bounds e2 = new Bounds(5,7);
        ArrayList<Bounds> e = new ArrayList<>();
        e.add(e1);
        e.add(e2);
        MBR boxE = new MBR(e);

        NodeEntry d1 = new NodeEntry(boxA);
        NodeEntry d2 = new NodeEntry(boxB);
        NodeEntry d3 = new NodeEntry(boxC);
        NodeEntry d4 = new NodeEntry(boxE);
        ArrayList<NodeEntry> entries = new ArrayList<>();
        entries.add(d2);
        entries.add(d1);
        entries.add(d3);


        Node n1 = new Node();
        n1.setBlockID(1);
        n1.setEntries(entries);
        IndexFile.createIndexFileBlock(n1);
        Node dummy = IndexFile.readIndexBlock(1);

        ArrayList<NodeEntry> entries2 = new ArrayList<>();
        entries2.add(d1);
        entries2.add(d2);
        Node n2 = new Node();
        n2.setBlockID(1);
        n2.setEntries(entries2);
        dummy = IndexFile.readIndexBlock(1);

        RStarTree tree = new RStarTree();
        NodeEntry result = tree.chooseSubtree(n1, d4,-1);
        for (Bounds bound : result.getMBR().getBounds()) {
            System.out.println(bound.prettyPrint());
        }
    }
}
