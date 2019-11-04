package dict;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;


/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * 
 * The file format has one keyword per line:
 * <pre>word:def1:def2:def3,...</pre>
 * 
 * Note that an empty definition list is allowed (in which case the entry would have the form: <pre>word:</pre> 
 * 
 * @author talm
 *
 */
public class InMemoryDictionary extends TreeMap<String,String> implements PersistentDictionary  {
	private static final long serialVersionUID = 1L; // (because we're extending a serializable class)

	//constructor
	private File file;
	public InMemoryDictionary(File dictFile) {
		file = dictFile;
	}


	@Override
	public void open() throws IOException {
		//check if the file opens
		if (!file.exists()){
			if (!file.createNewFile()){
				throw new IOException("New file can not be created.");
			}
		}

		BufferedReader reader = null;
		//initialize 3 string for the word, definition, and line to read from the file
		String def = "", word = "", line = "";
		int i;
		try {
			//try opening the BufferedReader
			reader = new BufferedReader( new FileReader(file));

			//run a loop on all the lines in the file
			while((line = reader.readLine()) != null){
				//for each line find the ':' character and split it there
				i = line.indexOf(':');
				//spiting the line
				word = line.substring(0,i);
				def = line.substring(i+1);
				this.put(word, def);
				//initializing for a case that definition will be empty
				word = "";
				def = "";
			}
		} catch (IOException e) {
			//print err message to system.err
			System.err.println("Error: " + e);
		} finally {
			if (reader != null) reader.close();
		}
	}

	@Override
	public void close() throws IOException {
		BufferedWriter writer = null;
		//initialize 3 string for the word, definition, and line to read from the file
		String def = "";
		try {
			//try to open a BufferedWriter to write to the file
			writer = new BufferedWriter(new FileWriter(file));

			//run a loop to write all the lines in the map to a file
			for(String word : this.keySet()){
				def = this.get(word);
				//output to the file the full line with the ':' char
				writer.write(word + ":" + def);
				//enter a new line character
				writer.newLine();
				//flush the writer
				writer.flush();
			}
		} catch (IOException e) {
			//print err message to system.err
			System.err.println("Error: " + e);
		} finally {
			if (writer != null) writer.close();
		}
	}
	
}
