package org.blueshard.theosUI.utils;

public class TheosFile {

    private final String filename;
    private final String path;
    private final SizeUnit size;
    private final String dateOfUpload;

    public TheosFile(String filename, String path, SizeUnit size, String dateOfUpload) {
        this.filename = filename;
        this.path = path;
        this.size = size;
        this.dateOfUpload = dateOfUpload;
    }

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public SizeUnit getSize() {
        return size;
    }

    public String getDateOfUpload() {
        return dateOfUpload;
    }

}
