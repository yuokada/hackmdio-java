package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.HackMdService;
import jakarta.inject.Inject;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * A command to open a note's publish link in the default browser.
 */
@Command(name = "open", description = "Open a note's publish link in the browser")
public class OpenCommand implements Callable<Integer> {

  @Inject HackMdService hackMdService;

  @Parameters(index = "0", description = "The ID of the note to open.", paramLabel = "NOTE_ID")
  String noteId;

  @Override
  public Integer call() {
    try {
      NoteDetailResponse note = hackMdService.getNote(noteId);
      String publishLink = note.publishLink();

      if (publishLink == null || publishLink.isEmpty()) {
        System.err.println("Error: Note does not have a publish link.");
        return 1;
      }

      return openInBrowser(publishLink) ? 0 : 1;
    } catch (Exception e) {
      System.err.println("Error opening note: " + e.getMessage());
      return 1;
    }
  }

  boolean openInBrowser(String url) throws IOException, URISyntaxException {
    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      System.out.println("Desktop browsing is not available on this system.");
      System.out.println("Please open the following URL manually: " + url);
      return false;
    }

    Desktop.getDesktop().browse(new URI(url));
    System.out.println("Opened: " + url);
    return true;
  }
}
