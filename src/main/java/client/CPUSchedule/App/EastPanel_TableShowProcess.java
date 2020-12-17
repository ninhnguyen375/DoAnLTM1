/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.DTO.ResultAfterExecuteAlgorithm;
import client.CPUSchedule.Constant.Constant;
import static client.CPUSchedule.Control.ProcessTablePanelAction.renderGraph;
import static client.CPUSchedule.Control.ProcessTablePanelAction.updateTable;
import client.CPUSchedule.DTO.Row;
import client.Client;
import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Ram4GB
 */
public class EastPanel_TableShowProcess extends JPanel {

    public static JScrollPane jScrollPane;
    public static JTable tableProcess;
    public static DefaultTableModel defaultTableModel;

    public EastPanel_TableShowProcess() {
        setPreferredSize(new Dimension(Constant.WIDTH_EAST_PANEL, 150));
        setBorder(BorderFactory.createTitledBorder(Constant.PROCESS_TABLE_NAME));
        setLayout(new BorderLayout());

        defaultTableModel = new DefaultTableModel();
        defaultTableModel.addColumn("Process Name");
        defaultTableModel.addColumn("Time start");
        defaultTableModel.addColumn("Process time");
        defaultTableModel.addColumn("Priority");

        // refer to constant file
        Constant.defaultTableModel = defaultTableModel;

        tableProcess = new JTable(defaultTableModel);
        jScrollPane = new JScrollPane(tableProcess);
        jScrollPane.setSize(Constant.WIDTH_EAST_PANEL, Constant.HIGHT_PANEL / 2);
        tableProcess.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                System.out.println("User is choosing at row " + tableProcess.getSelectedRow());
            }
        });
        int condition = JTable.WHEN_FOCUSED;
        InputMap inputMap = tableProcess.getInputMap(condition);
        ActionMap actionMap = tableProcess.getActionMap();

        // khi nhấn nút delete thì xóa row đó
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteRow");
        actionMap.put("deleteRow", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int row = tableProcess.getSelectedRow();
                if (row >= 0) {
                    DefaultTableModel model = (DefaultTableModel) tableProcess.getModel();
                    model.removeRow(row);
                    Constant.arrayListProcess.remove(row);
                    try {
                        Client.socketSend("get-algorythm-" + Constant.defaultTypeAlgorithm);
                        Client.socketSend(new Gson().toJson(Constant.arrayListProcess));
                        ResultAfterExecuteAlgorithm result = new Gson().fromJson(Client.socketReadLine(), ResultAfterExecuteAlgorithm.class);
                        // Việc Update Table dưới client
                        updateTable();
                        // Update Graph, việc vẽ grap sẽ do server trả kết quả về
                        renderGraph(result);
                    } catch (Exception ex) {
                        Logger.getLogger(EastPanel_TableShowProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        add(jScrollPane, BorderLayout.CENTER);
        JButton saveButton = new JButton("Save table");
        add(saveButton, BorderLayout.SOUTH);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                int totalRow = tableProcess.getModel().getRowCount();
                ArrayList<Row> temp = new ArrayList<>();
                boolean isShouldUpdate = true;
                for (int i = 0; i < totalRow; i++) {
                    String processName = tableProcess.getModel().getValueAt(i, 0).toString();
                    int processTimeStart = 0;
                    int processBurstTime = 0;
                    int processPriority = 0;
                    try {
                        // kiểm tra là số
                        processTimeStart = Integer.parseInt(tableProcess.getModel().getValueAt(i, 1).toString());
                        processBurstTime = Integer.parseInt(tableProcess.getModel().getValueAt(i, 2).toString());
                        processPriority = Integer.parseInt(tableProcess.getModel().getValueAt(i, 3).toString());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "process at row " + i + " has value which is not a number", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        isShouldUpdate = false;
                        break;
                    }

                    int flag = -1;
                    for (int j = 0; j < temp.size(); j++) {
                        if (temp.get(j).getProcessName().equals(processName)) {
                            flag = j;
                        }
                    }

                    // kiểm tra trùng tên process
                    if (flag != -1) {
                        JOptionPane.showMessageDialog(null, "Duplicated name at row " + (i + 1) + " with row " + (flag + 1), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        isShouldUpdate = false;
                        break;
                    }
                    temp.add(new Row(processName, processTimeStart, processBurstTime, processPriority));
                }
                if (isShouldUpdate == true) {
                    try {
                        Constant.arrayListProcess = temp;
                        Client.socketSend("get-algorythm-" + Constant.defaultTypeAlgorithm);
                        Client.socketSend(new Gson().toJson(Constant.arrayListProcess));
                        ResultAfterExecuteAlgorithm result = new Gson().fromJson(Client.socketReadLine(), ResultAfterExecuteAlgorithm.class);
                        // Việc Update Table dưới client
                        updateTable();
                        // Update Graph, việc vẽ grap sẽ do server trả kết quả về
                        renderGraph(result);
                        JOptionPane.showMessageDialog(null, "Cập nhật thành công", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        Logger.getLogger(EastPanel_TableShowProcess.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
}
