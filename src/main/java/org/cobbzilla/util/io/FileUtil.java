package org.cobbzilla.util.io;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileUtil {

    public static final File DEFAULT_TEMPDIR = new File(System.getProperty("java.io.tmpdir"));

    public static String chopSuffix(String path) {
        if (path == null) return null;
        final int lastDot = path.lastIndexOf('.');
        if (lastDot == -1 || lastDot == path.length()-1) return path;
        return path.substring(0, lastDot);
    }

    public static File createTempDir(String prefix) throws IOException {
        return createTempDir(DEFAULT_TEMPDIR, prefix);
    }

    public static File createTempDir(File parentDir, String prefix) throws IOException {
        Path parent = FileSystems.getDefault().getPath(parentDir.getAbsolutePath());
        return new File(Files.createTempDirectory(parent, prefix).toAbsolutePath().toString());
    }

    public static File createTempDirOrDie(String prefix) {
        return createTempDirOrDie(DEFAULT_TEMPDIR, prefix);
    }

    public static File createTempDirOrDie(File parentDir, String prefix) {
        try {
            return createTempDir(parentDir, prefix);
        } catch (IOException e) {
            throw new IllegalStateException("createTempDirOrDie: error creating directory with prefix="+parentDir.getAbsolutePath()+"/"+prefix+": "+e, e);
        }
    }

    public static void writeResourceToFile(String resourcePath, File outFile, Class clazz) throws IOException {
        if (!outFile.getParentFile().exists() || !outFile.getParentFile().canWrite() || (outFile.exists() && !outFile.canWrite())) {
            throw new IllegalArgumentException("outFile is not writeable: "+outFile.getAbsolutePath());
        }
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(resourcePath);
             OutputStream out = new FileOutputStream(outFile)) {
            if (in == null) throw new IllegalArgumentException("null data at resourcePath: "+resourcePath);
            IOUtils.copy(in, out);
        }
    }

    public static List<String> loadResourceAsStringListOrDie(String resourcePath, Class clazz) {
        try {
            return loadResourceAsStringList(resourcePath, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("loadResourceAsStringList error: "+e, e);
        }
    }

    public static List<String> loadResourceAsStringList(String resourcePath, Class clazz) throws IOException {
        final List<String> strings = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(StreamUtil.loadResourceAsReader(resourcePath, clazz))) {
            String line;
            while ((line = r.readLine()) != null) {
                strings.add(line.trim());
            }
        }
        return strings;
    }

    public static String toStringOrDie (String f) {
        return toStringOrDie(new File(f));
    }

    public static String toStringOrDie (File f) {
        try {
            return toString(f);
        } catch (IOException e) {
            final String path = f == null ? "null" : f.getAbsolutePath();
            throw new IllegalArgumentException("Error reading file ("+ path +"): "+e, e);
        }
    }

    public static String toString (String f) throws IOException {
        return toString(new File(f));
    }

    public static String toString (File f) throws IOException {
        StringWriter writer = new StringWriter();
        try (Reader r = new FileReader(f)) {
            IOUtils.copy(r, writer);
        }
        return writer.toString();
    }

    public static Properties toPropertiesOrDie (String f) {
        return toPropertiesOrDie(new File(f));
    }

    private static Properties toPropertiesOrDie(File f) {
        try {
            return toProperties(f);
        } catch (IOException e) {
            final String path = f == null ? "null" : f.getAbsolutePath();
            throw new IllegalArgumentException("Error reading properties file ("+ path +"): "+e, e);
        }
    }

    public static Properties toProperties (String f) throws IOException {
        return toProperties(new File(f));
    }

    public static Properties toProperties (File f) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(f)) {
            props.load(in);
        }
        return props;
    }

    public static Properties resourceToPropertiesOrDie (String path, Class clazz) {
        try {
            return resourceToProperties(path, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading resource ("+ path +"): "+e, e);
        }
    }

    public static Properties resourceToProperties (String path, Class clazz) throws IOException {
        Properties props = new Properties();
        try (InputStream in = StreamUtil.loadResourceAsStream(path, clazz)) {
            props.load(in);
        }
        return props;
    }

    public static File toFileOrDie (String file, String data) {
        return toFileOrDie(new File(file), data);
    }

    private static File toFileOrDie(File file, String data) {
        try {
            return toFile(file, data);
        } catch (IOException e) {
            String path = file.getAbsolutePath();
            throw new IllegalStateException("toFileOrDie: error writing to file: "+ (path == null ? "null" : path));
        }
    }

    public static File toFile (String file, String data) throws IOException {
        return toFile(new File(file), data);
    }

    private static File toFile(File file, String data) throws IOException {
        try (OutputStream out = new FileOutputStream(file)) {
            IOUtils.copy(new ByteArrayInputStream(data.getBytes()), out);
        }
        return file;
    }
}
