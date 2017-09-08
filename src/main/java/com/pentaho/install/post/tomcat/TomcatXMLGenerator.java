package com.pentaho.install.post.tomcat;

import com.pentaho.install.*;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.file.ConfigFileHandler;
import com.pentaho.install.file.TestConfigFileHandler;
import com.pentaho.install.post.XMLGenerator;
import com.pentaho.install.post.tomcat.conf.server.Connector;
import com.pentaho.install.post.tomcat.conf.server.Server;
import com.pentaho.install.post.tomcat.conf.server.Service;
import com.pentaho.install.post.tomcat.webapps.Context;
import com.pentaho.install.post.tomcat.webapps.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.pentaho.install.DBParam.RESOURCE_NAME_AUDIT;

public class TomcatXMLGenerator extends XMLGenerator {
    private InstallParam installParam;
    private Scanner scanner;

    public TomcatXMLGenerator(InstallParam installParam, Scanner scanner) {
        this.installParam = installParam;
        this.scanner = scanner;
    }

    private TomcatConf createConfServer() {
        Server server = new Server();
        Service service = server.getService();
        Connector httpConnector = service.getHttpConnector();
        httpConnector.setPort("9080");
        httpConnector.setRedirectPort("9443");
        return server;
    }

    public TomcatConf createWebappsContext() {
        Context context = new Context();

        List<Resource> resourceList = new ArrayList<>();

        DBInstance hibernateDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            hibernateDbInstance.setName(DBParam.DB_NAME_HIBERNATE_DI);
        }
        Dialect dialect = InstallUtil.createDialect(hibernateDbInstance);
        resourceList.add(createResource(hibernateDbInstance, dialect));

        DBInstance auditDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
        auditDbInstance.setResourceName(RESOURCE_NAME_AUDIT);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            auditDbInstance.setName(DBParam.DB_NAME_HIBERNATE_DI);
        }
        resourceList.add(createResource(auditDbInstance, dialect));

        DBInstance quartzDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_QUARTZ);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            quartzDbInstance.setName(DBParam.DB_NAME_QUARTZ_DI);
        }
        resourceList.add(createResource(quartzDbInstance, dialect));

        DBInstance penOpMartDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_PENT_OP_MART);
        resourceList.add(createResource(penOpMartDbInstance, dialect));

        //DBInstance pdiOpMartDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_PDI_OP_MART);
        //context.setPdiOpMart(createResource(pdiOpMartDbInstance, dialect));

        context.setResourceList(resourceList);

        return context;
    }

    private Resource createResource(DBInstance db, Dialect dialect) {
        return new Resource(
                dialect.getJdbcDriverClass(),
                db.getResourceName(),
                db.getPassword(),
                dialect.getJdbcUrl(db, false),
                db.getUsername(),
                dialect.getValidationQuery());
    }

    public boolean createTomcatConfig(ConfigFileHandler configFileHandler) {
        boolean success = false;
        try {
            TomcatConf context = createWebappsContext();
            configFileHandler.handleTomcatContextFile(context, installParam, scanner);

            if (InstallUtil.isDI(installParam.pentahoServerType)) {
                TomcatConf server = createConfServer();
                configFileHandler.handleTomcatServerFile(server, installParam, scanner);
            }

            success = true;
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return success;
    }

    public static void main(String[] args) throws Exception {
        InstallParam installParam = new InstallParam();
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        installParam.dbType = DBParam.DB.Psql;
        installParam.dbInstanceMap = DBParam.initDbInstances(installParam.dbType);

        TomcatXMLGenerator g = new TomcatXMLGenerator(installParam, new Scanner(System.in));
        g.createTomcatConfig(new TestConfigFileHandler());
    }
}
