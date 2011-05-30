package org.openmeetings.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StoredFile {
    private static final Set<String> convertExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "ppt", "odp", "odt", "sxw", "wpd",
                    "doc", "rtf", "txt", "ods", "sxc", "xls", "sxi", "pptx",
                    "docx", "xlsx" }));

    private static final Set<String> pdfExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "pdf", "ps" }));

    private static final Set<String> imageExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "png", "gif", "svg", "dpx", "exr",
                    "pcd", // PhotoCD
                    "pcds", // PhotoCD
                    "psd", // Adobe Photoshop
                    "tiff", // Tagged Image File Format
                    "ttf", // TrueType font
                    "xcf", // GIMP image
                    "wpg", // Word Perfect Graphics
                    "txt", // Raw text file
                    "bmp", "ico", // Microsoft Icon
                    "tga", // Truevision Targa
                    "jpg", "jpeg" }));

    private static final Set<String> chartExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "xchart" }));

    private static final Set<String> videoExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "avi", "mov", "flv", "mp4" }));

    private static final Set<String> asIsExtensions = new HashSet<String>(
            Arrays.asList(new String[] { "jpg", "xchart" }));

    private final String name;
    private final String ext;

    public StoredFile(String name, String ext) {
        this.name = name;
        this.ext = ext;
    }

    public boolean isConvertable() {
        return convertExtensions.contains(ext);
    }

    public boolean isPresentation() {
        return isConvertable() || isPdf();
    }

    public boolean isPdf() {
        return pdfExtensions.contains(ext);
    }

    public boolean isImage() {
        return imageExtensions.contains(ext);
    }

    public boolean isVideo() {
        return videoExtensions.contains(ext);
    }

    public boolean isChart() {
        return chartExtensions.contains(ext);
    }

    public boolean isAsIs() {
        return asIsExtensions.contains(ext);
    }
}
