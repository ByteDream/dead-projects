package org.blueshard.theosUI.utils;

import org.blueshard.theosUI.Main;

import java.io.InputStream;
import java.net.URL;

public class TheosUIImage {

    private URL imageURL;
    private InputStream imageStream;

    public TheosUIImage(ImageName imageName) {
        imageURL = Main.class.getResource("resources/images/" + imageName.name() + ".svg");
        imageStream = Main.class.getResourceAsStream("resources/images/" + imageName.name() + ".svg");
    }

    public URL asURL() {
        return imageURL;
    }

    public InputStream asStream() {
        return imageStream;
    }

    public enum ImageName {
        option_dots,
        download,
        email,
        green_dot,
        lock,
        password_hide,
        password_show,
        ping_0,
        ping_1,
        ping_2,
        ping_3,
        question_mark,
        red_dot,
        theos_logo,
        user
    }

}
