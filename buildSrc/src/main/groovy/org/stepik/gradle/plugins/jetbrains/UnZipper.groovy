package org.stepik.gradle.plugins.jetbrains

import org.gradle.tooling.BuildException
import org.jetbrains.annotations.NotNull

import java.util.zip.ZipFile

/**
 * @author meanmail
 */
class UnZipper {
    static void unZip(@NotNull File pluginZip, @NotNull File targetDirectory) {
        final String ERROR_MESSAGE = "Cannot unzip: " + pluginZip.absolutePath

        def zipFile
        try {
            zipFile = new ZipFile(pluginZip)
            def entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                def entry = entries.nextElement()
                def path = entry.getName()
                if (!path) {
                    continue
                }

                def dest = new File(targetDirectory, path)

                if (entry.isDirectory()) {
                    if (!dest.exists() && !dest.mkdirs()) {
                        throw new BuildException(ERROR_MESSAGE, null)
                    }
                } else {
                    if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
                        throw new BuildException(ERROR_MESSAGE, null)
                    }

                    OutputStream output
                    try {
                        output = new BufferedOutputStream(new FileOutputStream(dest))
                        def input = zipFile.getInputStream(entry)
                        def buffer = new byte[1024]
                        int len

                        while ((len = input.read(buffer)) >= 0) {
                            output.write(buffer, 0, len)
                        }
                    } finally {
                        if (output) {
                            output.close()
                        }
                    }
                }
            }
        } catch (IOException ignored) {
            throw new BuildException(ERROR_MESSAGE, null)
        } finally {
            if (zipFile) {
                zipFile.close()
            }
        }
    }
}
