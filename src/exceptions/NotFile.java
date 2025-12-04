package exceptions;

public class NotFile extends RuntimeException {
    public NotFile(String path) {
        super("O caminho " + path + " não é um arquivo");
    }
}
