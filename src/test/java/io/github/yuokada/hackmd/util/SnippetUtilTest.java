package io.github.yuokada.hackmd.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SnippetUtilTest {

  @Test
  void testBasicSnippet() {
    String content = "This is a simple test content with the word hello in it.";
    String snippet = SnippetUtil.generateSnippet(content, "hello");
    assertTrue(snippet.contains("[hello]"));
  }

  @Test
  void testCaseInsensitive() {
    String content = "This content has HELLO in uppercase.";
    String snippet = SnippetUtil.generateSnippet(content, "hello");
    assertTrue(snippet.contains("[HELLO]"));
  }

  @Test
  void testMatchAtBeginning() {
    String content = "hello world, this is a test.";
    String snippet = SnippetUtil.generateSnippet(content, "hello");
    assertTrue(snippet.startsWith("[hello]"));
  }

  @Test
  void testMatchAtEnd() {
    String content = "This is a test hello";
    String snippet = SnippetUtil.generateSnippet(content, "hello");
    assertTrue(snippet.contains("[hello]"));
      assertFalse(snippet.endsWith("..."));
  }

  @Test
  void testNullContent() {
    assertEquals("N/A", SnippetUtil.generateSnippet(null, "hello"));
  }

  @Test
  void testEmptyContent() {
    assertEquals("N/A", SnippetUtil.generateSnippet("", "hello"));
  }

  @Test
  void testNullSearchTerm() {
    assertEquals("N/A", SnippetUtil.generateSnippet("some content", null));
  }

  @Test
  void testNoMatch() {
    assertEquals("N/A", SnippetUtil.generateSnippet("some content", "xyz"));
  }

  @Test
  void testNewlineReplacement() {
    String content = "line one\nline two\ncontains hello\nline four";
    String snippet = SnippetUtil.generateSnippet(content, "hello");
      assertFalse(snippet.contains("\n"));
    assertTrue(snippet.contains("[hello]"));
  }

  @Test
  void testLongContentTruncation() {
    String content = "a".repeat(100) + "hello" + "b".repeat(100);
    String snippet = SnippetUtil.generateSnippet(content, "hello");
    // Snippet should be truncated to MAX_SNIPPET_LENGTH + "..."
    assertTrue(snippet.length() <= 83); // 80 + "..."
  }

  @Test
  void testEllipsisWhenTruncatedBefore() {
    String content = "a".repeat(100) + "hello" + " end";
    String snippet = SnippetUtil.generateSnippet(content, "hello");
    assertTrue(snippet.startsWith("..."));
  }
}
