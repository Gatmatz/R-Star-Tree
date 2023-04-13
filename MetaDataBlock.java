import java.io.Serializable;

public class MetaDataBlock implements Serializable
{
    public long blockId;
    private long nofRecords;
    private long nofBlocks;
    public MetaDataBlock()
    {
        this.blockId=0;
        nofRecords=0;
        nofBlocks=0;
    }

    public void increaseRecords()
    {
        nofRecords++;
    }

    public void increaseBlocks()
    {
        nofBlocks++;
    }

    //Getters
    public long getNofBlocks()
    {
        return nofBlocks;
    }

    public long getNofRecords()
    {
        return nofRecords;
    }

    public long getBlockId()
    {
        return blockId;
    }
}
