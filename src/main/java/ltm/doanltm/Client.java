/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ltm.doanltm;

import ltm.doanltm.dijkstra.Constant;
import ltm.doanltm.dijkstra.MainDijkstra;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author ninhn
 */
public class Client {
    public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        // Set default ui like current OS
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // Dijstra main panel
        MainDijkstra panel = new MainDijkstra();
        
        // Show on frame
        JFrame frame = new JFrame();
        frame.add(panel);

        frame.setSize(Constant.mainWidth, Constant.mainHeight);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
