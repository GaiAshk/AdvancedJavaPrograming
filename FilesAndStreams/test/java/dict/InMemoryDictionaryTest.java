package dict;


import java.io.File;
import java.io.IOException;

public class InMemoryDictionaryTest extends PersistentDictionaryTest {
	@Override
    PersistentDictionary getDictionary(File file) throws IOException {
		return new InMemoryDictionary(dictFile);
	}
}
