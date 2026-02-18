package io.github.ferdinandmehlan.whisperspringserver.transcription.api;

import java.util.List;

public record TranscriptionEvent(long start, long end, String text, List<TokenDetail> tokens) {}
