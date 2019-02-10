/**
 * In this tutorial we will introduce the ability for
 * our hash map to dynamically grow whenever it starts
 * to get to full. To do this, we will introduce the concept
 * of --load balancing--. We won't actually let our hash table
 * completely fill up before we resize.
 *
 * @param <K> key type - i.e. Integer
 * @param <V> value type - i.e. String
 */
public class Tutorial05<K, V> {
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
    private class _Entry<K, V> {
        K key;
        V value;
        /* We store the hash code so that we can directly
         * reference it if needed rather than having to
         * re-compute it via key.hashCode()
         */
        final int hashCode;
        /*
         * Each entry can now act as a linked list so that we
         * can stack multiple entries at the same index!
         */
        _Entry next = null;

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
    public Tutorial05() {
        this(_MINIMUM_CAPACITY);
    }

    /**
     * Allows the user to set the capacity they want the table
     * to start off with, so long as it's not smaller than the minimum.
     */
    public Tutorial05(int capacity) {
        // First make sure that the user did not request
        // a smaller capacity than the default
        _capacity = capacity < _MINIMUM_CAPACITY ? _MINIMUM_CAPACITY : capacity;

        // Extend this constructor to create the internal table
        _table = new _Entry[_capacity];
    }

    /**
     * Adds a new key-value pair to our hash table.
     * @return true if it was added and false otherwise.
     */
    public boolean put(K key, V value) {
        // First compute the hash code using Java's
        // build-in hashCode() function
        int hash = key.hashCode();

        /*
         * Now we need to convert the hash code into an
         * index we can actually use in our table. Since
         * the hash code is a 32-bit integer, it can have huge
         * values that will give us index out of bounds errors. To
         * fix this, we use modulus (%) with _capacity to constrain
         * it to the range of indices we want.
         */
        int index = hash % _capacity;

        _Entry e = _table[index];
        // Collision detection: Is there already an entry at this index?
        if (e != null) {
            _Entry current = e;
            // We first need to walk the linked list to make sure
            // the key we're adding doesn't exist - remember, a hash table
            // requires all keys to be unique!
            while (current != null) {
                // If they key exists then we will overwrite its value,
                // but will not add a new key-value pair
                if (current.key.equals(key)) {
                    current.value = value;
                    return false;
                }
                current = current.next;
            }

            // If we got here then the key-value pair did not exist, so
            // add it!
            _Entry newEntry = new _Entry<>(key, value, hash);
            newEntry.next = e.next; // Make sure we don't break the existing chain
            e.next = newEntry;
        } else {
            // That index has nothing there, so put a brand new entry
            // in its spot
            _table[index] = new _Entry<>(key, value, hash);
        }

        ++_size;
        return true; // added
    }

    /**
     * @return true if the given key exists in our table and false if not.
     */
    public boolean containsKey(K key) {
        // Perform the same process as with put() to get the hash code
        // and then convert it to an index, except this time we're not
        // actually modifying the table
        int hash = key.hashCode();
        int index = hash % _capacity;

        _Entry e = _table[index];
        // Walk the list at this index and see if the key exists
        while (e != null) {
            if (e.key.equals(key)) return true;
            e = e.next;
        }
        return false;
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
        Tutorial05<Integer, String> table = new Tutorial05<>();
        System.out.println("size: " + table.size() + ", capacity: " + table.capacity());

        // Add a whole bunch of objects
        for (int i = 0; i < 256; ++i) {
            table.put(i, "lol");
            assert(table.containsKey(i));
        }
        System.out.println("size after add: " + table.size() +
                ", capacity after add: " + table.capacity());

        // Now use the second constructor
        Tutorial05<Integer, Object> table2 = new Tutorial05<>(256);
        System.out.println("size: " + table2.size() + ", capacity: " + table2.capacity());
    }
}
