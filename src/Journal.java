import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Journal {
    private static final String JOURNAL_FILE = "file-system.journal";
    private final List<JournalItem> items;

    public Journal() {
        items = loadFromDisk();
    }

    public void logOperation(FileOperation operation, String path, boolean success) {
        JournalItem entry = new JournalItem(operation, path, success);
        items.add(entry);
        save();
    }

    public void logOperation(FileOperation operation, String path, String[] arguments, boolean success) {
        JournalItem entry = new JournalItem(operation, path, arguments, success);
        items.add(entry);
        save();
    }

    public List<JournalItem> getItems() {
        return new ArrayList<>(items);
    }

    public void printLog() {
        System.out.println("=== Journal Log ===");
        for (JournalItem entry : items) {
            System.out.println(entry);
        }
        System.out.println("===================");
    }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(JOURNAL_FILE))) {
            oos.writeObject(items);
        } catch (IOException e) {
            System.err.println("Erro ao salvar journal: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private List<JournalItem> loadFromDisk() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(JOURNAL_FILE))) {
            return (List<JournalItem>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar journal: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
