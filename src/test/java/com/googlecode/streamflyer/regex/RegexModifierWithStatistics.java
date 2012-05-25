/**
 * Copyright (C) 2011 rwoo@gmx.de
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
