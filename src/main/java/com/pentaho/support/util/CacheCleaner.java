package com.pentaho.support.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CacheCleaner {
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

        CacheCleaner cc = new CacheCleaner();
        cc.clean(dir.getAbsolutePath());
    }

    public void clean(String dirName) {
        delete(Paths.get(dirName + "/server/pentaho-server/pentaho-solutions/system/jackrabbit/repository"));
        delete(Paths.get(dirName + "/server/pentaho-server/pentaho-solutions/system/karaf/cache"));
        delete(Paths.get(dirName + "/server/pentaho-server/pentaho-solutions/system/karaf/instances"));
        delete(Paths.get(dirName + "/server/pentaho-server/tomcat/temp"));
        delete(Paths.get(dirName + "/server/pentaho-server/tomcat/work"));
    }

    private void delete(Path path) {
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
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
