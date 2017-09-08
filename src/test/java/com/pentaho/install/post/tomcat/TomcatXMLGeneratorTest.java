package com.pentaho.install.post.tomcat;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.PentahoServerParam;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TomcatXMLGeneratorTest {
    @Test
    public void testMySQL() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Mysql;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        TomcatXMLGenerator txg = new TomcatXMLGenerator(installParam, new Scanner(System.in));
        TomcatConf context = txg.createWebappsContext();
        StringWriter writer = new StringWriter();
        txg.createXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/tomcat/context_mysql.xml");
    }

    @Test
    public void testPostgreSQL() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Psql;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        TomcatXMLGenerator txg = new TomcatXMLGenerator(installParam, new Scanner(System.in));
        TomcatConf context = txg.createWebappsContext();
        StringWriter writer = new StringWriter();
        txg.createXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/tomcat/context_postgresql.xml");
    }

    @Test
    public void testSQLServer() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Sqlserver;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        TomcatXMLGenerator txg = new TomcatXMLGenerator(installParam, new Scanner(System.in));
        TomcatConf context = txg.createWebappsContext();
        StringWriter writer = new StringWriter();
        txg.createXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/tomcat/context_sqlserver.xml");
    }

    @Test
    public void testOracle() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Orcl;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        TomcatXMLGenerator txg = new TomcatXMLGenerator(installParam, new Scanner(System.in));
        TomcatConf context = txg.createWebappsContext();
        StringWriter writer = new StringWriter();
        txg.createXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/tomcat/context_oracle.xml");
    }

    private void assertAllLines(String xml, String filename) {
        //System.out.println(xml);

        URL url = this.getClass().getResource(filename);
        File file = new File(url.getFile());
        try {
            String[] lines1 = xml.split("\n");
            List<String> lines2 = Files.readAllLines(file.toPath());

            Assert.assertEquals(lines1.length, lines2.size());

            int min = lines1.length < lines2.size() ? lines1.length : lines2.size();
            for (int i = 0; i < min; i++) {
                Assert.assertEquals(lines1[i].trim(), lines2.get(i).trim());
                //System.out.println(lines1[i].trim());
                //System.out.println(lines2.get(i).trim());
                //System.out.println("------------------------");
            }
        } catch (Exception ex) {
            Assert.fail(ex.getMessage());
        }
    }
}