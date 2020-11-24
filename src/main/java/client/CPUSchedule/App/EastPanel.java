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

/**
 *
 * @author Ram4GB
 */
public class EastPanel extends JPanel {

    public static EastPanel_AddProcessPanel addProcessPanel;
    public static EastPanel_TableShowProcess processTablePanel;
    public static EastPanel_ChooseTypeAlgorithmPanel chooseTypeAlgorithmPanel;

    EastPanel() {
        setPreferredSize(new Dimension(Constant.WIDTH_EAST_PANEL, Constant.HIGHT_PANEL));
        setLayout(new BorderLayout());

        addProcessPanel = new EastPanel_AddProcessPanel();
        processTablePanel = new EastPanel_TableShowProcess();
        chooseTypeAlgorithmPanel = new EastPanel_ChooseTypeAlgorithmPanel();

        add(chooseTypeAlgorithmPanel, BorderLayout.NORTH);
        add(addProcessPanel, BorderLayout.CENTER);
        add(processTablePanel, BorderLayout.SOUTH);
    }
}
