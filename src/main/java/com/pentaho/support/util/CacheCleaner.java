package com.pentaho.support.util;

import com.pentaho.install.InstallUtil;
import com.pentaho.install.input.StringInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

public class CacheCleaner {
    Scanner scanner;

    public CacheCleaner(Scanner scanner) {
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Need directory name");
            System.out.println("-dir C:\\Pentaho\\pentaho71");
            System.exit(0);
        }

        String dirName = null;
        for (int i=0;i<args.length;i++) {
            if ("-dir".equals(args[i])) {
                dirName = args[++i];
            }
        }

        File dir = new File(dirName);
        if (!dir.exists() || !dir.isDirectory() || !dir.canRead() || !dir.canWrite()) {
            System.out.println("Directory is invalid. Make sure it exists, current user has read/write permission on it.");
            System.exit(0);
        }

        Scanner scanner = new Scanner(System.in);

        CacheCleaner cc = new CacheCleaner(scanner);
        cc.clean(dir.getAbsolutePath());

        scanner.close();
    }

    public void clean() {
        StringInput input = new StringInput("Tell me where Pentaho server is installed: ");
        InstallUtil.ask(scanner, input);
        clean(input.getValue());
    }

    public void clean(String dirName) {
        delete(Paths.get(dirName + "/server/pentaho-server/pentaho-solutions/system/jackrabbit/repository"));
        delete(Paths.get(dirName + "/server/pentaho-server/pentaho-solutions/system/karaf/cache"));
        delete(Paths.get(dirName + "/server/pentaho-server/pentaho-solutions/system/karaf/instances"));
        delete(Paths.get(dirName + "/server/pentaho-server/tomcat/temp"));
        delete(Paths.get(dirName + "/server/pentaho-server/tomcat/work"));
    }

    private void delete(Path path) {
        InstallUtil.output("Deleting " + path);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            InstallUtil.output("\t[deleted]");
        } catch (NoSuchFileException nsfe) {
            InstallUtil.output("\t[doesn't exist, skip]");
            //InstallUtil.output("\t[]");
        } catch (Exception ex) {
            InstallUtil.error(ex.getMessage());
        }
    }
}
