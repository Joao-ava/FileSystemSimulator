import exceptions.PathNotFound;

import java.io.*;

public class FileSystemSimulator {
    private Directory root;
    private static final String datPath = "file-system.dat";

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

    public void createDirectory(Directory directory, String name) throws IOException {
        String basePath = directory.getParent() == null ? "" : directory.getPath();
        String path = basePath + "/" + name;
        directory.addFile(new Directory(directory, path));
        save();
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

    public void save() throws IOException {
        FileOutputStream file = new FileOutputStream(datPath);
        ObjectOutputStream output = new ObjectOutputStream(file);
        output.writeObject(this.root);
        output.flush();
        output.close();
    }

    public static FileSystemSimulator fromDisk() {
        try {
            FileInputStream file = new FileInputStream(datPath);
            ObjectInputStream input = new ObjectInputStream(file);
            Directory directory = (Directory) input.readObject();
            return new FileSystemSimulator(directory);
        } catch (IOException | ClassNotFoundException e) {
            return new FileSystemSimulator();
        }
    }
}
