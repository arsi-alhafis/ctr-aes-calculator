package com.aes;

/**
 * Created by arsi on 06-Apr-17.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Apps extends JPanel{
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
    private boolean keyFileSelected;

    private boolean keyAllowed;

    public Apps(){
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
        keyFileSelected = false;
        keyAllowed = false;

        allowedKeyLength = new ArrayList<>();
        allowedKeyLength.add(16);
        allowedKeyLength.add(24);
        allowedKeyLength.add(32);

        GOOOOOButton.setEnabled(false);

        inputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(Apps.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    inputFile = fc.getSelectedFile();
                    inputFileSelected = true;

                    textArea2.setText(inputFile.getAbsolutePath());

                    if (keyAllowed) GOOOOOButton.setEnabled(true);
                }
            }
        });

        keyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(Apps.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    keyFile = fc.getSelectedFile();

                    Path keyPath = Paths.get(keyFile.getAbsolutePath());
                    try {
                        String keyHex = Files.readAllLines(keyPath).get(0);
                        byte[] keyBytes = Aes.hexStringToByteArray(keyHex);

                        textArea1.setText(keyFile.getAbsolutePath() + "\n\n");
                        textArea1.append("Key length: " + String.valueOf(keyBytes.length * 8) + " bits");

                        if (allowedKeyLength.contains(keyBytes.length)) {
                            keyAllowed = true;
                            textArea1.append(" [Allowed]");
                            if (inputFileSelected) GOOOOOButton.setEnabled(true);
                        } else {
                            textArea1.append(" [Not Allowed]");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        GOOOOOButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (group.getSelection().getActionCommand().equals("enc")) {
                    try {
                        String result = Aes.start(inputFile, keyFile, Type.ENCRYPT);
                        textArea3.setText("ENCRYPTION COMPLETE\n\n");
                        textArea3.append("Location: " + result);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        String result = Aes.start(inputFile, keyFile, Type.DECRYPT);
                        textArea3.setText("DECRYPTION COMPLETE\n\n");
                        textArea3.append("Location: " + result);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CTR-AES Calculator");
        frame.setContentPane(new Apps().panel1);
        frame.setPreferredSize(new Dimension(800, 400));
        frame.setLocation(200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
