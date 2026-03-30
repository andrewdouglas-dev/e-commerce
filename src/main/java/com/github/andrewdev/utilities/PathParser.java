package com.github.andrewdev.utilities;

public class PathParser {
    private PathParser(){}

    public static boolean isSpecificResourceRequested(String path) {
        path = path.substring(1);
        String[] pathSegments = path.split("/");

        return pathSegments.length > 3;
    }

    public static long extractResourceId(String path) {
        path = path.substring(1);
        String[] pathSegments = path.split("/");

        if (pathSegments.length < 4) {
            throw new IllegalArgumentException("Provided path does not contain a resource ID");
        }

        return Long.parseLong(pathSegments[3]);
    } 
}
