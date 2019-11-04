package util;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is only used to practice regular expressions.
 * @author talm
 *
 */
public class RegexpPractice implements RegexpPracticeInterface {
	/**
	 * Search for the first occurrence of text between single quotes, return the text (without the quotes).
	 * Allow an empty string. If no quoted text is found, return null. 
	 * Some examples :
	 * <ul>
	 * <li>On input "this is some 'text' and some 'additional text'" the method should return "text".
	 * <li>On input "this is an empty string '' and another 'string'" it should return "".
	 * </ul>
	 * @param input
	 * @return the first occurrence of text between single quotes
	 */
	public String findSingleQuotedTextSimple(String input) {
		Pattern p = Pattern.compile("\'([^\']*)\'");
		Matcher m = p.matcher(input);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	
	/**
	 * Search for the first occurrence of text between double quotes, return the text (without the quotes).
	 * (should work exactly like {@link #findSingleQuotedTextSimple(String)}), except with double instead 
	 * of single quotes.
	 * @param input
	 * @return the first occurrence of text between double quotes
	 */
	public String findDoubleQuotedTextSimple(String input) {
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(input);
		if(m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	/**
	 * Search for the all occurrences of text between single quotes <i>or</i> double quotes. 
	 * Return a list containing all the quoted text found (without the quotes). Note that a double-quote inside
	 * a single-quoted string counts as a regular character (e.g, on the string [quote '"this"'] ["this"] should be returned).  
	 * Allow empty strings. If no quoted text is found, return an empty list. 
	 * @param input
	 * @return
	 */
	public List<String> findDoubleOrSingleQuoted(String input) {
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("('|\")(.*?)\\1");
		Matcher m = p.matcher(input);
		while (m.find()) {
			list.add(m.group(2));
		}
		return list;
	}

	/**
	 * Parse a date string with the following general format:<br> 
	 * Wdy, DD-Mon-YYYY HH:MM:SS GMT<br>
	 * Where:
	 * 	 <i>Wdy</i> is the day of the week,
	 * 	 <i>DD</i> is the day of the month, 
	 *   <i>Mon</i> is the month,
	 *   <i>YYYY</i> is the year, <i>HH:MM:SS</i> is the time in 24-hour format,
	 *   and <i>GMT</i> is a the constant timezone string "GMT".
	 * 
	 * You should also accept variants of the format: 
	 * <ul>
	 * <li>a date without the weekday, 
	 * <li>spaces instead of dashes (i.e., "DD Mon YYYY"), 
	 * <li>case-insensitive month (e.g., allow "Jan", "JAN" and "jAn"),
	 * <li>a two-digit year (assume it's between 1970 and 2069 in that case)
	 * <li>a missing timezone
	 * <li>allow multiple spaces wherever a single space is allowed.
	 * </ul> 
	 *     
	 * The method should return a java {@link Calendar} object with fields  
	 * set to the corresponding date and time. Return null if the input is not a valid date string.
	 * @param input
	 * @return
	 */
	public Calendar parseDate(String input) {
		int year = 0, month = 0, date = 0, hourOfDay = 0, minute = 0, second = 0;
		Pattern datePattern = Pattern.compile("(\\w{3},\\s*)?([0-9]{2})(-|\\s+)(\\w{3})(-|\\s+)([0-9]{2}([0-9]{2})?)\\s+([0-9]{2}):([0-9]{2}):([0-9]{2})(\\s+GMT)?");
		Matcher m = datePattern.matcher(input);
		if (!m.matches()) {
			return null;
		} else {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			date = Integer.parseInt(m.group(2));

			//finding the month
			String s = m.group(4).toLowerCase();
			if (s.charAt(0) == 'j') {
				if (s.charAt(1) == 'a') month = 0;
				else if (s.charAt(2) == 'n') month = 5;
				else month = 6;
			} else if (s.charAt(0) == 'f') {
				month = 1;
			} else if (s.charAt(0) == 'm') {
				if (s.charAt(2) == 'r') month = 2;
				else month = 4;
			} else if (s.charAt(0) == 'a') {
				if (s.charAt(1) == 'p') month = 3;
				else month = 7;
			} else if (s.charAt(0) == 's') {
				month = 8;
			} else if (s.charAt(0) == 'o') {
				month = 9;
			} else if (s.charAt(0) == 'n') {
				month = 10;
			} else if (s.charAt(0) == 'd'){ month = 11; }
			else { month = -1;
			}

			if (month == -1) {
				return null;
			} else {
				year = Integer.parseInt(m.group(6));
				//calculte the year if it has only two digits
				if (year <= 99) {
					if(year >= 70) year += 1900;
					else year += 2000;
				}

				hourOfDay = Integer.parseInt(m.group(8));
				minute = Integer.parseInt(m.group(9));
				second = Integer.parseInt(m.group(10));

				cal.set(year, month, date, hourOfDay, minute, second);
				return cal;
			}
		}
	}


	/**
	 * Separate the input into <i>tokens</i> and return them in a list.
	 * A token is any mixture of consecutive word characters and single-quoted strings (single quoted strings
	 * may contain any character except a single quote).
	 * The returned tokens should not contain the quote characters. 
	 * A pair of single quotes is considered an empty token (the empty string).
	 * 
	 * For example, the input "this-string 'has only three tokens'" should return the list
	 * {"this", "string", "has only three tokens"}. 
	 * The input "this*string'has only two@tokens'" should return the list 
	 * {"this", "stringhas only two@tokens"}
	 * 
	 * @param input
	 * @return
	 */
	public List<String> wordTokenize(String input) {
		List<String> list = new ArrayList<>();
		String s;
		Pattern p = Pattern.compile("(\\w+|'[^']*')+");
		Matcher m = p.matcher(input);
		while (m.find()) {
			s = m.group(0).replaceAll("'","");
			list.add(s);
		}
		return list;
	}
	

	/**
	 * Search for the all occurrences of text between single quotes, but treating "escaped" quotes ("\'") as
	 * normal characters. Return a list containing all the quoted text found (without the quotes, and with the quoted escapes
	 * replaced). 
	 * Allow empty strings. If no quoted text is found, return an empty list. 
	 * Some examples :
	 * <ul>
	 * <li>On input "'This is not wrong' and 'this is isn\'t either", the method should return a list containing 
	 * 		("This is not wrong" and "This isn't either").
	 * <li>On input "No quoted \'text\' here" the method should return an empty list.
	 * </ul>
	 * @param input
	 * @return all occurrences of text between single quotes, taking escaped quotes into account.
	 */

	public List<String> findSingleQuotedTextWithEscapes(String input) {
		List<String> list = new ArrayList<>();
		String s = "";
		Pattern p = Pattern.compile("(^|[^\\\\]|\\G)'(((.*?)[^\\\\])??)'");
		Matcher m = p.matcher(input);
		while (m.find()) {
			s = m.group(2).replaceAll("\\\\'", "'");
			list.add(s);
		}

		return list;
	}

	/**
	 * Search for the all occurrences of text between single quotes, but treating "escaped" quotes ("\'") as
	 * normal characters. Return a list containing all the quoted text found (without the quotes, and with the quoted escapes
	 * replaced). 
	 * Allow empty strings. If no quoted text is found, return an empty list. 
	 * Some examples :
	 * <ul>
	 * <li>On input "'This is not wrong' and 'this is isn\'t either", the method should return a list containing 
	 * 		("This is not wrong" and "This isn't either").
	 * <li>On input "No quoted \'text\' here" the method should return an empty list.
	 * </ul>
	 * @param input
	 * @return all occurrences of text between single quotes, taking escaped quotes into account.
	 */
	public List<String> findDoubleQuotedTextWithEscapes(String input) {
		List<String> list = new ArrayList<>();
		String s = "";
		Pattern p = Pattern.compile("(^|[^\\\\]|\\G)\"(((.*?)[^\\\\])??)\"");
		Matcher m = p.matcher(input);
		while (m.find()) {
			s = m.group(2).replaceAll("\\\\\"", "\"");
			list.add(s);
		}

		return list;
	}


    /**
	 * Parse the input into a list of attribute-value pairs.
	 * The input should be a valid attribute-value pair list: attr=value; attr=value; attr; attr=value...
	 * If a value exists, it must be either an HTTP token (see {@link AVPair}) or a double-quoted string.
	 * 
	 * @param input
	 * @return
	 */
	public List<AVPair> parseAvPairs(String input) {
        // TODO: Implement
        return null;
	}


    /**
     * Parse the input into a list of attribute-value pairs, with input checking.
     * The input should be a valid attribute-value pair list: attr=value; attr=value; attr; attr=value...
     * If a value exists, it must be either an HTTP token (see {@link AVPair}) or a double-quoted string.
     *
     * This  method should return null if the input is not a list of attribute-value pairs with the format
     * specified above.
     * @param input
     * @return
     */
    @Override
    public List<AVPair> parseAvPairs2(String input) {
        // TODO: Implement
        return null;
    }
}
