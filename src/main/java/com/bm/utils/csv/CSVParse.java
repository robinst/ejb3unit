package com.bm.utils.csv;

import java.io.IOException;

/**
 * Read files in comma separated value format.
 * 
 * @author Daniel Wiese
 * @since 17.04.2006
 */
public interface CSVParse {

	/**
	 * Read the next value from the file. The line number from which this value
	 * was taken can be obtained from getLastLineNumber().
	 * 
	 * @return the next value or null if there are no more values.
	 * @throws IOException
	 *             if an error occurs while reading.
	 * 
	 */
	String nextValue() throws IOException;

	/**
	 * Get the line number that the last token came from.
	 * 
	 * @return line number or -1 if no tokens have been returned yet.
	 * 
	 */
	int lastLineNumber();

	/**
	 * Get the line number that the last token came from.
	 * <p>
	 * New line breaks that occur in the middle of a token are not counted in
	 * the line number count.
	 * 
	 * @return line number or -1 if no tokens have been returned yet.
	 * 
	 */
	int getLastLineNumber();

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
	void changeDelimiter(char newDelim);

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
	void changeQuote(char newQuote);

	/**
	 * Close any stream upon which this parser is based.
	 * 
	 * @since ostermillerutils 1.02.26
	 * @throws IOException
	 *             if an error occurs while closing the stream.
	 */
	void close() throws IOException;

}
