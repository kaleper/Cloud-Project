package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;

// Chromosome class represents one whole solution, where all vms are allocated to hosts.
public class Chromosome {
	
    private List<Allocation> allocations;
    private double totalCost;
    private double totalLatency;
    
    public Chromosome() {
        this.allocations = new ArrayList<>();
   
    }

    public void addAllocation(int allocationId, int vmId, int datacenterId, double cost, double latency) {
        Allocation allocation = new Allocation(allocationId, vmId, datacenterId, cost, latency);
        allocations.add(allocation);
    }
    
    public void addAllocation(Allocation allocation) {
       allocations.add(allocation);
    }
    
    // Assess average cost for each population
    public double computeAverageCost() {
        if (allocations.isEmpty()) {
            return 0;
        }
        return totalCost / allocations.size();
    }

    // Assess average latency for each population
    public double computeAverageLatency() {
        if (allocations.isEmpty()) {
            return 0;
        }
        return totalLatency / allocations.size();
    }

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public double getTotalLatency() {
		return totalLatency;
	}

	public void setTotalLatency(double totalLatency) {
		this.totalLatency = totalLatency;
	}
	
	public void printAllAllocations() {
	    for (Allocation allocation : allocations) {
	        System.out.println("Allocation ID: " + allocation.getAllocationId());
	        System.out.println("VM Id: " + allocation.getVmId());
	        System.out.println("Datacenter ID: " + allocation.getDatacenterId());
	        System.out.println("Cost: $" + String.format("%.2f", allocation.getCost()));
	        System.out.println("Latency: " + String.format("%.2f", allocation.getLatency()) + " ms\n");
	    }
	}
	
 // Method to get allocation by index
    public Allocation getAllocation(int index) {
        if (index >= 0 && index < allocations.size()) {
            return allocations.get(index);
        }
        // if Invalid index
        return null; 
    }
    
 public double calculateFitness (double minCost, double maxCost, int minLatency, int maxLatency, Allocation allocation) {
    	
    	// Can change weightings depending on which one I favour more
    	// Cost weighted slightly more than latency in my example
    	double costWeight = 0.6; 
    	// Latency weighted less
        double latencyWeight = 0.4; 

        // Reversed normalization to get a cost or latency between 0-1, with closer to 1 being better
        double normalizedCost = (maxCost - allocation.getCost()) / (maxCost - minCost);
        double normalizedLatency = (maxLatency - allocation.getLatency()) / (maxLatency - minLatency);

        // Calculate weighted sum
        double fitness = costWeight * normalizedCost + latencyWeight * normalizedLatency;

        return fitness;
    	
    }
    
}