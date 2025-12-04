import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class JournalItem implements Serializable {
    private final Instant timestamp;
    private final FileOperation operation;
    private final String path;
    private final String[] arguments;
    private final boolean success;

    public JournalItem(FileOperation operation, String path, boolean success) {
        this(operation, path, new String[0], success);
    }

    public JournalItem(FileOperation operation, String path, String[] arguments, boolean success) {
        this.timestamp = Instant.now();
        this.operation = operation;
        this.path = path;
        this.arguments = arguments;
        this.success = success;
    }

    public String toString() {
        String status = success ? "SUCCESS" : "FAILED";
        String detailsStr = arguments.length == 0 ? "" : String.join(", ", arguments);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneOffset.ofHours(-3));
        return String.format("[%s] %s | %s | %s%s", formatter.format(timestamp), status, operation, path, detailsStr);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public FileOperation getOperation() {
        return operation;
    }

    public String getPath() {
        return path;
    }

    public String[] getArguments() {
        return arguments;
    }

    public boolean isSuccess() {
        return success;
    }
}
