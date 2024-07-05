package org.cloudbus.cloudsim;

public class VMAllocationPolicyPseudocode {

	// 0. Starting parameters / assumptions:
	
		// Assume only 1 PE for each host - at least initially, then determine if more should be added based on program complexity.
		
		// Latency just set (ms) values that simulate expected delay; physical distance specifications are not needed in this simulation.
		// Cost can be determined by CostPerMIPS, CostPerMemory, CostPerStorage, and CostPerBw all summed up.
			// Goal: Minimize cost and latency 
	
	// 1. Randomize VM settings (CPU, RAM, memory, ETC)
		// Loop through and instantiate based on appropriate range
	
	// 2. Random allocation - initially allocate all VMs to a feasible hosts
	
	// 3. Use MOGA to minimize latency and costs
	
	// 4. Repeat X number of times
	
	
	
	// MOGA - Assume 10 hosts and 10 VMs
	// MOGA Phases:
	
	
	/* 1. Spawning
	 * 	- Hosts will be labeled with 10 IDs.
	 *  - 10 VMs will be allocated to the hosts
	 * 
	 */
}
