import java.util.ArrayList;

public class MaxHeapForLinearNearestNeighbours {
    private double[] Heap;
    private int size;
    private int maxsize;
    public ArrayList<LinearNeighboursInfo> nearestNeighbours;


    /**
     * Constructor to initialize an empty max heap with given maximum capacity.
     */
    public MaxHeapForLinearNearestNeighbours(ArrayList<LinearNeighboursInfo> nearest, int maxsize)
    {
        // This keyword refers to current instance itself
        this.maxsize = maxsize;
        this.size = 0;
        Heap = new double[this.maxsize];
        for (LinearNeighboursInfo a: nearest){
            insert(a.distance);
        }
        nearestNeighbours=new ArrayList<>();
        nearestNeighbours.addAll(nearest);

    }

    /**
     * Returning position of parent
     */
    private int parent(int pos) { return (pos - 1) / 2; }

    /**
     * Swapping nodes.
     * @param fpos first node position.
     * @param spos second node position.
     */
    private void swap(int fpos, int spos)
    {
        double tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    /**
     * Inserts a new element to max heap
     * @param element the element I want to insert.
     */
    public void insert(double element)
    {
        Heap[size] = element;
        // Traverse up and fix violated property
        int current = size;
        while (Heap[current] > Heap[parent(current)]) {
            swap(current, parent(current));
            current = parent(current);
        }
        size++;
    }

    /**
     * Find the max element. If the max element (popped) is bigger than the neighbour element,
     * then the max element is removed and add the neighbour to nearestNeighbours.
     * @param neighbour the point I want to insert to nearestNeighbours.
     * @return the renewed ArrayList nearestNeighbours.
     */
    public ArrayList<LinearNeighboursInfo> findMax(LinearNeighboursInfo neighbour)
    {
        double popped = Heap[0];
        if (popped>neighbour.getDistance()) {
            Heap[0] = Heap[--size];
            for (int i = 0; i < nearestNeighbours.size(); i++) {
                if (nearestNeighbours.get(i).getDistance()==popped) {
                    nearestNeighbours.remove(nearestNeighbours.get(i));
                }
            }
            nearestNeighbours.add(neighbour);
        }
        return nearestNeighbours;
    }

}