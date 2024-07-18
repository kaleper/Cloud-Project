package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Population {
	
	private List<Chromosome> chromosomes;
	private int populationSize;
	private double minCost;
	private double maxCost;
	private int minLatency;
	private int maxLatency;
	
	public Population(int populationSize, double minCost, double maxCost, int minLatency, int maxLatency) {
		
	    this.populationSize = populationSize;
	    this.minCost = minCost;
	    this.maxCost = maxCost;
	    this.minLatency = minLatency;
	    this.maxLatency = maxLatency;
	    this.chromosomes = new ArrayList<>();
	}
	
	// Initialize population with random chromosomes
	public void initializePopulation(List<Datacenter> datacenters, List<Vm> vms) {
		
	    Random random = new Random();
	    
	    for (int i = 0; i < populationSize; i++) {
	    	
	        Chromosome chromosome = new Chromosome();
	        for (Vm vm : vms) {
	            Datacenter randomDatacenter = datacenters.get(random.nextInt(datacenters.size()));
//	            double cost = calculateCost(randomDatacenter, vm);
//	            double latency = calculateLatency(randomDatacenter, vm);
//	            chromosome.addAllocation(vm.getId(), randomDatacenter.getId(), cost, latency);
	        }
	        chromosomes.add(chromosome);
	    }
	}
	
}
