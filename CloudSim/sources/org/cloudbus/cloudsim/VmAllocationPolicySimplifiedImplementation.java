/* This code will allow me to create a MOGA algorithm geared towards allocating VMs eventually 
independent of the CloudSim environment. Code will eventually be used and evolved
to be used within the CloudSim Architecture.
In this file, factors like latency and cost are simplified and the logic behind how they will be 
weighted is abstracted away. 
VM settings will also be simplified to make more binary distinctions between them rather 
than focusing on specific details like RAM, CPU, ETC*/
package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class VmAllocationPolicySimplifiedImplementation {

	
	// List of VMs to be allocated
	private Map<String, Host> vmTable;
	
	// Host VMs that are available
	private List<Host> availableHosts;
	
	// Host VMs that are available
	private List<Host> unavailableHosts;
	
	
}


