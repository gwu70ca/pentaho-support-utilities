package com.pentaho.install.post.tomcat;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.PentahoServerParam;
import org.junit.Test;

import java.util.Map;

public class TomcatXMLGeneratorTest {
    @Test
    public void testGenerateContextXml() {
        System.setProperty("local", "true");
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.Psql;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(DBParam.DB.Psql);
        installParam.dbInstanceMap = dbInstanceMap;

        TomcatXMLGenerator txg = new TomcatXMLGenerator(installParam);
        txg.createTomcatConfig();
    }
}