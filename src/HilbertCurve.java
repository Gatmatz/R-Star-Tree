package src;

/**
 * Java Class that contains all required tools to implement a Hilbert Curve.
 * The Hilbert curve is a continuous fractal space-filling curve.
 * It is used for imposing a linear ordering on multidimensional data.
 */
public class HilbertCurve {
    private static final int dimensions = DataFile.nofCoordinates; //The number of dimensions of data
    private static final int precisionBits = 16; //Precision bits. Set to 16 for safety.
    private static final int dimensionSize = 1 << precisionBits; //Represents the size of each dimension
    private static int mask = dimensionSize -1; //Variable to extract the relevant bits from the coordinates

    /**
     * Function that converts a multidimensional point to a Hilbert value.
     * Firstly, it scales the coordinates of the record by multiplying them with the size dimensionSize.
     * Then, it calls the hilbertIndex function with the scaled coordinates.
     * @param r a record to be converted.
     * @return the position of the given record on the Hilbert curve.
     */
    public static long convertToDValue(Record r) {
        long[] coordinates = new long[dimensions];
        //Scale the coordinates of each dimension.
        for (int i = 0; i < dimensions; i++) {
            coordinates[i] = Math.round(r.getCoordinates().get(i) * dimensionSize);
        }
        return hilbertIndex(coordinates);
    }

    /**
     * Function that calculates the Hilbert index for a given set of coordinates on the Hilbert curve.
     * @param coordinates the given set of coordinates.
     * @return the position of the coordinates along the curve
     */
    private static long hilbertIndex(long[] coordinates) {
        long index = 0;
        //Iteration over each bit position from the most significant bit (bits-1) to 0.
        for (int bit = precisionBits - 1; bit >= 0; bit--) {
            long hilbertValue = 0;
            //For each bit position, iteration over each dimension of the coordinates.
            for (int i = 0; i < dimensions; i++) {
                hilbertValue <<= 1;
                hilbertValue |= (coordinates[i] >>> bit) & 1;
            }

            index |= hilbertValue << (2 * bit);
            //If the current bit position is not the last bit
            if (bit > 0) {
                hilbertRotate(coordinates, mask, hilbertValue);
            }
        }
        return index;
    }

    /**
     * Function that applies a rotation operation to the coordinates depending on the Hilbert value and the bit position.
     * @param coordinates current coordinates.
     * @param mask the given mask
     * @param hilbertValue the Hilbert value at that bit position
     */
    private static void hilbertRotate(long[] coordinates,long mask, long hilbertValue) {
        int n = 2 << (dimensions - 1);
        for (int i = 0; i < dimensions; i++) {
            //If bit at position it performs a rotation operation on the coordinates.
            if (((n >> i) & 1) == 1) {
                if ((hilbertValue & 1) == 1) {
                    coordinates[i] = coordinates[i] ^ ((~coordinates[(i + 1) % dimensions]) & mask);
                } else {
                    coordinates[i] = coordinates[i] ^ (coordinates[(i + 1) % dimensions] & mask);
                }
            }
        }
    }
}