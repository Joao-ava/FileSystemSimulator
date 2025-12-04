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

    public void renameDirectory(Directory directory, String oldName, String newName) throws IOException {
        Directory target = getDirectory(directory, oldName);
        target.rename(newName);
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

    public void runOperation(FileOperation operation, Directory directory, String[] arguments) throws Exception {
        switch (operation) {
            case CREATE_DIR:
                if (arguments.length < 1) {
                    throw new Exception("Uso: mkdir <nome-diretório>");
                }
                createDirectory(directory, arguments[0]);
                break;
            case CREATE_FILE:
                if (arguments.length < 2) {
                    throw new Exception("Uso: touch <nome-arquivo>");
                }
                createFile(directory, arguments[0]);
            case COPY_FILE:
                if (arguments.length < 2) {
                    throw new Exception("Uso: cp <nome-antigo> <nome-novo>");
                }
                // TODO: Copiar arquivos
                break;
            case DELETE_DIR:
                if (arguments.length < 1) {
                    throw new Exception("Uso: rm-dir <nome-antigo>");
                }
                // TODO: Apagar diretórios
                break;
            case DELETE_FILE:
                if (arguments.length < 1) {
                    throw new Exception("Uso: rm <nome-antigo>");
                }
                // TODO: Apagar arquivos
                break;
            case RENAME_DIR:
                if (arguments.length < 2) {
                    throw new Exception("Uso: mv-dir <nome-antigo> <nome-novo>");
                }
                renameDirectory(directory, arguments[0], arguments[1]);
                break;
            case RENAME_FILE:
                if (arguments.length < 2) {
                    throw new Exception("Uso: mv <nome-antigo> <nome-novo>");
                }
                // TODO: Renomear arquivos
                break;
        }
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
