package org.cloudbus.cloudsim;

// Created this class to represent the varying characteristics that hosts will have. This will cause variation when allocating VMs to hosts.

public class DatacenterModel {
    private String arch;
    private String os;
    private String vmm;
    private double time_zone;
    private double cost;
    private double costPerMem;
    private double costPerStorage;
    private double costPerBw;

    // Constructor for class
    public DatacenterModel(String arch, String os, String vmm, double time_zone,
                            double cost, double costPerMem, double costPerStorage, double costPerBw) {
        this.arch = arch;
        this.os = os;
        this.vmm = vmm;
        this.time_zone = time_zone;
        this.cost = cost;
        this.costPerMem = costPerMem;
        this.costPerStorage = costPerStorage;
        this.costPerBw = costPerBw;
    }

    // Getters and setters
    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVmm() {
        return vmm;
    }

    public void setVmm(String vmm) {
        this.vmm = vmm;
    }

    public double getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(double time_zone) {
        this.time_zone = time_zone;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCostPerMem() {
        return costPerMem;
    }

    public void setCostPerMem(double costPerMem) {
        this.costPerMem = costPerMem;
    }

    public double getCostPerStorage() {
        return costPerStorage;
    }

    public void setCostPerStorage(double costPerStorage) {
        this.costPerStorage = costPerStorage;
    }

    public double getCostPerBw() {
        return costPerBw;
    }

    public void setCostPerBw(double costPerBw) {
        this.costPerBw = costPerBw;
    }
}
