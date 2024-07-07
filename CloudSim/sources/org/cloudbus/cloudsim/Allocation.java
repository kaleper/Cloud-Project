package org.cloudbus.cloudsim;

// Allocation class represents one single pairing between a VM and a host.
public class Allocation {
	private int allocationId;
    private int vmId;
    private int datacenterId;
    private double cost;
    private double latency;

    public Allocation(int allocationId, int vmId, int datacenterId, double cost, double latency) {
        this.allocationId = allocationId;
    	this.vmId = vmId;
        this.datacenterId = datacenterId;
        this.cost = cost;
        this.latency = latency;
    }

    // Getters and setters (optional depending on usage)
    public int getVmId() {
        return vmId;
    }

    public void setVmId(int vmId) {
        this.vmId = vmId;
    }

    public int getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(int datacenterId) {
        this.datacenterId = datacenterId;
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
    public int getAllocationId() {
		return allocationId;
	}

	public void setAllocationId(int allocationId) {
		this.allocationId = allocationId;
	}
    
	 // Represent object in strong form
    @Override
    public String toString() {
    	return "AllocationID: " + allocationId + "\n" +
    	           "VM Id: " + vmId + "\n" +
    	           "Datacenter ID: " + datacenterId + "\n" +
    	           "Cost: $" + String.format("%.2f", cost) + "\n" +
    	           "Latency: " + latency + "ms";
    }

	
}

