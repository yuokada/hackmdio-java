package io.github.yuokada.hackmd.auth;

import java.net.URI;

/** Request context passed to credentials providers. */
public record HackmdRequestContext(String method, URI uri) {}
