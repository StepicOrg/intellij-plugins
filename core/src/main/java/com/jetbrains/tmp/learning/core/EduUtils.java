package com.jetbrains.tmp.learning.core;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;

public class EduUtils {
    private EduUtils() {
    }

    /**
     * Gets id in directory names like "step1", "lesson2"
     *
     * @param dirName full name of directory
     * @param prefix  part of name without index
     * @return id of object
     */
    public static int parseDirName(@NotNull final String dirName, @NotNull final String prefix) {
        if (!dirName.startsWith(prefix)) {
            return -1;
        }
        try {
            return Integer.parseInt(dirName.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static boolean isImage(@NotNull String fileName) {
        final String[] readerFormatNames = ImageIO.getReaderFormatNames();
        for (@NonNls String format : readerFormatNames) {
            final String ext = format.toLowerCase();
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}
