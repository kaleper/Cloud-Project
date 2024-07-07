package org.cloudbus.cloudsim;

// Allocation class represents one single pairing between a VM and a host.
public class Allocation {
    private String vmId;
    private String hostName;
    private double cost;
    private double latency;

    public Allocation(String vmId, String hostName, double cost, double latency) {
        this.vmId = vmId;
        this.hostName = hostName;
        this.cost = cost;
        this.latency = latency;
    }

    // Getters and setters (optional depending on usage)
    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostId(String hostId) {
        this.hostName = hostId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }
}

