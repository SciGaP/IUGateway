package org.scigap.iucig.filemanager.util;

import java.util.*;

/**
 * Created by swithana on 3/24/14.
 */
public class StringUtils {
    private List<String> pathList;
    private List<String> commandList;
    private Stack<String> pathStack;

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
            path += "/" +item;
        }
        return path;
    }

    public List<String> deconstructCommand(String command) {
        commandList = new ArrayList<String>();

        StringTokenizer tokenizer = new StringTokenizer(command);

        while (tokenizer.hasMoreTokens()) {
            commandList.add(tokenizer.nextToken());
        }
        return commandList;
    }

    public Stack<String> getPathStack(String path) {
        pathStack = new Stack<String>();

        StringTokenizer tokenizer = new StringTokenizer(path, "/");

        while (tokenizer.hasMoreTokens()) {
            pathStack.push(tokenizer.nextToken());
        }
        return pathStack;
    }
    public String constructPathFromStack(Stack<String> pathAsAStack) {
        String path = "";
        for (String item : pathAsAStack) {
            path += "/" +item;
        }
        return path;
    }

    public Map<String, String> categorizeResult(List<String> resultList) {
        Map<String, String> resultMap = new HashMap<String, String>();
        List<String> temp =null;
        String fileOrFolder = null;

        StringTokenizer tokenizer = new StringTokenizer(resultList.toString(),"\n");

        List<String> filteredResults = new ArrayList<String>();

        while (tokenizer.hasMoreTokens()) {
            filteredResults.add(tokenizer.nextToken());
        }


        for (String line : filteredResults) {
            temp = new ArrayList<String>();
            tokenizer = new StringTokenizer(line," ");
            while (tokenizer.hasMoreTokens()) {
                temp.add(tokenizer.nextToken());
            }
            if (temp.size() > 2) {
                fileOrFolder = (temp.get(0).charAt(0) == 'd') ? "dir" : "file";
                resultMap.put(temp.get(temp.size() - 1),fileOrFolder);
            }
        }
        return resultMap;
    }
}
