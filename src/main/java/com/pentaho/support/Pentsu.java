package com.pentaho.support;

import com.pentaho.install.ActionResult;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.input.SelectInput;
import com.pentaho.install.post.PostInstaller;
import com.pentaho.support.connection.Connector;
import com.pentaho.support.util.CacheCleaner;

import java.util.Scanner;

import static com.pentaho.install.InstallUtil.EXIT;
import static com.pentaho.install.InstallUtil.NEW_LINE;

public class Pentsu {
    public static String PENSU = ".pensu";

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SelectInput input = new SelectInput(prompt(), new String[]{"1", "2", "3", "0"});

        try {
            while (true) {
                InstallUtil.ask(scanner, input);
                String opt = input.getValue();

                if (EXIT.equals(opt)) {
                    break;
                }

                try {
                    switch (opt) {
                        case "1":
                            try {
                                PostInstaller postInstaller = new PostInstaller(scanner, null);
                                postInstaller.install();
                            } catch (Exception ex) {
                                System.err.println(ex.getMessage());
                            }
                            break;
                        case "2":
                            Connector connector = new Connector(scanner);
                            connector.execute();
                            break;
                        case "3":
                            CacheCleaner cc = new CacheCleaner(scanner);
                            cc.clean();
                            break;
                    }
                } catch (Exception ex) {
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        } finally {
            scanner.close();
        }
    }

    public static String prompt() {
        StringBuilder buf = new StringBuilder();
        buf.append(InstallUtil.NEW_LINE).append(InstallUtil.bar()).append(InstallUtil.NEW_LINE);

        buf.append("1: Pentaho Server post installation (Archive mode, ver 7.x, ver 6.x)\n");
        buf.append("2: Connection test (JDBC, LDAP)\n");
        buf.append("3: Server cache clean up\n");
        buf.append(InstallUtil.exitMenuEntry()).append(NEW_LINE);

        buf.append(InstallUtil.bar()).append(InstallUtil.NEW_LINE);
        buf.append("? ");
        return buf.toString();
    }

    public ActionResult execute() {
        SelectInput input = new SelectInput(prompt(), new String[]{"1", "2", "3"});
        InstallUtil.ask(scanner, input);
        return new ActionResult(input.getValue());
    }
}
