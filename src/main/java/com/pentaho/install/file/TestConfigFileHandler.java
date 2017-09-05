package com.pentaho.install.file;

import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.post.PentahoXMLConfig;
import com.pentaho.install.post.XMLGenerator;
import com.pentaho.install.post.tomcat.TomcatConf;

import java.io.StringWriter;
import java.util.Scanner;

public class TestConfigFileHandler implements ConfigFileHandler {
    public boolean handleJackrabbitRepositiryFile(PentahoXMLConfig repository, InstallParam installParam, Scanner scanner) throws Exception {
        boolean success = false;

        StringWriter writer = null;

        try {
            writer = new StringWriter();
            XMLGenerator xmlGen = new XMLGenerator();
            success = xmlGen.createXml(repository, writer);

            System.out.println(writer.toString());
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        } finally {
            close(writer);
        }

        return success;
    }

    public boolean handleTomcatContextFile(TomcatConf context, InstallParam installParam, Scanner scanner) throws Exception {
        boolean success = false;

        StringWriter writer = null;

        try {
            writer = new StringWriter();
            XMLGenerator xmlGen = new XMLGenerator();
            success = xmlGen.createXml(context, writer);

            System.out.println(writer.toString());
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        } finally {
            close(writer);
        }

        return success;
    }

    public boolean handleTomcatServerFile(TomcatConf server, InstallParam installParam, Scanner scanner) throws Exception {
        boolean success = false;

        StringWriter writer = null;

        try {
            writer = new StringWriter();
            XMLGenerator xmlGen = new XMLGenerator();
            success = xmlGen.createXml(server, writer);

            System.out.println(writer.toString());
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        } finally {
            close(writer);
        }

        return success;
    }
}
