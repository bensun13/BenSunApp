package com.example.bsun.bensunapp;

import java.util.List;

/**
 * Created by bsun on 11/22/16.
 */

public class SubredditWrapper {

    String kind;

    Data data;

    public class Data {
        String modhash;
        List<Children> children;
    }

    @Override
    public String toString() {

        return kind + " " + data.children.size();
    }

    public class Children {
        String kind;

        ChildrenData data;

    }

    public class PreviewData {
        List<Images> images;

    }

    public class Images {
        SourceImage source;

    }

    public class SourceImage {
        String url;
        Integer width;
        Integer height;

    }

    public class ChildrenData {
        String id;

        PreviewData preview;

        @Override
        public String toString() {

            return id;
        }

    }

}
