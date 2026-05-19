package io.github.yuokada.hackmd.auth;

/** Resolves credentials for outgoing HackMD requests. */
public interface HackmdCredentialsProvider {

  String token(HackmdRequestContext context);
}
