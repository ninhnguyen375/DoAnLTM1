/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.DTO.ResultAfterExecuteAlgorithm;
import client.CPUSchedule.Constant.Constant;
import client.CPUSchedule.Control.ProcessTablePanelAction;
import static client.CPUSchedule.Control.ProcessTablePanelAction.renderGraph;
import static client.CPUSchedule.Control.ProcessTablePanelAction.updateTable;
import client.CPUSchedule.DTO.Row;
import client.Client;
import com.google.gson.Gson;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public static JLabel labelPriority;

    public static JTextField textFieldPriority;
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
        // Chọn file
        buttonChooseFile.addActionListener((var arg0) -> {
            handleClickChooseFile();
        });
        labelProcessName = new JLabel("Process Name");
        labelProcessTime = new JLabel("Process Time (ms)");
        labelProcessTimeStart = new JLabel("Process Time Start (ms)");
        labelPriority = new JLabel("Priority");

        textFieldPriority = new JTextField(200);
        textFieldPriority.setEnabled(false);
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
        buttonAddProcess.addActionListener((ActionEvent arg0) -> {
            String processName = Constant.textFieldProcessName.getText();
            String processTime = Constant.textFieldProcessTime.getText();
            String processTimeStart = Constant.textFieldProcessTimeStart.getText();
            String processPriority = textFieldPriority.getText();

            if (processName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Process Name");
            } else if (processTime.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Process Time");
            } else if (processTime.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Process Time Start");

            } else {
                Row row = null;
                boolean flag = true;
                try {
                    if (Constant.defaultTypeAlgorithm.equals("PP") || Constant.defaultTypeAlgorithm.equals("PNP")) {
                        if (Integer.parseInt(processTimeStart) < 0
                                || Integer.parseInt(processTime) < 0
                                || Integer.parseInt(processPriority) < 0) {
                            JOptionPane.showMessageDialog(null, "Must be >= 0");
                            return;
                        }
                        row = new Row(processName,
                                Integer.parseInt(processTimeStart),
                                Integer.parseInt(processTime),
                                Integer.parseInt(processPriority)
                        );
                    } else {
                        if (Integer.parseInt(processTimeStart) < 0
                                || Integer.parseInt(processTime) < 0) {
                            JOptionPane.showMessageDialog(null, "Must be >= 0");
                            return;
                        }
                        row = new Row(processName,
                                Integer.parseInt(processTimeStart),
                                Integer.parseInt(processTime)
                        );
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "process has value which is not a number", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    flag = false;
                }

                if (flag) { // nếu mà không bị throw NumberFormatException
                    // check exists process name in list
                    int index = -1;
                    for (int i = 0; i < Constant.arrayListProcess.size(); i++) {
                        if (row.getProcessName().equals(Constant.arrayListProcess.get(i).getProcessName())) {
                            index = i;
                            break;
                        } else {
                            continue;
                        }
                    }

                    if (index != -1) { // nếu bị trùng
                        int option = JOptionPane.showConfirmDialog(null, "Your process is duplicated. Do you want to rewrite it?", "Warning", JOptionPane.YES_NO_OPTION);
                        // mún override cái bị trùng hay không?
                        // Ok 0
                        // No 1
                        // Close -1

                        System.out.println(option);

                        if (option == -1) {
                            // keep it and do nothing
                        } else if (option == 0) {
                            // đồng ý override bị trùng
                            Constant.arrayListProcess.set(index, row);

                            // Constant.textFieldProcessName.setText("");
                            // Constant.textFieldProcessTime.setText("");
                            // Constant.textFieldProcessTimeStart.setText("");
                            // call server and get result
                            try {
                                Client.socketSend("get-algorythm-" + Constant.defaultTypeAlgorithm);
                                Client.socketSend(new Gson().toJson(Constant.arrayListProcess));
                                ResultAfterExecuteAlgorithm result = new Gson().fromJson(Client.socketReadLine(), ResultAfterExecuteAlgorithm.class);
                                // Việc Update Table dưới client
                                updateTable();
                                // Update Graph, việc vẽ grap sẽ do server trả kết quả về
                                renderGraph(result);
                            } catch (Exception ex) {
                                Logger.getLogger(ProcessTablePanelAction.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else if (option == 1) {
                            // keep it and do nothing
                        }
                    } else { // không bị trùng tên process
                        Constant.arrayListProcess.add(row);
                        // thêm xong làm trống input
                        // Constant.textFieldProcessName.setText("");
                        // Constant.textFieldProcessTime.setText("");
                        // Constant.textFieldProcessTimeStart.setText("");

                        // call server and get result
                        try {
                            Client.socketSend("get-algorythm-" + Constant.defaultTypeAlgorithm);
                            Client.socketSend(new Gson().toJson(Constant.arrayListProcess));
                            String s = Client.socketReadLine();
                            ResultAfterExecuteAlgorithm result = new Gson().fromJson(s, ResultAfterExecuteAlgorithm.class);
                            // Việc Update Table Sẽ được server xử kết quả sau đó render ra
                            // Option 1 to update
                            // Việc Update Table dưới client
                            // Option 2 to update
                            // Constant.defaultTableModel.addRow(new Object[]{processName, processTime, processTimeStart});
                            // Constant.defaultTableModel.fireTableDataChanged();
                            // Update Graph, việc vẽ grap sẽ do server trả kết quả về
                            updateTable();
                            renderGraph(result);
                        } catch (Exception ex) {
                            Logger.getLogger(ProcessTablePanelAction.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

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
        gbc.insets = new Insets(5, 5, 5, 5);
        add(labelPriority, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(textFieldPriority, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(buttonAddProcess, gbc);
    }

    // cái này là upload file lên
    private int handleClickChooseFile() {
        JFileChooser fileChooser = new JFileChooser(new File(Constant.testFilesPath));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            ArrayList<Row> arr = new ArrayList<>();
            try {
                Scanner scanner = new Scanner(file);

                while (scanner.hasNext()) {
                    String line = scanner.nextLine();
                    Pattern p = Pattern.compile("^\\w+ \\d \\d \\d$");
                    Matcher matcher = p.matcher(line);

                    if (matcher.find()) {
                        String[] values = line.split(" ");

                        String processName = values[0];
                        int arrivalTime = 0;
                        int burstTime = 0;
                        int priority = 0;

                        try {
                            arrivalTime = Integer.parseInt(values[1]);
                            burstTime = Integer.parseInt(values[2]);
                            priority = Integer.parseInt(values[3]);
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "process has value which is not a number", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            return -1; // thoát ra và không cần xử lý
                        }

                        arr.add(new Row(processName, arrivalTime, burstTime, priority));
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong content file", "Error File", JOptionPane.ERROR_MESSAGE);
                        return -1;
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

                    // nếu mà đã được add rồi thì update lại giá trị của nó
                    if (found == -1) {
                        // Add new
                        Constant.arrayListProcess.add(arr.get(i));
                    } else {
                        // Update that value
                        Constant.arrayListProcess.set(found, arr.get(i));
                    }
                }

                // Update graph
                // Gửi yêu cầu cho server
                Client.socketSend("get-algorythm-" + Constant.defaultTypeAlgorithm);
                Client.socketSend(new Gson().toJson(Constant.arrayListProcess));
                // Nhận kết quả của server
                ResultAfterExecuteAlgorithm result = new Gson().fromJson(Client.socketReadLine(), ResultAfterExecuteAlgorithm.class);
                // Render ra kết quả
                renderGraph(result);
                // Update table
                ProcessTablePanelAction.updateTable();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(EastPanel_AddProcessPanel.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(EastPanel_AddProcessPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 1;
        } else {
            System.out.println("Canceled by user");
            return -1;
        }
    }
}
