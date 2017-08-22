package com.pentaho.install.post.tomcat;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.PentahoServerParam;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Map;
import java.util.Scanner;

public class TomcatXMLGeneratorTest {
    String SQLSERVER_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "  <Context docbase=\"webapps/pentaho/\" path=\"/pentaho\">\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.microsoft.sqlserver.jdbc.SQLServerDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Hibernate\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:sqlserver://localhost:1433;DatabaseName=hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.microsoft.sqlserver.jdbc.SQLServerDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Audit\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:sqlserver://localhost:1433;DatabaseName=hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.microsoft.sqlserver.jdbc.SQLServerDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Quartz\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:sqlserver://localhost:1433;DatabaseName=quartz\" username=\"pentaho_user\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.microsoft.sqlserver.jdbc.SQLServerDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/pentaho_operations_mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:sqlserver://localhost:1433;DatabaseName=hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.microsoft.sqlserver.jdbc.SQLServerDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/PDI_Operations_Mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:sqlserver://localhost:1433;DatabaseName=hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "</Context>";

    String POSTGRESQL_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "  <Context docbase=\"webapps/pentaho/\" path=\"/pentaho\">\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"org.postgresql.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Hibernate\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:postgresql://localhost:5432/hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"org.postgresql.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Audit\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:postgresql://localhost:5432/hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"org.postgresql.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Quartz\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:postgresql://localhost:5432/quartz\" username=\"pentaho_user\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"org.postgresql.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/pentaho_operations_mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:postgresql://localhost:5432/hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"org.postgresql.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/PDI_Operations_Mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:postgresql://localhost:5432/hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "</Context>";

    String MYSQL_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "  <Context docbase=\"webapps/pentaho/\" path=\"/pentaho\">\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Hibernate\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql://localhost:3306/hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Audit\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql://localhost:3306/hibernate\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Quartz\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql://localhost:3306/quartz\" username=\"pentaho_user\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/pentaho_operations_mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql://localhost:3306/pentaho_operations_mart\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"com.mysql.jdbc.Driver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/PDI_Operations_Mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:mysql://localhost:3306/pentaho_operations_mart\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "</Context>";

    String ORACLE_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                    "  <Context docbase=\"webapps/pentaho/\" path=\"/pentaho\">\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"oracle.jdbc.OracleDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Hibernate\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:oracle:thin:@localhost:1521/XE\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"oracle.jdbc.OracleDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Audit\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:oracle:thin:@localhost:1521/XE\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"oracle.jdbc.OracleDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/Quartz\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:oracle:thin:@localhost:1521/XE\" username=\"pentaho_user\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"oracle.jdbc.OracleDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/pentaho_operations_mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:oracle:thin:@localhost:1521/XE\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "  <Resource auth=\"Container\" driverClassName=\"oracle.jdbc.OracleDriver\" factory=\"org.apache.commons.dbcp.BasicDataSourceFactory\" maxIdle=\"5\" maxTotal=\"20\" maxWaitMillis=\"10000\" name=\"jdbc/PDI_Operations_Mart\" password=\"password\" type=\"javax.sql.DataSource\" url=\"jdbc:oracle:thin:@localhost:1521/XE\" username=\"hibuser\" validationQuery=\"select 1\"/>\n" +
                    "</Context>";

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
        txg.createContextXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        String[] lines1 = xml.split("\n");
        String[] lines2 = MYSQL_XML.split("\n");
        Assert.assertEquals(lines1.length, lines2.length);

        for (int i=0;i<lines1.length;i++) {
            //System.out.println("generated: " + lines1[i].trim());
            //System.out.println("expected : " + lines2[i].trim());
            Assert.assertEquals(lines1[i].trim(), lines2[i].trim());
        }
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
        txg.createContextXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        String[] lines1 = xml.split("\n");
        String[] lines2 = POSTGRESQL_XML.split("\n");
        Assert.assertEquals(lines1.length, lines2.length);

        for (int i=0;i<lines1.length;i++) {
            //System.out.println("generated: " + lines1[i].trim());
            //System.out.println("expected : " + lines2[i].trim());
            Assert.assertEquals(lines1[i].trim(), lines2[i].trim());
        }
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
        txg.createContextXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        String[] lines1 = xml.split("\n");
        String[] lines2 = SQLSERVER_XML.split("\n");
        Assert.assertEquals(lines1.length, lines2.length);

        for (int i=0;i<lines1.length;i++) {
            Assert.assertEquals(lines1[i].trim(), lines2[i].trim());
        }
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
        txg.createContextXml(context, writer);
        String xml = writer.toString();
        Assert.assertNotNull(xml);

        String[] lines1 = xml.split("\n");
        String[] lines2 = ORACLE_XML.split("\n");
        Assert.assertEquals(lines1.length, lines2.length);

        for (int i=0;i<lines1.length;i++) {
            Assert.assertEquals(lines1[i].trim(), lines2[i].trim());
        }
    }
}