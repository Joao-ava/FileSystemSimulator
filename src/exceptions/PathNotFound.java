package exceptions;

public class PathNotFound extends RuntimeException {
    public PathNotFound(String path) {
        super("Caminho não encontrado: " + path);
    }
}
