import java.io.Serializable;

public class File extends Directory implements Serializable {
    private String content;

    public File(Directory parent, String path, String content) {
        this.parent = parent;
        this.path = path;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public FileType getType() {
        return FileType.FILE;
    }

    public String toString() {
        return getName();
    }
}
