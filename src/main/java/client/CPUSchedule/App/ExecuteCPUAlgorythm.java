/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.CPUSchedule.App;

import client.CPUSchedule.Algorithms.CPUScheduler;
import client.CPUSchedule.Algorithms.FirstComeFirstServe;
import client.CPUSchedule.Algorithms.PriorityNonPreemptive;
import client.CPUSchedule.Algorithms.PriorityPreemptive;
import client.CPUSchedule.Algorithms.RoundRobin;
import client.CPUSchedule.Algorithms.ShortestJobFirst;
import client.CPUSchedule.Algorithms.ShortestRemainingTime;
import client.CPUSchedule.Constant.Constant;
import client.CPUSchedule.DTO.Row;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ram4GB
 */
public class ExecuteCPUAlgorythm {

    private ArrayList<Row> arrayProcess;
    private String algorithmName;

    public ExecuteCPUAlgorythm(String edges, String algorithmName) throws IOException {
        applyEdgesFromJsonString(edges);
        this.algorithmName = algorithmName;
    }

    public CPUScheduler execute() {
        // Implement
        CPUScheduler algorithm = null;
        if (algorithmName == null) {
            algorithmName = Constant.defaultTypeAlgorithm;
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
            // add tất cả process trong list vào
            for (int i = 0; i < arrayProcess.size(); i++) {
                algorithm.add(arrayProcess.get(i));
            }
            // chạy hàm xử lý thuật toán
            algorithm.process();
        }
        return algorithm;
    }

    public void applyEdgesFromJsonString(String string) {
        Gson gson = new Gson();
        List list = gson.fromJson(string, ArrayList.class
        );
        ArrayList<Row> arrayProcess = new ArrayList<>();

        for (Object objEdge : list) {
            Row edge = gson.fromJson(gson.toJson(objEdge), Row.class);
            arrayProcess.add(edge);
        }

        this.arrayProcess = arrayProcess;

        System.out.println(
                this.arrayProcess);
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
