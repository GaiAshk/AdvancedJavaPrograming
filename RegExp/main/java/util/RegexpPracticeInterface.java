package util;

import java.util.Calendar;
import java.util.List;

/**
 * Interface used by automated tests.
 */
public interface RegexpPracticeInterface {
    String findSingleQuotedTextSimple(String input);

    String findDoubleQuotedTextSimple(String input);

    List<String> findDoubleOrSingleQuoted(String input);

    Calendar parseDate(String input);

    List<String> wordTokenize(String input);

    List<String> findSingleQuotedTextWithEscapes(String input);

    List<String> findDoubleQuotedTextWithEscapes(String input);

    List<AVPair> parseAvPairs(String input);

    List<AVPair> parseAvPairs2(String input);
}
