[![Travis build status](https://travis-ci.org/rwitzel/streamflyer-regex-fast.svg)](https://travis-ci.org/rwitzel/streamflyer-regex-fast)
[![GPL-2.0](https://img.shields.io/badge/license-GPL%202.0-red.svg)](http://www.gnu.org/licenses/gpl-2.0.txt)


Provides a faster algorithm to match regular expressions on character streams than the algorithm used by [Streamflyer](https://github.com/rwitzel/streamflyer).


## Usage ##

A typical example:
```
// choose the character stream to modify
Reader originalReader = ... // this reader is connected to the original data source

// we use FastRegexModifier instead of RegexModifier
Modifier fastModifier = new FastRegexModifier("edit(\\s+)stream", Pattern.DOTALL, "modify$1stream");

// create the modifying reader that wraps the original reader
Reader modifyingReader = new ModifyingReader(originalReader, fastModifier);

... // use the modifying reader instead of the original reader
```

In this example the chosen Modifier replaces the string "edit stream" with "modify stream". The modifier preserves the whitespace between "edit" and "stream".

## Why is this fast matcher not included in the Streamflyer project itself? ##

The code of the fast matcher relies on a modification of the
[java.util.regex](http://docs.oracle.com/javase/6/docs/api/java/util/regex/package-summary.html) package. The regex package is licensed with _GNU General Public License v2_ but the streamflyer project is licensed with _Apache License 2.0_. In order to deal with this license mismatch, the fast matcher is separated from the streamflyer project.

## Why licensed with GNU General Public License v2? ##

See above.

## Installation

streamflyer-regex-fast-1.0.1 is compiled with JDK 1.6.

### Download

streamflyer-regex-fast-1.0.1 can be downloaded from [Maven Central](http://search.maven.org/#artifactdetails%7Ccom.googlecode.streamflyer-regex-fast%7Cstreamflyer-regex-fast%7C1.0.1%7Cjar).

The Maven coordinates are
```
<dependency>
   <groupId>com.googlecode.streamflyer-regex-fast</groupId>
   <artifactId>streamflyer-regex-fast</artifactId>
   <version>1.0.1</version>
</dependency>
```

### Dependencies

streamflyer-regex-fast-1.0. depends on Streamflyer and [Commons IO](http://commons.apache.org/io/).

The maven coordinates of theses dependencies are

```
<dependency>
   <groupId>com.googlecode.streamflyer</groupId>
   <artifactId>streamflyer-core</artifactId>
   <version>1.1.3</version>
</dependency>
<dependency>
   <groupId>commons-io</groupId>
   <artifactId>commons-io</artifactId>
   <version>2.0</version>
</dependency>
```

## Questions, Suggestions, Issues ##

Please read the corresponding section on the web page of the [streamflyer project](https://github.com/rwitzel/streamflyer#user-content-questions-suggestions-issues).