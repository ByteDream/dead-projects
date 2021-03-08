package org.blueshard.android.cryptogx.filedirchooser;


import android.graphics.drawable.Drawable;

import java.io.File;

public class FileDirData {

    private final File file;
    private final Drawable image;

    public FileDirData(File file, Drawable image) {
        this.file = file;
        this.image = image;
    }

    public File getFile() {
        return file;
    }

    public Drawable getImage() {
        return image;
    }

}
