public class ALU {
    int C;
    int V;
    int N;
    int S;
    int Z;

    public byte operate(byte op1 , byte op2 , String operation){
        byte result;

        switch (operation){
            case "ADD" : result = (byte)(op1 + op2);
                        this.updateCarryFlag(op1, op2);
                        this.updateOverflowFlag(op1, op2 , result, true);

                        break;
            case "SUB" : result = (byte)(op1 - op2);
                        this.updateOverflowFlag(op1, op2, result, false);
                        break;
            case "MUL": result = (byte)(op1 * op2);break;
            case "AND" : result = (byte)(op1 & op2);break;
            case "OR"  : result = (byte)(op1 | op2);break;
            case "SLC" : result =  (byte)((op1 << op2) | (op1 >>> 8 - op2));break;
            case "SRC" : result =  (byte)((op1 >>> op2) | (op1 << 8 - op2));break;
            default: result = Byte.parseByte(null);
        }
        Z = (result == 0)? 1: 0;
        N = (result < 0)? 1:0;
        if(operation.equals("ADD") || operation.equals("SUB"))
            this.updateSignFlag();


        return result;

    }

    public static String convertToBinary(int n, int size) {
        if (n >= 0)
            return String.format("%" + size + "s", Integer.toBinaryString(n)).replaceAll(" ", "0");
        return String.format("%" + size + "s", Integer.toBinaryString(n)).substring(32 - size);

    }

    public void updateOverflowFlag(byte op1, byte op2 ,byte result ,boolean isAdd ) {
        String binaryOp1 = convertToBinary(op1 , 8);
        String binaryOp2 = convertToBinary(op2 , 8);
        String binaryresult = convertToBinary(result, 8);
        this.V = 0;

        boolean isSameSign = binaryOp1.charAt(0) == binaryOp2.charAt(0);
        if (isAdd && isSameSign && (binaryresult.charAt(0) != binaryOp1.charAt(0)))
            this.V = 1;

        if (!isAdd && !isSameSign && (binaryresult.charAt(0) == binaryOp2.charAt(0)))
            this.V = 1;

    }



    public void updateSignFlag(){
        this.S = (this.N != this.V)? 1 : 0;

    }

    public void updateCarryFlag(byte op1 , byte op2){
        int unsignedOp1 = op1 & 0x000000FF;
        int unsignedOp2 = op2 & 0x000000FF;
        int mask = 0b100000000;
        this.C = ((unsignedOp1 + unsignedOp2) & mask) == mask ? 1: 0;

    }
    public static void main(String[]args){


//        byte signedByte = -5;
//        int unsignedByte = signedByte & 0x000000FF; // Convert to unsigned byte
//
//        System.out.println(unsignedByte & 0b100000000);
//        String binaryString = Integer.toBinaryString(unsignedByte); // Convert to binary string
//        // Pad with leading zeros if necessary to ensure 8 bit
//         binaryString = String.format("%16s", binaryString).replace(' ', '0');
//        System.out.println(binaryString);
//
//        char char1 = 'A';
//        char char2 = 'B';
//
//        Character charObj1 = Character.valueOf(char1);
//        Character charObj2 = Character.valueOf(char2);
//
//        boolean areEqual = charObj1.equals(charObj2);
//
//        System.out.println('1' == '1');

        ALU alu = new ALU();
        byte op1 = -127;
        byte op2 = 64;
        System.out.println(convertToBinary(op1,8));
        System.out.println(convertToBinary(op2,8));

        byte result = alu.operate(op1 , op2 , "ADD");
        System.out.println(result);
        System.out.println(convertToBinary(result,8));

        System.out.println("C:" + alu.C);
        System.out.println("V:" + alu.V);
        System.out.println("N:" + alu.N);
        System.out.println("S:" + alu.S);
        System.out.println("Z:" + alu.Z);




    }















}

