package com.gumgum.pageTagger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

/**
 * Utility for tagging text (in the form of a String or a URL for a webpage),
 * and tagging it with tokens indicating part-of-speech.
 *
 * @author chrchan
 */
public class PageTagger {

    private static final String DEFAULT_MODEL =
            "english-left3words-distsim.tagger";

    private static final String DEFAULT_USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36";

    private MaxentTagger tagger = null;

    // the default chunk size in characters
    private static final int DEFAULT_CHUNK_SIZE = 2000;

    private static final int BUF_SIZE = 2048;

    /**
     * Main method that takes a single command-line argument of a URL whose
     * content is to be tagged.
     *
     * @param argv
     *            URL whose content we want to tag
     */
    public static void main(final String[] argv) {

        URL url = null;
        if (argv.length < 1) {
            System.err.println("You need to give me a URL.");
            System.exit(1);
        }
        try {
            url = new URL(argv[0]);
        } catch (MalformedURLException e) {
            // bail, since there is nothing we can do at this point
            e.printStackTrace();
            System.exit(1);
        }

        PageTagger pageTagger = new PageTagger();
        String text = null;
        try {
            text = pageTagger.getText(url);
        } catch (Exception e) {
            // bail, since there is nothing we can do at this point
            e.printStackTrace();
            System.exit(1);
        }
        String taggedText = pageTagger.tagText(text);
        System.out.println(taggedText);
    }

    public PageTagger() {
        tagger = new MaxentTagger(DEFAULT_MODEL);
    }

    /**
     * Given a String, returns a new String where the text is tagged with tokens
     * indicating part-of-speech.
     *
     * @param text
     *            the text that we want to tag
     * @return a new String with tags
     */
    public String tagText(final String text) {

        // MaxentTagger does not like Unicode Replacement Character so strip it.
        String cleanText = text.replaceAll("\uFFFD","");

        // If text is too large, chunk it.
        List<String> chunks = chunkText(cleanText);
        StringBuilder sb = new StringBuilder();
        for (String chunk : chunks) {
            sb.append(tagger.tagString(chunk));
            sb.append(" ");
        }

        return sb.toString();
    }

    /**
     * Returns a String containing all the text in the body of a web page
     * excluding HTML tags and JavaScript.
     *
     * @param url
     *            the location of the the web page to fetch the text of.
     * @return String containing all the text in the body of a web page
     *         excluding HTML tags and JavaScript.
     * @throws IOException
     */
    public String getText(final URL url) throws IOException {

        URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
        InputStream is = conn.getInputStream();
        StringBuilder sb = new StringBuilder();
        int bytesRead = 0;
        try {
            do {
                byte[] b = new byte[BUF_SIZE];
                bytesRead = is.read(b, 0, BUF_SIZE);
                if (bytesRead != -1) {
                    sb.append(new String(b, 0, bytesRead));
                }
            } while (bytesRead != -1);
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            is.close();
        }

        String ret = sb.toString();
        ret = cleanText(ret);

        return ret;
    }

    /**
     * Cleans a String by removing thing that we do not consider text - scripts,
     * tags, comments.
     *
     * @param s input String to clean
     * @return a new String that is the old String with stuff removed
     */
    private String cleanText(final String s) {
        String ret = s;

        // Remove scripts. The "(?s)" enables single-line mode so that newlines
        // are included in the ".*" part of the pattern.
        ret = ret.replaceAll("(?is)<script.*?/script>", " ");

        // Remove anything in the head tag
        ret = ret.replaceAll("(?is)<head.*?/head>"," ");

        // Remove HTML comments.
        ret = ret.replaceAll("(?s)<!--.*?-->", " ");

        // Remove all tags.
        ret = ret.replaceAll("<.*?>", " ");

        // Unescape any escaped HTML entities.
        ret = StringEscapeUtils.unescapeHtml4(ret);
        return ret;
    }

    /**
     * Breaks a String into chunks of at most DEFAULT_CHUNK_SIZE.
     *
     * @param largeText the String we want to break up
     * @return a List of Strings
     */
    private List<String> chunkText(final String largeText) {
        List<String> chunks = new LinkedList<String>();
        int totalSize = largeText.length();
        int currentInd = 0;
        while (currentInd < totalSize) {
            int nextInd = currentInd + DEFAULT_CHUNK_SIZE - 1;
            if (nextInd > totalSize) {
                // we have reached the end
                nextInd = totalSize - 1;
            } else {
                // We are somewhere in the middle of the document, so find a
                // good place to separate. Look for a sentence boundary first.
                int periodIndex = largeText.lastIndexOf('.', nextInd);
                if ((periodIndex != -1) && (periodIndex > currentInd)) {
                    nextInd = periodIndex + 1;
                } else {
                    // no sentence boundary, so try a word boundary
                    int wordIndex = largeText.lastIndexOf(' ', nextInd);
                    if ((wordIndex != -1) && (wordIndex > currentInd)) {
                        nextInd = wordIndex + 1;
                    }
                }
                // If no sentence or word boundary could be found, just break
                // it at the chunk-size boundary.
            }
            String chunk = largeText.substring(currentInd, nextInd);
            chunks.add(chunk);
            currentInd = nextInd + 1;
        }

        return chunks;
    }

}
