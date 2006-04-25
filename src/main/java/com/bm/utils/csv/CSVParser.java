package com.bm.utils.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * CSV is a file format used as a portable representation of a database. Each
 * line is one entry or record and the fields in a record are separated by
 * commas. Commas may be preceded or followed by arbitrary space and/or tab
 * characters which are ignored.
 * <P>
 * If field includes a comma or a new line, the whole field must be surrounded
 * with double quotes. When the field is in quotes, any quote literals must be
 * escaped by \" Backslash literals must be escaped by \\. Otherwise a backslash
 * and the character following will be treated as the following character, IE.
 * "\n" is equivalent to "n". Other escape sequences may be set using the
 * setEscapes() method. Text that comes after quotes that have been closed but
 * come before the next comma will be ignored.
 * <P>
 * Empty fields are returned as as String of length zero: "". The following line
 * has three empty fields and three non-empty fields in it. There is an empty
 * field on each end, and one in the middle. One token is returned as a space.<br>
 * 
 * <pre>
 *  ,second,,&quot; &quot;,fifth,
 * </pre>
 * 
 * <P>
 * Blank lines are always ignored. Other lines will be ignored if they start
 * with a comment character as set by the setCommentStart() method.
 * <P>
 * An example of how CVSLexer might be used:
 * 
 * <pre>
 * CSVParser shredder = new CSVParser(System.in);
 * shredder.setCommentStart(&quot;#;!&quot;);
 * shredder.setEscapes(&quot;nrtf&quot;, &quot;\n\r\t\f&quot;);
 * String t;
 * while ((t = shredder.nextValue()) != null) {
 * 	System.out.println(&quot;&quot; + shredder.lastLineNumber() + &quot; &quot; + t);
 * }
 * </pre>
 * 
 * <P>
 * Some applications do not output CSV according to the generally accepted
 * standards and this parse may not be able to handle it. One such application
 * is the Microsoft Excel spreadsheet. A separate class must be use to read
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public class CSVParser implements CSVParse {

	/**
	 * InputStream on which this parser is based.
	 * 
	 */
	private InputStream inStream;

	/**
	 * Reader on which this parser is based.
	 * 
	 */
	private Reader inReader;

	/**
	 * Does all the dirty work. Calls for new tokens are routed through this
	 * object.
	 * 
	 */
	private CSVLexer lexer;

	/**
	 * Token cache. Used for when we request a token from the lexer but can't
	 * return it because its on the next line.
	 * 
	 */
	private String tokenCache;

	/**
	 * Line cache. The line number that goes along with the tokenCache. Not
	 * valid if the tokenCache is null.
	 * 
	 */
	private int lineCache;

	/**
	 * The line number the last token came from, or -1 if no tokens have been
	 * returned.
	 * 
	 */
	private int lastLine = -1;

	/**
	 * Create a parser to parse comma separated values from an InputStream.
	 * <p>
	 * Byte to character conversion is done using the platform default locale.
	 * 
	 * @param in
	 *            stream that contains comma separated values.
	 * 
	 */
	public CSVParser(InputStream in) {
		inStream = in;
		lexer = new CSVLexer(in);
	}

	/**
	 * Create a parser to parse delimited values from an InputStream.
	 * <p>
	 * Byte to character conversion is done using the platform default locale.
	 * 
	 * @param in
	 *            stream that contains comma separated values.
	 * @param delimiter
	 *            record separator
	 * 
	 * @throws BadDelimiterException
	 *             if the specified delimiter cannot be used
	 * 
	 */
	public CSVParser(InputStream in, char delimiter) {
		inStream = in;
		lexer = new CSVLexer(in);
		changeDelimiter(delimiter);
	}

	/**
	 * Create a parser to parse comma separated values from a Reader.
	 * 
	 * @param in
	 *            reader that contains comma separated values.
	 * 
	 */
	public CSVParser(Reader in) {
		inReader = in;
		lexer = new CSVLexer(in);
	}

	/**
	 * Create a parser to parse delimited values from a Reader.
	 * 
	 * @param in
	 *            reader that contains comma separated values.
	 * @param delimiter
	 *            record separator
	 * 
	 * @throws BadDelimiterException
	 *             if the specified delimiter cannot be used
	 * 
	 */
	public CSVParser(Reader in, char delimiter) {
		inReader = in;
		lexer = new CSVLexer(in);
		changeDelimiter(delimiter);
	}

	/**
	 * Create a parser to parse delimited values from an InputStream.
	 * <p>
	 * Byte to character conversion is done using the platform default locale.
	 * 
	 * @param in
	 *            stream that contains comma separated values.
	 * @param escapes
	 *            a list of characters that will represent escape sequences.
	 * @param replacements
	 *            the list of replacement characters for those escape sequences.
	 * @param commentDelims
	 *            list of characters a comment line may start with.
	 * @param delimiter
	 *            record separator
	 * 
	 * @throws BadDelimiterException
	 *             if the specified delimiter cannot be used
	 * 
	 */
	public CSVParser(InputStream in, char delimiter, String escapes,
			String replacements, String commentDelims) {
		inStream = in;
		lexer = new CSVLexer(in);
		setEscapes(escapes, replacements);
		setCommentStart(commentDelims);
		changeDelimiter(delimiter);
	}

	/**
	 * Create a parser to parse comma separated values from an InputStream.
	 * <p>
	 * Byte to character conversion is done using the platform default locale.
	 * 
	 * @param in
	 *            stream that contains comma separated values.
	 * @param escapes
	 *            a list of characters that will represent escape sequences.
	 * @param replacements
	 *            the list of replacement characters for those escape sequences.
	 * @param commentDelims
	 *            list of characters a comment line may start with.
	 * 
	 */
	public CSVParser(InputStream in, String escapes, String replacements,
			String commentDelims) {
		inStream = in;
		lexer = new CSVLexer(in);
		setEscapes(escapes, replacements);
		setCommentStart(commentDelims);
	}

	/**
	 * Create a parser to parse delimited values from a Reader.
	 * 
	 * @param in
	 *            reader that contains comma separated values.
	 * @param escapes
	 *            a list of characters that will represent escape sequences.
	 * @param replacements
	 *            the list of replacement characters for those escape sequences.
	 * @param commentDelims
	 *            list of characters a comment line may start with.
	 * @param delimiter
	 *            record separator
	 * 
	 * @throws BadDelimiterException
	 *             if the specified delimiter cannot be used
	 * 
	 */
	public CSVParser(Reader in, char delimiter, String escapes,
			String replacements, String commentDelims) {
		inReader = in;
		lexer = new CSVLexer(in);
		setEscapes(escapes, replacements);
		setCommentStart(commentDelims);
		changeDelimiter(delimiter);
	}

	/**
	 * Create a parser to parse comma separated values from a Reader.
	 * 
	 * @param in
	 *            reader that contains comma separated values.
	 * @param escapes
	 *            a list of characters that will represent escape sequences.
	 * @param replacements
	 *            the list of replacement characters for those escape sequences.
	 * @param commentDelims
	 *            list of characters a comment line may start with.
	 * 
	 */
	public CSVParser(Reader in, String escapes, String replacements,
			String commentDelims) {
		inReader = in;
		lexer = new CSVLexer(in);
		setEscapes(escapes, replacements);
		setCommentStart(commentDelims);
	}

	/**
	 * Close any stream upon which this parser is based.
	 * 
	 * @throws IOException
	 *             if an error occurs while closing the stream.
	 */
	public void close() throws IOException {
		if (inStream != null) {
			inStream.close();
		}
		if (inReader != null) {
			inReader.close();
		}
	}

	/**
	 * get the next value.
	 * 
	 * @return the next value or null if there are no more values.
	 * @throws IOException
	 *             if an error occurs while reading.
	 * 
	 */
	public String nextValue() throws IOException {
		if (tokenCache == null) {
			tokenCache = lexer.getNextToken();
			lineCache = lexer.getLineNumber();
		}
		lastLine = lineCache;
		String result = tokenCache;
		tokenCache = null;
		return result;
	}

	/**
	 * Get the line number that the last token came from.
	 * <p>
	 * New line breaks that occur in the middle of a token are no counted in the
	 * line number count.
	 * 
	 * @return line number or -1 if no tokens have been returned yet.
	 * 
	 */
	public int lastLineNumber() {
		return lastLine;
	}

	/**
	 * Specify escape sequences and their replacements. Escape sequences set
	 * here are in addition to \\ and \". \\ and \" are always valid escape
	 * sequences. This method allows standard escape sequenced to be used. For
	 * example "\n" can be set to be a newline rather than an 'n'. A common way
	 * to call this method might be:<br>
	 * <code>setEscapes("nrtf", "\n\r\t\f");</code><br>
	 * which would set the escape sequences to be the Java escape sequences.
	 * Characters that follow a \ that are not escape sequences will still be
	 * interpreted as that character.<br>
	 * The two arguments to this method must be the same length. If they are
	 * not, the longer of the two will be truncated.
	 * 
	 * @param escapes
	 *            a list of characters that will represent escape sequences.
	 * @param replacements
	 *            the list of replacement characters for those escape sequences.
	 * 
	 */
	public void setEscapes(String escapes, String replacements) {
		lexer.setEscapes(escapes, replacements);
	}

	/**
	 * Change this parser so that it uses a new delimiter.
	 * <p>
	 * The initial character is a comma, the delimiter cannot be changed to a
	 * quote or other character that has special meaning in CSV.
	 * 
	 * @param newDelim
	 *            delimiter to which to switch.
	 * @throws BadDelimiterException
	 *             if the character cannot be used as a delimiter.
	 * 
	 */
	public void changeDelimiter(char newDelim) {
		lexer.changeDelimiter(newDelim);
	}

	/**
	 * Change this parser so that it uses a new character for quoting.
	 * <p>
	 * The initial character is a double quote ("), the delimiter cannot be
	 * changed to a comma or other character that has special meaning in CSV.
	 * 
	 * @param newQuote
	 *            character to use for quoting.
	 * @throws BadQuoteException
	 *             if the character cannot be used as a quote.
	 * 
	 */
	public void changeQuote(char newQuote) {
		lexer.changeQuote(newQuote);
	}

	/**
	 * Set the characters that indicate a comment at the beginning of the line.
	 * For example if the string "#;!" were passed in, all of the following
	 * lines would be comments:<br>
	 * 
	 * <pre>
	 *   # Comment
	 *   ; Another Comment
	 *   ! Yet another comment
	 * </pre>
	 * 
	 * By default there are no comments in CVS files. Commas and quotes may not
	 * be used to indicate comment lines.
	 * 
	 * @param commentDelims
	 *            list of characters a comment line may start with.
	 * 
	 */
	public void setCommentStart(String commentDelims) {
		lexer.setCommentStart(commentDelims);
	}

	/**
	 * Get the number of the line from which the last value was retrieved.
	 * 
	 * @return line number or -1 if no tokens have been returned.
	 * 
	 */
	public int getLastLineNumber() {
		return lastLine;
	}
}
