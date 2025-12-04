import java.util.Arrays;
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
            if (line.isEmpty()) {
                continue;
            }
            try {
                runCommand(line);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void runCommand(String line) throws Exception {
        String[] arguments = line.split(" ");
        String command = arguments[0];
        switch (command) {
            case "help":
                printHelp();
                break;
            case "pwd":
                System.out.println(currentDirectory.getPath());
                break;
            case "journal":
                simulator.getJournal().printLog();
                break;
            case "clear":
                int lines = 50;
                for (int i = 0; i < lines; i++) {
                    System.out.println();
                }
                break;
            // Listar arquivos de um diretório
            case "ls":
                if (arguments.length < 2) {
                    System.out.println(currentDirectory);
                    break;
                }
                Directory directory = simulator.getDirectory(currentDirectory, arguments[1]);
                System.out.println(directory);
                break;
            // Criar diretórios
            case "mkdir":
                simulator.runOperation(FileOperation.CREATE_DIR, currentDirectory, slice(arguments));
                break;
            case "cd":
                currentDirectory = simulator.getDirectory(currentDirectory, arguments[1]);
                break;
            case "touch":
                simulator.runOperation(FileOperation.CREATE_FILE, currentDirectory, slice(arguments));
                break;
            case "cp":
                simulator.runOperation(FileOperation.COPY_FILE, currentDirectory, slice(arguments));
                break;
            case "rm":
                simulator.runOperation(FileOperation.DELETE_FILE, currentDirectory, slice(arguments));
                break;
            case "mv":
                simulator.runOperation(FileOperation.RENAME_FILE, currentDirectory, slice(arguments));
                break;
            case "rm-dir":
                simulator.runOperation(FileOperation.RENAME_DIR, currentDirectory, slice(arguments));
                break;
            case "mv-dir":
                simulator.runOperation(FileOperation.CREATE_DIR, currentDirectory, slice(arguments));
                break;
            default:
                System.out.println("Command invalido, digite help para ver comandos validos");
                break;
        }
    }

    private void printHelp() {
        System.out.println("Comandos Disponíveis");
        System.out.println("Navegação:");
        System.out.println("\tls [diretório]          - Listar arquivos do diretório atual ou especificado");
        System.out.println("\tcd <diretório>          - Mudar para o diretório especificado");
        System.out.println("\tpwd                     - Exibir o caminho do diretório atual");
        System.out.println();
        System.out.println("Manipulação de Arquivos:");
        System.out.println("\ttouch <arquivo>         - Criar um novo arquivo");
        System.out.println("\tcp <origem> <destino>   - Copiar arquivo");
        System.out.println("\trm <arquivo>            - Remover arquivo");
        System.out.println("\tmv <antigo> <novo>      - Renomear arquivo");
        System.out.println();
        System.out.println("Manipulação de Diretórios:");
        System.out.println("\tmkdir <diretório>       - Criar um novo diretório");
        System.out.println("\trmdir <diretório>       - Remover diretório");
        System.out.println("\tmvdir <antigo> <novo>   - Renomear diretório");
        System.out.println();
        System.out.println("Sistema:");
        System.out.println("\thelp                    - Exibir esta mensagem de ajuda");
        System.out.println("\tjournal                 - Exibir journal");
        System.out.println("\tclear                   - Limpar a tela");
        System.out.println("\texit                    - Sair do simulador");
        System.out.println();
    }

    private String[] slice(String[] arguments) {
        int start = 1;
        if (start > arguments.length) {
            return new String[0];
        }
        int loop = arguments.length;
        int size = arguments.length - start;
        String[] result = new String[size];
        for (int i = 0; i < loop; i++) {
            if (i < start) continue;
            result[i - start] = arguments[i];
        }
        return result;
    }
}
