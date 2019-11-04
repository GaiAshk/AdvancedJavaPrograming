package dict;

import java.io.IOException;

/**
 * Stores a dictionary: a map of words to definitions.
 * This class behaves like a regular in-memory map, 
 * with an extra {@link #close()} method.
 * 
 *  
 * @author talm
 *
 */
public interface PersistentDictionary {

	/**
	 * Open and Load the dictionary. 
	 * Accessing the dictionary is only allowed after the first opening.
	 * Calling this method will undo any changes since the last {@link #close()}.
	 *  
	 * @throws IOException
	 */
	public void open() throws IOException;
	
	/**
	 * Flush the contents of the dictionary to disk and close any open files. 
	 * After a close, the contents are guaranteed to persist across program executions.
	 * The dictionary may not be accessed until the open method is called.
	 * @throws IOException
	 */
	public void close() throws IOException;

	
	/*======= Methods to read/write definitions (a subset of Map<String,String>)  ======*/
	
	/**
	 * Returns the value to which the specified key is mapped, or null if this map 
	 * contains no mapping for the key.
	 * More formally, if this map contains a mapping from a key k to a value v such that
	 * (key==null ? k==null : key.equals(k)), then this method returns v; otherwise it
	 * returns null. (There can be at most one such mapping.)
	 * 
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
	 * @throws ClassCastException if the key is of an inappropriate type for this map
	 * @throws NullPointerException if the specified key is null
	 */
	public String get(Object key);
	
	/**
	 * Associates the specified value with the specified key in this map. 
	 * If the map previously contained a mapping for the key, the old value is replaced by the specified value. 
	 * (A map m is said to contain a mapping for a key k if and only if m.containsKey(k) would return true.)
	 *
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 * @throws ClassCastException if the key is of an inappropriate type for this map
	 * @throws NullPointerException if the specified key is null
	 * @throws UnsupportedOperationException if the remove operation is not supported by this map 
	 * @throws IllegalArgumentException if some property of the specified key or value prevents it from being stored in this map
	 */
	public String put(String key, String value);
	

	/**
	 * Returns true if this map contains a mapping for the specified key. 
	 * More formally, returns true if and only if this map contains a mapping for a key k such that 
	 * (key==null ? k==null : key.equals(k)). (There can be at most one such mapping.)
	 *
	 * @param key key whose presence in this map is to be tested
	 * @return true if this map contains a mapping for the specified key
	 * @throws ClassCastException if the key is of an inappropriate type for this map
	 * @throws NullPointerException if the specified key is null
	 */
	public boolean containsKey(Object key);
	
	/**
	 * Returns the number of key-value mappings in this map.
	 * @return the number of key-value mappings in this map.
	 */
	public int size(); 
    
	/**
	 * Removes the mapping for a key from this map if it is present.
	 * More formally, if this map contains a mapping from key k to value v such that
	 * (key==null ? k==null : key.equals(k)), that mapping is removed. 
	 * (The map can contain at most one such mapping.)
	 * Returns the value to which this map previously associated the key,
	 *  or null if the map contained no mapping for the key.
	 *  
	 * @param key key whose mapping is to be removed from the map
	 * @return the previous value associated with key, or null if there was no mapping for key.
	 * @throws ClassCastException if the key is of an inappropriate type for this map
	 * @throws NullPointerException if the specified key is null
	 * @throws UnsupportedOperationException if the remove operation is not supported by this map 
	 */
	public String remove(Object key);
	
	/**
	 * Removes all of the mappings from this map.
	 */
	public void clear();

}
