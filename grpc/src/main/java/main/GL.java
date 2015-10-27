package main;

import java.io.File;
import java.util.Optional;

public class GL {
    public static Optional<File> findExec(String name) {
        String path = System.getenv("PATH");
        String[] dirs = path.split(File.pathSeparator);

        File f;
        for (String dir : dirs) {
            f = new File(dir, name);
            if (f.exists() && f.isFile() && f.canExecute()) {
                return Optional.of(f);
            }
        }

        return Optional.empty();
    }

    public static String bothSideCrop(String s) {
        try {
            return s.substring(1, s.length() - 1);
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }
}
