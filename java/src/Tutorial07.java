import java.util.Iterator;
import java.util.Map;

/**
 * Welcome to the final tutorial! Now we will make it so
 * the user can iterate over the elements of our hash table
 * using the range-for loop. For example, we might end up with
 * something like this:
 *      Tutorial07<Integer, String> table = new Tutorial07<>();
 *      for (Map.Entry<Integer, String> e : table) {
 *          ...
 *      }
 *
 * To do this we need to do a few things. First, we are going to
 * change our _Entry class so that it implements the Map.Entry
 * interface.
 *
 * Next we will need to create a new internal class that
 * we will call _Iterator. It will implement the Iterator interface and
 * keep track of which element inside the table that the user is
 * currently looking at, and it will also be able to move to
 * the next element.
 *
 * Also, pay attention to the Tutorial07 class: it now implements
 * the Iterable interface, which is very important! The Iterable interface
 * requires us to include a method called "iterator()" which automatically
 * gets called when the user sets up a for-each loop using our class.
 *
 * Exercises:
 *      1) Can you add a putAll function that takes a Collection of Map.Entry
 *         objects and adds them all to the table?
 *      2) Can you add a putIfAbsent that only modifies the table if the key
 *         does not exist at all?
 *      3) Can you add support for a .keySet() method which returns a set of
 *         only the keys without their values?
 *      4) Can you create a HashSet type of class that uses a hash map behind
 *         the scenes?
 *
 * @param <K> key type - i.e. Integer
 * @param <V> value type - i.e. String
 */
public class Tutorial07<K, V> implements Iterable<Map.Entry<K, V>> {
    /**
     * Our hash table needs to have a minimum capacity, so
     * for our case we will choose a power of 2. If the user
     * asks for a size smaller than this we will ignore their
     * request and use this instead.
     */
    private static final int _MINIMUM_CAPACITY = 16;

    /**
     * For performance reasons, we will always resize our table
     * whenever it is 75% full.
     */
    private static final double _LOAD_FACTOR = 0.75;

    /**
     * An entry represents a mapping of a single key
     * to a single value. This will form the backbone of our
     * hash table and will be how we store all of the things
     * the user puts into it.
     */
    private static class _Entry<K, V> implements Map.Entry<K, V> {
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
        _Entry<K, V> next = null;

        _Entry(K key, V value, int hashCode) {
            this.key = key;
            this.value = value;
            this.hashCode = hashCode;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            // Note: this returns the old value
            V old = this.value;
            this.value = value;
            return old;
        }
    }

    /**
     * This class is what enables the user to iterate over the
     * entries in our hash table. It keeps track of both the
     * current _Entry object, as well as the location in the table.
     * Whenever we need to move to the next element, this class has
     * just enough internal information to make the move.
     */
    private class _Iterator implements Iterator<Map.Entry<K, V>> {
        /**
         * Current entry that we're on. This lets us keep track of
         * where the user is inside of our map, and also allows us to
         * move to the next entry inside of the chain.
         */
        private _Entry<K, V> _currentEntry = null;

        /**
         * Store a reference to the table so that it's impossible
         * for it to be garbage collected while an instance of an iterator
         * exists.
         */
        private _Entry[] _tableRef = _table;

        /**
         * Once we run out of links in the _currentEntry chain, we need
         * to move to the next index in the table. This keeps track of
         * our current index into the table.
         */
        private int _entryIndex = 0;

        @Override
        public boolean hasNext() {
            // If we have another link in the current chain, immediately
            // return null
            if (_currentEntry != null && _currentEntry.next != null) return true;
            int capacity = _tableRef.length;
            // Otherwise, move onto the next entries in the table and see
            // if any of them are non-null. If our _currentEntry is null,
            // then we are going to start indexing into the table using whatever
            // value is already present in _entryIndex. If not null, we need to
            // start checking at the next set of entries (_entryIndex + 1)
            int index = _currentEntry == null ? _entryIndex : _entryIndex + 1;
            for (; index < capacity; ++index) {
                _Entry<K, V> e = _tableRef[index];
                if (e != null) return true;
            }
            return false; // We're on the final entry
        }

        @Override
        public Map.Entry<K, V> next() {
            _Entry<K, V> e = _currentEntry != null ? _currentEntry.next : null;
            // If e is null then we can't easily get the next entry, and instead
            // have to start looking through the table
            if (e == null) {
                int capacity = _tableRef.length;
                // If _currentEntry is null then this is probably the first time
                // that next() is being called, so we will just let _entryIndex stay
                // its same value. Otherwise we will set it to _entryIndex + 1 to
                // get the next list of entries.
                _entryIndex = _currentEntry == null ? _entryIndex : _entryIndex + 1;
                for (; _entryIndex < capacity; ++_entryIndex) {
                    e = _tableRef[_entryIndex];
                    if (e != null) break;
                }
            }
            _currentEntry = e;
            return _currentEntry;
        }
    }

    /**
     * Now we also need to add a list of entries so we
     * can store stuff.
     */
    private _Entry[] _table;

    /**
     * Allows our class to have its elements iterated over.
     */
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new _Iterator();
    }

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
    public Tutorial07() {
        this(_MINIMUM_CAPACITY);
    }

    /**
     * Allows the user to set the capacity they want the table
     * to start off with, so long as it's not smaller than the minimum.
     */
    public Tutorial07(int capacity) {
        // First make sure that the user did not request
        // a smaller capacity than the default
        _capacity = capacity < _MINIMUM_CAPACITY ? _MINIMUM_CAPACITY : capacity;

        // Extend this constructor to create the internal table
        _table = new _Entry[_capacity];
    }

    /**
     * Resizes the internal hash table given a new capacity. One extremely
     * important thing to keep in mind is that since all indices are calculated
     * in terms of the current table's capacity, when we resize we invalidate
     * all existing key-value locations. Because of this, we unfortunately have
     * to go back through and re-calculate all of their positions (in effect,
     * re-add everything into the new table).
     *
     * @param newCapacity should not be smaller than the existing capacity!
     */
    private void _resize(int newCapacity) {
        if (newCapacity < _capacity) return;
        // Create a new table with the larger capacity
        _Entry[] table = new _Entry[newCapacity];
        // Re-insert all the entries since it's very likely
        // that their locations need to change since we increased
        // the table size
        for (_Entry<K, V> e : _table) {
            _Entry<K, V> current = e;
            while (current != null) {
                _put(current.key, current.value, current.hashCode, table);
                current = current.next;
            }
        }
        // Overwrite the current table/capacity
        _table = table;
        _capacity = newCapacity;
    }

    /**
     * Special private instance of put - it takes a table. This is useful
     * because when we perform a table resize, we actually need to re-add
     * all of the entries into the new table!
     */
    private boolean _put(K key, V value, int hash, _Entry[] table) {
        // Get the capacity
        int capacity = table.length;

        /*
         * Now we need to convert the hash code into an
         * index we can actually use in our table. Since
         * the hash code is a 32-bit integer, it can have huge
         * values that will give us index out of bounds errors. To
         * fix this, we use modulus (%) with _capacity to constrain
         * it to the range of indices we want.
         */
        int index = hash % capacity;

        _Entry<K, V> e = table[index];
        // Collision detection: Is there already an entry at this index?
        if (e != null) {
            _Entry<K, V> current = e;
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
            _Entry<K, V> newEntry = new _Entry<>(key, value, hash);
            newEntry.next = e.next; // Make sure we don't break the existing chain
            e.next = newEntry;
        } else {
            // That index has nothing there, so put a brand new entry
            // in its spot
            table[index] = new _Entry<>(key, value, hash);
        }
        return true; // added
    }

    /**
     * Adds a new key-value pair to our hash table.
     * @return true if it was added and false otherwise.
     */
    public boolean put(K key, V value) {
        // First check if we need to resize - would it be
        // more efficient if we cached adjustedCapacity by making it
        // a member variable?
        int adjustedCapacity = (int)(_capacity * _LOAD_FACTOR);
        if (_size >= adjustedCapacity) {
            // We will opt to double our capacity during
            // each resize
            _resize(_capacity * 2);
        }
        boolean result = _put(key, value, key.hashCode(), _table);
        if (result) ++_size;
        return result;
    }

    /**
     * @return true if the given key exists in our table and false if not.
     */
    public boolean containsKey(K key) {
        // Now that we have get(), this method becomes much simpler
        return get(key) != null;
    }

    /**
     * Removes a key-value pair from the table, if it exists.
     * @param key key to remove (removes its value as well)
     * @return true if it was removed and false if it didn't exist
     */
    public boolean remove(K key) {
        int hash = key.hashCode();
        int index = hash % _capacity;

        _Entry<K, V> current = _table[index];
        _Entry<K, V> previous = null;
        // Walk the list and see if the key exists - remove it
        // if it does, but be careful not to break the existing chain!
        while (current != null) {
            if (current.key.equals(key)) {
                // Unlink this node from the linked list
                if (previous == null) {
                    _table[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                // Make sure we change the size
                --_size;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false; // Key not exist
    }

    /**
     * @param key key whose value you want to retrieve
     * @return a value if its key-value pair existed, but null otherwise
     */
    public V get(K key) {
        int hash = key.hashCode();
        int index = hash % _capacity;
        _Entry<K, V> e = _table[index];
        while (e != null) {
            if (e.key.equals(key)) return e.value;
            e = e.next;
        }
        return null;
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
        Tutorial07<Integer, String> table = new Tutorial07<>();
        System.out.println("size: " + table.size() + ", capacity: " + table.capacity());

        // Add a whole bunch of objects
        for (int i = 0; i < 512; ++i) {
            boolean result = table.put(i, Integer.toString(i));
            assert(result);
            assert(table.containsKey(i));
        }
        System.out.println("size after add: " + table.size() +
                ", capacity after add: " + table.capacity());

        // Now retrieve the objects and remove them at the same time
        for (int i = 0; i < 512; ++i) {
            String result = table.get(i);
            assert(result != null && result.equals(Integer.toString(i)));
            // Now remove it
            table.remove(i);
            assert( !table.containsKey(i) );
        }
        System.out.println("size after add: " + table.size() +
                ", capacity after add: " + table.capacity());

        // Now use the second constructor
        Tutorial07<Integer, Object> table2 = new Tutorial07<>(256);
        System.out.println("size: " + table2.size() + ", capacity: " + table2.capacity());

        // Add a few entries and then iterate over them
        for (int i = 0; i < 16; ++i) {
            table2.put(i, Integer.toString(i));
        }

        for (Map.Entry<Integer, Object> e : table2) {
            System.out.println(e.getKey() + ", " + e.getValue());
        }
        System.out.println(table2.containsKey(0));
    }
}
