/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.DTO;

import java.util.ArrayList;

/**
 *
 * @author Ram4GB
 */
public class ResultAfterExecuteAlgorithm {

    ArrayList<Row> rows;
    ArrayList<Event> timeline;
    int timeQuantum;

    public ResultAfterExecuteAlgorithm(ArrayList<Row> rows, ArrayList<Event> timeline, int timeQuantum) {
        this.rows = rows;
        this.timeline = timeline;
        this.timeQuantum = timeQuantum;
    }

    public ArrayList<Row> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Row> rows) {
        this.rows = rows;
    }

    public ArrayList<Event> getTimeline() {
        return timeline;
    }

    public void setTimeline(ArrayList<Event> timeline) {
        this.timeline = timeline;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }
}
