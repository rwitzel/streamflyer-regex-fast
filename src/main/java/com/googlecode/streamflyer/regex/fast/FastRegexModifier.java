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
package com.googlecode.streamflyer.regex.fast;

import com.googlecode.streamflyer.regex.OnStreamStandardMatcher;
import com.googlecode.streamflyer.regex.RegexModifier;
import com.googlecode.streamflyer.regex.ReplacingProcessor;

/**
 * In comparison to {@link RegexModifier} this class uses the fast
 * {@link OnStreamExtendedMatcher} instead of the slower
 * {@link OnStreamStandardMatcher}.
 * <p>
 * The class can be used exactly the same way as the (slower)
 * {@link RegexModifier}:
 * <code><pre class="prettyprint lang-java">// choose the character stream to modify
Reader originalReader = new StringReader("edit stream");

// select the modifier
Modifier myModifier = new FastRegexModifier("edit stream", 0, "modify stream");

// create the modifying reader that wraps the original reader
Reader modifyingReader = new ModifyingReader(originalReader, myModifier);

// use the modifying reader instead of the original reader
String output = IOUtils.toString(modifyingReader);
assertEquals("modify stream", output);</pre></code>
 * 
 * @author rwoo
 * 
 */
public class FastRegexModifier extends RegexModifier {

	public FastRegexModifier(String regex, int flags, String replacement) {
		this(regex, flags, replacement, 0, 2048);
	}

	public FastRegexModifier(String regex, int flags, String replacement,
			int minimumLengthOfLookBehind, int newNumberOfChars) {
		super();

		Matcher jdkMatcher = Pattern.compile(regex, flags).matcher("");
		jdkMatcher.useTransparentBounds(true);
		init(new OnStreamExtendedMatcher(jdkMatcher), new ReplacingProcessor(
				replacement), minimumLengthOfLookBehind, newNumberOfChars);
	}

}
