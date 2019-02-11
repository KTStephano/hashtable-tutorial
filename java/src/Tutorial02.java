/**
 * In this tutorial we are going to add a way to actually
 * store the key-value pairs internally. The way we will accomplish
 * this is by adding two things: first, an internal class called
 * _Entry which stores a single Key and Value. Second we will add
 * an array of _Entry objects.
 *
 * Exercises:
 *     1) Tutorial 3 will implement the ability to let the user actually 
 *        add elements to the hash table. If you were to try to do this without
 *        looking at the solution, what would you do? Hint: You will
 *        need to make use of Java's hashCode() function as well
 *        as the modulus (%) operator.
 *
 * @param <K> key type - i.e. Integer
 * @param <V> value type - i.e. String
 */
public class Tutorial02<K, V> {
    /**
     * Our hash table needs to have a minimum capacity, so
     * for our case we will choose a power of 2. If the user
     * asks for a size smaller than this we will ignore their
     * request and use this instead.
     */
    private static final int _MINIMUM_CAPACITY = 16;

    /**
     * An entry represents a mapping of a single key
     * to a single value. This will form the backbone of our
     * hash table and will be how we store all of the things
     * the user puts into it.
     */
    private static class _Entry<K, V> {
        K key;
        V value;
        /* We store the hash code so that we can directly
         * reference it if needed rather than having to
         * re-compute it via key.hashCode()
         */
        final int hashCode;

        _Entry(K key, V value, int hashCode) {
            this.key = key;
            this.value = value;
            this.hashCode = hashCode;
        }
    }

    /**
     * Now we also need to add a list of entries so we
     * can store stuff.
     */
    private _Entry[] _table;

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
    public Tutorial02() {
        this(_MINIMUM_CAPACITY);
    }

    /**
     * Allows the user to set the capacity they want the table
     * to start off with, so long as it's not smaller than the minimum.
     */
    public Tutorial02(int capacity) {
        // First make sure that the user did not request
        // a smaller capacity than the default
        _capacity = capacity < _MINIMUM_CAPACITY ? _MINIMUM_CAPACITY : capacity;

        // Extend this constructor to create the internal table
        _table = new _Entry[_capacity];
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
        Tutorial02<Integer, String> table = new Tutorial02<>();
        System.out.println("size: " + table.size() + ", capacity: " + table.capacity());

        // Now use the second constructor
        Tutorial02<Integer, Object> table2 = new Tutorial02<>(256);
        System.out.println("size: " + table2.size() + ", capacity: " + table2.capacity());
    }
}
