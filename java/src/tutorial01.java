/**
 * A hash table provides a way to associate unique keys
 * to values. It is an extension of the concept of an array -
 * an array maps ints to values, i.e. array[1] = new Object().
 * Instead of forcing you to use ints for keys, a hash table
 * allows you to use any type that can be hashed.
 *
 * For example, you might have hashtable["hello"] = new Object().
 * To do this we have to use the concept of hashing. A hash function,
 * for our purposes, takes an object and converts it into an integer.
 * Since all Java objects have a built in 'hashCode()' function, we can
 * do something like 'int hash = new Object().hashCode();'. We will make
 * extensive use of this method throughout the tutorial.
 *
 * In order to allow the user to use any type of Object-derived key in
 * our hash table, we need to make our class generic. To do this we
 * will introduce generic types K, V.
 *
 * @param <K> key type - i.e. Integer
 * @param <V> value type - i.e. String
 */
public class tutorial01<K, V> {
    /**
     * Our hash table needs to have a minimum size, so
     * for our case we will choose a power of 2. If the user
     * asks for a size smaller than this we will ignore their
     * request and use this instead.
     */
    private static int _DEFAULT_SIZE = 16;

    /**
     * We need a way to store the entries. For this tutorial
     */
    private class _Entry {
        K key;
        V value;
    }
}
