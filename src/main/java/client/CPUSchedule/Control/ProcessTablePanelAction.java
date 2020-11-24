/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.Control;

import client.CPUSchedule.DTO.Row;
import client.CPUSchedule.App.CenterPanel;
import static client.CPUSchedule.App.CenterPanel.convertResultAlgorithmToProcessResult;
import client.CPUSchedule.Constant.Constant;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.gantt.GanttCategoryDataset;
import client.CPUSchedule.Algorithms.CPUScheduler;
import client.CPUSchedule.Algorithms.FirstComeFirstServe;
import client.CPUSchedule.Algorithms.PriorityNonPreemptive;
import client.CPUSchedule.Algorithms.PriorityPreemptive;
import client.CPUSchedule.Algorithms.RoundRobin;
import client.CPUSchedule.Algorithms.ShortestJobFirst;
import client.CPUSchedule.Algorithms.ShortestRemainingTime;

/**
 *
 * @author Ram4GB
 */
public class ProcessTablePanelAction {

    public static void renderGraph(String algorithmName) {
        // Implement

        CPUScheduler algorithm = null;

        if (algorithmName == null) {
            algorithmName = Constant.defaultTypeAlgorithm;
        }

        if (algorithmName.equals("FCFS")) {
            algorithm = new FirstComeFirstServe();
        } else if (algorithmName.equals("SJF")) {
            algorithm = new ShortestJobFirst();
        } else if (algorithmName.equals("RR")) {
            algorithm = new RoundRobin();
        } else if (algorithmName.equals("PP")) {
            algorithm = new PriorityPreemptive();
        } else if (algorithmName.equals("PNP")) {
            algorithm = new PriorityNonPreemptive();
        } else if (algorithmName.equals("SRT")) {
            algorithm = new ShortestRemainingTime();
        } else {
            // default
            algorithm = new FirstComeFirstServe();
        }

        if (Constant.arrayListProcess.size() > 0 && algorithm != null) {
            // add process to this algoithm and resolve it
            for (int i = 0; i < Constant.arrayListProcess.size(); i++) {
                algorithm.add(Constant.arrayListProcess.get(i));
            }

            algorithm.process();

            Constant.centerPanel.removeAll();

            // Draw graph
            GanttCategoryDataset dataset;
            dataset = CenterPanel.createDataset(convertResultAlgorithmToProcessResult(algorithm));
            JFreeChart chart = CenterPanel.createChart(dataset);
            Constant.panel = new ChartPanel(chart);
            Constant.panel.setPreferredSize(new Dimension(Constant.WIDTH_CENTER_PANEL, Constant.HIGHT_PANEL));
            Constant.centerPanel.add(Constant.panel);

            // When you add all component, this time to show it
            // Constant.centerPanel.removeAll();
            // Constant.centerPanel.validate();
            // Constant.centerPanel.repaint();
            // Add Component
            // => This case do not working
            // Do it like this code
            Constant.centerPanel.validate();
            Constant.centerPanel.repaint();
        }
    }

    public static void renderDefaultGraph() {
        Constant.centerPanel.removeAll();
        Constant.centerPanel.validate();
        Constant.centerPanel.repaint();
    }

    public static class AddProcessAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            String processName = Constant.textFieldProcessName.getText();
            String processTime = Constant.textFieldProcessTime.getText();
            String processTimeStart = Constant.textFieldProcessTimeStart.getText();

            if (processName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Process Name");
            } else if (processTime.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Process Time");
            } else if (processTime.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Process Time Start");

            } else {
                Row row = new Row(processName,
                        Integer.parseInt(processTimeStart),
                        Integer.parseInt(processTime)
                );

                int index = -1;
                for (int i = 0; i < Constant.arrayListProcess.size(); i++) {
                    if (row.getProcessName().equals(Constant.arrayListProcess.get(i).getProcessName())) {
                        index = i;
                        break;
                    } else {
                        continue;
                    }
                }

                if (index != -1) {
                    int option = JOptionPane.showConfirmDialog(null, "Your process is duplicated. Do you want to rewrite it?", "Warning", JOptionPane.YES_NO_OPTION);
                    // Ok 0
                    // No 1
                    // Close -1

                    System.out.println(option);

                    if (option == -1) {
                        // keep it and do nothing
                    } else if (option == 0) {
                        // update
                        Constant.arrayListProcess.set(index, row);
                        renderGraph(Constant.defaultTypeAlgorithm);
                        Constant.textFieldProcessName.setText("");
                        Constant.textFieldProcessTime.setText("");
                        Constant.textFieldProcessTimeStart.setText("");
                        updateTable();
                    } else if (option == 1) {
                        // keep it and do nothing
                    }
                } else {
                    Constant.arrayListProcess.add(row);
                    Constant.textFieldProcessName.setText("");
                    Constant.textFieldProcessTime.setText("");
                    Constant.textFieldProcessTimeStart.setText("");
                    // Option 1 to update
                    updateTable();
                    // Option 2 to update
                    // Constant.defaultTableModel.addRow(new Object[]{processName, processTime, processTimeStart});
                    // Constant.defaultTableModel.fireTableDataChanged();
                    renderGraph(Constant.defaultTypeAlgorithm);
                }
            }

        }
    }

    public static class HandleSelectTypeAction implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent item) {
            Constant.defaultTypeAlgorithm = item.getItem().toString();

            if (!Constant.defaultTypeAlgorithm.isEmpty()) {
                renderGraph(Constant.defaultTypeAlgorithm);
            } else {
                renderDefaultGraph();
            }
        }
    }

    public static void updateTable() {
        // Remove all
        Constant.defaultTableModel.setRowCount(0);

        // add new
        for (int i = 0; i < Constant.arrayListProcess.size(); i++) {
            Constant.defaultTableModel.addRow(new Object[]{Constant.arrayListProcess.get(i).getProcessName(),
                Constant.arrayListProcess.get(i).getArrivalTime(),
                Constant.arrayListProcess.get(i).getBurstTime()});
        }
        Constant.defaultTableModel.fireTableDataChanged();
    }
}
