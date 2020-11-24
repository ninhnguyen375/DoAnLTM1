/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.Constant.Constant;
import client.CPUSchedule.Control.ProcessTablePanelAction;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Ram4GB
 */
public class EastPanel_ChooseTypeAlgorithmPanel extends JPanel {

    public static JLabel labelType;
    public static JComboBox<String> typeAlgorithmArr;

    public EastPanel_ChooseTypeAlgorithmPanel() {
        setPreferredSize(new Dimension(Constant.WIDTH_EAST_PANEL, 50));
        setBorder(BorderFactory.createTitledBorder("Choose algorithm type"));

        labelType = new JLabel("Algorithm Type: ");
        typeAlgorithmArr = new JComboBox<>();
        Constant.defaultTypeAlgorithm = "FCFS";

        typeAlgorithmArr.addItem("FCFS");
        typeAlgorithmArr.addItem("SJF");
        typeAlgorithmArr.addItem("RR");
        typeAlgorithmArr.addItem("PP");
        typeAlgorithmArr.addItem("PNP");
        typeAlgorithmArr.addItem("SRT");
        typeAlgorithmArr.addItemListener(new ProcessTablePanelAction.HandleSelectTypeAction());

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(labelType, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(typeAlgorithmArr, gbc);
    }

}
