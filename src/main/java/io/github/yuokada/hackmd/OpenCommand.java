package io.github.yuokada.hackmd;

import io.github.yuokada.hackmd.model.NoteDetailResponse;
import io.github.yuokada.hackmd.service.HackMdService;
import jakarta.inject.Inject;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * A command to open a note's permanent link in the default browser.
 */
@Command(name = "open", description = "Open a note's permanent link in the browser")
public class OpenCommand implements Runnable {

  @Inject HackMdService hackMdService;

  @Parameters(index = "0", description = "The ID of the note to open.", paramLabel = "NOTE_ID")
  String noteId;

  @Override
  public void run() {
    try {
      NoteDetailResponse note = hackMdService.getNote(noteId);
      String permalink = note.permalink();

      if (permalink == null || permalink.isEmpty()) {
        System.err.println("Error: Note does not have a permalink.");
        return;
      }

      openInBrowser(permalink);
      System.out.println("Opened: " + permalink);
    } catch (Exception e) {
      System.err.println("Error opening note: " + e.getMessage());
    }
  }

  private void openInBrowser(String url) throws IOException, URISyntaxException {
    if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      System.err.println("Error: Desktop browsing is not supported on this system.");
      System.out.println("Please open the following URL manually: " + url);
      return;
    }

    Desktop.getDesktop().browse(new URI(url));
  }
}
