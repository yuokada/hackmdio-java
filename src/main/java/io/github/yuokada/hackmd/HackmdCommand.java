package io.github.yuokada.hackmd;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

/**
 * The main command for the HackMD CLI application.
 * It serves as the entry point and aggregates all subcommands.
 */
@TopCommand
@Command(
    mixinStandardHelpOptions = true,
    subcommands = {
      ListCommand.class,
      CreateCommand.class,
      GetCommand.class,
      IndexCommand.class,
      SearchCommand.class
    })
public class HackmdCommand {}
