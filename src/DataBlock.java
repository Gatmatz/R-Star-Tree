package src;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Java Class that represents a data block which consists of records.
 */
public class DataBlock implements Serializable
{
    public long blockId;
    public ArrayList<Record> records;

    /**
     *A constructor for a data block that takes as parameters the id of the block.
     * @param blockId block id.
     */
    public DataBlock(long blockId)
    {
        this.blockId=blockId;
        records=new ArrayList<>();
    }

    /**
     * Adds a new record to a specific block.
     * @param n the record I want to add to this block.
     */
    public void addRecord(Record n)
    {
        records.add(n);
    }

    /**
     * Gets the size of an ArrayList of Records.
     * @return long size from the Arraylist of Records.
     */
    public long getNofRecords()
    {
        return records.size();
    }
}
