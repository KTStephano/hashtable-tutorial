/**
 * A hash table provides a way to associate unique keys
 * to values. It is an extension of the concept of an array -
 * an array maps ints to values, i.e. array[1] = new Object().
 * Instead of forcing you to use ints for keys, you can use any
 * object type that is capable of being hashed (more on this below).
 *
 * For example, you might have hashtable["hello"] = new Object().
 * To do this we have to use the concept of hashing. A hash function,
 * for our purposes, takes an object and converts it into an integer. Once
 * we have the integer value, we can use it as a sort of index just like
 * we would if we were implementing a regular array. Since all Java objects 
 * have a built in 'hashCode()' function, we can do something like 
 * 'int hash = new Object().hashCode();'. We will make extensive use of this 
 * method throughout the tutorial.
 *
 * In order to allow the user to use any type of Object-derived key in
 * our hash table, we need to make our class generic. To do this we
 * will introduce generic types K, V. It will allow our class to be
 * created as in `Tutorial01<Integer, String> table = new Tutorial01<>();`
 *
 * @param <K> key type - i.e. Integer
 * @param <V> value type - i.e. String
 */
public class Tutorial01<K, V> {
    /**
     * Our hash table needs to have a minimum capacity, so
     * for our case we will choose a power of 2. If the user
     * asks for a size smaller than this we will ignore their
     * request and use this instead.
     */
    private static final int _MINIMUM_CAPACITY = 16;

    /**
     * Size keeps track of how many key-value pairs currently
     * exist in our table. Not only does this allow us to report
     * to the user how many elements there are, but it allows us
     * to make decisions as to when we should grow the table.
     */
    private int _size = 0;

    /**
     * Unlike size, this keeps track of how many elements we
     * can store in the table. However (and this will become
     * clear later), it is possible for _size to be greater
     * than _capacity if we decide to never grow our table. In
     * these cases, the efficiency of our table will most likely
     * decrease.
     */
    private int _capacity;

    /**
     * Takes no arguments and sets the capacity to be the minimum.
     */
    public Tutorial01() {
        this(_MINIMUM_CAPACITY);
    }

    /**
     * Allows the user to set the capacity they want the table
     * to start off with, so long as it's not smaller than the minimum.
     */
    public Tutorial01(int capacity) {
        // First make sure that the user did not request
        // a smaller capacity than the default
        _capacity = capacity < _MINIMUM_CAPACITY ? _MINIMUM_CAPACITY : capacity;
    }

    /**
     * @return number of elements currently in the table
     */
    public int size() {
        return _size;
    }

    /**
     * @return current upper bound for the number of elements
     *         that can be stored in the table before it needs
     *         to resize itself.
     */
    public int capacity() {
        return _capacity;
    }

    // Some simple test code for this tutorial
    public static void main(String[] args) {
        // Use the default constructor
        Tutorial01<Integer, String> table = new Tutorial01<>();
        System.out.println("size: " + table.size() + ", capacity: " + table.capacity());

        // Now use the second constructor
        Tutorial01<Integer, Object> table2 = new Tutorial01<>(256);
        System.out.println("size: " + table2.size() + ", capacity: " + table2.capacity());
    }
}
