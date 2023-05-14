import java.util.ArrayList;

public class MaxHeap {
    private double[] Heap;
    private int size;
    private int maxsize;
    public ArrayList<NeighboursInfo> nearestNeighbours;


    /**
     * Constructor to initialize an empty max heap with given maximum capacity.
     */
    public MaxHeap(ArrayList<NeighboursInfo> nearest, int maxsize)
    {
        // This keyword refers to current instance itself
        this.maxsize = maxsize;
        this.size = 0;
        Heap = new double[this.maxsize];
        for (NeighboursInfo a: nearest){
            insert(a.distance);
        }
        nearestNeighbours=new ArrayList<>();
        nearestNeighbours.addAll(nearest);

    }

    // Returning position of parent
    private int parent(int pos) { return (pos - 1) / 2; }

    // Returning left children
    private int leftChild(int pos) { return (2 * pos) + 1; }

    // Returning right children
    private int rightChild(int pos)
    {
        return (2 * pos) + 2;
    }

    // Returning true if given node is leaf
    private boolean isLeaf(int pos)
    {
        if (pos > (size / 2) && pos <= size) {
            return true;
        }
        return false;
    }

    // Swapping nodes
    private void swap(int fpos, int spos)
    {
        double tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    /*// Recursive function to max heapify given subtree
    private void maxHeapify(int pos)
    {
        if (isLeaf(pos))
            return;

        if (Heap[pos] < Heap[leftChild(pos)]
                || Heap[pos] < Heap[rightChild(pos)]) {

            if (Heap[leftChild(pos)]
                    > Heap[rightChild(pos)]) {
                swap(pos, leftChild(pos));
                maxHeapify(leftChild(pos));
            }
            else {
                swap(pos, rightChild(pos));
                maxHeapify(rightChild(pos));
            }
        }
    }*/

    // Inserts a new element to max heap
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

    // Remove an element from max heap
    public ArrayList<NeighboursInfo> extractMax(NeighboursInfo neighbour)
    {
        double popped = Heap[0];
        if (popped>neighbour.getDistance()) {
            Heap[0] = Heap[--size];
            //maxHeapify(0);
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