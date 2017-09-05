package com.pentaho.install.post.jackrabbit;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.post.PentahoXMLConfig;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class JackrabbitXMLGeneratorTest {
    @Test
    public void testMySQLPentaho7() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Mysql;
        installParam.pentahoServerType = PentahoServerParam.SERVER.HYBRID;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        JackrabbitXMLGenerator jxg = new JackrabbitXMLGenerator(installParam, new Scanner(System.in));
        PentahoXMLConfig repository = jxg.createRepository();
        StringWriter writer = new StringWriter();
        jxg.createXml(repository, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/repository_7_mysql.xml");
    }

    @Test
    public void testMySQLPentaho6BA() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Mysql;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        JackrabbitXMLGenerator jxg = new JackrabbitXMLGenerator(installParam, new Scanner(System.in));
        PentahoXMLConfig repository = jxg.createRepository();
        StringWriter writer = new StringWriter();
        jxg.createXml(repository, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/repository_ba_mysql.xml");
    }

    @Test
    public void testMySQLPentaho6DI() {
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Mysql;
        installParam.pentahoServerType = PentahoServerParam.SERVER.DI;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(installParam.dbType);
        installParam.dbInstanceMap = dbInstanceMap;

        JackrabbitXMLGenerator jxg = new JackrabbitXMLGenerator(installParam, new Scanner(System.in));
        PentahoXMLConfig repository = jxg.createRepository();
        StringWriter writer = new StringWriter();
        jxg.createXml(repository, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        assertAllLines(xml, "/repository_di_mysql.xml");
    }

    private void assertAllLines(String xml, String filename) {
        System.out.println(xml);

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
