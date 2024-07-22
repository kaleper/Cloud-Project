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
import org.cloudbus.cloudsim.Config;
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

		try {

			// First step: Initialize the CloudSim package.
			int num_user = 1; 
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;

			// Initialize the GridSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters with random data center models.

			// I'll define how many data centers are created iteratively
			int numberOfDataCenters = Config.numberOfDataCenters;
			
			// I'll define how many generations to run through
			int numberOfGenerations= Config.numberOfGenerations;
			
			// This can specify how many genes a chromosome can have (or how many different vm - model pairings.
			int numberOfAllocationsPerChromosome = Config.numberOfAllocationsPerChromosome;

			// Use to define how many VM models will be used
			int numberOfVms = Config.numberOfVms;

			// Population instantiation
			int populationSize = Config.populationSize;
			
			// Mutation chance
			double mutationChance = Config.mutationChance;
			
			List<Datacenter> datacenters = new ArrayList<>();

			// Will hold all Vm instances with a model associated to it
			List<Vm> vmlist = new ArrayList<Vm>();
			
			// Models adjusted to closely reflect real world pricing.
			
			// Params: Archetype, OS, VMM, Time_zone(of resource), Cost(GB per month), costPerMem(GB per month), costPerStorage(GB per month), costPerBw

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


			// Population Testing:

			Population population1 = new Population(populationSize);
			

			// Create initial population - random VM allocation to hosts
			population1.initializePopulation(numberOfAllocationsPerChromosome, numberOfVms, brokerId, numberOfDataCenters, datacenters, vmlist,
					datacenterModels, vmModels);
			
			
			// GNU PLOT USE - Initial generation, cost, and latency 
//			System.out.println(0 +  " " + String.format("%.2f", population1.calculatePopulationCost()) + " " + String.format("%.2f", population1.calculatePopulationLatency(datacenters, vmlist)));
			
			// GNU PLOT USE - Initial population fitness 
//			System.out.println(0 + " " + String.format("%.2f", population1.calculatePopulationFitness(7.22, 42.6119, 1, 255)));
			
			/**************/
			/** DEMO USE **/
			/**************/
			
//			// DEMO USE - Program Title
//			System.out.println(" ");
//			System.out.println("*******************************************************************");
//			System.out.println("*******************************************************************");
//			
//			System.out.println(" ");
//			System.out.println("Allocating VMs to Hosts - Using MOGA to optimize for Latency & Cost");
//			System.out.println("By: Kalen Peredo ");
//			System.out.println(" ");
//			System.out.println(" ");
//			
//			// DEMO USE - Configurations
//			System.out.println("Configurations: ");
//			System.out.println("----------------");
//			System.out.println("Population Size: " + populationSize);
//			System.out.println("Number of Generations: " + numberOfGenerations);
//			System.out.println("Number of Allocations/Chromosome: " + numberOfAllocationsPerChromosome);
//			System.out.println("Mutation Chance: " + mutationChance);
//			System.out.println(" ");
//			System.out.println("*******************************************************************");
//			System.out.println("*******************************************************************");
//			System.out.println(" ");
//			
//			// DEMO USE - Testing Title
//			System.out.println("Population Testing: ");
//			System.out.println("-------------------");
//			
//			
//			// DEMO USE - Initial population fitness
//			System.out.println("Population 0 - Initial: ");
//			System.out.println("Total Cost: $" + String.format("%.2f", population1.calculatePopulationCost()));
//		    System.out.println("Total Latency:" + String.format("%.2f", population1.calculatePopulationLatency(datacenters, vmlist)) + "ms");
//			System.out.println(String.format("Total Fitness: %.2f", population1.calculatePopulationFitness(7.22, 42.6119, 1, 255)));
//
//			System.out.println("---");
			

				for (int i = 1; i < numberOfGenerations + 1; i++) {
					
					// Create a generation
				    population1.doGeneration(population1, datacenters, vmlist);
	
				    
				    // Save cost & latency for population
				    double totalCostPopulation = 0.0;
				    double totalLatencyPopulation = 0.0;
	
				    /* Uses double for loop to get the cost and latency of a populations' chromosomes' allocations' */
				    // Iterates through all chromosomes 
				    for (int j = 0; j < population1.getChromosomes().size() - 1; j++) {
				    	
					    // Get the current chromosome from the population
					    Chromosome currentChromosome = population1.getChromosomes().get(j);
		
					    // Initialize the total cost and latency for the current chromosome
					    double totalCostChromosome = 0.0;
					    double totalLatencyChromosome = 0.0;
		
					    // Iterate over the allocations of the current chromosome
					    for (Allocation allocation : currentChromosome.getAllocations()) {
					    	
					        // Add the cost of the current allocation to the total cost
					        totalCostChromosome += allocation.getCost();
					        totalLatencyChromosome += allocation.getLatency();
	
					    }
					    
					 // Add the total cost of the current chromosome to the total population cost
		                totalCostPopulation += totalCostChromosome;
		                totalLatencyPopulation += totalLatencyChromosome; 
				
			
				    }
//				    // DEMO USE - Current population cost, latency and fitness
//				    System.out.println("Population " + i + ":");
//				    System.out.println("Total Cost: $" + String.format("%.2f", totalCostPopulation));
//				    System.out.println("Total Latency:" + String.format("%.2f", totalLatencyPopulation) + "ms");
//				    System.out.println("Total Fitness:" + String.format("%.2f", population1.calculatePopulationFitness(7.22, 42.6119, 1, 255)));
//				    System.out.println("---");
				    
				    // GNU PLOT USE - Current population latency and cost per population:
//				    System.out.println(i + " " + String.format("%.2f", totalCostPopulation) + " " + String.format("%.2f", totalLatencyPopulation));
				 
				    // GNU PLOT USE - Fitness per population
				    System.out.println(i + " " + String.format("%.2f", population1.calculatePopulationFitness(7.22, 42.6119, 1, 255)));
				}

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