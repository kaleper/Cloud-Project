package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
	public void initializePopulation(int numberOfVms, int brokerId, int numberOfDataCenters, List<Datacenter> datacenters, List<Vm> vmlist, List<DatacenterModel> datacenterModels, List<VMModel> vmModels) {
		
	    
	    for (int i = 0; i < populationSize; i++) {
	    	
	        Chromosome chromosome = new Chromosome();
	     
	      
	        // Clears any allocations 
	        datacenters.clear();
	    	
	        
	        
	        // Shuffling creates unique combinations each time
	        
	        // Shuffle datacenters
	        Collections.shuffle(datacenterModels);
	        
			// Randomly assigns a data center model to a data center based on number of data centers specified. Gives each data center unique processing power
			setHostsFromModels(numberOfDataCenters, datacenterModels, datacenters);
			
			
			// Clears any allocations to start over 
			
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
        	chromosome.allocateAllVmsRandomly(chromosome, datacenters, vmlist);
        	
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
		    System.out.println("--------");
		    System.out.println("--------");
		    System.out.println("DATACENTER ID:");
		    System.out.println(datacenter.getId());
		    System.out.println("--------");
		    System.out.println("--------");
		    
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
		    System.out.println("VM ID: " + vm.getId());
		    System.out.println("VM Model ID: " + vm.getVmModelId());
            System.out.println("MIPS: " + vm.getMips());
            System.out.println("RAM: " + vm.getRam());
            System.out.println("Storage Size: " + vm.getSize());
            System.out.println("Bandwidth: " + vm.getBw());
            System.out.println("Number of CPUs: " + vm.getNumberOfPes());
            System.out.println("VMM: " + vm.getVmm());
            System.out.println();
		    
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
		System.out.println("CREATING TIME_ZONE:");
		System.out.println(model.getTime_zone());
		
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
}

