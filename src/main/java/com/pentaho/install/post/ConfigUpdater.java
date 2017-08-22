package com.pentaho.install.post;

import com.pentaho.install.*;
import com.pentaho.install.action.CopyFileAction;
import com.pentaho.install.db.Dialect;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.input.FileInput;
import com.pentaho.install.input.SelectInput;
import com.pentaho.install.input.StringInput;
import com.pentaho.install.post.jackrabbit.JackrabbitXMLGenerator;
import com.pentaho.install.post.tomcat.TomcatXMLGenerator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ConfigUpdater extends InstallAction {
    private Scanner scanner;
    private InstallParam installParam;
    private String serverDirName;

    public ConfigUpdater(InstallParam installParam, Scanner scanner) throws Exception {
        this.installParam = installParam;
        this.scanner = scanner;

        this.serverDirName = PentahoServerParam.getServerDirectoryName(installParam.pentahoServerType);
    }

    private String prompt() {
        StringBuilder buf = new StringBuilder();
        buf.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);

        buf.append("1: Apache Tomcat\n");
        buf.append("2: Jboss Application Server\n");

        buf.append(InstallUtil.bar()).append(NEW_LINE);
        buf.append("Select the application server type: ");
        return buf.toString();
    }

    public ActionResult execute() throws Exception {
        if (!PostInstaller.SILENT) {
            do {
                InstallUtil.newLine();
                BooleanInput askToProceed = new BooleanInput("Installer is going to update various configuration files, do you want to continue [y/n]? ");
                InstallUtil.ask(scanner, askToProceed);
                if (askToProceed.yes()) {
                    break;
                }

                BooleanInput askToQuit = new BooleanInput("Do you want to quit [y/n]? ");
                InstallUtil.ask(scanner, askToQuit);
                if (askToQuit.yes()) {
                    System.exit(0);
                }
            } while (true);
        }

        update();

        InstallUtil.output("All files were updated.");
        return new ActionResult("");
    }

    private void update() throws Exception {
        Dialect dialect = InstallUtil.createDialect(installParam.dbType);

        //update quartz
        if (!updateQuartz(dialect)) {
            if (PostInstaller.SILENT) {
            } else {
                BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
                InstallUtil.ask(scanner, askForContinue);
                if (!askForContinue.yes()) {
                    return;
                }
            }
        }
        InstallUtil.output("\t[done]");

        //update hibernate
        if (!updateHibernate(dialect)) {
            if (PostInstaller.SILENT) {
            } else {
                BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
                InstallUtil.ask(scanner, askForContinue);
                if (!askForContinue.yes()) {
                    return;
                }
            }
        }
        InstallUtil.output("\t[done]");

        //update audit
        if (!updateAudit(dialect)) {
            if (PostInstaller.SILENT) {

            } else {
                BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
                InstallUtil.ask(scanner, askForContinue);
                if (!askForContinue.yes()) {
                    return;
                }
            }
        }
        InstallUtil.output("\t[done]");

        //update jackrabbit
        if (!updateJackrabbit()) {
            if (PostInstaller.SILENT) {
            } else {
                BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
                InstallUtil.ask(scanner, askForContinue);
                if (!askForContinue.yes()) {
                    return;
                }
            }
        }
        InstallUtil.output("\t[done]");

        boolean isTomcat, isJboss;
        if (PostInstaller.SILENT) {
            AppServerParam.SERVER appServer = installParam.appServerType;
            isTomcat = appServer.equals(AppServerParam.SERVER.TOMCAT);
            isJboss = appServer.equals(AppServerParam.SERVER.JBOSS);
        } else {
            InstallUtil.newLine();
            SelectInput appServerTypeInput = new SelectInput(prompt(), new String[]{"1", "2"});
            InstallUtil.ask(scanner, appServerTypeInput);
            isTomcat = "1".equals(appServerTypeInput.getValue());
            isJboss = "2".equals(appServerTypeInput.getValue());
        }

        if (isTomcat) {
            //update tomcat
            updateTomcat();
        } else if (isJboss) {
            //update jboss
        }

        InstallUtil.newLine();
    }

    private void updateTomcat() throws Exception {
        if (PostInstaller.SILENT) {
        } else {
            String tomcatDirName = "tomcat";
            StringInput tomcatDirInput = new StringInput(String.format("Input Tomcat directory name [%s]: ", tomcatDirName));
            tomcatDirInput.setDefaultValue(tomcatDirName);
            InstallUtil.ask(scanner, tomcatDirInput);
            installParam.appServerDir = tomcatDirInput.getValue();
        }

        TomcatXMLGenerator gen = new TomcatXMLGenerator(installParam, scanner);
        boolean success = gen.createTomcatConfig();

        if (!success) {
            if (PostInstaller.SILENT) {

            } else {
                BooleanInput askForContinue = new BooleanInput("There is error happened, do you want to move to next step (y/n)? ");
                InstallUtil.ask(scanner, askForContinue);
                if (!askForContinue.yes()) {
                    System.exit(0);
                }
            }
        }
        InstallUtil.output("\t[done]");

        if (PostInstaller.SILENT) {

        } else {
            InstallUtil.newLine();
            BooleanInput askForContinue = new BooleanInput("Do you want installer to copy the JDBC driver jar file (y/n)? ");
            InstallUtil.ask(scanner, askForContinue);
            if (askForContinue.yes()) {
                do {
                    FileInput jdbcDriverJar = new FileInput("Input location of the JDBC driver jar file: ");
                    InstallUtil.ask(scanner, jdbcDriverJar);
                    String jarPath = jdbcDriverJar.getValue();
                    String jarFilename = InstallUtil.getFilenameFromPath(jarPath);

                    File source = new File(jarPath);
                    File target = new File(installParam.installDir + "/server/" + serverDirName + "/" + installParam.appServerDir + "/lib", jarFilename);
                    CopyFileAction action = new CopyFileAction(source, target);
                    try {
                        action.execute();
                        InstallUtil.output("\t[done]");
                        break;
                    } catch (IOException ioe) {
                        askForContinue = new BooleanInput("There is error happened, do you want to try again (y/n)? ");
                        InstallUtil.ask(scanner, askForContinue);
                        if (askForContinue.yes()) {
                            continue;
                        }
                        break;
                    }
                } while (true);
            }
        }
    }

    private boolean updateJackrabbit() {
        JackrabbitXMLGenerator jxg = new JackrabbitXMLGenerator(installParam, scanner);
        return jxg.createJackrabbitConfig();
    }

    private boolean updateQuartz(Dialect dialect) {
        String quartzDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/quartz";
        quartzDir = quartzDir.replace('/', File.separatorChar);
        File original = new File(quartzDir + File.separator + "quartz.properties");

        if (!InstallUtil.backup(original, scanner)) {
            return false;
        }

        boolean success = false;
        InstallUtil.output("Updating quartz configuration file " + original.getAbsolutePath());
        try {
            StringBuilder builder = new StringBuilder();
            Scanner sc = new Scanner(original);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("#") || line.trim().length() == 0) {
                    builder.append(line).append(NEW_LINE);
                    continue;
                }
                if (line.startsWith("org.quartz.jobStore.driverDelegateClass")) {
                    line = "org.quartz.jobStore.driverDelegateClass = " + dialect.getQuartzDriverDelegateClass();
                } else if (line.startsWith("org.quartz.dataSource.myDS.jndiURL")) {
                    line = "org.quartz.dataSource.myDS.jndiURL = Quartz";
                }
                builder.append(line).append(NEW_LINE);
            }
            sc.close();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(original), StandardCharsets.UTF_8);
            writer.write(builder.toString());
            writer.close();

            success = true;
        } catch (FileNotFoundException e) {
            InstallUtil.output("\nCould not find the file " + original.getAbsolutePath());
        } catch (IOException ioe) {
            InstallUtil.output("\nCould not write to the file " + original.getAbsolutePath());
        }
        return success;
    }

    private boolean updateHibernate(Dialect dialect) {
        String hibernateDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/hibernate";
        hibernateDir = hibernateDir.replace('/', File.separatorChar);
        String hibernateCfgFile = dialect.getHibernateConfigFile();
        File original = new File(hibernateDir + File.separator + "hibernate-settings.xml");

        if (!InstallUtil.backup(original, scanner)) {
            return false;
        }

        boolean success = false;
        InstallUtil.output("\nUpdating hibernate configuration file " + original.getAbsolutePath());
        try {
            String STR = "<config-file>system/hibernate/";
            StringBuilder builder = new StringBuilder();
            Scanner sc = new Scanner(original);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().startsWith(STR)) {
                    line = line.substring(0, line.indexOf("<config-file>")) + "<config-file>system/hibernate/" + hibernateCfgFile + "</config-file>";
                }
                builder.append(line).append(NEW_LINE);
            }
            sc.close();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(original), StandardCharsets.UTF_8);
            writer.write(builder.toString());
            writer.close();

            success = true;
        } catch (FileNotFoundException e) {
            InstallUtil.output("\nCould not find the file " + original.getAbsolutePath());
        } catch (IOException ioe) {
            InstallUtil.output("\nCould not write to the file " + original.getAbsolutePath());
        }

        if (!success) {
            BooleanInput askToContinue = new BooleanInput("Do you want to continue [y/n]? ");
            InstallUtil.ask(scanner, askToContinue);
            if (!askToContinue.yes()) {
                return false;
            }
        }

        original = new File(hibernateDir + File.separator + hibernateCfgFile);
        if (!InstallUtil.backup(original, scanner)) {
            return success;
        }

        InstallUtil.output("Updating hibernate configuration file " + original.getAbsolutePath());
        try {
            DBInstance hibernateDbInstance = installParam.dbInstanceMap.get(DBParam.DB_NAME_HIBERNATE);

            String JDBC_STR = "<property name=\"connection.url\">";
            String USERNAME_STR = "<property name=\"connection.username\">";
            String PASSWORD_STR = "<property name=\"connection.password\">";
            StringBuilder builder = new StringBuilder();
            Scanner sc = new Scanner(original);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().startsWith(JDBC_STR)) {
                    line = line.substring(0, line.indexOf(JDBC_STR) + JDBC_STR.length()) +
                            dialect.getJdbcUrl(hibernateDbInstance, false) + "</property>";
                } else if (line.trim().startsWith(USERNAME_STR)) {
                    line = line.substring(0, line.indexOf(USERNAME_STR) + USERNAME_STR.length()) + hibernateDbInstance.getUsername() + "</property>";
                } else if (line.trim().startsWith(PASSWORD_STR)) {
                    line = line.substring(0, line.indexOf(PASSWORD_STR) + PASSWORD_STR.length()) + hibernateDbInstance.getPassword() + "</property>";
                }
                builder.append(line).append(NEW_LINE);
            }
            sc.close();

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(original), StandardCharsets.UTF_8);
            writer.write(builder.toString());
            writer.close();

            success = true;
        } catch (FileNotFoundException e) {
            InstallUtil.output("\nCould not find the file " + original.getAbsolutePath());
        } catch (IOException ioe) {
            InstallUtil.output("\nCould not write to the file " + original.getAbsolutePath());
        }

        return success;
    }

    private boolean updateAudit(Dialect dialect) {
        String auditDir = installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/dialects";
        auditDir = auditDir.replace('/', File.separatorChar);
        InstallUtil.output("\nCopying audit configuration file");

        //backup the original file first
        File original = new File(installParam.installDir + "/server/" + serverDirName + "/pentaho-solutions/system/audit_sql.xml");
        if (!InstallUtil.backup(original, scanner)) {
            return false;
        }

        boolean success = false;
        File newAuditFile = new File(auditDir + File.separator + dialect.getAuditDirName() + File.separator + "audit_sql.xml");
        CopyFileAction action = new CopyFileAction(newAuditFile, original);
        try {
            action.execute();
            success = true;
        } catch (IOException ex) {
            InstallUtil.output("\nFailed to copy from \n\t" + newAuditFile.getAbsolutePath() + "\n\tto" + original.getAbsolutePath());
        }

        return success;
    }
}
