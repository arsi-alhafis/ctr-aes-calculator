package com.aes;

/**
 * Created by arsi on 06-Apr-17.
 */

import com.aes.spec.CalculatorSpec;
import com.aes.util.StringUtil;
import com.aes.util.Type;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
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
    private JButton submitButton;
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
        encryptRadioButton.setActionCommand(Type.ENCRYPT.toString());
        decryptRadioButton.setActionCommand(Type.DECRYPT.toString());

        textArea2.setEditable(false);
        textArea1.setEditable(false);
        textArea3.setEditable(false);

        ButtonGroup group = new ButtonGroup();
        group.add(encryptRadioButton);
        group.add(decryptRadioButton);
        encryptRadioButton.setSelected(true);

        fc = new JFileChooser();

        FileFilter inputFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.length() < 300 * 1024 * 1024;
            }

            @Override
            public String getDescription() {
                return "File size under 300MB";
            }
        };

        FileFilter keyFilter = new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                return f.length() == 64 ||
                        f.length() == 48 ||
                        f.length() == 32;
            }

            @Override
            public String getDescription() {
                return "128, 192, or 256 bits key file";
            }
        };

        inputFileSelected = false;
        keyAllowed = false;

        allowedKeyLength = new ArrayList<>();
        allowedKeyLength.add(16);
        allowedKeyLength.add(24);
        allowedKeyLength.add(32);

        submitButton.setEnabled(false);
        submitButton.setText(Type.ENCRYPT.toString());
        encryptRadioButton.addActionListener(e -> submitButton.setText(Type.ENCRYPT.toString()));
        decryptRadioButton.addActionListener(e -> submitButton.setText(Type.DECRYPT.toString()));

        inputButton.addActionListener(e -> {
            fc.setFileFilter(inputFilter);
            int returnVal = fc.showOpenDialog(Calculator.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputFile = fc.getSelectedFile();

                textArea2.setText(inputFile.getAbsolutePath()+"\n\n");

                if (inputFile.length() < 300 * 1024 * 1024) {
                    inputFileSelected = true;
                    if (keyAllowed) submitButton.setEnabled(true);
                } else {
                    textArea2.append("Maximum file size allowed is 300MB");
                }
            }
        });

        keyButton.addActionListener(e -> {
            fc.setFileFilter(keyFilter);
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
                        if (inputFileSelected) submitButton.setEnabled(true);
                    } else {
                        textArea1.append(" [NOT ALLOWED]\n");
                        textArea1.append("Use 128, 192, or 256 bits key.");
                    }
                } catch (Exception e1) {
                    textArea1.setText(keyFile.getAbsolutePath() + "\n\n");
                    textArea1.append("[INVALID KEY FILE]\n");
                    textArea1.append("Use 128, 192, or 256 bits key.\n");
                    textArea1.append("Key file must a hexadecimal string.");
                }
            }
        });

        submitButton.addActionListener(e -> {
            CalculatorSpec spec = new CalculatorSpec();
            spec.setInputFile(inputFile);
            spec.setKeyFile(keyFile);

            if (group.getSelection().getActionCommand().equals(Type.ENCRYPT.toString())) {
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
                    textArea3.setText("Unmatched input file and key file.\n\n");
                    textArea3.append("This can be caused by: \n");
                    textArea3.append("- The input file is not a cipher file, or \n");
                    textArea3.append("- The key file is a wrong key. \n");
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