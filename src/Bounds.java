package src;

import java.io.Serializable;

/**
 * Java Class that represents the Bounds of an object in one dimension.
 */
public class Bounds implements Serializable {
    private double lower; //Lower bound of the range of object in one dimension
    private double upper; //Upper bound of the range of object in one dimension

    Bounds(double lower, double upper)
    {
        //If lower bound is bigger than upper then swap lower to upper
        if (lower > upper) {
            this.lower = upper;
            this.upper = lower;
        } else {
            this.lower = lower;
            this.upper = upper;
        }
    }

    /**
     * Getter that returns the lower bound of object in the specific dimension.
     * @return double value of lower bound
     */
    double getLower()
    {
        return lower;
    }

    /**
     * Getter that return the upper bound of object in the specific dimension.
     * @return double value of upper bound
     */
    double getUpper()
    {
        return upper;
    }
    /**
     * Function that transforms double to String and creates a String that contains lower and upper bounds of an interval.
     * @return a String of lower and upper bounds.
     */
    public String prettyPrint()
    {
        return String.valueOf(lower) + ' '+ String.valueOf(upper);
    }

    public double getLength()
    {
        return upper-lower;
    }

    /**
     * Calculates the overlap between two bounds in one dimension
     * @param A Bounds of one dimension of object A
     * @param B Bounds of one dimension of object B
     * @return the overlap value between object A and B in one dimension
     */
    public static Double calculateOverlapAxis(Bounds A, Bounds B)
    {
        double max = Math.min(A.upper,B.upper);
        double min = Math.max(A.lower,B.lower);
        if (max-min >=0)
            return max-min;
        else
            return 0.0;
    }
}
