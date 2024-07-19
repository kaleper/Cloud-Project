package org.cloudbus.cloudsim;

public class Config{
	
	//For GA
	
		public static int numberOfGenerations = 50;
		public static int populationSize = 10;
		
		// Use to define how many VM models will be used
		public static int numberOfVms = 5;
	
		// 4% chance of mutating an offspring - can change value if i want.
		public static double mutationChance = 0.04;
		
	
	
	// For Allocations
	
		// I'll define how many data centers are created iteratively
		public static int numberOfDataCenters = 5;
		// This can specify how many genes a chromosome can have (or how many different vm - model pairings.
		public static int numberOfAllocationsPerChromosome = 20;
	}
