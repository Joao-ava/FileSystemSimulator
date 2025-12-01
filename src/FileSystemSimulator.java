import exceptions.NotDirectory;
import exceptions.PathNotFound;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

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
        Directory currentDirectory = directory;
        ArrayList<String> parts = new ArrayList<>(Arrays.stream(name.split("/")).toList());
        String fileName = parts.removeFirst();
        while (!parts.isEmpty()) {
            Directory newDirectory = currentDirectory.getChild(fileName);
            if (newDirectory == null) {
                createDirectory(currentDirectory, fileName);
                newDirectory = currentDirectory.getChild(fileName);
            }
            currentDirectory = newDirectory;
            fileName = parts.removeFirst();
        }

        String basePath = currentDirectory.getParent() == null ? "" : currentDirectory.getPath();
        String path = basePath + "/" + fileName;
        Directory newDirectory = new Directory(currentDirectory, path);
        currentDirectory.addFile(newDirectory);
        save();
    }

    public void createFile(Directory directory, String name) throws IOException {
        Directory currentDirectory = directory;
        ArrayList<String> parts = new ArrayList<>(Arrays.stream(name.split("/")).toList());
        String fileName = parts.removeFirst();
        while (!parts.isEmpty()) {
            Directory newDirectory = currentDirectory.getChild(fileName);
            if (newDirectory == null) {
                createDirectory(currentDirectory, fileName);
                newDirectory = currentDirectory.getChild(fileName);
            }
            currentDirectory = newDirectory;
            fileName = parts.removeFirst();
        }

        String basePath = currentDirectory.getParent() == null ? "" : currentDirectory.getPath();
        String path = basePath + "/" + name;
        currentDirectory.addFile(new File(currentDirectory, path, ""));
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
            if (current.getType().equals(FileType.FILE)) throw new NotDirectory(path);
        }
        return current;
    }

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
