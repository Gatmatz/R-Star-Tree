package src;

import java.io.IOException;
import java.util.ArrayList;

public class Queries {

    /**
     * Function that executes all queries and then prints out the time results.
     */
    public void all() throws IOException, ClassNotFoundException {
        LinearNearestNeighbours();
        NearestNeighboursQuery();
        LinearRangeQueryRadius();
        RangeRadiusQuery();
        LinearRangeQueryMBR();
        RangeMBRQuery();
        Skyline();
    }

    /**
     * Prints the k nearest neighbors using linear search and its execution time.
     */
    public void LinearNearestNeighbours(){
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(40.6872005);
        pointCoordinates.add(22.8488031);
        LinearNearestNeighbours a=new LinearNearestNeighbours(10, pointCoordinates);
        long startTime=System.nanoTime();
        a.search();
        long endTime= System.nanoTime();
        double execution=(double) (endTime - startTime) / 1000000;
        a.print();
        System.out.println("Linear Nearest Neighbours execution time: " + execution + " milliseconds");
        System.out.println();
    }
    /**
     * Prints the k nearest neighbors using R* Tree and its execution time.
     */
    public void NearestNeighboursQuery() throws IOException, ClassNotFoundException {
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(40.6872005);
        pointCoordinates.add(22.8488031);
        NearestNeighboursQuery a=new NearestNeighboursQuery(10, pointCoordinates);
        long startTime=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        a.search(node);
        long endTime = System.nanoTime();
        double execution=(double) (endTime- startTime) / 1000000;
        a.print();
        System.out.println("R* Tree Nearest Neighbours execution time: " + execution + " milliseconds");
        System.out.println();
    }
    /**
     * Prints the points which belong to the Range Query with Radius using linear search and its execution time.
     */
    public void LinearRangeQueryRadius(){
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(40.6872005);
        pointCoordinates.add(22.8488031);
        LinearRangeQueryRadius a=new LinearRangeQueryRadius(0.0005, pointCoordinates);
        long startTimer=System.nanoTime();
        a.search();
        long endTimer = System.nanoTime();
        double execution=(double) (endTimer - startTimer) / 1000000;
        a.print();
        a.printSize();
        System.out.println("Linear Range Query Radius execution time: " + execution + " milliseconds");
        System.out.println();
    }
    /**
     * Prints the points which belong to the Range Query with Radius using R* Tree and its execution time.
     */
    public void RangeRadiusQuery() throws IOException, ClassNotFoundException {
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(40.6872005);
        pointCoordinates.add(22.8488031);
        RangeRadiusQuery a=new RangeRadiusQuery(0.0005, pointCoordinates);
        long startTimer=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        a.search(node);
        long endTimer = System.nanoTime();
        double execution=(double) (endTimer - startTimer) / 1000000;
        a.print();
        a.printSize();
        System.out.println("R* Tree Range Query Radius execution time: " + execution + " milliseconds");
        System.out.println();
    }

    /**
     * Prints the points which belong to the Range Query with MBR using linear search and its execution time.
     */
    public void LinearRangeQueryMBR(){
        Bounds b1= new Bounds(41.2,41.5);
        Bounds b2= new Bounds(26.0,26.5);
        ArrayList<Bounds> BR= new ArrayList<>();
        BR.add(b1);
        BR.add(b2);
        MBR MBR1= new MBR(BR);
        LinearRangeQueryMBR a=new LinearRangeQueryMBR(BR);
        long startTimer=System.nanoTime();
        a.search();
        long endTimer = System.nanoTime();
        double execution=(double) (endTimer - startTimer) / 1000000;
//        a.print();
        a.printSize();
        System.out.println("MBR's area is: "+MBR1.calculateArea());
        System.out.println("Linear Range Query Minimum Bounding Rectangle execution time: " + execution + " milliseconds");
        System.out.println();

    }

    /**
     * Prints the points which belong to the Range Query with MBR using R* Tree and its execution time.
     */
    public void RangeMBRQuery() throws IOException, ClassNotFoundException {
        Bounds b1= new Bounds(41.2,41.5);
        Bounds b2= new Bounds(26.0,26.5);
        ArrayList<Bounds> BR= new ArrayList<>();
        BR.add(b1);
        BR.add(b2);
        MBR MBR1= new MBR(BR);
        RangeMBRQuery a=new RangeMBRQuery(MBR1);
        long startTimer=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        a.search(node);
        long endTimer = System.nanoTime();
        double execution=(double) (endTimer - startTimer) / 1000000;
//        a.print();
        a.printSize();
        System.out.println("MBR's area is: "+MBR1.calculateArea());
        System.out.println("R* Tree Range Query Minimum Bounding Rectangle execution time: " + execution + " milliseconds");
        System.out.println();
    }

    /**
     * Prints the points which belong to the Skyline using R* Tree and its execution time.
     */
    public void Skyline() throws IOException, ClassNotFoundException {
        Skyline s=new Skyline();
        long startTimer=System.nanoTime();
        Node node=IndexFile.readIndexBlock(1);
        s.search(node);
        long endTimer = System.nanoTime();
        double execution=(double) (endTimer - startTimer) / 1000000;
        s.print();
        System.out.println("R* Tree Skyline execution time: " + execution + " milliseconds");
    }

}
