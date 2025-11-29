import java.io.IOException;
import java.util.Scanner;

public class CLI {
    private final FileSystemSimulator simulator;
    private Directory currentDirectory;

    public CLI(FileSystemSimulator simulator) {
        this.simulator = simulator;
        this.currentDirectory = simulator.getRoot();
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(this.currentDirectory.getPath() + "> ");
            String line = sc.nextLine();
            if (line.equals("exit")) {
                break;
            }
            try {
                runCommand(line);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void runCommand(String line) throws IOException {
        String[] arguments = line.split(" ");
        String command = arguments[0];
        switch (command) {
            case "help":
                System.out.println("Comandos:");
                System.out.println("\tls: para listar os arquivos do diretório atual");
                System.out.println("\tmkdir nome-diretório: para criar diretórios.");
                System.out.println("\tcd diretório: para mudar o diretório atual.");
                break;
            // Listar arquivos de um diretório
            case "ls":
                // TODO: lidar com ls com comandos
                if (arguments.length < 2) {
                    System.out.println(currentDirectory);
                    break;
                }
                Directory directory = simulator.getDirectory(currentDirectory, arguments[1]);
                System.out.println(directory);
                break;
            // Criar diretórios
            case "mkdir":
                simulator.createDirectory(currentDirectory, arguments[1]);
                break;
            case "cd":
                currentDirectory = simulator.getDirectory(currentDirectory, arguments[1]);
                break;
            // Copiar arquivos
            // Apagar arquivos
            // Renomear arquivos
            // Apagar diretórios
            // Renomear diretórios
            default:
                System.out.println("Command invalido, digite help para ver comandos validos");
                break;
        }
    }
}
