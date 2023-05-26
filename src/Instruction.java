public class Instruction {
    String instruction;
    int id;
    String r1;
    String r2;
    String imm;
    String[] control = new String[9]; // control [RegDst, Branch, MemRead, MemtoReg, ALUOp, MemWrite, R1Value, ALUSrc(R2Value, Immediate), RegWrite]

    public Instruction(String instruction) {
        this.instruction = instruction;
    }
    public Instruction () {}

    public void setInstructionID (int id) {
        this.id = id;
    }

    public void set(String opCode, String r1, String r2, String imm, byte valR1, byte valR2, byte valImmediate) {
        // control = [RegDst, Branch, MemRead, MemtoReg (0: R, 1: I), ALUOp, MemWrite, R1Value, ALUSrc, RegWrite]
        this.r1 = r1;
        this.r2 = r2;
        this.imm = imm;
        switch (opCode) {
            // R-type
            case "0000":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "0"; // MemtoReg (R-type)
                control[4] = "ADD"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valR2 + ""; // ALUSrc (pass r2)
                control[8] = "1"; // RegWrite
                break;
            case "0001":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "0"; // MemtoReg (R-type)
                control[4] = "SUB"; // ALUOp
                control[6] = valR1 + "";
                control[7] = valR2 + ""; // ALUSrc (pass r2)
                control[8] = "1"; // RegWrite
                break;
            case "0010":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "0"; // MemtoReg (R-type)
                control[4] = "MUL"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valR2 + ""; // ALUSrc (pass r2)
                control[8] = "1"; // RegWrite
                break;
            case "0101":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "0"; // MemtoReg (R-type)
                control[4] = "AND"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valR2 + ""; // ALUSrc (pass r2)
                control[8] = "1"; // RegWrite
                break;
            case "0110":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "0"; // MemtoReg (R-type)
                control[4] = "OR"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valR2 + ""; // ALUSrc (pass r2)
                control[8] = "1"; // RegWrite
                break;
            case "0111":
                control[0] = null; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "0"; // MemtoReg (R-type)
                control[4] = "JR"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valR2 + ""; // ALUSrc (pass r2)
                control[8] = "0"; // RegWrite
                break;
            // I-type
            case "0011":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "1"; // MemtoReg (I-type)
                control[4] = "LDI"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valImmediate + ""; // ALUSrc (pass immediate)
                control[8] = "1"; // RegWrite
                break;
            case "0100":
                control[0] = null; // RegDst
                control[1] = "1"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "1"; // MemtoReg (I-type)
                control[4] = "BEQZ"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valImmediate + ""; // ALUSrc (pass immediate)
                control[8] = "0"; // RegWrite
                break;
            case "1000":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "1"; // MemtoReg (I-type)
                control[4] = "SLC"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valImmediate + ""; // ALUSrc (pass immediate)
                control[8] = "1"; // RegWrite
                break;
            case "1001":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "1"; // MemtoReg (I-type)
                control[4] = "SRC"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valImmediate + ""; // ALUSrc (pass immediate)
                control[8] = "1"; // RegWrite
                break;
            case "1010":
                control[0] = r1; // RegDst
                control[1] = "0"; // Branch
                control[2] = "1"; // MemRead
                control[3] = "1"; // MemtoReg (I-type)
                control[4] = "LB"; // ALUOp
                control[5] = "0"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valImmediate + ""; // ALUSrc (pass immediate)
                control[8] = "1"; // RegWrite
                break;
            case "1011":
                control[0] = null; // RegDst
                control[1] = "0"; // Branch
                control[2] = "0"; // MemRead
                control[3] = "1"; // MemtoReg (I-type)
                control[4] = "SB"; // ALUOp
                control[5] = "1"; // MemWrite
                control[6] = valR1 + "";
                control[7] = valImmediate + ""; // ALUSrc (pass immediate)
                control[8] = "0"; // RegWrite
                break;
        }
    }
}
