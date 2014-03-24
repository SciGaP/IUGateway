package org.scigap.iucig.filemanager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by swithana on 3/24/14.
 */
public class StringUtils {
    private List<String> pathList;

    public List<String> deconstructPath(String path) {
        pathList = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(path, "/");

        while (tokenizer.hasMoreTokens()) {
            pathList.add(tokenizer.nextToken());
        }

        return pathList;
    }

    public String constructPathString(List<String> pathAsAList) {
        String path = "";
        for (String item : pathAsAList) {
            path += "/" + item;
        }
        return path;
    }

}
