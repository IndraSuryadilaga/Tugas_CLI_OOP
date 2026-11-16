import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

interface Page {
    String getTitle();
    void render();
    Page handleInput(String input);
}

abstract class AbstractPage implements Page {
    private final String title;

    public AbstractPage(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    protected void renderHeader() {
        System.out.println("\n========================================");
        System.out.println("  PAGE: " + title.toUpperCase());
        System.out.println("========================================");
    }

    protected void renderFooter() {
        System.out.println("----------------------------------------");
    }

    protected void printError(String message) {
        System.out.println("[ERROR] " + message);
    }
}

class MainMenuPage extends AbstractPage {
    private final Map<String, Page> subPages = new HashMap<>();

    public MainMenuPage() {
        super("Menu Utama");
    }

    public void registerPage(String optionKey, Page page) {
        subPages.put(optionKey, page);
    }

    @Override
    public void render() {
        renderHeader();
        System.out.println("Pilih menu yang ingin Anda akses:");
        for (Map.Entry<String, Page> entry : subPages.entrySet()) {
            System.out.println(" [" + entry.getKey() + "] " + entry.getValue().getTitle());
        }
        System.out.println(" [X] Keluar dari Aplikasi");
        renderFooter();
        System.out.print("Masukkan pilihan Anda: ");
    }

    @Override
    public Page handleInput(String input) {
        String choice = input.trim().toUpperCase();
        if (choice.equals("X")) {
            System.out.println("\nTerima kasih telah menggunakan aplikasi!");
            return null;
        }

        if (subPages.containsKey(choice)) {
            return subPages.get(choice);
        }

        printError("Pilihan tidak valid! Silakan coba lagi.");
        return this;
    }
}

class InputEchoPage extends AbstractPage {
    private final Page parentPage;
    private String lastStoredInput = "(Belum ada input yang dimasukkan)";

    public InputEchoPage(Page parentPage) {
        super("Scan & Tampilkan Input User");
        this.parentPage = parentPage;
    }

    @Override
    public void render() {
        renderHeader();
        System.out.println("Input Terakhir Anda : " + lastStoredInput);
        System.out.println("\nKetik teks baru untuk di-scan dan ditampilkan,");
        System.out.println("atau ketik 'KEMBALI' untuk ke menu utama.");
        renderFooter();
        System.out.print("Ketik Input: ");
    }

    @Override
    public Page handleInput(String input) {
        String trimmed = input.trim();
        if (trimmed.equalsIgnoreCase("KEMBALI")) {
            return parentPage;
        }

        if (trimmed.isEmpty()) {
            printError("Input tidak boleh kosong!");
            return this;
        }

        this.lastStoredInput = trimmed;
        System.out.println("\n>>> [SUKSES] Berhasil scan input: \"" + this.lastStoredInput + "\"");
        return this;
    }
}

class AboutPage extends AbstractPage {
    private final Page parentPage;

    public AboutPage(Page parentPage) {
        super("Tentang Aplikasi");
        this.parentPage = parentPage;
    }

    @Override
    public void render() {
        renderHeader();
        System.out.println("Sistem Template CLI Modular Berbasis Java Base.");
        System.out.println("Menerapkan: Abstract Class, Interface, & Composition.");
        renderFooter();
        System.out.print("Ketik sembarang tombol lalu ENTER untuk kembali: ");
    }

    @Override
    public Page handleInput(String input) {
        return parentPage;
    }
}

class InputScanner {
    private final Scanner scanner;

    public InputScanner() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public void close() {
        scanner.close();
    }
}

class CLIApplication {
    private final InputScanner inputScanner;
    private Page currentPage;

    public CLIApplication() {
        this.inputScanner = new InputScanner();
        this.setupPages();
    }

    private void setupPages() {
        MainMenuPage mainMenu = new MainMenuPage();
        InputEchoPage echoPage = new InputEchoPage(mainMenu);
        AboutPage aboutPage = new AboutPage(mainMenu);

        mainMenu.registerPage("1", echoPage);
        mainMenu.registerPage("2", aboutPage);

        this.currentPage = mainMenu;
    }

    public void run() {
        while (currentPage != null) {
            currentPage.render();
            String userInput = inputScanner.readLine();
            currentPage = currentPage.handleInput(userInput);
        }
        inputScanner.close();
    }
}

public class Main {
    public static void main(String[] args) {
        CLIApplication app = new CLIApplication();
        app.run();
    }
}
