package com.example.whatever;

public class Controller {
    String[] instructionMemory;
    byte[] dataMemory;
    byte[] registerFile;
    ALU alu;
    short pc; // program counter register
    short prevPC;
    byte status;

    int clockCycle; // initially clock cycle is equal to 1.
    public Instruction prevFetchedInstruction; // instruction that is going to be decoded at the current decode stage.
    public Instruction nextFetchedInstruction; // instruction that contains fetched instruction at the current stage and that is going to be decoded at the next stage.
    public Instruction prevDecodedInstruction; // instruction that is going to be executed at the current execute stage.
    public Instruction nextDecodedinstruction; // instruction that contains decoded instruction at the current stage and  that is going to be executed at the next stage.

    public Controller() {}

    public void set (byte[] dataMemory, String[] instructionMemory, byte[] registerFile, ALU alu) {
        this.pc = (short)0;
        this.status = (byte)0;
        this.clockCycle = 1;
        this.dataMemory = dataMemory;
        this.instructionMemory = instructionMemory;
        this.registerFile = registerFile;
        this.alu = alu;
    }


    public void execute() {
        String[] control = prevDecodedInstruction.control;
        String insType = control[3];
        String op = control[4];
        byte r1 = Byte.parseByte(control[6]);
        byte r2 = Byte.parseByte(control[7]);
        byte imm = Byte.parseByte(control[7]);
        int r1_addr = op.equals("JR") || op.equals("BEQZ") || op.equals("SB")? -1 : Integer.parseInt(control[0], 2);

        status = 0;

        if (op.equals("LB"))
        {
            registerFile[r1_addr] = dataMemory[imm];
            checkDataHazard(control[0], dataMemory[imm]);
            System.out.println(", Update Register: [R" + r1_addr + " = " + dataMemory[imm] + "]");
        }
        else if (op.equals("SB")) {
            dataMemory[imm] = r1;
            System.out.println(", Update Memory: Mem[" + imm + "] = " + r1 + "]");
        }
        else if (op.equals("LDI")) {
            registerFile[r1_addr] = imm;
            checkDataHazard(control[0], imm);
            System.out.println(", Update Register: [R" + r1_addr + " = " + imm + "]");
        } else if (op.equals("BEQZ") && r1 == 0) {
            pc = (short) (prevPC + 1 + imm);  // pc = pc (old???) + 1 + imm
            System.out.println(", Update PC: [pc" + " = " + pc + "]");
            // flush fetched and decoded isntructions
            nextFetchedInstruction = null;
            nextDecodedinstruction = null;
        } else if (op.equals("JR")) {
            pc = Short.parseShort(convertToBinary(r1, 8) + convertToBinary(r2, 8), 2);
            System.out.println(", Update PC: [pc" + " = " + pc + "]");
            // flush fetched and decoded isntructions
            nextFetchedInstruction = null;
            nextDecodedinstruction = null;
        } else {
            byte res;
            if (insType.equals("0")){
                res = alu.operate(r1, r2, op);
            }
            else
            {
                res = alu.operate(r1, imm, op);
            }
            registerFile[r1_addr] = res;
            checkDataHazard(control[0], res);
            System.out.println(", Update Register: [R" + r1_addr + " = " + res + "]");
            status = Byte.parseByte("000" + alu.C + "" + alu.V + "" + alu.N + "" + alu.S + "" + alu.Z, 2);
        }
    }

    // Handling data hazards by forwading.
    public void checkDataHazard (String r, byte valR) {
        if (nextDecodedinstruction != null) {
            if (nextDecodedinstruction.r1.equals(r)) {
                nextDecodedinstruction.control[6] = valR + "";
            }
            if (nextDecodedinstruction.r2.equals(r)) {
                nextDecodedinstruction.control[7] = valR + "";
            }
        }
    }

    public static String convertToBinary(int n, int size) {
        if (n >= 0)
            return String.format("%" + size + "s", Integer.toBinaryString(n)).replaceAll(" ", "0");
        return String.format("%" + size + "s", Integer.toBinaryString(n)).substring(32 - size);
    }
    public void run() {
        // Is there still something to be done here ?
        do {
            // First: fetch the instruction from the instruction memory.
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("Clock cycle: " + clockCycle + ", Program counter = " + pc + "");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            if (instructionMemory[pc] != null) {
                fetch();
                System.out.print("Fetch Stage: ");
                System.out.println("Instruction Number " + (nextFetchedInstruction.id) + " (Binary Format: " + nextFetchedInstruction.instruction + ")");
                System.out.println("---------------------------------------------------------------------------------------------------------------------------");
            }
            else {
                nextFetchedInstruction = null;
            }
            if (prevFetchedInstruction != null) {
                decode();
                System.out.print("Decode Stage: ");
                System.out.print("Instruction Number " + (prevFetchedInstruction.id));
                if (nextDecodedinstruction.control[3].equals("0")) {
                    System.out.print(", Instruction: " + nextDecodedinstruction.control[4] + " R" + Integer.parseInt(nextDecodedinstruction.r1, 2) + " R" + Integer.parseInt(nextDecodedinstruction.r2, 2));
                    System.out.print(", Previous Registers Value: [R" + Integer.parseInt(nextDecodedinstruction.r1, 2) + " = " + nextDecodedinstruction.control[6] + " , R" + Integer.parseInt(nextDecodedinstruction.r2, 2) + " = " + nextDecodedinstruction.control[7] + "]");
                }
                else {
                    System.out.print(", Instruction: " + nextDecodedinstruction.control[4] + " R" + Integer.parseInt(nextDecodedinstruction.r1, 2) + " " + nextDecodedinstruction.control[7]);
                    System.out.print(", Previous Registers Value: [R"+Integer.parseInt(nextDecodedinstruction.r1, 2)+" = " + nextDecodedinstruction.control[6] + "]");
                }
                System.out.println();
                System.out.println("---------------------------------------------------------------------------------------------------------------------------");
            }
            else {
                nextDecodedinstruction = null;
            }
            if (prevDecodedInstruction != null) {
                System.out.print("Execute Stage: ");
                System.out.print("Instruction Number: " + (prevDecodedInstruction.id));

                if (prevDecodedInstruction.control[3].equals("0")) {
                    System.out.print(", Instruction: " + prevDecodedInstruction.control[4] + " R" + Integer.parseInt(prevDecodedInstruction.r1, 2) + " R" + Integer.parseInt(prevDecodedInstruction.r2, 2));
                    System.out.print(", Previous Registers Value: [R" + Integer.parseInt(prevDecodedInstruction.r1, 2) + " = " + prevDecodedInstruction.control[6] + " , R" + Integer.parseInt(prevDecodedInstruction.r2, 2) + " = " + prevDecodedInstruction.control[7] + "]");
                }
                else {
                    System.out.print(", Instruction: " + prevDecodedInstruction.control[4] + " R" + Integer.parseInt(prevDecodedInstruction.r1, 2) + " " + prevDecodedInstruction.control[7]);
                    System.out.print(", Previous Registers Value: [R"+Integer.parseInt(prevDecodedInstruction.r1, 2)+" = " + prevDecodedInstruction.control[6] + "]");
                }
                execute();
                System.out.println("---------------------------------------------------------------------------------------------------------------------------");
            }
            System.out.println("Status register = " + convertToBinary(status, 8));
            System.out.println("---------------------------------------------------------------------------------------------------------------------------");
            System.out.println();
            clockCycle++;
            prevFetchedInstruction = nextFetchedInstruction;
            prevDecodedInstruction = nextDecodedinstruction;
        } while (instructionMemory[pc] != null || prevFetchedInstruction != null || prevDecodedInstruction != null);
        // TODO the content of all registers.
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("|           Program Ended           |");
        System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        System.out.println("Program counter: " + pc);
        System.out.println("Status register: " + status);
        displayRegisterFile();
        // TODO the full content of the instruction memory.
        displayInstructionMemory();
        // TODO the full content of the data memory.
        displayDataMemory();
    }



    public void fetch() {
        String fetchedInstruction;
        fetchedInstruction = instructionMemory[pc];
        nextFetchedInstruction = new Instruction(fetchedInstruction); // Create an Instruction object.
        nextFetchedInstruction.setInstructionID(pc + 1);
        prevPC = pc;
        pc++; // increment pc to point to the next instruction in the instruction memory.
    }

    public void decode () {
        nextDecodedinstruction = new Instruction();
        nextDecodedinstruction.setInstructionID(prevFetchedInstruction.id);
        String instruction = prevFetchedInstruction.instruction;
        String opCode = instruction.substring(0, 4); // opcode from bit 0 to 3.
        String r1 = instruction.substring(4, 10); // r1 from bit 4 to 9.
        String r2 = instruction.substring(10, 16);  // r2 from bit 10 to 15.
        String imm = instruction.substring(10, 16); // immediate from bit 10 to 15.
        byte valR1 = registerFile[Byte.parseByte(r1, 2)];
        byte valR2 = registerFile[Byte.parseByte(r2, 2)];
        byte valImmediate = binaryToDecimal(imm);
        nextDecodedinstruction.set(opCode, r1, r2, imm, valR1, valR2, valImmediate);
    }

    public byte binaryToDecimal(String binary) {
        if (binary.charAt(0) == '1') {  // Check if negative number
            binary = binary.substring(1); // Remove leading 1
            String invert = "";
            for (char c : binary.toCharArray()) {
                invert += c == '1' ? '0' : '1';
            }
            return (byte)(-1 * (Byte.parseByte(invert, 2) + 1));
        }
        else {
            return Byte.parseByte(binary, 2);
        }
    }
    public void displayInstructionMemory() {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Instruction Memory");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < instructionMemory.length; i++) {
            System.out.println(i + ": " + instructionMemory[i]);
        }
    }
    public void displayDataMemory() {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Data Memory");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < dataMemory.length; i++) {
            System.out.println(i + ": " + dataMemory[i]);
        }
    }
    public void displayRegisterFile() {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Register File");
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        for (int i = 0; i < registerFile.length; i++) {
            System.out.println(i + ": " + registerFile[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.parse("src/prog.txt");
        Controller c = new Controller();
        c.set(new byte[2048], p.instructionMemory, new byte[64], new ALU());
        c.run();
    }
}



