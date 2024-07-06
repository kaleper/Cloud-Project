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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.HostList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


/**
 * A simple example showing how to create
 * two datacenters with one host each and
 * run two cloudlets on them.
 */
public class CloudSimAlgorithm {

	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
//	private static List<Vm> vmlist;

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		Log.printLine("Starting CloudSimAlgorithm...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the GridSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters.
			
			// I'll define how many data centers are created iteratively
			int numberOfDataCenters = 5;
			
			List <Datacenter> datacenters = new ArrayList<>();
			
			// Loop to create however many datacenters
			for (int i = 0; i < numberOfDataCenters; i++) {
				Datacenter datacenter = createDatacenter("Datacenter-" + i);
			
				datacenters.add(datacenter);
			}
		
//			@SuppressWarnings("unused")
//			Datacenter datacenter0 = createDatacenter("Datacenter_0");
//			@SuppressWarnings("unused")
//			Datacenter datacenter1 = createDatacenter("Datacenter_1");
		

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create one virtual machine
			
			// Will hold all vms
			List <Vm> vmlist = new ArrayList<Vm>();

			//VM description
			int vmid = 0;
			int mips = 250;
			long size = 10000; //image size (MB)
			int ram = 512; //vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; //number of cpus
			String vmm = "Xen"; //VMM name

			// Use to define how many VMs will be used 
			int numberOfVms = 4;
			

			
			for (int i= 0; i < numberOfVms; i++) {
				
				Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
				vmid++;
				
				// Add each VM to the VMlist
				vmlist.add(vm);
				
			}
			
	

			//submit vm list to the broker
			broker.submitVmList(vmlist);


			//Fifth step: Create two Cloudlets
			cloudletList = new ArrayList<Cloudlet>();

			//Cloudlet properties
			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();

			
			// Generates VMs
			for (int i= 0; i < numberOfVms; i++) {
				
				Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
				vmid++;
				
				// Add each VM to the VMlist
				vmlist.add(vm);
			
				
			}
		
			
			int numberOfCloudlets = 4;
			// Generates cloudlets
			for (int i= 0; i < numberOfCloudlets; i++) {
				
				Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
				cloudlet.setUserId(brokerId);
				id++;

				
				// Add each cloudlet to cloudletList
				cloudletList.add(cloudlet);
				
			}
			
//			Cloudlet cloudlet1 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//			cloudlet1.setUserId(brokerId);
//
//			id++;
//			Cloudlet cloudlet2 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//			cloudlet2.setUserId(brokerId);
//			
//			id++;
//			Cloudlet cloudlet3 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//			cloudlet3.setUserId(brokerId);
//			
//			id++;
//			Cloudlet cloudlet4 = new Cloudlet(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
//			cloudlet4.setUserId(brokerId);
//			id++;
//	
//
//			//add the cloudlets to the list
//			cloudletList.add(cloudlet1);
//			cloudletList.add(cloudlet2);
//			cloudletList.add(cloudlet3);
//			cloudletList.add(cloudlet4);
	
		
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
			System.out.println(getTotalCostOfAllVMs(datacenters, vmlist));
			
		
			// COST OF ONE VM
//			System.out.println(getCostOfSingleVM(datacenters.getFirst(), vmlist.getFirst()));
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		//4. Create Host with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;
		
		


		//in this example, the VMAllocatonPolicy in use is SpaceShared. It means that only one VM
		//is allowed to run on each Pe. As each Host has only one Pe, only one VM can run on each Host.
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		); // This is our first machine

		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		// Adjusted Datacenter prices to more closely reflect real world pricing. Prices are in dollars
		double time_zone = 10.0;         // time zone this resource located
		// Processing power per hr
		double cost = 0.02;        
		// GB per month
		double costPerMem = 0.01;		
		// GB per month
		double costPerStorage = 0.002;	
		
		double costPerBw = 0.001;
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

	       DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
	                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);
	       
	       
	


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
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
	// TODO: Figure out way to make this cost function work. Lists VS just using separate values individually? Can't access VMlist / database.characteristics due to protected status
	public static double getCostOfSingleVM(Datacenter datacenter, Vm vm) {

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
			double processingCost = processingRate * (vmProcessing/1000) * usageHrsPerMonth;
			
	
			double memoryCost = memoryRate * vmMemory;
			double storageCost = storageRate * vmStorage;
			// Usually free from big data centers like Microsoft for some? Rate set very low
			//https://azure.microsoft.com/en-us/pricing/details/bandwidth/
			// Most cloud providers charge gb per month
			double bwCost = bwRate * vmBw;
			
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

	     // Test output
	        //System.out.println("VM " + vm.getId() + " assigned to Datacenter " + datacenter.getId() + ", Cost: $" + vmCost);
	    }


		

		return totalCost;
	    
	    
	}

}
