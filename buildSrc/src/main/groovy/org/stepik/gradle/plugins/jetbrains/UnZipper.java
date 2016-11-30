package org.stepik.gradle.plugins.jetbrains;

import org.gradle.tooling.BuildException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author meanmail
 */
class UnZipper {
    static void unZip(@NotNull File pluginZip, @NotNull File targetDirectory) {
        final String ERROR_MESSAGE = "Cannot unzip: " + pluginZip.getAbsolutePath();

        try (ZipFile zipFile = new ZipFile(pluginZip)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String path = entry.getName();
                if (path == null) {
                    continue;
                }

                File dest = new File(targetDirectory, path);

                if (entry.isDirectory()) {
                    if (!dest.exists() && !dest.mkdirs()) {
                        throw new BuildException(ERROR_MESSAGE, null);
                    }
                } else {
                    if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
                        throw new BuildException(ERROR_MESSAGE, null);
                    }

                    try (OutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
                        InputStream input = zipFile.getInputStream(entry);
                        byte[] buffer = new byte[1024];
                        int len;

                        while ((len = input.read(buffer)) >= 0) {
                            output.write(buffer, 0, len);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new BuildException(ERROR_MESSAGE, null);
        }
    }
}
