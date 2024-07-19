package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Allocation;
import org.cloudbus.cloudsim.Chromosome;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.DatacenterModel;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Population;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.VMModel;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmAllocationPolicyImplementation;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.HostList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/* Running a MOGA alogrithm to optimize VM allocation to hosts for costs and latency */

public class CloudSimAlgorithm {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/* Program runs off main method */
	public static void main(String[] args) {

		Log.printLine("Starting CloudSimAlgorithm...");

		try {

			// First step: Initialize the CloudSim package.
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			// Initialize the GridSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters with random data center models.

			// I'll define how many data centers are created iteratively
			int numberOfDataCenters = 5;
			
			// This can specify how many genes a chromosome can have (or how many different vm - model pairings.
			int numberOfAllocationsPerChromosome = 20;

			List<Datacenter> datacenters = new ArrayList<>();

			// First model has Adjusted Datacenter prices to more closely reflect real world
			// pricing. Prices are in dollars
			// Params: Archetype, OS, VMM, Time_zone(of resource), Cost(GB per month),
			// costPerMem(GB per month), costPerStorage(GB per month), costPerBw

			// MOST Expensive Datacenter(Model2): Cost(GB per month): 0.09, costPerMem(GB
			// per month): 0.025, costPerStorage: 0.0032 , costPerBw: 0.0015
			// Least Expensive Datacenter (Model3): Cost(GB per month): 0.04, costperMem(GB
			// per month): 0.01, costPerStorage: 0.001, costPerBw: 0.0005

			DatacenterModel datacenterModel1 = new DatacenterModel(0, "x86", "Linux", "Xen", 12.0, 0.06, 0.013, 0.02,
					0.01);
			DatacenterModel datacenterModel2 = new DatacenterModel(1, "x86", "Linux", "Xen", 0.0, 0.09, 0.025, 0.032,
					0.015);
			DatacenterModel datacenterModel3 = new DatacenterModel(2, "x86", "Linux", "Xen", 3.0, 0.04, 0.01, 0.01,
					0.005);
			DatacenterModel datacenterModel4 = new DatacenterModel(3, "x86", "Linux", "Xen", 0, 0.05, 0.022, 0.024,
					0.010);
			DatacenterModel datacenterModel5 = new DatacenterModel(4, "x86", "Linux", "Xen", 6.0, 0.07, 0.018, 0.029,
					0.008);

			// Put all the different models in a list
			List<DatacenterModel> datacenterModels = Arrays.asList(datacenterModel1, datacenterModel2, datacenterModel3,
					datacenterModel4, datacenterModel5);

			// Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create virtual machines with random vm models.

			// Different models with different specifications.
			// These models will be randomized into actual instances of VMs.
			// Params id, MIPS, image size (MB), memory (MB), Bandwith, # of cpus, VMM name
			// Most expensive VM(Model4): MIPS: 650 Storage: 18000. RAM: 2048. Bandwidth:
			// 2000
			// Least expensive VM(Model1): MIPS: 250 Storage: 10000. RAM: 512. Bandwidth:
			// 1000

			VMModel vmModel1 = new VMModel(0, 250, 10000, 512, 1000, 1, "Xen");
			VMModel vmModel2 = new VMModel(1, 500, 15000, 1024, 1500, 1, "VirtualBox");
			VMModel vmModel3 = new VMModel(2, 350, 12000, 768, 1200, 1, "VMPower");
			VMModel vmModel4 = new VMModel(3, 650, 18000, 2048, 2000, 1, "FastVM");
			VMModel vmModel5 = new VMModel(4, 350, 13500, 2048, 1600, 1, "Mirror");

			// Put all the different models in a list
			List<VMModel> vmModels = Arrays.asList(vmModel1, vmModel2, vmModel3, vmModel4, vmModel5);

			// Will hold all Vm instances with a model associated to it
			List<Vm> vmlist = new ArrayList<Vm>();

			// Use to define how many VMs will be used
			int numberOfVms = 5;

			// Population instantiation
			int populationSize = 10;
			Population population1 = new Population(populationSize);
			

			population1.initializePopulation(numberOfAllocationsPerChromosome, numberOfVms, brokerId, numberOfDataCenters, datacenters, vmlist,
					datacenterModels, vmModels);
			
			
			/** TESTING OUT SELECTION **/
			
			 // Print the initial population and their fitness
	        System.out.println("Initial population:");
	        for (Chromosome chromosome : population1.getChromosomes()) {
	            System.out.println("Chromosome fitness: " + chromosome.getFitness());
	        }

	        // Test tournament selection
	        System.out.println("\nSelected parents:");
	        System.out.println("Tournament Selection Results:");
	        
//	        for (int i = 0; i < 5; i++) { // Select 5 pairs of parents
	        
	        	
	            Chromosome parent1 = population1.tournamentSelection();
	            Chromosome parent2 = population1.tournamentSelection();

	            // Ensure parent1 and parent2 are not the same
	            while (parent1 == parent2) {
	                parent2 = population1.tournamentSelection();
	            }

	            System.out.println("Parent 1 fitness: " + parent1.getFitness());
	            System.out.println("Parent 2 fitness: " + parent2.getFitness());
	            System.out.println("---");
	            
	            Chromosome offspring = population1.crossover(parent1, parent2);
//	            
//	            System.out.println(" ");
//	            // Print offspring details
//	            System.out.println("Offspring fitness: " + offspring.getFitness());
//	            System.out.println("Offspring Allocations:");
//	            System.out.println(offspring.getAllocations());
//	        }
	     

	
	        
	          
	           
			
			/** Testing populations **/
			System.out.println(" ");
			System.out.println("****TESTING POPULATIONS****");
			System.out.println(" ");
//			 // Print out or manipulate the chromosomes as needed
//	        for (int i = 0; i < population1.getChromosomes().size(); i++) {
//	            Chromosome currentChromosome = population1.getChromosomes().get(i);
//;	            System.out.println("Chromosome " + i + ":");
//	            currentChromosome.printAllAllocations(); // Example method
//	            System.out.println("-----------------------");
//	            System.out.println("Chromosome " + i + " fitness: ");
//	            System.out.println(currentChromosome.calculateChromosomeFitness(7.22, 42.6119, 1, 255));
//	            System.out.println("-----------------------");
//	        }
			System.out.println("Population fitness:");
			System.out.println(population1.calculatePopulationFitness(7.22, 42.6119, 1, 255));
			System.out.println(" ");

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			// Fifth step: Create CloudLets
			cloudletList = new ArrayList<Cloudlet>();

			// Cloudlet properties
			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			int numberOfCloudlets = 5;

			// Create cloudlets
			generateCloudlets(id, length, fileSize, outputSize, utilizationModel, numberOfCloudlets, brokerId);

			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);

			// Bind cloudlets to a VM
			for (int i = 0; i < numberOfCloudlets; i++) {
				broker.bindCloudletToVm(cloudletList.get(i).getCloudletId(), vmlist.get(i).getId());
			}

			// Sixth step: Starts the simulation
			//CloudSim.startSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			//CloudSim.stopSimulation();

//			printCloudletList(newList);

			//Log.printLine("CloudSimAlgorithm finished!");

			/** TEST CASES **/

//			Chromosome chromosome = new Chromosome();

			// COST OF ALL VMS
//			System.out.println("-----------------------");
//			System.out.println("COST OF ALL VMS:");
//			System.out.println("$" + chromosome.getTotalCostOfAllVMs(datacenters, vmlist) + " /month");
//			System.out.println("-----------------------");
//			System.out.println("LATENCY COST FOR THE FIRST DATA CENTER");
//			System.out.println(chromosome.calculateLatencyCost(datacenters.getFirst(), vmlist.getFirst()) + " ms");
//
//			System.out.println("-----------------------");
//			System.out.println("CHROMOSOME: ALL ALLOCATION INFORMATION:");
//
//			chromosome.allocateAllVmsRandomly(chromosome, datacenters, vmlist);
//			chromosome.printAllAllocations();
//
//			System.out.println("-----------------------");
//			System.out.println("FITNESS CALCULATION FIRST ALLOCATION:");

			// Min latency = 1ms, Max latency = 255ms.
			// Min latency = 1ms, Max latency = 255ms.

			// Most expensive VM(Model4): MIPS: 650 Storage: 18000. RAM: 2048. Bandwidth:
			// 2000
			// Least expensive VM(Model1): MIPS: 250 Storage: 10000. RAM: 512. Bandwidth:
			// 1000

			// MOST Expensive Datacenter(Model2): Cost(GB per month): 0.09, costPerMem(GB
			// per month): 0.025, costPerStorage: 0.0032 , costPerBw: 0.0015
			// Most expensive cost total = 42.6119
			// Least Expensive Datacenter (Model3): Cost(GB per month): 0.04, costperMem(GB
			// per month): 0.01, costPerStorage: 0.001, costPerBw: 0.0005
			// Least expensive cost total = 7.222
//			double fitness = chromosome.calculateFitness(7.222, 42.6119, 1, 255, chromosome.getAllocation(0));
//			System.out.println(fitness);
//
//			System.out.println("-----------------------");
//			System.out.println("FITNESS CALCULATION ALL ALLOCATIONS");
//			// (7.222, 42.6119, 1, 255, chromosome.getAllocation(2));
//			double chromosomeFitness = chromosome.calculateChromosomeFitness(7.222, 42.6119, 1, 255);
//			System.out.println(chromosomeFitness);
			
		    

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}


	/* Potentially modify */
	private static DatacenterBroker createBroker() {

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * 
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");)
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}

	/** Migrate to population class **/
//	public static void setHostsFromModels(int numberOfDataCenters, List<DatacenterModel> datacenterModels, List <Datacenter> datacenters) {
//		
//		/* Loop to create however many data centers I specify.
//		 * This randomization currently assumes that more than one data center can share the same types of models. Now when cost is calculated, 
//		 * the total costs of all VMs are different everytime. */
//		for (int i = 0; i < numberOfDataCenters; i++) {
//			
//		    // Generate a random index based on number of models available
//		    int randomDataCenterModelIndex = (int) (Math.random() * datacenterModels.size()); 
//		    
//		    // Get the model corresponding to the random index
//		    DatacenterModel randomModel = datacenterModels.get(randomDataCenterModelIndex); 
//		    
//		    // Create a Datacenter using the random model
//		    Datacenter datacenter = createDatacenter("Datacenter-" + i, randomModel);
//		    
//		    // Add the datacenter to the list
//		    datacenters.add(datacenter);
//		}
//	}
//	
//	public static void setVmsFromModels(int numberOfVms, List<VMModel> vmModels, int brokerId, List <Vm> vmlist) {
//		
//		// Assign VM models to VM
//		for (int i= 0; i < numberOfVms; i++) {
//			
//			// Generate a random index based on number of models available
//		    int randomVmModelIndex = (int) (Math.random() * (vmModels.size()));
//		    
//		    // Get the model corresponding to the random index
//		    VMModel vmModel = vmModels.get(randomVmModelIndex); 
//			
//		    Vm vm = new Vm(i,vmModel.getVmModelId(), brokerId, vmModel.getMips(),
//                    vmModel.getPesNumber(), vmModel.getRam(),
//                    vmModel.getBw(), vmModel.getSize(),
//                    vmModel.getVmm(), new CloudletSchedulerTimeShared());
//		    
//		    /** Test 0utput to be deleted later **/
//		    System.out.println("VM ID: " + vm.getId());
//		    System.out.println("VM Model ID: " + vm.getVmModelId());
//            System.out.println("MIPS: " + vm.getMips());
//            System.out.println("RAM: " + vm.getRam());
//            System.out.println("Storage Size: " + vm.getSize());
//            System.out.println("Bandwidth: " + vm.getBw());
//            System.out.println("Number of CPUs: " + vm.getNumberOfPes());
//            System.out.println("VMM: " + vm.getVmm());
//            System.out.println();
//		    
//			// Add each VM to the VMlist
//			vmlist.add(vm);
//		}
//	}
//	
//	// Simulate assigning VMs to a user by assigning them a timezone where a user would be (Replicates different geographical locations)
//	public static void assignVmsToUser(List <Vm> vmlist){
//	
//	for (int i = 0; i < vmlist.size(); i++) {
//		
//		// Generate a random offset between 1 and 12
//		int randomOffset = (int) (Math.random() * 12) + 1;
//		vmlist.get(i).setTime_zone(randomOffset);
//	}
//}

	// Generates cloudlets
	public static void generateCloudlets(int id, long length, long fileSize, long outputSize,
			UtilizationModel utilizationModel, int numberOfCloudlets, int brokerId) {
		for (int i = 0; i < numberOfCloudlets; i++) {

			// 1 IS PES NUMBER; I'm assuming all CPUs have 1 core. Change to pesNumber = ;
			// if needed.
			Cloudlet cloudlet = new Cloudlet(id, length, 1, fileSize, outputSize, utilizationModel, utilizationModel,
					utilizationModel);
			cloudlet.setUserId(brokerId);
			id++;

			// Add each cloudlet to cloudletList
			cloudletList.add(cloudlet);
		}
	}
	
	
}