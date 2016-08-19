package models;

import core.AlertsImpl;
import core.Logger;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class MenuViewModel {

    private AlertsImpl alerts = new AlertsImpl();

    private final static String EMBEDDED_LIST_FILE = "embedded_list.txt";
    private final static String DELETE_CONFIGURATION = "Do you want to delete configuration file?";
    private final static String FILE_NOT_FOUND = "File not found.";
    private final static String CREATING_NEW_FILE = "File with embedded system configurations could not be found. Creating new file.";
    private final static String ACCESS_DENIED = "Access to file denied. Check your security settings.";
    private final static String FILE_CORRUPTED = "File seems to be corrupted. Cannot load saved configurations.";
    private final static String FILE_CONFIGURATION_DELETED = "File configuration deleted.";
    private final static String FILE_CONFIGURATION_ADDED = "Added new configuration to file.";

    private File file = new File(EMBEDDED_LIST_FILE);
    private Logger logger;
    private ArrayList<String> embeddedSystems = new ArrayList<>();

    public MenuViewModel(Logger logger) {
        this.logger = logger;
    }

    public void add(String ipAddress, String embeddedType) {
        try {
            File file = createOrOpenFile();
            writeContentToFile(file, ipAddress, embeddedType);
            logger.log(FILE_CONFIGURATION_ADDED);
        } catch (IOException e) {
            alerts.createErrorAlert(null, e.toString());
        }
    }

    public void emptyConfigurationFile() throws IOException {
        if (file.isFile()) {
            Optional<ButtonType> result = alerts.createConfirmationAlert(null, DELETE_CONFIGURATION).showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                FileWriter fileWriter = new FileWriter(file, false);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("");
                bufferedWriter.close();
                logger.log(FILE_CONFIGURATION_DELETED);
            }
        } else {
            alerts.createErrorAlert(null, FILE_NOT_FOUND);
        }
    }

    private File createOrOpenFile() throws IOException {
        if (file.isFile()) {
            return file;
        } else {
            if (file.createNewFile()) {
                alerts.createInfoAlert(null, CREATING_NEW_FILE);
                return file;
            } else {
                alerts.createWarningAlert(null, ACCESS_DENIED);
                return null;
            }
        }
    }

    private void writeContentToFile(File file, String ipAddress, String embeddedType) throws IOException {
        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile(), true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(ipAddress + ":" + embeddedType);
        bufferedWriter.newLine();
        bufferedWriter.close();
    }

    public void readFileContent(boolean firstRead) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        if (!firstRead) {
            embeddedSystems.clear();
        }
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || !line.contains(":")) {
                alerts.createErrorAlert(null, FILE_CORRUPTED);
                logger.log(FILE_CORRUPTED);
            } else {
                String[] tokens = line.split(":");
                if (tokens.length == 2) {
                    embeddedSystems.add(tokens[0] + " (" + tokens[1] + ")");
                } else {
                    alerts.createErrorAlert(null, FILE_CORRUPTED);
                    logger.log(FILE_CORRUPTED);
                }
            }
        }
    }

    public String splitSelectedSystemString(String selectedSystem) {
        String[] tokens = selectedSystem.split("\\(");
        String[] parts = tokens[1].split("\\)");
        selectedSystem = parts[0];
        return selectedSystem;
    }

    public String splitIpFromSelectedString(String selectedSystemType) {
        String[] tokens = selectedSystemType.split(" ");
        selectedSystemType = tokens[0];
        return selectedSystemType;
    }

    public ArrayList<String> getEmbeddedSystems() {
        return embeddedSystems;
    }
}
