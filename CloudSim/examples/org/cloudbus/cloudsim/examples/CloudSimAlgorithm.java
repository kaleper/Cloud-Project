/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009, The University of Melbourne, Australia
 */

/* TODO: 1. Currently, all specifications are made to be uniform in specification. Randomization is needed to initial specifications before being fed into loops to initialize; 
 * 			Randomization criteria to be entered to make characteristics heterogeneous
 * 
 */

package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the GridSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters with random data center models.
			
			// I'll define how many data centers are created iteratively
			int numberOfDataCenters = 5;
			
			List <Datacenter> datacenters = new ArrayList<>();
			
			// First model has Adjusted Datacenter prices to more closely reflect real world pricing. Prices are in dollars
			// Params: Archetype, OS, VMM, Time_zone(of resource), Cost(GB per month), costPerMem(GB per month), costPerStorage(GB per month), costPerBw
	
			
			// MOST Expensive Datacenter(Model2): Cost(GB per month): 0.09, costPerMem(GB per month): 0.025, costPerStorage: 0.0032 , costPerBw: 0.0015
			// Least Expensive Datacenter (Model3): Cost(GB per month): 0.04, costperMem(GB per month): 0.01, costPerStorage: 0.001, costPerBw: 0.0005
			
			DatacenterModel datacenterModel1 = new DatacenterModel("x86", "Linux", "Xen", 12.0, 0.06, 0.013, 0.02, 0.01);
			DatacenterModel datacenterModel2 = new DatacenterModel("x86", "Linux", "Xen", 0.0, 0.09, 0.025, 0.032, 0.015);
			DatacenterModel datacenterModel3 = new DatacenterModel("x86", "Linux", "Xen", 3.0, 0.04, 0.01, 0.01, 0.005);
			DatacenterModel datacenterModel4 = new DatacenterModel("x86", "Linux", "Xen", 0, 0.05, 0.022, 0.024, 0.010);
			DatacenterModel datacenterModel5 = new DatacenterModel("x86", "Linux", "Xen", 6.0, 0.07, 0.018, 0.029, 0.008);

			// Put all the different models in a list
			List<DatacenterModel> datacenterModels = Arrays.asList(datacenterModel1, datacenterModel2, datacenterModel3, datacenterModel4, datacenterModel5);

			// Randomly assigns a data center model to a data center based on number of data centers specified. Gives each data center unique processing power
			setHostsFromModels(numberOfDataCenters, datacenterModels, datacenters);

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create virtual machines with random vm models.
			
			// Different models with different specifications. 
			// These models will be randomized into actual instances of VMs.
			// Params id, MIPS, image size (MB), memory (MB), Bandwith, # of cpus, VMM name
			// Most expensive VM(Model4): MIPS: 650 Storage: 18000. RAM: 2048. Bandwidth: 2000 
			// Least expensive VM(Model1): MIPS: 250 Storage: 10000. RAM: 512. Bandwidth: 1000
			
			VMModel vmModel1 = new VMModel(0, 250, 10000, 512, 1000, 1, "Xen");
			VMModel vmModel2 = new VMModel(1, 500, 15000, 1024, 1500, 1, "VirtualBox");
			VMModel vmModel3 = new VMModel(2, 350, 12000, 768, 1200, 1, "VMPower");
			VMModel vmModel4 = new VMModel(3, 650, 18000, 2048, 2000, 1, "FastVM");
			VMModel vmModel5 = new VMModel(4, 350, 13500, 2048, 1600, 1, "Mirror");

			// Put all the different models in a list
			List<VMModel> vmModels = Arrays.asList(vmModel1, vmModel2, vmModel3, vmModel4, vmModel5);
			

			// Will hold all Vm instances with a model associated to it
			List <Vm> vmlist = new ArrayList<Vm>();

			// Use to define how many VMs will be used 
			int numberOfVms = 5;		
			
			// Randomly assigns a vm model to a vm based on number of vms specified. Gives each vm unique processing needs
			setVmsFromModels(numberOfVms, vmModels,brokerId, vmlist);
			
			// Simulate assigning VMs to a user by assigning them a timezone where a user would be (Replicates different geographical locations)
			assignVmsToUser(vmlist);

			//submit vm list to the broker
			broker.submitVmList(vmlist);


			//Fifth step: Create CloudLets
			cloudletList = new ArrayList<Cloudlet>();

			//Cloudlet properties
			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

	
			int numberOfCloudlets = 5;

			// Create cloudlets 
			generateCloudlets(id,length,fileSize, outputSize, utilizationModel, numberOfCloudlets, brokerId);
		
			//submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);


			
			// Bind cloudlets to a VM
			for (int i= 0; i < numberOfCloudlets; i++) {
				broker.bindCloudletToVm(cloudletList.get(i).getCloudletId(),vmlist.get(i).getId());
			}

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();


			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

        	printCloudletList(newList);

			Log.printLine("CloudSimAlgorithm finished!");
			
			/** TEST CASE **/
			// COST OF ALL VMS
			System.out.println("-----------------------");
			System.out.println("COST OF ALL VMS:");
			System.out.println("$" + getTotalCostOfAllVMs(datacenters, vmlist) + " /month");
			System.out.println("-----------------------");
			System.out.println("LATENCY COST FOR THE FIRST DATA CENTER");
			System.out.println(calculateLatencyCost(datacenters.getFirst(), vmlist.getFirst()) + " ms");
			
	
			System.out.println("-----------------------");
			System.out.println("CHROMOSOME: ALL ALLOCATION INFORMATION:");
			Chromosome chromosome = new Chromosome();
			allocateAllVmsRandomly(chromosome, datacenters, vmlist);
			chromosome.printAllAllocations();
			
			System.out.println("-----------------------");
			System.out.println("FITNESS CALCULATION FIRST ALLOCATION:");
			
			// Min latency = 1ms, Max latency = 255ms.
			// Min latency = 1ms, Max latency = 255ms.
			
			// Most expensive VM(Model4): MIPS: 650 Storage: 18000. RAM: 2048. Bandwidth: 2000 
			// Least expensive VM(Model1): MIPS: 250 Storage: 10000. RAM: 512. Bandwidth: 1000
			
			// MOST Expensive Datacenter(Model2): Cost(GB per month): 0.09, costPerMem(GB per month): 0.025, costPerStorage: 0.0032 , costPerBw: 0.0015
				// Most expensive cost total = 42.6119
			// Least Expensive Datacenter (Model3): Cost(GB per month): 0.04, costperMem(GB per month): 0.01, costPerStorage: 0.001, costPerBw: 0.0005
				// Least expensive cost total = 7.222 
			double fitness = chromosome.calculateFitness(7.222, 42.6119, 1, 255, chromosome.getAllocation(0));
			System.out.println(fitness);
					
			System.out.println("-----------------------");
			System.out.println("FITNESS CALCULATION ALL ALLOCATIONS");
					//(7.222, 42.6119, 1, 255, chromosome.getAllocation(2));
			double chromosomeFitness = chromosome.calculateChromosomeFitness(7.222, 42.6119, 1, 255);
			System.out.print(chromosomeFitness);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
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
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return datacenter;
	}

	/* Potentially modify */
	private static DatacenterBroker createBroker(){

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
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}
	}
	
	// Obtains costs of single VM based on it's specifications and what the datacenter charges.
	public static double getCostOfSingleVM(Datacenter datacenter, Vm vm) {
		
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
	
	// Currently, obtains cost for vms that are allocated sequentially to available hosts or data centers
	public static double getTotalCostOfAllVMs(List<Datacenter> datacenters, List<Vm> vmlist) {
		
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
	
	// Latency in MS
	public static double calculateLatencyCost(Datacenter datacenter, Vm vm) {
		
		// Starting point for latency (between 1-15ms); even if the host and the vm are in the same time zone, expected to have some delay
		double latency = 1 + Math.random() * 14;
		double datacenterTimezone = datacenter.getCharacteristics().getTimeZone();
		double vmTimezone = vm.getTime_zone();
		
		// Multiple of between 15-20 to account for randomness and other variables
		latency +=  Math.abs(datacenterTimezone - vmTimezone) * ((int) (Math.random() * 6) + 15);
		
		return latency;
			
	}
	
	// Iterate through all VMS to allocate to hosts randomly
	public static void allocateAllVmsRandomly(Chromosome chromosome, List<Datacenter> datacenters, List<Vm> vmlist) {
		
		// Iterates through until the vms are all allocated or there are not enough hosts
	    for (int i = 0; i < vmlist.size() && i<datacenters.size(); i++) {
	    	
    	 	Vm vm = vmlist.get(i);
	        Datacenter datacenter = datacenters.get(i);
	        
	        // Calculate the cost of assigning the current VM to the current datacenter
	        double vmCost = getCostOfSingleVM(datacenter, vm);
	        
	        // Calculate the latency of assigning the current VM to the current datacenter
	        double vmLatency= calculateLatencyCost(datacenter, vm);
	        
	        // Save allocation details
	        Allocation allocation = new Allocation(i, vm.getId(), datacenter.getId(), vmCost, vmLatency);
	        
	        chromosome.addAllocation(allocation);     
	    }
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

	// Generates cloudlets
	public static void generateCloudlets(int id, long length, long fileSize, long outputSize, UtilizationModel utilizationModel, int numberOfCloudlets, int brokerId) {
		for (int i= 0; i < numberOfCloudlets; i++) {
			
			// 1 IS PES NUMBER; I'm assuming all CPUs have 1 core. Change to pesNumber = ; if needed.
			Cloudlet cloudlet = new Cloudlet(id, length, 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet.setUserId(brokerId);
			id++;

			// Add each cloudlet to cloudletList
			cloudletList.add(cloudlet);
		} 		
	}
}