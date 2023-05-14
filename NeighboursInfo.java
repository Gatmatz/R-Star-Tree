public class NeighboursInfo {
    public long recordId;
    public double distance;

    /**
     * A constructor that holds some characteristics of a record such as
     * the record's id and the distance between it and search point.
     * @param recordId record ID.
     * @param distance the distance of two points.
     */
    NeighboursInfo(long recordId, double distance){
        this.recordId=recordId;
        this.distance=distance;
    }

    /**
     * Gets the distance of two points.
     * @return double distance of two points.
     */
    public double getDistance(){
        return distance;
    }

    /**
     * Gets the id of a specific record.
     * @return long record ID.
     */
    public long getRecordId() {
        return recordId;
    }
}
