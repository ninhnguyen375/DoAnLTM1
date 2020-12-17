/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.CPUSchedule;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import server.CPUSchedule.Algorithms.*;
import server.CPUSchedule.DTO.Row;

/**
 *
 * @author Ram4GB
 */
public class ExecuteCPUAlgorythm {

    private ArrayList<Row> arrayProcess;
    private String algorithmName;

    public ExecuteCPUAlgorythm(String arrayProcessString, String algorithmName) throws IOException {
        applyArrayProcessFromJsonString(arrayProcessString);
        this.algorithmName = algorithmName;
    }

    public CPUScheduler execute() {
        // Implement
        CPUScheduler algorithm = null;
        if (algorithmName == null) {
            algorithmName = "FCFS";
        }
        if (algorithmName.equals("FCFS")) {
            algorithm = new FirstComeFirstServe();
        } else if (algorithmName.equals("SJF")) {
            algorithm = new ShortestJobFirst();
        } else if (algorithmName.equals("RR")) {
            algorithm = new RoundRobin();
        } else if (algorithmName.equals("PP")) {
            algorithm = new PriorityPreemptive();
        } else if (algorithmName.equals("PNP")) {
            algorithm = new PriorityNonPreemptive();
        } else if (algorithmName.equals("SRT")) {
            algorithm = new ShortestRemainingTime();
        } else {
            // default
            algorithm = new FirstComeFirstServe();
        }

        if (arrayProcess.size() > 0) {
            // add tất cả process trong list vào ham thuat toan
            for (int i = 0; i < arrayProcess.size(); i++) {
                algorithm.add(arrayProcess.get(i));
            }
            // chạy hàm xử lý thuật toán
            algorithm.process();
        }
        
        return algorithm;
    }

    public void applyArrayProcessFromJsonString(String string) {
        Gson gson = new Gson();
        List list = gson.fromJson(string, ArrayList.class
        );
        ArrayList<Row> arrayProcess = new ArrayList<>();

        for (Object objEdge : list) {
            Row edge = gson.fromJson(gson.toJson(objEdge), Row.class);
            arrayProcess.add(edge);
        }

        this.arrayProcess = arrayProcess;
    }

    public ArrayList<Row> getArrayProcess() {
        return arrayProcess;
    }

    public void setArrayProcess(ArrayList<Row> arrayProcess) {
        this.arrayProcess = arrayProcess;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

}
