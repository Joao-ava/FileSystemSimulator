package exceptions;

public class NotDirectory extends RuntimeException {
    public NotDirectory(String path) {
        super("O arquivo " + path + " não é um diretório");
    }
}
