import java.util.ArrayList;

public class LinearRangeQueryBoundingBox {
    private ArrayList<Bounds> searchBoundingBox;
    private ArrayList<Long> qualifyingRecordIds;

    /**
     * The constructor of a point for which I run a range query withing a specific bounding box.
     * @param bounds the bounds of the search Bounding Box.
     */
    LinearRangeQueryBoundingBox(ArrayList<Bounds> bounds) {
        searchBoundingBox = bounds;
        qualifyingRecordIds = new ArrayList<>();
    }

    /**
     * Function which execute a range query withing a specific bounding box.
     * It examines all the blocks and all their records to find the included points.
     * With linear search.
     */
    public void create() {
        DataBlock block0 = DataFile.readDataFileBlock(0);
        int nofBlocks = (int) block0.records.get(2).id;
        int blockId = 1;
        while (blockId <= nofBlocks) {
            DataBlock block;
            block = DataFile.readDataFileBlock(blockId);
            ArrayList<Record> recordsInBlock;
            recordsInBlock = block.records;
            if (recordsInBlock != null) {
                for (Record record : recordsInBlock) {
                    if (check(searchBoundingBox, record)) {
                        qualifyingRecordIds.add(record.getId());
                    }

                }
            } else
                throw new IllegalStateException("Can't read records from datafile.");
            blockId++;
        }
    }

    /**
     * Prints the qualifying records.
     */
    public void print() {
        for (long a : qualifyingRecordIds) {
            System.out.println("id=" + a);
        }
    }

    /**
     * Prints the number of qualifying records.
     */
    public void printSize() {
        System.out.println("Returns " + qualifyingRecordIds.size() + " records");
    }

    /**
     * Checks if a record type Record is inside the bounding box.
     * @param searchBoundingBox the bounding box for which I am reviewing the points.
     * @param record the record for which I check if it is inside the bounding box.
     * @return true if the point is in the rectangle, else false.
     */
    public boolean check(ArrayList<Bounds> searchBoundingBox, Record record) {
        int size = DataFile.getNofCoordinates();
        int counter = 0;
        for (int i = 0; i < size; i++) {
            if (record.getCoordinates().get(i) >= searchBoundingBox.get(i).getLower() && record.getCoordinates().get(i) <= searchBoundingBox.get(i).getUpper())
                counter++;
        }
        if (counter == size)
            return true;
        else
            return false;
    }
}



