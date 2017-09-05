package com.pentaho.install.file;

import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.post.PentahoXMLConfig;
import com.pentaho.install.post.tomcat.TomcatConf;

import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

public interface ConfigFileHandler {
    boolean handleJackrabbitRepositiryFile(PentahoXMLConfig repository, InstallParam installParam, Scanner scanner) throws Exception;
    boolean handleTomcatContextFile(TomcatConf context, InstallParam installParam, Scanner scanner) throws Exception;
    boolean handleTomcatServerFile(TomcatConf context, InstallParam installParam, Scanner scanner) throws Exception;

    default void close(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException ioe) {
                InstallUtil.error(ioe.getMessage());
            }
        }
    }
}
