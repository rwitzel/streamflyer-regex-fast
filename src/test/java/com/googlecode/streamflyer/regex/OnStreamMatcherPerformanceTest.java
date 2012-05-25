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

import java.io.BufferedReader;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.googlecode.streamflyer.core.ModifyingReader;
import com.googlecode.streamflyer.core.ModifyingWriter;
import com.googlecode.streamflyer.experimental.regexj6.OnStreamJava6Matcher;
import com.googlecode.streamflyer.internal.thirdparty.ZzzAssert;
import com.googlecode.streamflyer.regex.fast.OnStreamExtendedMatcher;

/**
 * Tests the performance of different implementations of {@link OnStreamMatcher}
 * .
 * 
 * @author rwoo
 * @since 23.06.2011
 */
public class OnStreamMatcherPerformanceTest extends TestCase {

	/**
	 * <code><pre>
+++ Measurements +++
OnStreamStandardMatcher (wrapping java.util.regex package):
Time spent by   ModifyingReader: Found seconds 2.211 shall not exceed expected maximum of seconds 2.9 but did exceed
Time spent by   ModifyingWriter: Found seconds 3.038 shall not exceed expected maximum of seconds 3.1 but did exceed

OnStreamJava6Matcher (using reflection on the java.util.regex package):
Time spent by  ModifyingReader: Found seconds 2.335 shall not exceed expected maximum of seconds 3.0 but did exceed
Time spent by  ModifyingWriter: Found seconds 2.46 shall not exceed expected maximum of seconds 3.0 but did exceed

OnStreamExtendedMatcher (using an extended java.util.regex package):
Time spent by  ModifyingReader: Found seconds 0.982 shall not exceed expected maximum of seconds 3.0 but did exceed
Time spent by  ModifyingWriter: Found seconds 1.058 shall not exceed expected maximum of seconds 3.0 but did exceed

Replacement in memory (using String.replaceAll):
Time spent by String.replaceAll: Found seconds 0.613 shall not exceed expected maximum of seconds 3.0 but did exceed

Replacement by Perl (perl 5, version 12, subversion 2 (v5.12.2) built for MSWin32-x86-multi-thread):
$ ./regex-via-perl.sh
It took 31 seconds

Summary:

(1) Replacing strings in a 10M character stream (using a buffer of 500 characters if stream-based),
takes roughly
 - 0.6 seconds (in memory, i.e. not stream based),
 - 0.9 / 1.1 seconds (extended java.util.regex package),
 - 2.2 / 2.6 seconds (using reflection on the java.util.regex package),
 - 2.3 / 3.1 seconds (wrapping java.util.regex package),
 - 30 seconds (by Perl, probably not stream-based, includes writing the result to HD).
 
 This measurement for Perl is not very plausible. Even the output
 differs from the expected one. What went wrong?
 
 A pair of numbers indicates measurement for modifying readers and writers.
 
 (2) Replacing strings in a 10M character stream takes from 20% to 30%
 more time if a modifying writer is used instead of a modifying reader.
</pre></code>
	 */
	public void testPerformanceOfReplacements() throws Exception {

		int size = 10 * 1000 * 1000; // (10M characters, i.e. 20MB)
		assertPerformanceOfReplacements(size, OnStreamStandardMatcher.class,
				2.3, 3.1, 0.7);
		assertPerformanceOfReplacements(size, OnStreamJava6Matcher.class, //
				2.2, 2.6, 0.7);
		assertPerformanceOfReplacements(size, OnStreamExtendedMatcher.class,
				0.9, 1.2, 0.7);
	}

	private void assertPerformanceOfReplacements(
			int numberOfCharactersInStream,
			Class<? extends OnStreamMatcher> onStreamMatcherClass,
			double expectedMaxSpentTimeByModifyingReader,
			double expectedMaxSpentTimeByModifyingWriter,
			double expectedMaxSpentTimeAllInMemory) throws Exception {

		String regex = "<x>(.*?)</x>";
		String replacement = "<y>$1</y>";

		String input = createInput(numberOfCharactersInStream);
		long start = System.currentTimeMillis();
		String expectedOutput = input.replaceAll(regex, replacement);
		long end = System.currentTimeMillis();
		assertTime(end - start, expectedMaxSpentTimeAllInMemory,
				"Time spent by String.replaceAll:");

		// writeFileForComparisonWithPerl(input, expectedOutput);

		int newNumberOfChars;

		// test: max capacity
		newNumberOfChars = 100;
		// the number of characters to match in the worst case = 100 + 200 (max
		// length of match)
		// Therefore buffer length == 100 * 2 *2 does the job (doubled twice)
		int expectedMaxLength = newNumberOfChars * 2 * 2;
		assertReplacementByReader(
				input,
				createModifier(onStreamMatcherClass, regex, replacement,
						newNumberOfChars, true), expectedOutput, null,
				expectedMaxLength);
		assertReplacementByWriter(
				input,
				createModifier(onStreamMatcherClass, regex, replacement,
						newNumberOfChars, true), expectedOutput, null,
				expectedMaxLength);

		// test: max spent time
		newNumberOfChars = 1000;
		assertReplacementByReader(
				input,
				createModifier(onStreamMatcherClass, regex, replacement,
						newNumberOfChars, false), expectedOutput,
				expectedMaxSpentTimeByModifyingReader, null);
		assertReplacementByWriter(
				input,
				createModifier(onStreamMatcherClass, regex, replacement,
						newNumberOfChars, false), expectedOutput,
				expectedMaxSpentTimeByModifyingWriter, null);
	}

	private RegexModifier createModifier(
			Class<? extends OnStreamMatcher> onStreamMatcherClass,
			String regex, String replacement, int newNumberOfChars,
			boolean withStatistics) {
		// create matcher
		OnStreamMatcher matcher = createMatcher(onStreamMatcherClass, regex);

		// create modifier
		if (withStatistics)
			return new RegexModifierWithStatistics( //
					matcher, //
					new ReplacingProcessor(replacement), //
					0, //
					newNumberOfChars);
		else
			return new RegexModifier( //
					matcher, //
					new ReplacingProcessor(replacement), //
					0, //
					newNumberOfChars);
	}

	/**
	 * @param numberOfCharactersInStream
	 * @return Returns a string containing segments of whitespace (max length
	 *         399 characters) and segments of x-Elements containing whitespace
	 *         (max length 199 characters).
	 */
	private String createInput(int numberOfCharactersInStream) {

		StringBuilder sb = new StringBuilder(numberOfCharactersInStream);
		Random random = new Random();
		// random.setSeed(43753658);
		random.setSeed(65753433);

		int charsToAppend = 0;
		while (sb.length() < numberOfCharactersInStream - 600) {

			// append \s{0,399}, up to 399 whitespace characters
			charsToAppend = random.nextInt(400);
			for (int index = 0; index < charsToAppend; index++) {
				sb.append(' ');
			}

			// append <x>\s{0,199}</x>, i.e. an x-Element containing up to 199
			// whitespace characters
			sb.append("<x>");

			// append some characters
			charsToAppend = random.nextInt(200);
			for (int index = 0; index < charsToAppend; index++) {
				sb.append(' ');
			}
			sb.append("</x>");
		}

		while (sb.length() < numberOfCharactersInStream) {
			// append some characters
			sb.append(' ');
		}

		ZzzAssert.isTrue(sb.length() == numberOfCharactersInStream);

		return sb.toString();
	}

	private OnStreamMatcher createMatcher(
			Class<? extends OnStreamMatcher> onStreamMatcherClass, String regex) {

		if (onStreamMatcherClass.isAssignableFrom(OnStreamJava6Matcher.class)) {
			Matcher matcher = Pattern.compile(regex).matcher("");
			matcher.useTransparentBounds(true);
			return new OnStreamJava6Matcher(matcher);

		} else if (onStreamMatcherClass
				.isAssignableFrom(OnStreamStandardMatcher.class)) {
			Matcher matcher = Pattern.compile(regex).matcher("");
			matcher.useTransparentBounds(true);
			return new OnStreamStandardMatcher(matcher);
		} else if (onStreamMatcherClass
				.isAssignableFrom(OnStreamExtendedMatcher.class)) {
			com.googlecode.streamflyer.regex.fast.Matcher matcher = com.googlecode.streamflyer.regex.fast.Pattern
					.compile(regex).matcher("");
			matcher.useTransparentBounds(true);
			return new OnStreamExtendedMatcher(matcher);
		} else {
			throw new IllegalArgumentException(String.format(
					"class %s is not supported or of wrong type",
					onStreamMatcherClass));
		}
	}

	private void assertReplacementByReader(String input,
			RegexModifier modifier, String expectedOutput,
			Double expectedMaxSpentTime, Integer expectedMaxLength)
			throws Exception {

		// create reader
		Reader reader = new ModifyingReader(new BufferedReader(
				new StringReader(input)), modifier);

		// read the stream into an output stream
		long start = System.currentTimeMillis();
		String foundOutput = IOUtils.toString(reader);
		long end = System.currentTimeMillis();

		// compare the expected result with the found result
		if (!expectedOutput.equals(foundOutput)) {

			assertEquals(expectedOutput, foundOutput);
		}

		assertTime(end - start, expectedMaxSpentTime,
				"Time spent by   ModifyingReader:");
		assertMaxLength(modifier, expectedMaxLength,
				"Max length by   ModifyingReader:");
	}

	private void assertReplacementByWriter(String input,
			RegexModifier modifier, String expectedOutput,
			Double expectedMaxSpentTime, Integer expectedMaxLength)
			throws Exception {

		// setup: create modifier and writer
		StringWriter stringWriter = new StringWriter();
		ModifyingWriter writer = new ModifyingWriter(stringWriter, modifier);

		// write the stream to an output stream
		long start = System.currentTimeMillis();
		for (int index = 0; index < input.length(); index++) {
			writer.append(input.charAt(index));
		}
		writer.flush();
		writer.close();
		long end = System.currentTimeMillis();

		String foundOutput = stringWriter.toString();

		// compare the expected result with the found result
		if (!expectedOutput.equals(foundOutput)) {

			assertEquals(expectedOutput, foundOutput);
		}

		assertTime(end - start, expectedMaxSpentTime,
				"Time spent by   ModifyingWriter:");
		assertMaxLength(modifier, expectedMaxLength,
				"Max length by   ModifyingWriter:");
	}

	private void assertMaxLength(RegexModifier modifier,
			Integer expectedMaxLength, String callerDescription)
			throws Exception {

		if (expectedMaxLength == null) {
			return;
		}

		RegexModifierWithStatistics modifierWithStatistics = (RegexModifierWithStatistics) modifier;

		// test found max length
		int foundMaxLength = modifierWithStatistics
				.getMaxCharacterBufferLength();
		String message = String.format(callerDescription
				+ " Found max length %s shall not exceed"
				+ " expected maximum of length %s but did exceed",
				foundMaxLength, expectedMaxLength);
		// System.out.println(message);
		System.out.println(modifier);
		assertTrue(message, foundMaxLength <= expectedMaxLength);

		// test found max capacity in comparison to the found max length
		int foundMaxCapacity = modifierWithStatistics
				.getMaxCharacterBufferCapacity();

		// see AbstractStringBuilder.ensureCapacity(int i), i.e. we have no full
		// control over the capacity
		assertTrue(foundMaxCapacity <= (foundMaxLength + 1) * 2);
	}

	private void assertTime(long duration, Double expectedMaxSeconds,
			String callerDescription) throws Exception {

		if (expectedMaxSeconds == null) {
			return;
		}

		double foundSeconds = duration / 1000.0;
		String message = String.format(callerDescription
				+ " Found seconds %s shall not exceed"
				+ " expected maximum of seconds %s but did exceed",
				foundSeconds, expectedMaxSeconds);
		// System.out.println(message);
		assertTrue(message, foundSeconds <= expectedMaxSeconds);
	}

	@SuppressWarnings("unused")
	private void writeFileForComparisonWithPerl(String input,
			String expectedOutput) throws Exception {

		// print shell script
		File scriptFile = File.createTempFile("regex-via-perl", ".sh");
		FileUtils.copyURLToFile(getClass().getResource("regex-via-perl.sh"),
				scriptFile);
		System.out.println("scriptFile: " + scriptFile.getAbsolutePath());

		// print input file
		File inputFile = File.createTempFile("input", ".txt");
		FileUtils.write(inputFile, input);
		System.out.println("inputFile: " + inputFile.getAbsolutePath());

		// print expected output file
		File expectedOutputFile = File.createTempFile("expectedOutput", ".txt");
		FileUtils.write(expectedOutputFile, input);

		System.out.println("expectedOutputFile: "
				+ expectedOutputFile.getAbsolutePath());
	}
}
