import exceptions.FileAlreadyExist;
import exceptions.NotDirectory;
import exceptions.NotFile;
import exceptions.PathNotFound;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;

public class FileSystemSimulator {
    private Directory root;
    private final Journal journal;
    private boolean enabledJournal;
    private static final String datPath = "file-system.dat";

    public FileSystemSimulator() {
        root = new Directory(null, "/");
        journal = new Journal();
        enabledJournal = true;
    }

    public FileSystemSimulator(Directory root) {
        this.root = root;
        journal = new Journal();
        enabledJournal = true;
    }

    public Directory getRoot() {
        return root;
    }

    public void setRoot(Directory root) {
        this.root = root;
    }

    private void logOperation(FileOperation operation, Directory directory, String[] arguments, boolean success) {
        if (!isEnabledJournal()) return;
        journal.logOperation(operation, directory.getPath(), arguments, success);
    }

    public Directory createDirectory(Directory directory, String name) throws IOException {
        try {
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
            logOperation(FileOperation.CREATE_DIR, directory, new String[]{name}, true);
            return newDirectory;
        } catch (IOException e) {
            logOperation(FileOperation.CREATE_DIR, directory, new String[]{name}, false);
            throw e;
        }
    }

    public Directory renameDirectory(Directory directory, String oldName, String newName) throws IOException {
        try {
            Directory target = getDirectory(directory, oldName);
            if (directory.getChild(newName) != null) {
                throw new RuntimeException("Já existe um diretório com o nome: " + newName);
            }
            String oldPath = target.getPath();
            String basePath = target.getParent() == root ? "" : target.getParent().getPath();
            String newPath = basePath + "/" + newName;
            updatePathRecursively(target, oldPath, newPath);
            save();
            logOperation(FileOperation.RENAME_DIR, directory, new String[]{oldName, newName}, true);
            return target;
        } catch (IOException e) {
            logOperation(FileOperation.RENAME_DIR, directory, new String[]{oldName, newName}, false);
            throw e;
        }
    }

    private void updatePathRecursively(Directory directory, String oldPath, String newPath) {
        directory.setPath(newPath);
        for (Directory child : directory.getChildren()) {
            String childOldPath = child.getPath();
            String childNewPath = childOldPath.replace(oldPath, newPath);
            if (child.getType() == FileType.DIRECTORY) {
                updatePathRecursively(child, childOldPath, childNewPath);
                continue;
            }
            child.setPath(childNewPath);
        }
    }

    public Directory createFile(Directory directory, String name) throws IOException {
        try {
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
            File file = new File(currentDirectory, path, "");
            currentDirectory.addFile(file);
            save();
            logOperation(FileOperation.CREATE_FILE, directory, new String[]{name}, true);
            return file;
        } catch (IOException e) {
            logOperation(FileOperation.CREATE_FILE, directory, new String[]{name}, false);
            throw new IOException();
        }
    }

    public Directory renameFile(Directory directory, String oldName, String newName) throws IOException {
        try {
            Directory fileToRename = directory.getChild(oldName);
            if (fileToRename == null) throw new PathNotFound(oldName);

            if (directory.getChild(newName) != null) {
                throw new RuntimeException("Já existe um arquivo com o nome: " + newName);
            }
            if (fileToRename.getType() != FileType.FILE) throw new NotFile(fileToRename.getPath());

            String basePath = directory.getParent() == null ? "" : directory.getPath();
            String newPath = basePath + "/" + newName;
            fileToRename.setPath(newPath);
            save();
            logOperation(FileOperation.RENAME_FILE, directory, new String[]{oldName, newName}, true);
            return fileToRename;
        } catch (Exception e) {
            logOperation(FileOperation.RENAME_FILE, directory, new String[]{oldName, newName}, false);
            throw e;
        }
    }

    public void deleteFile(Directory directory, String name) throws IOException {
        try {
            Directory currentDirectory = directory;
            ArrayList<String> parts = new ArrayList<>(Arrays.stream(name.split("/")).toList());
            String fileName = parts.removeFirst();
            while (!parts.isEmpty()) {
                Directory newDirectory = currentDirectory.getChild(fileName);
                if (newDirectory == null) {
                    throw new PathNotFound(fileName);
                }
                currentDirectory = newDirectory;
                fileName = parts.removeFirst();
            }

            Directory target = currentDirectory.getChild(name);
            if (target == null) {
                throw new PathNotFound(name);
            }
            if (target.getType() == FileType.DIRECTORY) {
                throw new NotFile(target.getPath());
            }

            currentDirectory.removeFile(target);
            save();
            logOperation(FileOperation.DELETE_FILE, directory, new String[]{name}, true);
        } catch (Exception e) {
            logOperation(FileOperation.DELETE_FILE, directory, new String[]{name}, false);
            throw e;
        }
    }

    public void deleteDirectory(Directory directory, String name) throws IOException {
        try {
            Directory target = getDirectory(directory, name);
            Directory parent = target.getParent();
            if (parent == null) throw new RuntimeException("Não pode apagar a raíz.");

            if (target.getType() == FileType.FILE) throw new NotDirectory(target.getPath());

            parent.removeFile(target);
            save();
            logOperation(FileOperation.DELETE_DIR, directory, new String[]{name}, true);
        } catch (Exception e) {
            logOperation(FileOperation.DELETE_DIR, directory, new String[]{name}, false);
            throw e;
        }
    }

    public Directory getFile(Directory directory, String name) throws PathNotFound {
        Directory currentDirectory = directory;
        ArrayList<String> parts = new ArrayList<>(Arrays.stream(name.split("/")).toList());
        String fileName = parts.removeFirst();
        while (!parts.isEmpty()) {
            Directory newDirectory = currentDirectory.getChild(fileName);
            if (newDirectory == null) {
                throw new PathNotFound(fileName);
            }
            currentDirectory = newDirectory;
            fileName = parts.removeFirst();
        }

        return currentDirectory.getChild(name);
    }

    public Directory copyFile(Directory directory, String originName, String targetName) throws IOException {
        try {
            Directory origin = getFile(directory, originName);
            if (origin == null) throw new PathNotFound(originName);

            if (origin.getType() == FileType.DIRECTORY)
                throw new NotFile(origin.getPath());

            if (getFile(directory, targetName) != null)
                throw new FileAlreadyExist();

            File sourceFile = (File) origin;

            setEnabledJournal(false);
            File target = (File) createFile(directory, targetName);
            setEnabledJournal(true);
            target.setContent(sourceFile.getContent());

            save();
            logOperation(FileOperation.COPY_FILE, directory, new String[]{originName, targetName}, true);
            return target;
        } catch (Exception e) {
            logOperation(FileOperation.COPY_FILE, directory, new String[]{originName, targetName}, false);
            throw e;
        }
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

    public boolean isEnabledJournal() {
        return enabledJournal;
    }

    public void setEnabledJournal(boolean enabledJournal) {
        this.enabledJournal = enabledJournal;
    }

    public Directory runOperation(FileOperation operation, Directory directory, String[] arguments) throws Exception {
        switch (operation) {
            case CREATE_DIR:
                if (arguments.length < 1) {
                    throw new Exception("Uso: mkdir <nome-diretório>");
                }
                return createDirectory(directory, arguments[0]);
            case CREATE_FILE:
                if (arguments.length < 1) {
                    throw new Exception("Uso: touch <nome-arquivo>");
                }
                return createFile(directory, arguments[0]);
            case COPY_FILE:
                if (arguments.length < 2) {
                    throw new Exception("Uso: cp <nome-antigo> <nome-novo>");
                }
                return copyFile(directory, arguments[0], arguments[1]);
            case DELETE_DIR:
                if (arguments.length < 1) {
                    throw new Exception("Uso: rmdir <nome-antigo>");
                }
                deleteDirectory(directory, arguments[0]);
                break;
            case DELETE_FILE:
                if (arguments.length < 1) {
                    throw new Exception("Uso: rm <nome-antigo>");
                }
                deleteFile(directory, arguments[0]);
                break;
            case RENAME_DIR:
                if (arguments.length < 2) {
                    throw new Exception("Uso: mvdir <nome-antigo> <nome-novo>");
                }
                return renameDirectory(directory, arguments[0], arguments[1]);
            case RENAME_FILE:
                if (arguments.length < 2) {
                    throw new Exception("Uso: mv <nome-antigo> <nome-novo>");
                }
                return renameFile(directory, arguments[0], arguments[1]);
        }
        return null;
    }

    public Journal getJournal() {
        return journal;
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
            return fromJournal();
        }
    }

    private static FileSystemSimulator fromJournal() {
        FileSystemSimulator simulator = new FileSystemSimulator();
        simulator.setEnabledJournal(false);
        System.out.println("Carregando dados do journal...");
        ArrayList<FileOperation> createOperations = new ArrayList<>();
        createOperations.add(FileOperation.CREATE_FILE);
        createOperations.add(FileOperation.CREATE_DIR);
        for (JournalItem item : simulator.getJournal().getItems()) {
            try {
                if (!item.isSuccess()) continue;
                Directory directory = simulator.getDirectory(simulator.getRoot(), item.getPath());
                Directory result = simulator.runOperation(item.getOperation(), directory, item.getArguments());
                if (result == null) continue;
                if (createOperations.contains(item.getOperation())) {
                    result.setCreated(item.getTimestamp());
                }
                result.setModified(item.getTimestamp());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Dados restaurados");
        simulator.setEnabledJournal(true);
        return simulator;
    }
}
