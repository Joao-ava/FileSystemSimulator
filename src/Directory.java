import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import exceptions.FileAlreadyExist;

public class Directory implements Serializable {
    protected String path;
    protected Directory parent;
    private List<Directory> children;

    public Directory() {}

    public Directory(Directory parent, String path) {
        this.parent = parent;
        this.path = path;
        children = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        if (parent == null) {
            return path;
        }
        String[] parts = this.path.split("/");
        return parts[parts.length - 1];
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public List<Directory> getChildren() {
        return children;
    }

    public Directory getChild(String name) {
        for (Directory child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

    public void setChildren(List<Directory> children) {
        this.children = children;
    }

    public void addFile(Directory file) {
        if (getChild(file.getName()) != null) throw new FileAlreadyExist();
        this.children.add(file);
    }

    public void removeFile(Directory file) {
        this.children.remove(file);
    }

    public FileType getType() {
        return FileType.DIRECTORY;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Directory file : children) {
            if (!first) {
                sb.append("\n");
            }
            sb.append(file.getType()).append("\t").append(file.getName());
            first = false;
        }
        return sb.toString();
    }
}
