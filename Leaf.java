/**
 * Java Class that represents a leaf node in the R*-Tree.
 * Based on R*-tree implementation a leaf node has a minimum bounding rectangle of object(extended from NodeEntry) and
 * the identifiers of the corresponding object which is the block index and the record index in the specific block, saved in dataFile.
 */
public class Leaf extends NodeEntry
{
    private int dataBlockID; //Block index that points to a specific block in dataFile.
    private int recordID; //Record index that points into a Record in a specific block.


    Leaf(int dataBlockID,int recordID, MBR mbr) {
        super(mbr);
        this.dataBlockID = dataBlockID;
        this.recordID = recordID;
    }

    /**
     * Getter function that returns the block index of a DataBlock in dataFile.
     * @return integer index of a block in the DataFile
     */
    int getDataBlockID()
    {
        return dataBlockID;
    }

    /**
     * Getter function that return the record index in the dataBlock saved in dataFile.
     * @return integer index of a record in current block stored in DataFile.
     */
    int getRecordID()
    {
        return recordID;
    }
}
