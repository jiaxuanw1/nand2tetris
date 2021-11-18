package assembler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<String> asm;
    private int currentIndex;
    private String currentCommand;

    public enum CommandType {
        A_COMMAND, C_COMMAND, L_COMMAND
    }

    public Parser(String filepath) {
        try {
            asm = new ArrayList<String>();
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line = br.readLine();

            while (line != null) {
                // Remove comments from line
                if (line.contains("//")) {
                    line = line.substring(0, line.indexOf("//"));
                }

                // Add line to assembly list if not empty
                if (!line.isBlank()) {
                    asm.add(line.trim());
                }

                line = br.readLine();
            }
            br.close();
            reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMoreCommands() {
        return currentIndex < asm.size() - 1;
    }

    public void advance() {
        currentIndex++;
        currentCommand = asm.get(currentIndex);
    }

    public CommandType commandType() {
        switch (currentCommand.charAt(0)) {
            case '@':
                return CommandType.A_COMMAND;
            case '(':
                return CommandType.L_COMMAND;
            default:
                return CommandType.C_COMMAND;
        }
    }

    public String symbol() {
        switch (commandType()) {
            case A_COMMAND:
                return currentCommand.substring(1);
            case L_COMMAND:
                return currentCommand.substring(1, currentCommand.length() - 1);
            default:
                return null;
        }
    }

    public String dest() {
        if (commandType() != CommandType.C_COMMAND || !currentCommand.contains("=")) {
            return null;
        }
        return currentCommand.substring(0, currentCommand.indexOf("="));
    }

    public String comp() {
        if (commandType() != CommandType.C_COMMAND) {
            return null;
        }
        int startIndex = 0;
        int endIndex = currentCommand.length();
        if (currentCommand.contains("=")) {
            startIndex = currentCommand.indexOf("=") + 1;
        }
        if (currentCommand.contains(";")) {
            endIndex = currentCommand.indexOf(";");
        }
        return currentCommand.substring(startIndex, endIndex);
    }

    public String jump() {
        if (commandType() != CommandType.C_COMMAND || !currentCommand.contains(";")) {
            return null;
        }
        return currentCommand.substring(currentCommand.indexOf(";") + 1);
    }

    public void reset() {
        currentIndex = -1;
        currentCommand = null;
    }

}
