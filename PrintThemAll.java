import java.util.ArrayList;

public class PrintThemAll {

    /**
     * Function that executes all queries and then prints out the time results.
     */
    public void all(){
        LinearNearestNeighbours();
        LinearRangeQueryRadius();
        LinearRangeQueryBoundingBox();
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

    public void LinearRangeQueryBoundingBox(){
        Bounds b1= new Bounds(41.234,41.8);
        Bounds b2= new Bounds(26.567,26.9);
        ArrayList<Bounds> boundingBox= new ArrayList<>();
        boundingBox.add(b1);
        boundingBox.add(b2);
        LinearRangeQueryBoundingBox a=new LinearRangeQueryBoundingBox(boundingBox);
        long startTimerqb=System.nanoTime();
        a.create();
        long endTimerqb = System.nanoTime();
        double execution=(double) (endTimerqb - startTimerqb) / 1000000;
        //a.print();
        a.printSize();
        System.out.println("Linear Range Query Bounding Box execution time: " + execution + " milliseconds");


        DataBlock block0 = DataFile.readDataFileBlock(0);
        int nofRec = (int) block0.records.get(1).id;
        System.out.println(nofRec);
    }



}
