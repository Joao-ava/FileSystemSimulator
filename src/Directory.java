import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import exceptions.FileAlreadyExist;

public class Directory implements Serializable {
    protected String path;
    protected Directory parent;
    private List<Directory> children;
    protected Instant created;
    protected Instant modified;

    public Directory() {}

    public Directory(Directory parent, String path) {
        this.parent = parent;
        this.path = path;
        children = new ArrayList<>();
        created = Instant.now();
        modified = Instant.now();
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

    public void rename(String newName) {
        String[] parts = this.path.split("/");
        path = parts[parts.length - 2] +  "/" + newName;
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

    public Instant getCreated() {
        return created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    public FileType getType() {
        return FileType.DIRECTORY;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("Tipo\tNome\tCriado em\tAtualizado em\n");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.ofHours(-3));
        for (Directory file : children) {
            if (!first) {
                sb.append("\n");
            }
            sb.append(file.getType().toString().substring(0, 4))
                .append("\t")
                .append(file.getName())
                .append("\t")
                .append(formatter.format(file.getCreated()))
                .append("\t")
                .append(formatter.format(file.getModified()));
            first = false;
        }
        return sb.toString();
    }
}
