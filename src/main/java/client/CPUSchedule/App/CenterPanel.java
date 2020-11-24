/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.Algorithms.CPUScheduler;
import client.CPUSchedule.Algorithms.Event;
import client.CPUSchedule.Constant.Constant;
import client.CPUSchedule.DTO.ProcessResult;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.GanttCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

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
        // Init dataset

        GanttCategoryDataset dataset = createDataset(null);
        Constant.dataset = dataset;
        repaintContent(dataset);
    }

    public void repaintContent(GanttCategoryDataset dataset) {
        // Add dataset to jfreechart
        chart = createChart(dataset);
        Constant.chart = chart;

        panel = new ChartPanel(chart);

        // refer to FCFS constant
        Constant.panel = panel;
        // Set size for jTabbedPane
        panel.setPreferredSize(new Dimension(Constant.WIDTH_CENTER_PANEL, Constant.HIGHT_PANEL));

        add(panel);
    }

    // Create dataset
    public static GanttCategoryDataset createDataset(ArrayList<ProcessResult> arr) {
        if (arr == null) {
//            final TaskSeriesCollection collection = new TaskSeriesCollection();
//
//            TaskSeries ts1 = new TaskSeries("P" + 1);
//            TaskSeries ts2 = new TaskSeries("P" + 2);
//
//            final Task task1 = new Task("P1_0", new SimpleTimePeriod(0, 2));
//            final Task task2 = new Task("P2_0", new SimpleTimePeriod(10, 14));
//            final Task task3 = new Task("P1_1", new SimpleTimePeriod(2, 5));
//            final Task task4 = new Task("P2_1", new SimpleTimePeriod(5, 10));
//            final Task task5 = new Task("P1_2", new SimpleTimePeriod(14, 15));
//
//            ts1.add(task1);
//            ts1.add(task3);
//            ts2.add(task4);
//            ts2.add(task2);
//            ts1.add(task5);
//
//            collection.add(ts1);
//            collection.add(ts2);
//
//            return collection;
            return null;
        }

        HashMap<String, ArrayList<ProcessResult>> temp = new HashMap<>();

        final TaskSeriesCollection collection = new TaskSeriesCollection();

        for (int i = 0; i < arr.size(); i++) {
            if (!temp.containsKey(arr.get(i).getProcessName())) {
                ArrayList<ProcessResult> t = new ArrayList<>();
                t.add(arr.get(i));
                temp.put(arr.get(i).getProcessName(), t);
            } else {
                ArrayList<ProcessResult> t = temp.get(arr.get(i).getProcessName());
                t.add(arr.get(i));
                temp.put(arr.get(i).getProcessName(), t);
            }
        }

        for (String key : temp.keySet()) {
            TaskSeries ts = new TaskSeries(key);
            int count = 0;
            ArrayList<ProcessResult> t = temp.get(key);
            for (int i = 0; i < t.size(); i++) {
                ts.add(new Task(key + count++, new SimpleTimePeriod(t.get(i).getProcessStart(), t.get(i).getProcessEnd())));
            }
            collection.add(ts);
        }

        return collection;
    }

    public static ArrayList<ProcessResult> convertResultAlgorithmToProcessResult(CPUScheduler object) {
        ArrayList<ProcessResult> arr = new ArrayList<>();
        
        for (int i = 0; i < object.getTimeline().size(); i++) {
            List<Event> timeline = object.getTimeline();
            ProcessResult o = new ProcessResult(timeline.get(i).getProcessName(), timeline.get(i).getStartTime(), timeline.get(i).getFinishTime());
            arr.add(o);
        }
        
        return arr;
    }

    // Create chart
    public static JFreeChart createChart(final GanttCategoryDataset dataset) {
        final JFreeChart chart = ChartFactory.createGanttChart(
                "Gantt Chart", // chart title
                "Process", // domain axis label
                "TIME (ms)", // range axis label
                dataset, // data
                true, // include legend
                true, // tooltips
                false // urls
        );

        CategoryPlot plot = chart.getCategoryPlot();

        DateAxis axis = (DateAxis) plot.getRangeAxis();

        GanttRenderer ganttRenderer = (GanttRenderer) plot.getRenderer();
        IntervalCategoryToolTipGenerator tooltipGenerator = new IntervalCategoryToolTipGenerator() {
            @Override
            public String generateToolTip(CategoryDataset dataset, int row, int column) {
                String s = super.generateToolTip(dataset, row, column);
                String[] split = s.split(" = ");
                split = split[1].split("-");

                return "From " + split[0] + "(ms) to " + split[1] + "(ms)"; //To change body of generated methods, choose Tools | Templates.
            }

        };
        ganttRenderer.setBaseToolTipGenerator(tooltipGenerator);
        axis.setDateFormatOverride(new SimpleDateFormat("S")); // second
        return chart;
    }
}
