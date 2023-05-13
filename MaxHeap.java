public class MaxHeap {
    private long[] Heap;
    private int size;
    private int maxsize;

    /**
     * Constructor to initialize an empty max heap with given maximum capacity.
     */
    public MaxHeap(int maxsize)
    {
        // This keyword refers to current instance itself
        this.maxsize = maxsize;
        this.size = 0;
        Heap = new long[this.maxsize];
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
        long tmp;
        tmp = Heap[fpos];
        Heap[fpos] = Heap[spos];
        Heap[spos] = tmp;
    }

    // Recursive function to max heapify given subtree
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
    }

    // Remove an element from max heap
    public long extractMax()
    {
        long popped = Heap[0];
        Heap[0] = Heap[--size];
        maxHeapify(0);
        return popped;
    }

    // main driver method
    public static void main(String[] arg)
    {
        MaxHeap maxHeap = new MaxHeap(15);

        // Print and display the maximum value in heap
        System.out.println("The max val is "
                + maxHeap.extractMax());
    }
}