package fctreddit.impl.server.rest.resources;

import fctreddit.api.java.Content;
import fctreddit.api.java.Image;
import fctreddit.impl.server.java.JavaContent;
import fctreddit.impl.server.java.JavaImage;
import javassist.compiler.Javac;

import java.util.logging.Logger;

public class ContentResource {

    private static Logger Log = Logger.getLogger(ContentResource.class.getName());

    private Content impl;

    public ContentResource(Content impl) {
        //hibernate = Hibernate.getInstance();
        impl = new JavaContent();
    }

}
