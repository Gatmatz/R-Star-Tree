import java.io.Serializable;
import java.util.ArrayList;

public class DataBlock implements Serializable
{
    public int blockId;
    public ArrayList<Record> records;
    public DataBlock(int blockId)
    {
        this.blockId=blockId;
        records=new ArrayList<>();
    }
    public void addRecord(Record n)
    {
        records.add(n);
    }

    public long getNofRecords()
    {
        return records.size();
    }
}
