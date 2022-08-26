package io.woo.htmltopdf.wkhtmltopdf;

import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

class WkHtmlToPdfLoader {

    private static final File tmpDir = new File(System.getProperty("java.io.tmpdir"), "io.woo.htmltopdf");

    static WkHtmlToPdf load() {
        if ((!tmpDir.exists() && !tmpDir.mkdirs())) {
            throw new IllegalStateException("htmltopdf temporary directory cannot be created");
        }
        if (!tmpDir.canWrite()) {
            throw new IllegalStateException("htmltopdf temporary directory is not writable");
        }

        File libraryFile = new File(tmpDir, getLibraryResource());
        if (!libraryFile.exists()) {
            try {
                File dirPath = libraryFile.getParentFile();
                if (!dirPath.exists() && !dirPath.mkdirs()) {
                    throw new IllegalStateException("unable to create directories for native library");
                }
                try (InputStream in = WkHtmlToPdfLoader.class.getResourceAsStream(getLibraryResource())) {
                    Files.copy(in, libraryFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        WkHtmlToPdf instance = (WkHtmlToPdf)Native.loadLibrary(libraryFile.getAbsolutePath(), WkHtmlToPdf.class);
        instance.wkhtmltopdf_init(0);

        return instance;
    }

    private static String getLibraryResource() {
        if (!Platform.is64Bit()) {
            throw new RuntimeException("32 bit platforms are no longer supported");
        }

        String libPath = "/wkhtmltox/0.12.6/";

        libPath += Platform.isWindows() ? "" : "lib";
        libPath += "wkhtmltox";

        if (Platform.isWindows()) {
            libPath += ".dll";
        }
        else if (Platform.isMac()) {
            if (Platform.isARM()) {
                throw new RuntimeException("ARM Macs are not yet supported. See: https://github.com/wkhtmltopdf/wkhtmltopdf/issues/5219");
            }
            libPath += ".dylib";
        }
        else { // Linux
            libPath += Platform.isARM() ? ".arm64" : ".amd64";
            libPath += ".so";
        }

        return libPath;
    }
}
