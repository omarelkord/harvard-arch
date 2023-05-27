package com.example.whatever;

import java.util.Scanner;

public class Simulator {
    Parser parser;
    Controller controller;

    public Simulator() {
        parser = new Parser();
        controller = new Controller();
    }

    public void simulate (String fileName, byte[] dataMemory, byte[] registerFile) throws Exception {
        String file = "src/" + fileName;
        parser.parse(file);
        controller.set(dataMemory, parser.instructionMemory, registerFile, new ALU());
        controller.run();
    }

    public static void main (String[]args) throws Exception {
        Simulator s = new Simulator();
        byte[] dataMemory = new byte[2048];
        byte[] registerFile = new byte[64];
        String fileName;
        // TODO take input from user allowing him to insert values in dataMemory, instructionMemory and registerFIle.
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter file name: ");
        fileName = sc.next();
        System.out.println(fileName);
        System.out.print("Do you want to adjust the register file (y/n): ");
        if (sc.next().equals("y")) {
            do {
                System.out.print("Register address: ");
                int regAddress = sc.nextInt();
                System.out.println();
                System.out.print("Register value: ");
                byte regValue = sc.nextByte();
                registerFile[regAddress] = regValue;
                System.out.println();
                System.out.print("finished? (y/n): ");
            }
            while (sc.next().equals("y"));
        }
        System.out.print("Do you want to adjust the memory (y/n): ");
        if (sc.next().equals("y")) {
            do {
                System.out.print("Memory address: ");
                int memAddress = sc.nextInt();
                System.out.println();
                System.out.print("Value: ");
                byte memValue = sc.nextByte();
                dataMemory[memAddress] = memValue;
                System.out.println();
                System.out.print("finished? (y/n): ");
            }
            while (sc.next().equals("y"));
        }
        System.out.print("Program started: ");
        System.out.println();
        s.simulate(fileName, dataMemory, registerFile);
    }
}

