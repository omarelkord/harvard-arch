public class Controller {
    String[] instructionMemory;
    byte[] dataMemory;
    byte[] registerFile;
    ALU alu;
    short pc; // program counter register
    byte status;

    int clockCycle; // initially clock cycle is equal to 1.
    public Instruction prevFetchedInstruction; // instruction that is going to be decoded at the current decode stage.
    public Instruction nextFetchedInstruction; // instruction that contains fetched instruction at the current stage and that is going to be decoded at the next stage.
    public Instruction prevDecodedInstruction; // instruction that is going to be executed at the current execute stage.
    public Instruction nextDecodedinstruction; // instruction that contains decoded instruction at the current stage and  that is going to be executed at the next stage.




    public Controller(byte[] dataMemory, String[] instructionMemory, byte[] registerFile, ALU alu) {
        this.pc = (short)0;
        this.status = (byte)0;
        this.clockCycle = 1;
        this.dataMemory = dataMemory;
        this.instructionMemory = instructionMemory;
        this.registerFile = registerFile;
        this.alu = alu;
    }

//    public void execute2 () {
//        String[] control = prevDecodedInstruction.control;
//        int r1_addr = Integer.parseInt(control[0], 2);
//        String insType = control[3];
//        String op = control[4];
//        byte r1 = Byte.parseByte(control[6]);
//        byte r2 = Byte.parseByte(control[7]);
//        byte imm = Byte.parseByte(control[7]);
//        // ...
//    }

    public void execute() {
        String[] control = prevDecodedInstruction.control;
        int r1_addr = Integer.parseInt(control[0], 2);
        String insType = control[3];
        String op = control[4];
        byte r1 = Byte.parseByte(control[6]);
        byte r2 = Byte.parseByte(control[7]);
        byte imm = Byte.parseByte(control[7]);

        status = 0;


        if (op.equals("LB"))
            registerFile[r1_addr] = dataMemory[imm];
        else if (op.equals("SB"))
            dataMemory[imm] = registerFile[r1_addr];
        else if (op.equals("LDI")) {
            registerFile[r1_addr] = imm;
        } else if (op.equals("BEQZ") && r1 == 0) {
            pc = (short) (pc + 1 + imm);
        } else if (op.equals("JR")){
            pc = Short.parseShort(convertToBinary(r1, 8) + convertToBinary(r2, 8), 2);
        } else {
            byte res;

            if (insType.equals("0"))
                res = alu.operate(r1, r2, op);
            else
                res = alu.operate(r1, imm, op);

            registerFile[r1_addr] = res;
            status = Byte.parseByte("000" + alu.C + "" + alu.V + "" + alu.N + "" + alu.S + "" + alu.Z, 2);
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
            System.out.println("Clock cycle: " + clockCycle);
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
            if (instructionMemory[pc] != null) {
                fetch();
                System.out.println("I fetched");
            }
            else {
                nextFetchedInstruction = null;
            }
            if (prevFetchedInstruction != null) {
                decode();
                System.out.println("I decoded");
            }
            if (prevDecodedInstruction != null) {
                execute();
                System.out.println("I executed");
            }
            clockCycle++;
            prevFetchedInstruction = nextFetchedInstruction;
            prevDecodedInstruction = nextDecodedinstruction;
            nextFetchedInstruction = null;
            nextDecodedinstruction = null;
        } while (nextFetchedInstruction != null || prevFetchedInstruction != null || prevDecodedInstruction != null);

    }



    public void fetch() {
        String fetchedInstruction;
        fetchedInstruction = instructionMemory[pc];
        nextFetchedInstruction = new Instruction(fetchedInstruction); // Create an Instruction object.
        pc++; // increment pc to point to the next instruction in the instruction memory.
    }

    public void decode () {
        nextDecodedinstruction = new Instruction(prevFetchedInstruction.instruction);
        String instruction = nextDecodedinstruction.instruction;
        String opCode = instruction.substring(0, 4); // opcode from bit 0 to 3.
        String r1 = instruction.substring(4, 10); // r1 from bit 4 to 9.
        String r2 = instruction.substring(10, 16);  // r2 from bit 10 to 15.
        String imm = instruction.substring(10, 16); // immediate from bit 10 to 15.
        byte valR1 = registerFile[Integer.parseInt(r1, 2)];
        byte valR2 = registerFile[Integer.parseInt(r2, 2)];
        byte valImmediate = (Byte.parseByte(imm, 2));
        nextDecodedinstruction.set(opCode, r1, r2, imm, valR1, valR2, valImmediate);
    }

    public static void main(String[] args) throws Exception {
        Parser p = new Parser();
        p.parse("src/prog.txt");
        p.displayInstructionMemory();
        Controller c = new Controller(new byte[2048], p.instructionMemory, new byte[64], new ALU());
        c.run();
    }

}



