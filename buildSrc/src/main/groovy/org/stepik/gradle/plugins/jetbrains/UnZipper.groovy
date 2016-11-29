package org.stepik.gradle.plugins.jetbrains

import com.sun.istack.internal.NotNull
import org.gradle.tooling.BuildException

import java.util.zip.ZipFile

/**
 * @author meanmail
 */
class UnZipper {
    static void unZip(@NotNull File pluginZip, @NotNull File targetDirectory) {
        def zipFile = new ZipFile(pluginZip)
        try {
            def entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                def entry = entries.nextElement()
                def path = entry.name
                if (!path) {
                    continue
                }
                File dest = new File(targetDirectory, path)
                final ERROR_MESSAGE = "Cannot unzip: $pluginZip.absolutePath"
                if (entry.isDirectory()) {
                    if (!dest.exists() && !dest.mkdirs()) {
                        throw new BuildException(ERROR_MESSAGE)
                    }
                } else {
                    if (!dest.getParentFile().exists() && !dest.getParentFile().mkdirs()) {
                        throw new BuildException(ERROR_MESSAGE)
                    }
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(dest))
                    try {
                        copyInputStream(zipFile.getInputStream(entry), outputStream)
                    } finally {
                        outputStream.close()
                    }
                }
            }
        } finally {
            zipFile.close()
        }
    }

    private static void copyInputStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024]
        int len
        while ((len = input.read(buffer)) >= 0) {
            output.write(buffer, 0, len)
        }
        input.close()
        output.close()
    }
}