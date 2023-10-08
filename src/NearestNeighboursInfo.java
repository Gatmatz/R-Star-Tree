package src;

public class NearestNeighboursInfo {
    public long recordId;
    public double minDistance;
//    public double minMaxDistance;

    /**
     * A constructor that holds some characteristics of a record such as
     * the record's id and the distance between it and search point.
     * @param recordId record ID.
     * @param distance the distance of two points.
     */
    NearestNeighboursInfo(long recordId, double distance){
        this.recordId=recordId;
        this.minDistance=distance;
    }

    /**
     * Gets the distance of two points.
     * @return double distance of two points.
     */
    public double getMinDistance(){
        return minDistance;
    }

    /**
     * Gets the distance of two points with square.
     * @return double distance of two points.
     */
    public double getSquareMinDistance(){
        return Math.sqrt(minDistance);
    }

    /**
     * Gets the id of a specific record.
     * @return long record ID.
     */
    public long getRecordId() {
        return recordId;
    }


}
