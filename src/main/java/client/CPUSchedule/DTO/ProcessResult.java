/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.DTO;

/**
 *
 * @author Ram4GB
 */
// Dùng để bỏ render ra chart
public class ProcessResult {

    private String processName;
    private int processStart, processEnd;

    public ProcessResult(String processName, int processStart, int processEnd) {
        this.processName = processName;
        this.processStart = processStart;
        this.processEnd = processEnd;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getProcessStart() {
        return processStart;
    }

    public void setProcessStart(int processStart) {
        this.processStart = processStart;
    }

    public int getProcessEnd() {
        return processEnd;
    }

    public void setProcessEnd(int processEnd) {
        this.processEnd = processEnd;
    }

}
