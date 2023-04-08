import java.util.ArrayList;

public class Block {
    public long blockId;
    public ArrayList<Record> records;

    public Block(long id){
        this.blockId=id;
        if (id==0){

        }else
            records= new ArrayList<>();
    }


}
