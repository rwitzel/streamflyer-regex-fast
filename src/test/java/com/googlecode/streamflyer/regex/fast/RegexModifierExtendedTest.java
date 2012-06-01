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

import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.Modifier;
import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.regex.OnStreamMatcher;

/**
 * Tests {@link OnStreamExtendedMatcher}.
 * 
 * @author rwoo
 * 
 * @since 28.06.2011
 */
public class RegexModifierExtendedTest extends
		com.googlecode.streamflyer.regex.RegexModifierTest {

	@Override
	protected OnStreamMatcher createMatcher(String regex, int flags) {
		Matcher matcher = Pattern.compile(regex).matcher("");
		matcher.useTransparentBounds(true);
		return new OnStreamExtendedMatcher(matcher);
	}

	public void testExampleFromHomepage_usageRegexFast() throws Exception {

		// choose the character stream to modify
		Reader originalReader = new StringReader("edit\n\tstream");

		// we use FastRegexModifier instead of RegexModifier
		Modifier fastModifier = new FastRegexModifier("edit(\\s+)stream",
				Pattern.DOTALL, "modify$1stream");

		// create the modifying reader that wraps the original reader
		Reader modifyingReader = new ModifyingReader(originalReader,
				fastModifier);

		// use the modifying reader instead of the original reader
		String output = IOUtils.toString(modifyingReader);
		assertEquals("modify\n\tstream", output);
	}
}
