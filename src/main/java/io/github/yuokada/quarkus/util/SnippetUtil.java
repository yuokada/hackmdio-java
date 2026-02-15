package io.github.yuokada.quarkus.util;

/**
 * Utility class for generating text snippets around search term matches.
 */
public final class SnippetUtil {

  private static final int CONTEXT_CHARS = 50;
  private static final int MAX_SNIPPET_LENGTH = 80;
  private static final String ELLIPSIS = "...";
  private static final String NA = "N/A";

  private SnippetUtil() {
    // utility class
  }

  /**
   * Generates a snippet from content around the first occurrence of the search term.
   *
   * @param content    The full text content to extract a snippet from.
   * @param searchTerm The term to search for.
   * @return A snippet string with the match highlighted in brackets.
   */
  public static String generateSnippet(String content, String searchTerm) {
    if (content == null || content.isEmpty()
        || searchTerm == null || searchTerm.isEmpty()) {
      return NA;
    }

    // Replace newlines with spaces for table display
    String normalized = content.replace('\n', ' ').replace('\r', ' ');

    String lowerContent = normalized.toLowerCase();
    String lowerTerm = searchTerm.toLowerCase();
    int matchIndex = lowerContent.indexOf(lowerTerm);

    if (matchIndex < 0) {
      return NA;
    }

    int start = Math.max(0, matchIndex - CONTEXT_CHARS);
    int matchEnd = matchIndex + searchTerm.length();
    int end = Math.min(normalized.length(), matchEnd + CONTEXT_CHARS);

    StringBuilder sb = new StringBuilder();
    if (start > 0) {
      sb.append(ELLIPSIS);
    }
    sb.append(normalized, start, matchIndex);
    sb.append('[');
    sb.append(normalized, matchIndex, matchEnd);
    sb.append(']');
    sb.append(normalized, matchEnd, end);
    if (end < normalized.length()) {
      sb.append(ELLIPSIS);
    }

    String snippet = sb.toString();
    if (snippet.length() > MAX_SNIPPET_LENGTH) {
      snippet = snippet.substring(0, MAX_SNIPPET_LENGTH) + ELLIPSIS;
    }
    return snippet;
  }
}
