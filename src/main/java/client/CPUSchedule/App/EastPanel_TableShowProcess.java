/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.Constant.Constant;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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

        // refer to constant file
        Constant.defaultTableModel = defaultTableModel;

        tableProcess = new JTable(defaultTableModel);
        jScrollPane = new JScrollPane(tableProcess);
        jScrollPane.setSize(Constant.WIDTH_EAST_PANEL, Constant.HIGHT_PANEL / 2);

        add(jScrollPane, BorderLayout.CENTER);
    }
}
