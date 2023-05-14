import java.util.ArrayList;

public class PrintThemAll {

    /**
     * Function that executes all queries and then prints out the time results.
     */
    public void all(){
        LinearNearestNeighbours();
    }

    /**
     * Prints the k nearest neighbors using linear search and its execution time.
     */
    public void LinearNearestNeighbours(){
        ArrayList<Double> pointCoordinates= new ArrayList<>();
        pointCoordinates.add(40.0);
        pointCoordinates.add(25.0);
        LinearNearestNeighbours a=new LinearNearestNeighbours(1000, pointCoordinates);
        long startTime=System.nanoTime();
        a.create();
        long endTime = System.nanoTime();
        double execution=(double) (endTime - startTime) / 1000000;
        a.print();
        System.out.println("Execution time: " + execution + " milliseconds");
    }



}
