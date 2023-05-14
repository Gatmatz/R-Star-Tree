public class NeighboursInfo {
    public long recordId;
    public double distance;

    NeighboursInfo(long recordId, double distance){
        this.recordId=recordId;
        this.distance=distance;
    }

    public double getDistance(){
        return distance;
    }

    public long getRecordId() {
        return recordId;
    }
}
