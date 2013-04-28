/* Utility for generating unique identifiers that are totally ordered.
 * Maintains separate tracks for each sequence, so that you can create 
 * sequences for clients, messages, transactions etc. as needed
 * 
 * Usage:
 * 1. Use getNextInSequence(...) to get the next ID in the sequence.
 */
package components;

import java.util.HashMap;

public class IDGenerator {

	/**
	 * Represents a null (ie. not set) IDs.
	 */
	public static final long NULL_ID = -1;

	private static HashMap<String, Long> counters = new HashMap<String, Long>();

	/**
	 * Returns a unique (total ordered) ID for the sequence.
	 * If the sequence doesn't exist yet, a new one is started.
	 * 
	 * @return A unique ID for the sequence.
	 */
	public static long getNextInSequence(String sequenceName) {
		// Validate sequence name
		if (sequenceName != null) {
			// Get the last ID
			Long sequenceCount = 0l;
			if (counters.containsKey(sequenceName))
				sequenceCount = counters.get(sequenceName);
			// Increment the count
			sequenceCount++;
			counters.put(sequenceName, sequenceCount);

			return sequenceCount.longValue();
		} else
			return NULL_ID;
	}
}
