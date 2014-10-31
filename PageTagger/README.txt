Issues I ran into

0. Problem: Could not find the stanford-postagger in any public maven repo
   Solution: installed jar/pom in my local repository
1. Problem: Tried to use SAXParser, but HTML was SGML not XML.
   Solution: Don't use SAXParser. Read the whole document into a String.
2. Problem: www.popcrunch.com does not like "User-Agent: Java/1.7.0_15" and returns 403
   Solution: use URLConnection.setRequestProperty() to set User-Agent to Chrome
3. Problem: String.replace("<script.*?/script>") does not strip multi-line script tags
   (same problem with stripping multi-line HTML comments)
   Solution: prefix pattern with "(?s)" so that '.' matches newlines
4. Problem: BMW article has HTML-escaped entities in it (&rsquo;, &ldquo;, etc.)
   Solution: use StringEscapeUtils.unescapeHtml4()
5. Problem: BMW article gave WARNING: Untokenizable: ? (U+FFFD, decimal: 65533)
   Solution: strip Unicode Replacement Character \uFFFD
6. Problem: http://gumgum-public.s3.amazonaws.com/numbers.html ran out or heap space.
   Solution: break document into chunks and tag the chunks.

Ways to improve:
1. Chunk-splitting is done on periods and spaces only. It should look for other kinds
   of white-space also. And periods could be used for decimal numbers or abbreviations
   ("Dr. Evil is evil."), so splitting on a period needs to be smarter and look at
   surrounding context.
2. Improvements to command-line argument handling - allow multiple URLs to be specified
   and write output to a file (come up with naming convention of output file based on
   URL).
3. Allow tagger model, user-agent, chunk size, buffer size to be specified in an external
   properties/config file.
4. Integrate with logging library like slf4j.


1. What class and method is responsible for using too much memory?
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	 at edu.stanford.nlp.sequences.ExactBestSequenceFinder.bestSequence(ExactBestSequenceFinder.java:72)

2. Is this considered a memory leak? Can you elaborate either way?
Looking at the heap usage graph over time, it looks like a potential memory leak because the
graph continuously trends upwards (there are some localized drops in heap usage, but overall,
it trends upwards). Upon closer inspection of the heap dump and source code, however, it is
not actually a memory leak.

The heap dump shows that about >95% of the heap memory is used by double[] object(s). Inspecting
the source code, line 72 of ExactBestSequenceFinder looks like this:
69    double[][] windowScore = new double[padLength][];
70    for (int pos = leftWindow; pos < leftWindow + length; pos++) {
71      if (DEBUG) { System.err.println("scoring word " + pos + " / " + (leftWindow + length) + ", productSizes =  " + productSizes[pos] + ", tagNum = " + tagNum[pos] + "..."); }
72      windowScore[pos] = new double[productSizes[pos]];

The windowScore two-dimensional array is the culprit. In the for-loop at line 70, we are building
a two-dimensional array of doubles where the first dimension is the number of words in the
sentence currently being processed (in the case of the numbers page, since there is no punctuation
in the document, it is assuming the entire document is a long run-on sentence of 250005 words).
The second dimension of the array maxes out at 8000.

After the loop completes, the elements in the two-dimensional array are referenced later on in
another loop. Since the large data structure is referenced later in the code, it is not memory
that can be recovered prior to constructing all of the data.

3. Can you adjust the JVM to provide enough memory to tag this page? How?
-Xmx sets the max heap size for the JVM.
For the given dataset, we have a 250005 word sentence. So we will be building a two dimensional
array of Java doubles of size [250005][8000]
A Java double is 8bytes, so we need 250,005 * 8 * 8000 bytes just for that data structure.
16000320000 bytes = 14.9GB

We will need a bit more than that for other variables and data structures. My guess is that we
can probably get it to work with -Xmx20g

However, looking at the bestSequence() method, I see for-loops nested 4-deep. So it looks like
it will be very slow, unless we can break the data up like I have in my initial implementation.

4. What tool(s) and techniques can you use to find out why there is a memory problem?
Reading documentation on the library
Reading source code of the library
eclipse debugger
jvisualvm

5. Can this problem be solved by changing the garbage collection algorithm?
Using alternate GC strategies would not help in this particular case because of the way the
ExactBestSequenceFinder is written. The two-dimensional array of doubles that is being built
cannot be garbage collected. The entire two-dimensional array needs to be built and then it is
accessed afterwards. The OutOfMemoryError occurs before the array is done being built.

6. Suppose we are using this class to study the distribution of particular parts of speech on web
   pages in our publisher network. What changes would you suggest in order to continue to process
   pages without crashing on pages such as this?
Chunk the data, like how I did in my initial implementation.
