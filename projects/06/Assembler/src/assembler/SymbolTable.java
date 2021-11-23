package assembler;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private Map<String, Integer> symbolTable;

    public SymbolTable() {
        symbolTable = new HashMap<String, Integer>();
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        for (int i = 0; i <= 15; i++) {
            symbolTable.put("R" + i, i);
        }
    }

    public void addEntry(String symbol, int address) {
        symbolTable.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return symbolTable.get(symbol);
    }

}
