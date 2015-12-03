package org.scigap.iucig.filemanager.util;

public class Item {

    private String name;
    private String date;
//    private String owner;
    private String group;
    private String permission;
    private String fileType;
    private long size;

    public Item(String name, String date, String fileType, long size) {
        this.name = name;
        this.date = date;
        this.size = size;
        this.fileType= fileType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getOwner() {
//        return owner;
//    }
//
//    public void setOwner(String owner) {
//        this.owner = owner;
//    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
//                ", owner='" + owner + '\'' +
                ", group='" + group + '\'' +
                ", permission='" + permission + '\'' +
                ", fileType=" + fileType +
                ", size='" + size + '\'' +
                '}';
    }
}
