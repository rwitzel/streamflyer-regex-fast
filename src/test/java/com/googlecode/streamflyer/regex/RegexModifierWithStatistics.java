package com.googlecode.streamflyer.regex;

import com.googlecode.streamflyer.core.AfterModification;

/**
 * @author rwoo
 */
public class RegexModifierWithStatistics extends RegexModifier {

	/**
	 * The maximum length of the character buffer passed to the modifier.
	 */
	private int modifierCalls = 0;

	/**
	 * The maximum length of the character buffer passed to the modifier.
	 */
	private int maxCharacterBufferLength = 0;

	private long sumCharacterBufferLength = 0;

	/**
	 * The maximum capacity of the character buffer passed to the modifier.
	 */
	private int maxCharacterBufferCapacity = 0;

	private long sumCharacterBufferCapacity = 0;

	public RegexModifierWithStatistics(OnStreamMatcher matcher,
			MatchProcessor matchProcessor, int minimumLengthOfLookBehind,
			int newNumberOfChars) {
		super(matcher, matchProcessor, minimumLengthOfLookBehind,
				newNumberOfChars);
	}

	@Override
	public AfterModification modify(StringBuilder characterBuffer,
			int firstModifiableCharacterInBuffer, boolean endOfStreamHit) {

		modifierCalls++;
		sumCharacterBufferLength += characterBuffer.length();
		sumCharacterBufferCapacity += characterBuffer.capacity();

		if (maxCharacterBufferLength < characterBuffer.length()) {
			maxCharacterBufferLength = characterBuffer.length();
		}

		if (maxCharacterBufferCapacity < characterBuffer.capacity()) {
			maxCharacterBufferCapacity = characterBuffer.capacity();
		}

		return super.modify(characterBuffer, firstModifiableCharacterInBuffer,
				endOfStreamHit);
	}

	public int getMaxCharacterBufferCapacity() {
		return maxCharacterBufferCapacity;
	}

	public int getMaxCharacterBufferLength() {
		return maxCharacterBufferLength;
	}

	public int getAverageCharacterBufferCapacity() {
		return (int) (sumCharacterBufferCapacity / modifierCalls);
	}

	public int getAverageCharacterBufferLength() {
		return (int) (sumCharacterBufferLength / modifierCalls);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RegexModifierWithStatistics [modifierCalls=");
		builder.append(modifierCalls);
		builder.append(", \nmaxCharacterBufferLength=");
		builder.append(maxCharacterBufferLength);
		builder.append(", \naverageCharacterBufferLength=");
		builder.append(getAverageCharacterBufferLength());
		builder.append(", \nmaxCharacterBufferCapacity=");
		builder.append(maxCharacterBufferCapacity);
		builder.append(", \naverageCharacterBufferCapacity=");
		builder.append(getAverageCharacterBufferCapacity());
		builder.append("]");
		return builder.toString();
	}

}
