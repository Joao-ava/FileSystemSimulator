package exceptions;

public class FileAlreadyExist extends RuntimeException {
    public FileAlreadyExist() {
        super("O arquivo já existe");
    }
}
