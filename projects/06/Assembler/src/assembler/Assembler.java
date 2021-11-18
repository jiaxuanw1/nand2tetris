package assembler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import assembler.Parser.CommandType;

public class Assembler {

    private Parser parser;
    private SymbolTable symbolTable;
    private Code code;
    private String fileName;
    private List<String> binaryOutput;
    private int nextROMAddress;
    private int nextRAMAddress;

    public Assembler(String readFile) {
        fileName = readFile;
        parser = new Parser(readFile);
        symbolTable = new SymbolTable();
        code = new Code();
        binaryOutput = new ArrayList<String>();
        nextROMAddress = 0;
        nextRAMAddress = 16;
    }

    public static void main(String[] args) {
        Assembler assembler = new Assembler(args[0]);
        assembler.firstPass();
        assembler.secondPass();
    }

    public void firstPass() {
        parser.reset();
        while (parser.hasMoreCommands()) {
            parser.advance();
            if (parser.commandType() == CommandType.L_COMMAND) {
                symbolTable.addEntry(parser.symbol(), nextROMAddress);
            } else {
                nextROMAddress++;
            }
        }
    }

    public void secondPass() {
        parser.reset();
        while (parser.hasMoreCommands()) {
            parser.advance();

            String bin;
            switch (parser.commandType()) {
                case A_COMMAND:
                    String symbol = parser.symbol();
                    int address;
                    try {
                        // Symbol is a number
                        address = Integer.parseInt(symbol);
                    } catch (NumberFormatException e) {
                        // If new variable, add to symbol table with associated address
                        if (!symbolTable.contains(symbol)) {
                            symbolTable.addEntry(symbol, nextRAMAddress);
                            nextRAMAddress++;
                        }
                        // Get address associated with symbol from table
                        address = symbolTable.getAddress(symbol);
                    }
                    String binaryAddress = Integer.toBinaryString(address);
                    bin = "0" + "0".repeat(15 - binaryAddress.length()) + binaryAddress;
                    break;
                case C_COMMAND:
                    String dest = code.dest(parser.dest());
                    String comp = code.comp(parser.comp());
                    String jump = code.jump(parser.jump());
                    bin = "111" + comp + dest + jump;
                    break;
                default:
                    bin = null;
                    break;
            }

            if (bin != null) {
                binaryOutput.add(bin);
            }
        }

        try {
            String writePath = fileName.replace(".asm", ".hack");
            Files.write(Path.of(writePath), binaryOutput, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
