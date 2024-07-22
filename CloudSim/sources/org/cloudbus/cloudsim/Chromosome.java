package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Chromosome class represents one whole solution, where all vms are allocated to hosts.
public class Chromosome {
	
    private List<Allocation> allocations;
    private double totalCost;
    private double totalLatency;
    // Stores fitness of entire chromosomes
    private double fitness;
    
    public Chromosome() {
        this.allocations = new ArrayList<>();
        //fitness is 0 initially
        this.fitness= 0.0;
        this.totalLatency= 0.0;
        this.totalCost = 0.0;
   
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
		return this.totalCost;
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
	
	
	public List<Allocation> getAllocations() {
		return allocations;
	}

	public void setAllocations(List<Allocation> allocations) {
		this.allocations = allocations;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
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
    
 
 
//Obtains costs of single VM based on it's specifications and what the datacenter charges.
	public double getCostOfSingleVM(Datacenter datacenter, Vm vm) {
		
		// Min latency = 1ms, Max latency = 255ms.
		
		// Most expensive VM(Model4): MIPS: 650 Storage: 18000. RAM: 2048. Bandwidth: 2000 
		// Least expensive VM(Model1): MIPS: 250 Storage: 10000. RAM: 512. Bandwidth: 1000
		// MOST Expensive Datacenter(Model2): Cost(GB per month): 0.09, costPerMem(GB per month): 0.025, costPerStorage: 0.0032 , costPerBw: 0.0015
			// Most expensive total = 42.6119
		// Least Expensive Datacenter (Model3): Cost(GB per month): 0.04, costperMem(GB per month): 0.01, costPerStorage: 0.001, costPerBw: 0.0005
			// Least expensive total = 7.222                                             
		
		// Datacenter pricing rates
	
	
		// Assume dollars per GB / month
		double memoryRate = datacenter.getCharacteristics().getCostPerMem();
		
		// Assume dollars per GB / month
		double storageRate = datacenter.getCharacteristics().getCostPerStorage();
		// Assume dollars per GB
		double bwRate = datacenter.getCharacteristics().getCostPerBw();
		// Equal to cost for processing, dollars per second
		double processingRate = datacenter.getCharacteristics().getCostPerSecond();
		
	
		// VM characteristics
		//Assume megabytes per second
		double vmBw = vm.getBw();
		// MB
		double vmStorage = vm.getSize();
		// Dollars per MB
		double vmMemory = vm.getRam();
		//***DON'T NEED TO USE?? MIPS IS STANDARD, plan to increase price based on MIPS, or faster computers
		double vmProcessing = vm.getMips();
		
		// Convert the MB to GB
		double vmBwGB = vmBw / 1024; 
		// MB
		double vmStorageGB = vmStorage / 1024;
		// Dollars per MB
		double vmMemoryGB = vmMemory / 1024;
		//***DON'T NEED TO USE?? MIPS IS STANDARD, plan to increase price based on MIPS, or faster computers
		double vmProcessingGB = vm.getMips();
		
		// Hours in a month to get usage from user
		int usageHrsPerMonth = 720;
	
		// 1000 MIPS per processor, 
		// https://learn.microsoft.com/en-us/azure/virtual-machines/workloads/mainframe-rehosting/concepts/mainframe-compute-azure
		double processingCost = processingRate * (vmProcessingGB/1000) * usageHrsPerMonth;

		double memoryCost = memoryRate * vmMemoryGB;
		double storageCost = storageRate * vmStorageGB;
		
		// Usually free from big data centers like Microsoft for some? Rate set very low
		//https://azure.microsoft.com/en-us/pricing/details/bandwidth/
		// Most cloud providers charge gb per month
		double bwCost = bwRate * vmBwGB;
		
		double totalCost = memoryCost + storageCost + processingCost + bwCost;
		// Test case using current numbers come out to $29.72, closer to real world pricing compared to previous pricing
	
		return totalCost;	
	}
	
	 // Latency in MS
	 public double calculateLatencyCost(Datacenter datacenter, Vm vm) {
			
		// Starting point for latency (between 1-15ms); even if the host and the vm are in the same time zone, expected to have some delay
		double latency = 1 + Math.random() * 14;
		double datacenterTimezone = datacenter.getCharacteristics().getTimeZone();
		double vmTimezone = vm.getTime_zone();
		
		// Multiple of between 15-20 to account for randomness and other variables
		latency +=  Math.abs(datacenterTimezone - vmTimezone) * ((int) (Math.random() * 6) + 15);
		
		return latency;
				
		}


	//Iterate through all VMS to allocate to hosts randomly
	 public void allocateVmsRandomly(int numberOfAllocations, List<Datacenter> datacenters, List<Vm> vmlist) {
		    Random rand = new Random();
		    for (int i = 0; i < numberOfAllocations; i++) {
		        Vm vm = vmlist.get(rand.nextInt(vmlist.size())); // Select random VM
		        Datacenter datacenter = datacenters.get(rand.nextInt(datacenters.size())); // Select random Datacenter
		        
		        double vmCost = getCostOfSingleVM(datacenter, vm);
		        double vmLatency = calculateLatencyCost(datacenter, vm);
		        
		        Allocation allocation = new Allocation(i, vm.getId(), datacenter.getId(), vmCost, vmLatency);
		        addAllocation(allocation);
		        
		        // Update total cost and latency
		        totalCost += vmCost;
//		        System.out.println("Total cost is:" + totalCost);
//		        System.out.println("Current totalCost in allocateVmsRandomly: " + totalCost);
		        totalLatency += vmLatency;
//		        System.out.println("Total latency is:" + totalLatency);
		    }
		 // Set the total cost and latency for the chromosome
		    setTotalCost(totalCost);
		    setTotalLatency(totalLatency);
		}
	
	// Currently, obtains cost for vms that are allocated sequentially to available hosts or data centers
	public double getTotalCostOfAllVMs(List<Datacenter> datacenters, List<Vm> vmlist) {
		
	    double totalCost = 0.0;

	    // Iterates through until the vms are all allocated or there are not enough hosts
	    for (int i = 0; i < vmlist.size() && i<datacenters.size(); i++) {
	    	
	        Vm vm = vmlist.get(i);
	        Datacenter datacenter = datacenters.get(i);

	        // Calculate the cost of assigning the current VM to the current datacenter
	        double vmCost = getCostOfSingleVM(datacenter, vm);
	        totalCost += vmCost;
	    }
		return totalCost;
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
    
 public double calculateChromosomeFitness(double minCost, double maxCost, int minLatency, int maxLatency) {
	 
	 double fitness = 0;
	 
	 for (int i = 0; i < this.allocations.size(); i++) {
		 	// Can change weightings depending on which one I favour more
	    	// Cost weighted slightly more than latency in my example
	    	double costWeight = 0.6; 
	    	// Latency weighted less
	        double latencyWeight = 0.4; 
	        
	    	// Reversed normalization to get a cost or latency between 0-1, with closer to 1 being better
	        double normalizedCost = (maxCost - this.getAllocation(i).getCost()) / (maxCost - minCost);
	        double normalizedLatency = (maxLatency - this.getAllocation(i).getLatency()) / (maxLatency - minLatency);
	        // Keeps running total of fitness
	        fitness += (costWeight * normalizedCost + latencyWeight * normalizedLatency);
	 }
	 
	 // Divides fitness by size of chromosome to get average
	 fitness/= this.allocations.size();
	 
	 return fitness;
	 
 }

public void clearAllocations() {
	this.allocations.clear();
	
}
}