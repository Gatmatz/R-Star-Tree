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
    String prettyPrint()
    {
        return String.valueOf(lower) + ' '+ String.valueOf(upper);
    }
}
