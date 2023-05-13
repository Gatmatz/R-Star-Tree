import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        //Make datafile
        String csvPath = "data/map_processed.csv";
        String dataFilePath = "files/datafile.txt";
        DataFile df= new DataFile(csvPath,dataFilePath);
        df.createDatafile();
        LinearNearestNeighbours a=new LinearNearestNeighbours(3, 40.0, 25.0);
        a.create();

    }

    public void testNodeEntry()
    {
        //Create box A
        Bounds a1 = new Bounds(1,2);
        Bounds a2 = new Bounds(4,5);
        ArrayList<Bounds> a = new ArrayList<>();
        a.add(a1);
        a.add(a2);
        MBR boxA = new MBR(a);

        //Create box B
        Bounds b1 = new Bounds(2,3);
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

        NodeEntry d1 = new NodeEntry(boxA);
        NodeEntry d2 = new NodeEntry(boxB);
        NodeEntry d3 = new NodeEntry(boxC);
        ArrayList<NodeEntry> entries = new ArrayList<>();
        entries.add(d1);
        entries.add(d2);
        entries.add(d3);
        NodeEntry d = new NodeEntry();
        ArrayList<Bounds> result = d.findMinBounds(entries);
        for (Bounds bounds : result) {
            System.out.println(bounds.prettyPrint());
        }
    }
}
