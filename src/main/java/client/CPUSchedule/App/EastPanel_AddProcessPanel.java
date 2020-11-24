/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.Constant.Constant;
import client.CPUSchedule.Control.ProcessTablePanelAction;
import client.CPUSchedule.DTO.Row;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Ram4GB
 */
public class EastPanel_AddProcessPanel extends JPanel {

    public static JLabel labelTitleOption;
    public static JLabel labelProcessName;
    public static JLabel labelProcessTime;
    public static JLabel labelProcessTimeStart;

    public static JTextField textFieldProcessName;
    public static JTextField textFieldProcessTime;
    public static JTextField textFieldProcessTimeStart;

    public static JButton buttonAddProcess;

    public static JButton buttonChooseFile;

    EastPanel_AddProcessPanel() {
        setPreferredSize(new Dimension(Constant.WIDTH_EAST_PANEL, 200));
        setBorder(BorderFactory.createTitledBorder(Constant.ADDING_PANEL_NAME));

        labelTitleOption = new JLabel("Import By File");
        buttonChooseFile = new JButton("Choose file here");
        buttonChooseFile.addActionListener((var arg0) -> {
            handleClickAddProcess();
        });
        labelProcessName = new JLabel("Process Name");
        labelProcessTime = new JLabel("Process Time (ms)");
        labelProcessTimeStart = new JLabel("Process Time Start (ms)");

        textFieldProcessName = new JTextField(200);
//        textFieldProcessName.setText(Constant.prefixNameProcess + String.valueOf(Constant.startNumberProcess)); // For Process auto increament
//        textFieldProcessName.setEditable(false);
        textFieldProcessName.setText(Constant.defaultStartProcessName);
        textFieldProcessName.setToolTipText("Please enter Process name");

        labelProcessTime.setToolTipText("Please enter Process time ");
        labelProcessTimeStart.setToolTipText("Please enter Process time start");

        textFieldProcessTime = new JTextField(200);
        textFieldProcessTimeStart = new JTextField(200);
        buttonAddProcess = new JButton("Add process");
        buttonAddProcess.addActionListener(new ProcessTablePanelAction.AddProcessAction());

        // refer to constant file
        Constant.textFieldProcessName = textFieldProcessName;
        Constant.textFieldProcessTime = textFieldProcessTime;
        Constant.textFieldProcessTimeStart = textFieldProcessTimeStart;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(labelTitleOption, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(buttonChooseFile, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(labelProcessName, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(textFieldProcessName, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(labelProcessTimeStart, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(textFieldProcessTimeStart, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(labelProcessTime, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(textFieldProcessTime, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(buttonAddProcess, gbc);
    }

    private void handleClickAddProcess() {
        JFileChooser fileChooser = new JFileChooser(new File(Constant.testFilesPath));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            ArrayList<Row> arr = new ArrayList<>();
            try {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    Pattern p = Pattern.compile("^\\w+ \\d \\d$");
                    Matcher matcher = p.matcher(line);

                    if (matcher.find()) {
                        String[] values = line.split(" ");
                        
                        String processName = values[0];
                        int arrivalTime = Integer.parseInt(values[1]);
                        int burstTime = Integer.parseInt(values[2]);

                        arr.add(new Row(processName, arrivalTime, burstTime));
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong content file", "Error File", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                }

                // validate file
                // duplicated process
                for (int i = 0; i < arr.size(); i++) {
                    int found = -1;
                    for (int j = 0; j < Constant.arrayListProcess.size(); j++) {
                        if (Constant.arrayListProcess.get(j).getProcessName().equals(arr.get(i).getProcessName())) {
                            found = j;
                            break;
                        } else {
                            continue;
                        }
                    }

                    if (found == -1) {
                        // Add new
                        Constant.arrayListProcess.add(arr.get(i));
                    } else {
                        // Update that value
                        Constant.arrayListProcess.set(found, arr.get(i));
                    }
                }

                // Update graph
                ProcessTablePanelAction.renderGraph(null);
                // Update table
                ProcessTablePanelAction.updateTable();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EastPanel_AddProcessPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.println("Canceled by user");
        }
    }
}
