package org.scigap.iucig.filemanager.util;

public class Item {

    private String name;
    private String date;
    private String owner;
    private String group;
    private String permission;
    private boolean isFile;
    private String size;

    public Item(String name, String date, String owner,boolean isFile) {
        this.name = name;
        this.date = date;
        this.owner = owner;
        this.isFile= isFile;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

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

    public boolean isFile() {
        return isFile;
    }

    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", owner='" + owner + '\'' +
                ", group='" + group + '\'' +
                ", permission='" + permission + '\'' +
                ", isFile=" + isFile +
                ", size='" + size + '\'' +
                '}';
    }
}
