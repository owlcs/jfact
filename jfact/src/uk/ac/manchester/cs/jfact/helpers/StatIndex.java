package uk.ac.manchester.cs.jfact.helpers;

public enum StatIndex {
	Depth, Size, Branch, Gener, Freq;
	public int getIndex(final boolean pos) {
		return ordinal() * 2 + (pos ? 0 : 1);
	}

	public static int choose(final char c) {
		switch (c) {
			case 'S':
				return Size.getIndex(false);
			case 'D':
				return Depth.getIndex(false);
			case 'B':
				return Branch.getIndex(false);
			case 'G':
				return Gener.getIndex(false);
			default:
				return Freq.getIndex(false);
		}
	}

	/** add-up all stat values at once by explicit values */
	public static void updateStatValues(final int d, final int s, final int b,
			final int g, final boolean pos, final int[] stat) {
		stat[Size.getIndex(pos)] += s;
		stat[Branch.getIndex(pos)] += b;
		stat[Gener.getIndex(pos)] += g;
		if (d > stat[Depth.getIndex(pos)]) {
			stat[Depth.getIndex(pos)] = d;
		}
	}

	/** add-up all values at once by a given vertex */
	public static void updateStatValues(final DLVertex v, final boolean posV,
			final boolean pos, final int[] stat) {
		updateStatValues(getDepth(posV, v.stat), getSize(posV, v.stat),
				getBranch(posV, v.stat), getGener(posV, v.stat), pos, stat);
	}

	/** increment frequency value */
	public static void incFreqValue(final boolean pos, final int[] stat) {
		stat[Freq.getIndex(pos)] += 1;
	}

	/** general access to a stat value by index */
	public static int getDepth(final boolean pos, final int[] stat) {
		return stat[Depth.getIndex(pos)];
	}

	/** general access to a stat value by index */
	protected static int getSize(final boolean pos, final int[] stat) {
		return stat[Size.getIndex(pos)];
	}

	/** general access to a stat value by index */
	protected static int getBranch(final boolean pos, final int[] stat) {
		return stat[Branch.getIndex(pos)];
	}

	/** general access to a stat value by index */
	protected static int getGener(final boolean pos, final int[] stat) {
		return stat[Gener.getIndex(pos)];
	}

	/** general access to a stat value by index */
	protected static int getFreq(final boolean pos, final int[] stat) {
		return stat[Freq.getIndex(pos)];
	}
}
