/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.DTO.Row;
import client.CPUSchedule.Constant.Constant;
import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author Ram4GB
 */
public class MainPanel extends JPanel {

    public static CenterPanel centerPanel;
    public static EastPanel eastPanel;
    public static ArrayList<Row> arrayListProcess;

    public MainPanel() {
        setLayout(new BorderLayout());
        setSize(Constant.WIDTH_PANEL, Constant.HIGHT_PANEL);

        centerPanel = new CenterPanel();
        eastPanel = new EastPanel();
        arrayListProcess = new ArrayList<>();

        // refer to FSFS constaint
        Constant.arrayListProcess = arrayListProcess;
        Constant.centerPanel = centerPanel;
        Constant.eastPanel = eastPanel;

        addComponent();
    }

    private void addComponent() {
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(eastPanel, BorderLayout.EAST);
    }
}
