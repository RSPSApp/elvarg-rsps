package com.runescape.util;

public interface ProgressListener {
    void finishedDownloading();

    void progress(long bytesRead, long contentLength);

    void started();
}
