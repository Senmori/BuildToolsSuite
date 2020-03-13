package net.senmori.download;

import java.io.File;

public interface Downloader<T> {

    T download(String url, File target, String finalName);
}
