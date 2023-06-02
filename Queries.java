import java.io.IOException;
import java.util.ArrayList;

public class Queries {

    /**
     * Function that executes all queries and then prints out the time results.
     */
    public void all() throws IOException, ClassNotFoundException {
        LinearNearestNeighbours();
        //NearestNeighboursQuery();
        LinearRangeQueryRadius();
        RangeRadiusQuery();
        LinearRangeQueryMBR();
        RangeMBRQuery();
        //Skyline();
    }

    /**
     * Prints the k nearest neighbors using linear search and its execution time.
     */
    public void LinearNearestNeighbours(){
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(22.7165525);
        pointCoordinates.add(37.6839478);
        LinearNearestNeighbours a=new LinearNearestNeighbours(3, pointCoordinates);
        long startTimenn=System.nanoTime();
        a.create();
        long endTimenn = System.nanoTime();
        double execution=(double) (endTimenn - startTimenn) / 1000000;
        a.print();
        System.out.println("Linear Nearest Neighbours execution time: " + execution + " milliseconds");
        System.out.println();
    }

    /*public void NearestNeighboursQuery() throws IOException, ClassNotFoundException {
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(22.7165525);
        pointCoordinates.add(37.6839478);
        NearestNeighboursQuery a=new NearestNeighboursQuery(3, pointCoordinates);
        long startTimenn=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        a.create(node);
        long endTimenn = System.nanoTime();
        double execution=(double) (endTimenn - startTimenn) / 1000000;
        a.print();
        System.out.println("Linear Nearest Neighbours execution time: " + execution + " milliseconds");
        System.out.println();
    }*/

    public void LinearRangeQueryRadius(){
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(41.5031784);
        pointCoordinates.add(26.5323722);
        LinearRangeQueryRadius a=new LinearRangeQueryRadius(0.0005, pointCoordinates);
        long startTimerqr=System.nanoTime();
        a.create();
        long endTimerqr = System.nanoTime();
        double execution=(double) (endTimerqr - startTimerqr) / 1000000;
        a.print();
        a.printSize();
        System.out.println("Linear Range Query Radius execution time: " + execution + " milliseconds");
        System.out.println();
    }

    public void RangeRadiusQuery() throws IOException, ClassNotFoundException {
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(41.5031784);
        pointCoordinates.add(26.5323722);
        RangeRadiusQuery a=new RangeRadiusQuery(0.0005, pointCoordinates);
        long startTimerqr=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        a.create(node);
        long endTimerqr = System.nanoTime();
        double execution=(double) (endTimerqr - startTimerqr) / 1000000;
        a.print();
        a.printSize();
        System.out.println("R* Tree Range Query Radius execution time: " + execution + " milliseconds");
        System.out.println();
    }


    public void LinearRangeQueryMBR(){
        Bounds b1= new Bounds(41.234,41.4);
        Bounds b2= new Bounds(26.567,26.6);
        ArrayList<Bounds> BR= new ArrayList<>();
        BR.add(b1);
        BR.add(b2);
        LinearRangeQueryMBR a=new LinearRangeQueryMBR(BR);
        long startTimerqb=System.nanoTime();
        a.create();
        long endTimerqb = System.nanoTime();
        double execution=(double) (endTimerqb - startTimerqb) / 1000000;
        //a.print();
        a.printSize();
        System.out.println("Linear Range Query Minimum Bounding Rectangle execution time: " + execution + " milliseconds");

        DataBlock block0 = DataFile.readDataFileBlock(0);
        int nofRec = (int) block0.records.get(1).id;
        System.out.println(nofRec);
        System.out.println();

    }

    public void RangeMBRQuery() throws IOException, ClassNotFoundException {
        Bounds b1= new Bounds(41.234,41.4);
        Bounds b2= new Bounds(26.567,26.6);
        ArrayList<Bounds> BR= new ArrayList<>();
        BR.add(b1);
        BR.add(b2);
        MBR MBR1= new MBR(BR);
        RangeMBRQuery a=new RangeMBRQuery(MBR1);
        long startTimerqb=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        a.create(node);
        long endTimerqb = System.nanoTime();
        double execution=(double) (endTimerqb - startTimerqb) / 1000000;
        //a.print();
        a.printSize();
        System.out.println("R* Tree Range Query Minimum Bounding Rectangle execution time: " + execution + " milliseconds");
    }
}