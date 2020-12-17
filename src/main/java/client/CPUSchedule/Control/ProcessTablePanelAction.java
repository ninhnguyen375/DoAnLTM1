/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.Control;

import client.CPUSchedule.DTO.Event;
import client.CPUSchedule.DTO.ResultAfterExecuteAlgorithm;
import client.CPUSchedule.Constant.Constant;
import java.awt.Dimension;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.gantt.GanttCategoryDataset;
import client.CPUSchedule.DTO.ProcessResult;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

/**
 *
 * @author Ram4GB
 */
// Tất cả hàm xử lý vẽ biểu đồ năm ở ngay chô này
public class ProcessTablePanelAction {

    public static void renderGraph(ResultAfterExecuteAlgorithm resultAfterExecuteAlgorithm) {
        Constant.centerPanel.removeAll();
        // Vẽ biểu đồ
        // Cái này là làm theo documentation
        // biến đổi kết quả thuật toán trả về phụ hợp để bỏ vào chart
        GanttCategoryDataset dataset;
        // Chuyển về dạng dataset
        // ArrayList<ResultAfterExecuteAlgorithm> -> ArrayList<ProcessResult> -> dataset
        dataset = createDataset(convertResultAlgorithmToProcessResult(resultAfterExecuteAlgorithm));
        // Tạo chart
        JFreeChart chart = createChart(dataset);
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

    // chuyển đổi kết quả của algorithm sang dạng object cần xài
    // return ArrayList<ProcessResult>
    public static ArrayList<ProcessResult> convertResultAlgorithmToProcessResult(ResultAfterExecuteAlgorithm resultAfterExecuteAlgorithm) {
        ArrayList<ProcessResult> arr = new ArrayList<>();

        for (int i = 0; i < resultAfterExecuteAlgorithm.getTimeline().size(); i++) {
            ArrayList<Event> timeline = resultAfterExecuteAlgorithm.getTimeline();
            ProcessResult o = new ProcessResult(timeline.get(i).getProcessName(),
                    timeline.get(i).getStartTime(),
                    timeline.get(i).getFinishTime());
            arr.add(o);
        }

        System.out.println("time line" + resultAfterExecuteAlgorithm.getTimeline().size());

        return arr;
    }

    // Tạo chart từ
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

    // không hiển thị gì hết
    public static void renderDefaultGraph() {
        Constant.centerPanel.removeAll();
        Constant.centerPanel.validate();
        Constant.centerPanel.repaint();
    }

    public static void updateTable() {
        // Remove all
        Constant.defaultTableModel.setRowCount(0);

        // add new
        for (int i = 0; i < Constant.arrayListProcess.size(); i++) {
            Constant.defaultTableModel.addRow(new Object[]{Constant.arrayListProcess.get(i).getProcessName(),
                Constant.arrayListProcess.get(i).getArrivalTime(),
                Constant.arrayListProcess.get(i).getBurstTime(), Constant.arrayListProcess.get(i).getPriorityLevel()});
        }
        Constant.defaultTableModel.fireTableDataChanged();
    }

    // Tạo dataset
    public static GanttCategoryDataset createDataset(ArrayList<ProcessResult> arr) {
        if (arr == null) {
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
}
