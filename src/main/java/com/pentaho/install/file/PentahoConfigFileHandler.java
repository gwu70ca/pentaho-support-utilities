package com.pentaho.install.file;

import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.post.PentahoXMLConfig;
import com.pentaho.install.post.XMLGenerator;
import com.pentaho.install.post.tomcat.TomcatConf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PentahoConfigFileHandler implements ConfigFileHandler {
    public boolean handleJackrabbitRepositiryFile(PentahoXMLConfig repository, InstallParam installParam, Scanner scanner) throws Exception {
        boolean success = false;
        String repositoryFile = InstallUtil.getJackrabbitRepositoryFilePath(installParam);
        File original = new File(repositoryFile);
        if (InstallUtil.backup(original, scanner)) {
            InstallUtil.output("\nUpdating Jackrabbit repository configuration file");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(repositoryFile), StandardCharsets.UTF_8));
                XMLGenerator xmlGen = new XMLGenerator();
                success = xmlGen.createXml(repository, writer);
            } catch (Exception ex) {
                InstallUtil.error(ex.getMessage());
            } finally {
                close(writer);
            }
        }

        return success;
    }

    public boolean handleTomcatContextFile(TomcatConf context, InstallParam installParam, Scanner scanner) throws Exception {
        boolean success = false;

        String ctxFile = InstallUtil.getTomcatContextFilePath(installParam);
        File original = new File(ctxFile);
        if (InstallUtil.backup(original, scanner)) {
            InstallUtil.output("\nUpdating Tomcat server configuration file");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctxFile), StandardCharsets.UTF_8));
                XMLGenerator xmlGen = new XMLGenerator();
                success = xmlGen.createXml(context, writer);
            } catch (Exception ex) {
                InstallUtil.error(ex.getMessage());
            } finally {
                close(writer);
            }
        }

        return success;
    }

    public boolean handleTomcatServerFile(TomcatConf server, InstallParam installParam, Scanner scanner) throws Exception {
        boolean success = false;

        String serverConfFile = InstallUtil.getTomcatServerConfigFilePath(installParam);
        File original = new File(serverConfFile);
        if (InstallUtil.backup(original, scanner)) {
            InstallUtil.output("Updating Tomcat server configuration file");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serverConfFile), StandardCharsets.UTF_8));
                XMLGenerator xmlGen = new XMLGenerator();
                success = xmlGen.createXml(server, writer);
            } catch (Exception ex) {
                InstallUtil.error(ex.getMessage());
            } finally {
                close(writer);
            }
        }

        return success;
    }
}
