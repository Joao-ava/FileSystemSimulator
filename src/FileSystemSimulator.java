import exceptions.PathNotFound;

public class FileSystemSimulator {
    private Directory root;

    public FileSystemSimulator() {
        this.root = new Directory(null, "/");
    }

    public FileSystemSimulator(Directory root) {
        this.root = root;
    }

    public Directory getRoot() {
        return root;
    }

    public void setRoot(Directory root) {
        this.root = root;
    }

    public void createDirectory(Directory directory, String name) {
        String basePath = directory.getParent() == null ? "" : directory.getPath();
        String path = basePath + "/" + name;
        directory.addFile(new Directory(directory, path));
    }

    public Directory getDirectory(Directory directory, String path) {
        if (path.equals("/")) {
            return this.root;
        }
        boolean isFromRoot = path.startsWith("/");
        Directory current = isFromRoot ? root : directory;
        String pathToSplit = isFromRoot ? path.substring(1) : path;
        String[] parts = pathToSplit.split("/");

        for (String part : parts) {
            if (part.equals("..")) {
                current = current.getParent();
                continue;
            }
            current = current.getChild(part);
            if (current == null) {
                throw new PathNotFound(path);
            }
        }
        return current;
    }
    // TODO: Apagar diretórios
}
