/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.Constant.Constant;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author Ram4GB
 */
public final class CenterPanel extends JPanel {

    public static JPanel mainPanel;
    public static ChartPanel panel;
    public static JTabbedPane jTabbedPane;
    public static JFreeChart chart;

    CenterPanel() {
        // Init Center Panel
        setPreferredSize(new Dimension(Constant.WIDTH_CENTER_PANEL, Constant.HIGHT_PANEL));
    }
}
