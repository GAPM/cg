package exec;

import java.io.File;
import java.util.Optional;

public class GL {
    public static Optional<File> findExec(String name) {
        String path = System.getenv("PATH");
        String[] paths = path.split(File.pathSeparator);

        for (String dir: paths) {
            File f = new File(dir, name);
            if (!f.isDirectory() && f.exists()) {
                return Optional.of(f);
            }
        }

        return Optional.empty();
    }

    public static String bothSideCrop(String s) {
        return s.substring(1, s.length() - 1);
    }
}
