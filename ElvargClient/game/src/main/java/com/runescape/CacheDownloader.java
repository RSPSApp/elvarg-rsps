/*
 * Copyright (c) 2023, Mark7625
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.runescape;

import com.runescape.util.ProgressListener;
import com.runescape.util.ProgressManager;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Slf4j
public class CacheDownloader {

    public static final File RUNELITE_DIRR = new File(System.getProperty("user.home"), "." + "rspsApp");
    public static final File CACHE_DIRR = new File(RUNELITE_DIRR, "cache");

    private Client client;
    OkHttpClient httpClient;
    private File hashFileLocation = new File(RUNELITE_DIRR, "hash");
    private File outputFile = new File(CACHE_DIRR, "cache.zip");

    public CacheDownloader(Client client) {
        this.client = client;
        this.httpClient = new OkHttpClient();
    }


    public void init() {

        if (!needsUpdating()) {
            client.loadCacheArchives();
            System.err.println("Cache passed OK check");
            return;
        }

        log.info("Downloading Cache: {}", Configuration.CACHE_LINK);

        Request request = new Request.Builder().url(Configuration.CACHE_LINK).build();
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void finishedDownloading() {
                client.drawLoadingText(0, "Unzipping Cache");
                unzip(outputFile.toPath(), CACHE_DIRR.toPath());
                outputFile.delete();
                try {
                    FileUtils.writeStringToFile(hashFileLocation, getOnlineHash(), Charset.forName("UTF-8"));
                    client.loadCacheArchives();
                } catch (IOException e) {
                    sendError(8);
                }
            }

            @Override
            public void progress(long bytesRead, long contentLength) {
                long progress = (100 * bytesRead) / contentLength;
                client.drawLoadingText((int) progress, "Downloading Cache: " + (int) progress + " %");
            }

            @Override
            public void started() {
                client.drawLoadingText(0, "Downloading Cache");
            }
        };

        OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(chain -> {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressManager(originalResponse.body(), progressListener))
                    .build();
        }).build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Client.instance.loadingError = true;
                System.err.println("failed to connect to cache url..");
                return;
            }
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(response.body().bytes());
            progressListener.finishedDownloading();
        } catch (IOException e) {
            sendError(3);
        }


    }

    /**
     * Sends an error message with the given error code.
     *
     * @param code the error code to display.
     */
    public void sendError(int code) {
        Client.instance.drawLoadingText(2, "Error Downloading Cache :" + code);
        log.error("Error Downloading Cache:  {} ", Configuration.CACHE_LINK);
    }

    /**
     * Returns a boolean indicating whether the cache needs updating by comparing the local hash to the online hash.
     *
     * @return true if the file needs updating, false otherwise.
     */
    public boolean needsUpdating() {
        if (!hashFileLocation.exists()) {
            return true;
        }
        try {
            String localHash = Files.readString(hashFileLocation.toPath());
            String onlineHash = getOnlineHash();
            if (!localHash.equals(onlineHash)) {
                return true;
            }
        } catch (IOException e) {
            log.error("Unable to compare hashes, {}", e.getMessage());
        }
        return false;
    }

    /**
     * Returns a hash of the online hash.
     *
     * @return The Hash of the online File.
     */
    public String getOnlineHash() throws IOException {
        Request request = new Request.Builder().url(Configuration.CACHE_HASH_LINK).build();
        Call call = httpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();
    }

    /**
     * Unzips the given zip file to the specified output directory.
     *
     * @param zipFile    the zip file to unzip.
     * @param outputPath the directory to unzip the file to.
     */
    public void unzip(Path zipFile, Path outputPath) {
        int totalUnzipped = 0;
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {

            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {

                Path newFilePath = outputPath.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newFilePath);
                } else {
                    if (!Files.exists(newFilePath.getParent())) {
                        Files.createDirectories(newFilePath.getParent());
                    }
                    try (OutputStream bos = Files.newOutputStream(outputPath.resolve(newFilePath))) {
                        byte[] buffer = new byte[Math.toIntExact(entry.getSize())];

                        int location;

                        while ((location = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, location);
                        }
                    }
                }
                entry = zis.getNextEntry();
                totalUnzipped++;
                Client.instance.drawLoadingText(totalUnzipped * 10, "Unzipping: " + totalUnzipped * 10 + " %");
            }
        } catch (IOException e) {
            Client.instance.drawLoadingText(2, "Error Unzipping");
            e.printStackTrace();
        }
    }
}