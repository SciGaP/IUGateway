package org.scigap.iucig.filemanager.util;

import java.util.*;

public class StringUtils {
    private List<String> pathList;
    private List<String> commandList;
    private Stack<String> pathStack;
    private final String ARG_IDENTIFIER = "*";

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

    /*deconstruct the command
    * Usage
    * 1. cd temp -> [cd, temp]
    * 2. mv tem.txt text.txt --> [mv, tem.txt, text.txt]
    *
    * */
    public List<String> deconstructCommand(String command) {
        commandList = new ArrayList<String>();

        StringTokenizer tokenizer = new StringTokenizer(command," ");

        commandList.add(tokenizer.nextToken());

        String arguments = "";
        while (tokenizer.hasMoreTokens()) {
            arguments += tokenizer.nextToken();
            if(tokenizer.hasMoreTokens())
                arguments += " ";
        }

        tokenizer = new StringTokenizer(arguments,ARG_IDENTIFIER);
        while (tokenizer.hasMoreTokens()) {
            if(tokenizer.hasMoreTokens()){
               commandList.add(tokenizer.nextToken());
            }
        }
        return commandList;
    }

    public Stack<String> getPathStack(String path) {
        pathStack = new Stack<String>();
        if (path != null){
            StringTokenizer tokenizer = new StringTokenizer(path, "/");
            while (tokenizer.hasMoreTokens()) {
                pathStack.push(tokenizer.nextToken());
            }
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
        List<String> temp = null;
        String fileOrFolder = null;

        if (resultList != null) {
            for (String line : resultList) {
                temp = new ArrayList<String>();
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                while (tokenizer.hasMoreTokens()) {
                    temp.add(tokenizer.nextToken());
                }
                if (temp.size() > 3) {
                    fileOrFolder = (temp.get(0).charAt(0) == 'd') ? "dir" : "file";
                    String fileName = temp.get(8);
                    if (temp.size() > 9) {
                        for (int k = 9; k < temp.size(); k++) {
                            fileName = fileName + " " + temp.get(k);
                        }
                    }
                    System.out.println(fileName);
                    resultMap.put(fileName, fileOrFolder);
                }
            }
        }
        return resultMap;
    }
    public List<Item> getResultsList(List<String> resultList) {
        //Result Array
        List<Item> itemList = new ArrayList<Item>();
        Item item = null;
        String name;
        String date;
        String owner;
        String group;
        String permission;
        String size;

        List<String> temp = null;
        boolean isFile = false;
        if (resultList != null) {
            for (String line : resultList){
                temp = new ArrayList<String>();
                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                while (tokenizer.hasMoreTokens()) {
                    temp.add(tokenizer.nextToken());
                }
                if (temp.size() > 8) {
                    isFile = (temp.get(0).charAt(0) != 'd');
                    owner = temp.get(2);
                    group = temp.get(3);
                    size = temp.get(4);
                    String month = temp.get(5);
                    String day = temp.get(6);
                    String time = temp.get(7);
                    name = temp.get(8);
                    if (temp.size() > 9){
                        for (int k = 9; k < temp.size(); k++){
                            name = name + " " + temp.get(k);
                        }
                    }
                    permission = temp.get(0);

                    date = month + " " + day + " " + time;
                    item = new Item(name, date, owner, isFile);
                    item.setGroup(group);
                    item.setSize(size);
                    item.setPermission(permission);
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }
}
