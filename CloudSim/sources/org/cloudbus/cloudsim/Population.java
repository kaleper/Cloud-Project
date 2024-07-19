package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

public class Population {
	
	// Hold set of chromosomes
	private List<Chromosome> chromosomes;
	// Specify population size
	private int populationSize;
	
	// Calculated based on vm model and datacenter specs
	private double minCost = 7.22;
	private double maxCost = 42.6119;
	private int minLatency = 1;
	private int maxLatency = 255;


	public Population(int populationSize) {
		
	    this.populationSize = populationSize;
	    this.chromosomes= new ArrayList();
	}
	
	// Initialize population with random chromosomes
	public void initializePopulation(int numberOfAllocationsPerChromosome, int numberOfVms, int brokerId, int numberOfDataCenters, List<Datacenter> datacenters, List<Vm> vmlist, List<DatacenterModel> datacenterModels, List<VMModel> vmModels) {
		
	
		
	    
	    for (int i = 0; i < populationSize; i++) {
	    	
	        Chromosome chromosome = new Chromosome();
	     
	      
	        // Clears any previous datacenters for a fresh chromosome 
	        datacenters.clear();
	    	
	        
	        
	        // Shuffling creates unique combinations each time
	        
	        // Shuffle datacenters
	        Collections.shuffle(datacenterModels);
	        
			// Randomly assigns a data center model to a data center based on number of data centers specified. Gives each data center unique processing power
			setHostsFromModels(numberOfDataCenters, datacenterModels, datacenters);
			
			
			// Clears any allocations to start over for a fresh chromosome 
			
			vmlist.clear();
	        // Shuffle vmlist
	        Collections.shuffle(vmModels);
	    	// Randomly assigns a vm model to a vm based on number of vms specified. Gives each vm unique processing needs
			setVmsFromModels(numberOfVms, vmModels,brokerId, vmlist);
			
			// Simulate assigning VMs to a user by assigning them a timezone where a user would be (Replicates different geographical locations)
			assignVmsToUser(vmlist);
	        
	        
	        // Shuffle datacenters
	        Collections.shuffle(datacenters);
	        // Shuffle vmlist
	        Collections.shuffle(vmlist);
	        
	        
	        
	        //Allocate chromosomes randomly.
        	chromosome.allocateVmsRandomly(numberOfAllocationsPerChromosome, datacenters, vmlist);
        	
        	// Calculate fitness for the chromosome
            double chromosomeFitness = chromosome.calculateChromosomeFitness(minCost, maxCost, minLatency, maxLatency);
            chromosome.setFitness(chromosomeFitness);
	        
	        chromosomes.add(chromosome);
	    }
	}

	public List<Chromosome> getChromosomes() {
		return chromosomes;
	}

	public void setChromosomes(List<Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
	
public static void setHostsFromModels(int numberOfDataCenters, List<DatacenterModel> datacenterModels, List <Datacenter> datacenters) {
		
		/* Loop to create however many data centers I specify.
		 * This randomization currently assumes that more than one data center can share the same types of models. Now when cost is calculated, 
		 * the total costs of all VMs are different everytime. */
		for (int i = 0; i < numberOfDataCenters; i++) {
			
		    // Generate a random index based on number of models available
		    int randomDataCenterModelIndex = (int) (Math.random() * datacenterModels.size()); 
		    
		    // Get the model corresponding to the random index
		    DatacenterModel randomModel = datacenterModels.get(randomDataCenterModelIndex); 
		    
		    // Create a Datacenter using the random model
		    Datacenter datacenter = createDatacenter("Datacenter-" + i, randomModel);
//		    System.out.println("--------");
//		    System.out.println("--------");
//		    System.out.println("DATACENTER ID:");
//		    System.out.println(datacenter.getId());
//		    System.out.println("--------");
//		    System.out.println("--------");
		    
		    // Add the datacenter to the list
		    datacenters.add(datacenter);
		}
	}
	
	public static void setVmsFromModels(int numberOfVms, List<VMModel> vmModels, int brokerId, List <Vm> vmlist) {
		
		// Assign VM models to VM
		for (int i= 0; i < numberOfVms; i++) {
			
			// Generate a random index based on number of models available
		    int randomVmModelIndex = (int) (Math.random() * (vmModels.size()));
		    
		    // Get the model corresponding to the random index
		    VMModel vmModel = vmModels.get(randomVmModelIndex); 
			
		    Vm vm = new Vm(i,vmModel.getVmModelId(), brokerId, vmModel.getMips(),
                    vmModel.getPesNumber(), vmModel.getRam(),
                    vmModel.getBw(), vmModel.getSize(),
                    vmModel.getVmm(), new CloudletSchedulerTimeShared());
		    
		    /** Test 0utput to be deleted later **/
//		    System.out.println("VM ID: " + vm.getId());
//		    System.out.println("VM Model ID: " + vm.getVmModelId());
//            System.out.println("MIPS: " + vm.getMips());
//            System.out.println("RAM: " + vm.getRam());
//            System.out.println("Storage Size: " + vm.getSize());
//            System.out.println("Bandwidth: " + vm.getBw());
//            System.out.println("Number of CPUs: " + vm.getNumberOfPes());
//            System.out.println("VMM: " + vm.getVmm());
//            System.out.println();
		    
			// Add each VM to the VMlist
			vmlist.add(vm);
		}
	}
	
	// Simulate assigning VMs to a user by assigning them a timezone where a user would be (Replicates different geographical locations)
	public static void assignVmsToUser(List <Vm> vmlist){
	
		for (int i = 0; i < vmlist.size(); i++) {
			
			// Generate a random offset between 1 and 12
			int randomOffset = (int) (Math.random() * 12) + 1;
			vmlist.get(i).setTime_zone(randomOffset);
		}
		
	}
	private static Datacenter createDatacenter(String name, DatacenterModel model){

		// Store hosts in list
		List<Host> hostList = new ArrayList<Host>();

		// Each machine in my example will only contain one pe / core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		// Create and add pe's to list. Stores PE Id and MIPS rating
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); 

		// Create Host with its id and list of PEs and add them to the list of machines
		int hostId=0;
		// Memory in MB
		int ram = 2048; 
		// Storage
		long storage = 1000000; 
		int bw = 10000;

		// One VM per PE using the VmScheduleSpaceShared policy.
		hostList.add(
				
			new Host(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerSpaceShared(peList)
			)
		); 

		// DatacenterCharacteristics makes an instance of the properties of a datacenter.
		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
	        model.getArch(), model.getOs(), model.getVmm(), hostList,
	        model.getTime_zone(), model.getCost(), model.getCostPerMem(),
	        model.getCostPerStorage(), model.getCostPerBw());
		
		// Assigns timezone to datacenter
//		System.out.println("CREATING TIME_ZONE:");
		model.getTime_zone();
		
		LinkedList<Storage> storageList = new LinkedList<Storage>();	

		// Create a datacenter object using the characteristics and the allocation policy. The allocation policy is actually mainly implemented here, with the allocaiton class just assigning the Pes.
		Datacenter datacenter = null;
		try {
			int datacenterId = model.getId();
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
			datacenter.setId(datacenterId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return datacenter;
	}
	
	public double calculatePopulationFitness(double minCost, double maxCost, int minLatency, int maxLatency) {
		
		double populationFitness = 0;
		
		for (int i = 0; i < this.chromosomes.size(); i++) {
			
			populationFitness += this.chromosomes.get(i).calculateChromosomeFitness(minCost, maxCost, minLatency, maxLatency);
			
		}
		
		populationFitness /= this.chromosomes.size();
		return populationFitness;
	}
	
	
	public void doGeneration() {
		
		List<Chromosome> newGeneration = new ArrayList<>();
		
		// Generate new offspring until it has the same population size
		for (int i = 0; i < populationSize; i++) {
			
			// Get parents 
			Chromosome mother = tournamentSelection();
			Chromosome father = tournamentSelection();
			
			// Make sure mother and father are not the same to prevent identicalness
			while (mother == father) {
				father = tournamentSelection();
			}
			
			// Do the crossover
		}
	
	}
	
	// Selecting parents part of MOGA
	public Chromosome tournamentSelection() {
		
		// Selects a random chromosome 1 from current population
		int randomIndex = (int) (Math.random() * chromosomes.size());
		Chromosome candidate1 = chromosomes.get(randomIndex);
		
		// Selects a random chromosome 2 from current population
		int randomIndex2 = (int) (Math.random() * chromosomes.size());
		Chromosome candidate2 = chromosomes.get(randomIndex2);
		
		// Ensures that two candidates are not the same, otherwise there will be problems of identicalness when crossing over.
		while (candidate1 == candidate2) {
			candidate2 = chromosomes.get((int) (Math.random() * chromosomes.size()));
		}
		
		// Find the more fit candidate or chromosome
		if (candidate1.getFitness() > candidate2.getFitness()) {
			return candidate1;
		} else {
			return candidate2;
		}
	}
	
	public Chromosome crossover(Chromosome parent1, Chromosome parent2) {
		
		 // New offspring created 
		 Chromosome offspring = new Chromosome();
		 
		 // Gets the chromosome 'genes' or allocations
		 List<Allocation> parent1Allocations = parent1.getAllocations();
	     List<Allocation> parent2Allocations = parent2.getAllocations();
	     
	     // Test print allocations
	     System.out.println(" ");
	     System.out.println("Parent 1 allocations:");
	     System.out.println(parent1Allocations);
	     System.out.println(" ");
	     System.out.println("Parent 2 allocations:");
	     System.out.println(parent2Allocations);
	     
	     int crossoverPoint = (int) (Math.random() * parent1Allocations.size()); // Random crossover point

	     
	     System.out.println("Crossover point:" + crossoverPoint);

	     // Sets to track used VM and Datacenter IDs. Hashsets useful to prevent any dupes
	     Set<Integer> usedVMs = new HashSet<>();
	     Set<Integer> usedDatacenters = new HashSet<>();

	     // Add allocations from parent1 up to the crossover point
	     for (int i = 0; i < crossoverPoint; i++) {
	         Allocation allocation = parent1Allocations.get(i);
	         
	         // Checks to make sure that the VM id and the data center ID aren't already being used 
	         if (!usedVMs.contains(allocation.getVmId()) && !usedDatacenters.contains(allocation.getDatacenterId())) {
	        	 // Add the gene to the offspring if not
	             offspring.addAllocation(allocation);
	             
	             // Marks both the VM and the data center as used now
	             usedVMs.add(allocation.getVmId());
	             usedDatacenters.add(allocation.getDatacenterId());
	         }
	     }

	     // Add allocations from parent2 after the crossover point
	     for (int i = crossoverPoint; i < parent2Allocations.size(); i++) {
	         Allocation allocation = parent2Allocations.get(i);
	         // Check to make sure that the VMs and the datacenters aren't already being used.
	         
	         if (!usedVMs.contains(allocation.getVmId()) && !usedDatacenters.contains(allocation.getDatacenterId())) {
	        	 // Add the gene to new offspring if not
	             offspring.addAllocation(allocation);
	             usedVMs.add(allocation.getVmId());
	             usedDatacenters.add(allocation.getDatacenterId());
	         }
	     }
	     System.out.println(" ");
	     System.out.println(" ");
	     System.out.println("Offspring allocations:");
	     System.out.println(offspring.getAllocations());

	     return offspring;
	   
	}

	
}

