import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String SCENARIOS_FOLDER = "src\\TextAdventure\\scenarios";
    private static final Scanner scanner;

    public Main() {
    }

    public static void main(String[] args) {
        while (true) {
            List<String> scenarioList = getScenariosList("src\\TextAdventure\\scenarios");
            System.out.println("Доступные сценарии:");

            for (int i = 0; i < scenarioList.size(); ++i) {
                System.out.println(i + 1 + ". " + (String) scenarioList.get(i));
            }

            System.out.println("0. Выход");
            System.out.println("-1. Удалить сценарий");
            System.out.println("N. Создать новый сценарий");
            System.out.print("Выберите сценарий (или N для создания нового, 0 для выхода): ");
            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                System.out.println("Выход из программы.");
                scanner.close();
                System.exit(0);
            } else if (choice.equals("-1")) {
                deleteScenario();
            } else if (choice.equalsIgnoreCase("N")) {
                addCustomScenario();
            } else if (isNumeric(choice)) {
                int scenarioNumber = Integer.parseInt(choice);
                if (scenarioNumber > 0 && scenarioNumber <= scenarioList.size()) {
                    String selectedScenario = (String) scenarioList.get(scenarioNumber - 1);
                    runScenario("src\\TextAdventure\\scenarios" + File.separator + selectedScenario);
                } else {
                    System.out.println("Некорректный выбор. Попробуйте еще раз.");
                }
            } else {
                System.out.println("Некорректный выбор. Попробуйте еще раз.");
            }
        }
    }

    private static List<String> getScenariosList(String folderPath) {
        File folder = new File(folderPath);
        String[] scenarioArray = folder.list((dir, name) -> {
            return (new File(dir, name)).isDirectory();
        });
        return List.of(scenarioArray != null ? scenarioArray : new String[0]);
    }

    private static void runScenario(String scenarioPath) {
        try {
            File scenarioFolder = new File(scenarioPath);
            File mainScenarioFile = new File(scenarioFolder, "main.txt");
            if (mainScenarioFile.exists()) {
                List<String> mainScenarioLines = Files.readAllLines(mainScenarioFile.toPath());
                Iterator var4 = mainScenarioLines.iterator();

                while (var4.hasNext()) {
                    String line = (String) var4.next();
                    System.out.println(line);
                }

                handleUserChoice(scanner.nextLine(), scenarioFolder);
            } else {
                System.out.println("Основной файл сценария не найден.");
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    private static void handleUserChoice(String userChoice, File scenarioFolder) {
        try {
            int choiceIndex = Integer.parseInt(userChoice);
            File[] choiceFolders = scenarioFolder.listFiles(File::isDirectory);
            if (choiceFolders != null && choiceIndex > 0 && choiceIndex <= choiceFolders.length) {
                File choiceFolder = choiceFolders[choiceIndex - 1];
                showScenarioContent(choiceFolder);
                handleUserChoice(scanner.nextLine(), choiceFolder);
            } else if (choiceIndex == 0) {
                System.out.println("Вы завершили сценарий.");
            } else {
                System.out.println("Некорректный выбор. Попробуйте еще раз.");
            }
        } catch (NumberFormatException var5) {
            System.out.println("Некорректный выбор. Попробуйте еще раз.");
        }

    }

    private static void addCustomScenario() {
        System.out.print("Введите название нового сценария: ");
        String newScenarioName = scanner.nextLine();
        File newScenarioFolder = new File("src\\TextAdventure\\scenarios" + File.separator + newScenarioName);
        if (newScenarioFolder.exists()) {
            System.out.println("Сценарий с таким названием уже существует.");
        } else {
            if (newScenarioFolder.mkdirs()) {
                System.out.println("Сценарий успешно создан.");
                System.out.print("Хотите добавить варианты продолжения для основной истории? (да/нет): ");
                String addMainChoices = scanner.nextLine();
                if (addMainChoices.equalsIgnoreCase("да")) {
                    File mainScenarioFile = new File(newScenarioFolder, "main.txt");
                    System.out.println("Введите описание основной истории (введите 'выход' для завершения):");

                    try {
                        FileWriter writer = new FileWriter(mainScenarioFile);

                        while (true) {
                            String line;
                            if ((line = scanner.nextLine()).equalsIgnoreCase("выход")) {
                                writer.close();
                                System.out.println("Основной файл сценария успешно создан.");
                                break;
                            }

                            writer.write(line + System.lineSeparator());
                        }
                    } catch (IOException var6) {
                        var6.printStackTrace();
                        System.out.println("Ошибка при создании основного файла сценария.");
                        return;
                    }

                    addChoices(scanner, newScenarioFolder, "main", "choice");
                } else {
                    System.out.println("Основная история не будет содержать вариантов продолжения.");
                }

                System.out.println("Сценарий успешно создан.");
            } else {
                System.out.println("Ошибка при создании папки для нового сценария.");
            }

        }
    }

    private static void addChoices(Scanner scanner, File scenarioFolder, String storyType, String prefix) {
        boolean addChoices = true;

        for (int choiceNumber = 1; addChoices; ++choiceNumber) {
            System.out.print("Введите вариант выбора " + choiceNumber + " для " + storyType + " (введите 'выход' для завершения): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("выход")) {
                addChoices = false;
                break;
            }

            File choiceFolder = new File(scenarioFolder, prefix + choiceNumber);
            if (choiceFolder.mkdirs()) {
                System.out.println("Папка для варианта " + prefix + choiceNumber + " успешно создана.");
                File choiceFile = new File(choiceFolder, "choice.txt");

                try {
                    FileWriter writer = new FileWriter(choiceFile);
                    writer.write(choice + System.lineSeparator());
                    System.out.print("Введите продолжение истории для варианта " + prefix + choiceNumber + " (введите 'выход' для завершения): ");

                    String line;
                    while (!(line = scanner.nextLine()).equalsIgnoreCase("выход")) {
                        writer.write(line + System.lineSeparator());
                    }

                    writer.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                    System.out.println("Ошибка при создании файла для варианта " + prefix + choiceNumber + ".");
                }

                addChoices(scanner, choiceFolder, prefix + choiceNumber, prefix + choiceNumber + "_");
            } else {
                System.out.println("Ошибка при создании папки для варианта " + prefix + choiceNumber + ".");
            }
        }

    }

    private static void showScenarioContent(File scenarioFolder) {
        File[] scenarioFiles = scenarioFolder.listFiles((dir, name) -> {
            return name.endsWith(".txt");
        });
        if (scenarioFiles != null && scenarioFiles.length > 0) {
            try {
                List<String> lines = Files.readAllLines(scenarioFiles[0].toPath());
                Iterator var3 = lines.iterator();

                while (var3.hasNext()) {
                    String line = (String) var3.next();
                    System.out.println(line);
                }
            } catch (IOException var5) {
                System.out.println("Ошибка чтения сценария.");
            }
        } else {
            System.out.println("Продолжение истории не найдено.");
        }

    }

    private static void deleteScenario() {
        System.out.print("Введите номер сценария для удаления: ");
        String scenarioNumber = scanner.nextLine();
        if (isNumeric(scenarioNumber)) {
            int number = Integer.parseInt(scenarioNumber);
            List<String> scenarioList = getScenariosList("src\\TextAdventure\\scenarios");
            if (number > 0 && number <= scenarioList.size()) {
                String scenarioToDelete = (String) scenarioList.get(number - 1);
                File scenarioFolderToDelete = new File("src\\TextAdventure\\scenarios" + File.separator + scenarioToDelete);
                if (scenarioFolderToDelete.exists()) {
                    deleteDirectory(scenarioFolderToDelete);
                    System.out.println("Сценарий успешно удален.");
                } else {
                    System.out.println("Сценарий не найден.");
                }
            } else {
                System.out.println("Некорректный номер сценария.");
            }
        } else {
            System.out.println("Некорректный ввод. Введите число.");
        }

    }

    private static void deleteDirectory(File directory) {
        File[] contents = directory.listFiles();
        if (contents != null) {
            File[] var2 = contents;
            int var3 = contents.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                File file = var2[var4];
                deleteDirectory(file);
            }
        }

        directory.delete();
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException var2) {
            return false;
        }
    }

    static {
        scanner = new Scanner(System.in);
    }
}