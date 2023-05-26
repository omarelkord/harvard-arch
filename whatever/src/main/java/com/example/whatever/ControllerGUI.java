package com.example.whatever;

import com.example.whatever.ALU;
import com.example.whatever.Controller;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ControllerGUI extends Application {
    private TextArea registerTextArea;
    private VBox contentBox;
    private Controller microArchitecture;
    private TextArea outputTextArea;
    private TextArea memoryTextArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Controller GUI");

        // Input TextArea
        TextArea inputTextArea = new TextArea();
        inputTextArea.setPromptText("Enter code here");
        inputTextArea.setPrefHeight(400);

        // Run Button
        Button runButton = new Button("Run");
        runButton.setPrefWidth(80);
        runButton.setOnAction(e -> {
            String code = inputTextArea.getText();
            runCode(code);
        });

        // Output TextArea
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false);
        outputTextArea.setPrefHeight(100);

        // Register TextArea
        registerTextArea = new TextArea();
        registerTextArea.setEditable(false);
        registerTextArea.setPrefRowCount(12);
        registerTextArea.setPrefColumnCount(12);
        registerTextArea.setFont(Font.font("Courier New", 12));

        // Memory TextArea
        memoryTextArea = new TextArea();
        memoryTextArea.setEditable(false);
        memoryTextArea.setPrefRowCount(12);
        memoryTextArea.setPrefColumnCount(30);
        memoryTextArea.setFont(Font.font("Courier New", 12));

        // Show Data Memory Button
        Button showDataMemoryButton = new Button("Show Data Memory");
        showDataMemoryButton.setOnAction(e -> showDataMemory());

        // Show Instruction Memory Button
        Button showInstructionMemoryButton = new Button("Show Instruction Memory");
        showInstructionMemoryButton.setOnAction(e -> showInstructionMemory());

        // Panel for Memory, Instruction Memory, Output
        contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.getChildren().add(outputTextArea);

        // Layout
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));

        VBox codeBox = new VBox(10);
        codeBox.getChildren().addAll(inputTextArea, runButton);
        borderPane.setCenter(codeBox);

        VBox rightBox = new VBox(10);
        rightBox.getChildren().addAll(registerTextArea, showDataMemoryButton, showInstructionMemoryButton);
        rightBox.setPadding(new Insets(10));
        rightBox.setStyle("-fx-background-color: #f4f4f4;");
        borderPane.setRight(rightBox);

        VBox.setVgrow(registerTextArea, Priority.ALWAYS);

        borderPane.setBottom(contentBox); // Add contentBox to the bottom of the layout

        primaryStage.setScene(new Scene(borderPane, 800, 600));
        primaryStage.show();

        initializeRegisters();
        initializeMicroArchitecture();
    }

    private void initializeRegisters() {
        StringBuilder registers = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            registers.append("R").append(i).append(": 0\n");
        }
        registerTextArea.setText(registers.toString());
    }

    private void initializeMicroArchitecture() {
        byte[] registerFile = new byte[64];
        ALU alu = new ALU();
        microArchitecture = new Controller();
        microArchitecture.set(new byte[2048], new String[2048], registerFile, alu);
    }

    private void runCode(String code) {
        try {
            initializeMicroArchitecture();
            Parser p = new Parser();
            p.parse(code);
            microArchitecture.instructionMemory=p.instructionMemory;
            microArchitecture.run();

            StringBuilder output = new StringBuilder();
            output.append("Output\n");

            // Build the output based on the controller's output

            outputTextArea.setText(output.toString());
            updateRegisterValues(microArchitecture.registerFile);
            contentBox.getChildren().clear();
            contentBox.getChildren().add(outputTextArea);
        } catch (Exception e) {
            outputTextArea.setText("Error: " + e.getMessage());
            contentBox.getChildren().clear();
            contentBox.getChildren().add(outputTextArea);
        }
    }

    private void updateRegisterValues(byte[] registerFile) {
        StringBuilder registers = new StringBuilder();
        for (int i = 0; i < registerFile.length; i++) {
            registers.append("R").append(i).append(": ").append(registerFile[i]).append("\n");
        }
        registerTextArea.setText(registers.toString());
    }

    private void showDataMemory() {
        StringBuilder memoryContent = new StringBuilder();
        memoryContent.append("Data Memory\n");
        for (int i = 0; i < microArchitecture.dataMemory.length; i++) {
            memoryContent.append(i).append(": ").append(microArchitecture.dataMemory[i]).append("\n");
        }
        memoryTextArea.setText(memoryContent.toString());
        contentBox.getChildren().clear();
        contentBox.getChildren().add(memoryTextArea);
    }

    private void showInstructionMemory() {
        StringBuilder instructionMemoryContent = new StringBuilder();
        instructionMemoryContent.append("Instruction Memory\n");

        for (int i = 0; i < microArchitecture.instructionMemory.length; i++) {
            instructionMemoryContent.append(i).append(": ").append(microArchitecture.instructionMemory[i]).append("\n");
        }

        memoryTextArea.setText(instructionMemoryContent.toString());
        contentBox.getChildren().clear();
        contentBox.getChildren().add(memoryTextArea);
    }
}
