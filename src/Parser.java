import java.io.BufferedReader;
import java.io.FileReader;

public class Parser {
    public String[] instructionMemory; // word = 2 bytes

    public Parser() {
        instructionMemory = new String[1024]; // Size of the Instruction Memory = 1024
    }

    public void parse(String fileName) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        int current = 0;
        while ((line = reader.readLine()) != null) {

            if (line.length() == 0) { // empty line
                continue;
            }

            // In case the size of the program exceed the maximum limit of our instruction memory
            if (current >= 1024) {
                throw new Exception("Size of the program greater than size of the instruction memory");
            }

            String[] instruction = line.split(" ");
            // All instructions consist of three parts
            if (instruction.length != 3) {
                throw new Exception("Incorrect instructions format");
            }

            // Handle Opcode
            String o = instruction[0];
            String opCode;
            int format; // 0 : R-Format, 1 : I-Format

            switch (o) {
                case "ADD":
                    opCode = "0000";
                    format = 0;
                    break; // R-Format
                case "SUB":
                    opCode = "0001";
                    format = 0;
                    break; // R-Format
                case "MUL":
                    opCode = "0010";
                    format = 0;
                    break; // R-Format
                case "LDI":
                    opCode = "0011";
                    format = 1;
                    break; // I-Format
                case "BEQZ":
                    opCode = "0100";
                    format = 1;
                    break; // I-Format
                case "AND":
                    opCode = "0101";
                    format = 0;
                    break; // R-Format
                case "OR":
                    opCode = "0110";
                    format = 0;
                    break; // R-Format
                case "JR":
                    opCode = "0111";
                    format = 0;
                    break; // R-Format
                case "SLC":
                    opCode = "1000";
                    format = 1;
                    break; // I-Format
                case "SRC":
                    opCode = "1001";
                    format = 1;
                    break; // I-Format
                case "LB":
                    opCode = "1010";
                    format = 1;
                    break; // I-Format
                case "SB":
                    opCode = "1011";
                    format = 1;
                    break; // I-Format
                default:
                    opCode = "";
                    format = -1;             // format = any value but not 0 or 1
            }

            if (opCode.length() == 0) {
                throw new Exception("Invalid operation code");
            }

            if (format == 0) { // Instruction of type: R-format

                // Handle Operands
                String firstOper = instruction[1].substring(1);
                int val1 = Integer.parseInt(firstOper);
                String secondOper = instruction[2].substring(1);
                int val2 = Integer.parseInt(secondOper);

            /*
            Check: (for both operands)
                1. The first character of register is 'R'.
                2. that the register number lies between [0,63].
             */
                if (instruction[1].charAt(0) != 'R' || val1 > 63 || val1 < 0) {
                    throw new Exception("Invalid register: " + instruction[1]);
                }
                if (instruction[2].charAt(0) != 'R' || val2 > 63 || val2 < 0) {
                    throw new Exception("Invalid register: " + instruction[2]);
                }

                String reg1;
                reg1 = convertToBinary(val1, 6);

                String reg2;
                reg2 = convertToBinary(val2, 6);

                // Concatenation to get the parsing of the instruction into its bit representation.
                String parse;
                parse = opCode + reg1 + reg2;
                instructionMemory[current] = parse;
                current++;
            } else if (format == 1) { // Instruction of type: I-format
                // Handle Operands
                String firstOper = instruction[1].substring(1);
                int valReg = Integer.parseInt(firstOper);
                String immValue = instruction[2];
                int valImm = Integer.parseInt(immValue);

                if (instruction[1].charAt(0) != 'R' || valReg > 63 || valReg < 0) {
                    throw new Exception("Invalid register: " + instruction[1]);
                }
                if (valImm > 31 || valImm < -32) {
                    throw new Exception("Immediate value must be between -32 and 31");
                }

                String reg;
                reg = String.format("%6s", Integer.toBinaryString(valReg)).replaceAll(" ", "0");

                String imm = convertToBinary(valImm, 6);

                String parse;
                parse = opCode + reg + imm;
                instructionMemory[current] = parse;
                current++;
            }

        }
        reader.close();
    }

    public static String convertToBinary(int n, int size) {
        if (n >= 0) return String.format("%" + size + "s", Integer.toBinaryString(n)).replaceAll(" ", "0");
        return String.format("%" + size + "s", Integer.toBinaryString(n)).substring(32 - size);

    }


    public String[] instructionMemory() {
        return instructionMemory;
    }

    public void displayInstructionMemory() {
        System.out.println("Instruction Memory");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < instructionMemory.length; i++) {
            System.out.println(i + ": " + instructionMemory[i]);
        }
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }

    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.parse("src/program");
        p.displayInstructionMemory();
    }
}