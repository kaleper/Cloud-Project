package org.cloudbus.cloudsim;


// Just like DatacenterModel, I created this class to represent the varying characteristics that VMs will have. This will cause variation when allocating VMs to hosts.
public class VMModel {
    private int vmModelId;
    private int mips;
    private long size;
    private int ram;
    private long bw;
    private int pesNumber;
    private String vmm;

    public VMModel(int vmModelId, int mips, long size, int ram, long bw, int pesNumber, String vmm) {
        this.vmModelId = vmModelId;
        this.mips = mips;
        this.size = size;
        this.ram = ram;
        this.bw = bw;
        this.pesNumber = pesNumber;
        this.vmm = vmm;
    }

	public int getVmModelId() {
		return vmModelId;
	}

	public void setVmModelId(int vmModelId) {
		this.vmModelId = vmModelId;
	}

	public int getMips() {
		return mips;
	}

	public void setMips(int mips) {
		this.mips = mips;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public long getBw() {
		return bw;
	}

	public void setBw(long bw) {
		this.bw = bw;
	}

	public int getPesNumber() {
		return pesNumber;
	}

	public void setPesNumber(int pesNumber) {
		this.pesNumber = pesNumber;
	}

	public String getVmm() {
		return vmm;
	}

	public void setVmm(String vmm) {
		this.vmm = vmm;
	}
}