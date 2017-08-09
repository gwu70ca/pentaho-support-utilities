package com.pentaho.install.post.tomcat;

import com.pentaho.install.DBInstance;
import com.pentaho.install.DBParam;
import com.pentaho.install.InstallParam;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.post.XMLGenerator;
import com.pentaho.install.post.tomcat.conf.server.Connector;
import com.pentaho.install.post.tomcat.conf.server.Server;
import com.pentaho.install.post.tomcat.conf.server.Service;
import com.pentaho.install.post.tomcat.webapps.Context;
import com.pentaho.install.post.tomcat.webapps.Resource;

public class TomcatXMLGenerator extends XMLGenerator {
    private InstallParam installParam;

    public TomcatXMLGenerator(InstallParam installParam) {
        this.installParam = installParam;
    }

    private TomcatConf createConfServer() {
        Server server = new Server();
        Service service = server.getService();
        Connector httpConnector = service.getHttpConnector();
        httpConnector.setPort("9080");
        httpConnector.setRedirectPort("9443");
        return server;
    }

    private TomcatConf createWebappsContext() {
        Context context = new Context();

        DBInstance hibernateDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            hibernateDbInstance.setName(DBParam.DB_NAME_HIBERNATE_DI);
        }
        context.setHibernate(createResource(hibernateDbInstance));

        DBInstance auditDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            auditDbInstance.setName(DBParam.DB_NAME_HIBERNATE_DI);
        }
        context.setAudit(createResource(auditDbInstance));

        DBInstance quartzDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_QUARTZ);
        if (InstallUtil.isDI(installParam.pentahoServerType)) {
            quartzDbInstance.setName(DBParam.DB_NAME_QUARTZ_DI);
        }
        context.setQuartz(createResource(quartzDbInstance));

        DBInstance penOpMartDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_PENT_OP_MART);
        context.setPentahoOpMart(createResource(penOpMartDbInstance));

        /*
        String pdiOpMart = installParam.pentahoServerType.equals(PentahoServerParam.SERVER.BA) ? DBParam.DB_NAME_HIBERNATE : DBParam.DB_NAME_HIBERNATE_DI;
        DBInstance pdiOpMartDbInstance = installParam.dbInstanceMap.get(pdiOpMart);
        pdiOpMartDbInstance.setType(installParam.dbType);
        pdiOpMartDbInstance.setResourceName(DBParam.NAME_PDI_OPERATIONS_MART);
        context.setPdiOpMart(createResource(pdiOpMartDbInstance));
*/
        return context;
    }

    private Resource createResource(DBInstance db) {
        return new Resource(
                InstallUtil.getJdbcDriverClass(db.getType()),
                db.getResourceName(),
                db.getPassword(),
                InstallUtil.getJdbcUrl(db),
                db.getUsername(),
                DBParam.getValidationQuery(db.getType()));
    }

    public boolean createTomcatConfig() {
        boolean success = false;
        try {
            TomcatConf context = createWebappsContext();
            String ctxFile = InstallUtil.getTomcatContextFilePath(installParam);
            success = createXml(context, ctxFile, "Updating Tomcat context configuration file: ");

            if (InstallUtil.isDI(installParam.pentahoServerType)) {
                TomcatConf server = createConfServer();
                String serverConfFile = InstallUtil.getTomcatServerConfigFilePath(installParam);
                success = createXml(server, serverConfFile, "Updating Tomcat server configuration file: ");
            }
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }

        return success;
    }

    /*
    public static void main(String[] args) throws Exception {
        System.setProperty("local", "true");
        InstallParam installParam = new InstallParam();
        installParam.dbType = DBParam.DB.PostgreSQL;
        installParam.pentahoServerType = PentahoServerParam.SERVER.BA;
        Map<String, DBInstance> dbInstanceMap = DBParam.initDbInstances(PentahoServerParam.SERVER.BA);
        installParam.dbInstanceMap = dbInstanceMap;

        TomcatXMLGenerator txg = new TomcatXMLGenerator(installParam);
        txg.createTomcatConfig();
    }
    */
}
