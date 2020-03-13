module net.senmori {
    requires static lombok;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires java.datatransfer;
    requires java.desktop;
    requires com.google.gson;
    requires com.google.common;
    requires maven.invoker;
    requires org.apache.commons.text;
    requires org.apache.commons.io;
    requires org.eclipse.jgit;
    requires org.jsoup;
    requires toml;
    requires core;
    requires java.diff.utils.copy;

    opens net.senmori to javafx.fxml;
    exports net.senmori;
}