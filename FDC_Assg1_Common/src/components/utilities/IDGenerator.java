/* Utility for generating unique identifiers that are totally ordered.
 * Maintains separate tracks for each sequence, so that you can create 
 * sequences for clients, messages, transactions etc. as needed
 * 
 * Usage:
 * 1. Use getNextInSequence(...) to get the next ID in the sequence.
 * 2. Use getCurrentInSequence(...) to get the current ID in the sequence, if needed.
 * 3. Use setNextInSequence(...) to override and set the sequence to a desired ID.
 */
package components.utilities;

import java.util.HashMap;

public class IDGenerator {

	/**
	 * Represents a null (ie. not set) IDs.
	 */
	public static final long NULL_ID = -1;

	private HashMap<String, Long> counters = new HashMap<String, Long>();

	/**
	 * Returns a unique (total ordered) ID for the sequence.
	 * If the sequence doesn't exist yet, a new one is started.
	 * Sequences start from 1.
	 * 
	 * @param sequenceName
	 *            The name of this sequence of IDs, example "client", "transaction", etc.
	 * @return A unique ID for the sequence.
	 */
	public long getNextInSequence(String sequenceName) {
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

	/**
	 * Manually update the sequence count.
	 * Useful when synchronizing with a global sequence, for example.
	 * 
	 * @param sequenceName
	 *            The name of this sequence of IDs, example "client", "transaction", etc.
	 * @param newValue
	 *            The new value to override the current counter for this sequence.
	 */
	public void setNextInSequence(String sequenceName, long newValue) {
		// Validate sequence name
		if (sequenceName != null)
			counters.put(sequenceName, newValue);
	}

	/**
	 * Returns the current ID in the sequence, which means 
	 * it may have already been used.
	 * If the sequence doesn't exist yet, 0 is returned.
	 * 
	 * @param sequenceName
	 *            The name of this sequence of IDs, example "client", "transaction", etc.
	 * @return The current ID in the sequence.
	 */
	public long getCurrentInSequence(String sequenceName) {
		// Validate sequence name
		if (sequenceName != null) {
			// Get the last ID
			Long sequenceCount = 0l;
			if (counters.containsKey(sequenceName))
				sequenceCount = counters.get(sequenceName);
			else
				// We don't have one, so add this to start this sequence
				counters.put(sequenceName, sequenceCount);

			return sequenceCount.longValue();
		} else
			return NULL_ID;
	}
}
