/* Utility to convert lists of strings into CSV and vice versa.
 * The delimiter used is relatively hard to find in chat messages.
 * 
 * Usage:
 * 1. To convert a list to CSV, use toCSV(...)
 * It is up to the programmer to convert any non-string data types into plain strings.
 * 
 * 2. To convert a CSV string back into a list, use fromCSV(...)
 * It is up to the programmer to convert the strings into the necessary data types.
 */
package components;

import java.util.ArrayList;
import java.util.List;

public class CSVUtility {

	// Pretty unusual piece of text to be used as delimiter for the CSV
	private static final String separator = "#@#";

	/**
	 * Convert a list of strings into a single CSV string.
	 * 
	 * @param data
	 *            The list of strings.
	 * @return A single string as CSV.
	 */
	public static String toCSV(List<String> data) {
		Log.debug("CSVUtility", "toCSV", "working with list " + data);

		if (data != null) {
			StringBuilder csvBuilder = new StringBuilder();
			boolean isFirst = true;
			for (String element : data) {
				// Add the separator before all items except the first
				if (!isFirst)
					csvBuilder.append(separator);
				// Add the item itself
				csvBuilder.append(element);
				// It is no longer first if it was!
				if (isFirst)
					isFirst = false;
			}

			Log.debug("CSVUtility", "toCSV", "result: " + csvBuilder.toString());
			return csvBuilder.toString();

		} else
			return "";
	}

	/**
	 * Populate a full list from the supplied CSV string.
	 * 
	 * @param csvData
	 *            Single string to convert to a list.
	 * @return The list of strings developed from the supplied CSV.
	 */
	public static List<String> fromCSV(String csvData) {
		Log.debug("CSVUtility", "fromCSV", "working with string " + csvData);

		if (csvData != null) {
			List<String> data = new ArrayList<String>();
			// Split the csv into an array of strings
			String[] splitData = csvData.split(separator);
			// Populate the list
			for (String splitDataItem : splitData)
				data.add(splitDataItem);

			Log.debug("CSVUtility", "fromCSV", "result list: " + data);
			return data;

		} else
			return null;
	}
}
