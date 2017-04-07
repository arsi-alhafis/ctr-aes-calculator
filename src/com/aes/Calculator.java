package com.aes;

/**
 * Created by arsi on 06-Apr-17.
 */

import com.aes.spec.CalculatorSpec;
import com.aes.util.StringUtil;
import com.aes.util.Type;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Calculator extends JPanel{
    private JPanel panel1;
    private JRadioButton encryptRadioButton;
    private JRadioButton decryptRadioButton;
    private JButton inputButton;
    private JButton keyButton;
    private JButton GOOOOOButton;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JTextArea textArea3;
    private JFileChooser fc;

    private ArrayList<Integer> allowedKeyLength;

    private File inputFile;
    private File keyFile;

    private boolean inputFileSelected;
    private boolean keyAllowed;

    private Calculator(){
        encryptRadioButton.setActionCommand("enc");
        decryptRadioButton.setActionCommand("dec");

        textArea2.setEditable(false);
        textArea1.setEditable(false);
        textArea3.setEditable(false);

        ButtonGroup group = new ButtonGroup();
        group.add(encryptRadioButton);
        group.add(decryptRadioButton);
        encryptRadioButton.setSelected(true);

        fc = new JFileChooser();

        inputFileSelected = false;
        keyAllowed = false;

        allowedKeyLength = new ArrayList<>();
        allowedKeyLength.add(16);
        allowedKeyLength.add(24);
        allowedKeyLength.add(32);

        GOOOOOButton.setEnabled(false);

        inputButton.addActionListener(e -> {
            int returnVal = fc.showOpenDialog(Calculator.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputFile = fc.getSelectedFile();
                inputFileSelected = true;

                textArea2.setText(inputFile.getAbsolutePath());

                if (keyAllowed) GOOOOOButton.setEnabled(true);
            }
        });

        keyButton.addActionListener(e -> {
            int returnVal = fc.showOpenDialog(Calculator.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                keyFile = fc.getSelectedFile();

                Path keyPath = Paths.get(keyFile.getAbsolutePath());
                try {
                    String keyHex = Files.readAllLines(keyPath).get(0);
                    byte[] keyBytes = StringUtil.hexStringToByteArray(keyHex);

                    textArea1.setText(keyFile.getAbsolutePath() + "\n\n");
                    textArea1.append("Key length: " + String.valueOf(keyBytes.length * 8) + " bits");

                    if (allowedKeyLength.contains(keyBytes.length)) {
                        keyAllowed = true;
                        textArea1.append(" [ALLOWED]");
                        if (inputFileSelected) GOOOOOButton.setEnabled(true);
                    } else {
                        textArea1.append(" [NOT ALLOWED]\n");
                        textArea1.append("Use 128, 192, or 256 bits key.");
                    }
                } catch (IOException e1) {
                    textArea1.setText(keyFile.getAbsolutePath() + "\n\n");
                    textArea1.append("[INVALID KEY FILE]");
                }
            }
        });

        GOOOOOButton.addActionListener(e -> {
            CalculatorSpec spec = new CalculatorSpec();
            spec.setInputFile(inputFile);
            spec.setKeyFile(keyFile);

            if (group.getSelection().getActionCommand().equals("enc")) {
                spec.setType(Type.ENCRYPT);
                try {
                    String result = Aes.start(spec);
                    textArea3.setText("ENCRYPTION COMPLETE\n\n");
                    textArea3.append("Location: " + result);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            } else {
                spec.setType(Type.DECRYPT);
                try {
                    String result = Aes.start(spec);
                    textArea3.setText("DECRYPTION COMPLETE\n\n");
                    textArea3.append("Location: " + result);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CTR-AES Calculator");
        frame.setContentPane(new Calculator().panel1);
        frame.setPreferredSize(new Dimension(800, 400));
        frame.setLocation(200, 200);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}