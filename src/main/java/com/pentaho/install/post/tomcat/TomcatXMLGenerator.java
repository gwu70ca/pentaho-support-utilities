package com.pentaho.install.post.tomcat;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.post.XMLGenerator;
import com.pentaho.install.post.tomcat.conf.server.Connector;
import com.pentaho.install.post.tomcat.conf.server.Server;
import com.pentaho.install.post.tomcat.conf.server.Service;
import com.pentaho.install.post.tomcat.webapps.Context;
import com.pentaho.install.post.tomcat.webapps.Resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

        DBInstance hibernateDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            hibernateDbInstance.setName(DBParam.DB_NAME_HIBERNATE_DI);
        }
        Dialect dialect = InstallUtil.createDialect(hibernateDbInstance);
        context.setHibernate(createResource(hibernateDbInstance, dialect));

        DBInstance auditDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
        auditDbInstance.setResourceName(RESOURCE_NAME_AUDIT);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            auditDbInstance.setName(DBParam.DB_NAME_HIBERNATE_DI);
        }
        context.setAudit(createResource(auditDbInstance, dialect));

        DBInstance quartzDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_QUARTZ);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            quartzDbInstance.setName(DBParam.DB_NAME_QUARTZ_DI);
        }
        context.setQuartz(createResource(quartzDbInstance, dialect));

        DBInstance penOpMartDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_PENT_OP_MART);
        context.setPentahoOpMart(createResource(penOpMartDbInstance, dialect));

        DBInstance pdiOpMartDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_PDI_OP_MART);
        context.setPdiOpMart(createResource(pdiOpMartDbInstance, dialect));

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

    public boolean createTomcatConfig() {
        boolean success = false;
        try {
            TomcatConf context = createWebappsContext();
            String ctxFile = InstallUtil.getTomcatContextFilePath(installParam);
            File original = new File(ctxFile);
            if (!InstallUtil.backup(original, scanner)) {
                return success;
            }

            InstallUtil.output("Updating Tomcat server configuration file");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ctxFile), StandardCharsets.UTF_8));
                if (!createContextXml(context, writer)) {
                    return success;
                }
            } catch (Exception ex) {
                close(writer);
            }


            if (InstallUtil.isDI(installParam.pentahoServerType)) {
                TomcatConf server = createConfServer();
                String serverConfFile = InstallUtil.getTomcatServerConfigFilePath(installParam);
                original = new File(serverConfFile);
                if (!InstallUtil.backup(original, scanner)) {
                    return success;
                }

                InstallUtil.output("Updating Tomcat server configuration file");
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(serverConfFile), StandardCharsets.UTF_8));
                try {
                    success = createXml(server, writer);
                } catch (Exception ex) {
                    close(writer);
                }
            }
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return success;
    }

    public boolean createContextXml(TomcatConf context, Writer writer) {
        boolean success = false;
        if (!createXml(context, writer)) {
            BooleanInput askToContinue = new BooleanInput("Do you want to continue [y/n]? ");
            InstallUtil.ask(scanner, askToContinue);
            success = askToContinue.yes();
        }

        return success;
    }
}
