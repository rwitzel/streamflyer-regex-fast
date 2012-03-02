/*
 * Copyright (C) 2012 rwoo@gmx.de
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.googlecode.streamflyer.regex.fast;

import com.googlecode.streamflyer.regex.OnStreamMatcher;

/**
 * Implements {@link OnStreamMatcher} using an extended
 * <code>java.util.regex</code> package. See class comment at
 * {@link OnStreamMatcher}.
 * <p>
 * This implementation is the fastest implementation of {@link OnStreamMatcher}
 * at the moment - it takes less than twice as much time as
 * {@link String#replaceAll(String, String)} needs to match and replace data in
 * an character stream that is read entirely into a {@link CharSequence}.
 * 
 * @author rwoo
 * 
 * @since 20.06.2011
 */
public class OnStreamExtendedMatcher implements OnStreamMatcher {

	//
	// injected properties
	//

	protected Matcher matcher;

	//
	// constructors
	//

	/**
	 * @param matcher
	 */
	public OnStreamExtendedMatcher(Matcher matcher) {
		super();
		this.matcher = matcher;
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#reset(java.lang.CharSequence)
	 */
	@Override
	public void reset(CharSequence input_) {
		matcher.reset(input_);
	}

	/**
	 * 
	 <code><pre>
        boolean result = parentPattern.matchRoot.match(this, from, text);
     </pre></code>
	 * 
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#findUntilMatchOrHitEnd(int)
	 */
	@Override
	public boolean findUnlessHitEnd(int minFrom, int maxFrom) {
		return matcher.findUnlessHitEnd(minFrom, maxFrom);
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#lastFrom()
	 */
	@Override
	public int lastFrom() {
		return matcher.lastFrom();
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#hitEnd()
	 */
	@Override
	public boolean hitEnd() {
		return matcher.hitEnd();
	}

	/**
	 * @see com.googlecode.streamflyer.regex.OnStreamMatcher#requireEnd()
	 */
	@Override
	public boolean requireEnd() {
		return matcher.requireEnd();
	}

	//
	// implement interface MatchResult by delegating to underlying matcher
	//

	/**
	 * @see java.util.regex.MatchResult#start()
	 */
	@Override
	public int start() {
		return matcher.start();
	}

	/**
	 * @see java.util.regex.MatchResult#start(int)
	 */
	@Override
	public int start(int group) {
		return matcher.start(group);
	}

	/**
	 * @see java.util.regex.MatchResult#end()
	 */
	@Override
	public int end() {
		return matcher.end();
	}

	/**
	 * @see java.util.regex.MatchResult#end(int)
	 */
	@Override
	public int end(int group) {
		return matcher.end(group);
	}

	/**
	 * @see java.util.regex.MatchResult#group()
	 */
	@Override
	public String group() {
		return matcher.group();
	}

	/**
	 * @see java.util.regex.MatchResult#group(int)
	 */
	@Override
	public String group(int group) {
		return matcher.group(group);
	}

	/**
	 * @see java.util.regex.MatchResult#groupCount()
	 */
	@Override
	public int groupCount() {
		return matcher.groupCount();
	}
}