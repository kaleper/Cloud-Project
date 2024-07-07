package org.cloudbus.cloudsim;


public class Timezone {
    private String id;
 // Offset in hours from GMT
    private int offset; 

    public Timezone(int offset) {
        this.offset = offset;
    }

    public String getId() {
        return id;
    }

    public int getOffset() {
        return offset;
    }

    // Latency goes up arbitrarily by a factor of 10 the further away a host is from a user
    public static int calculateLatency(Timezone tz1, Timezone tz2) {
        return Math.abs(tz1.getOffset() - tz2.getOffset()) * 10;
    }

    @Override
    public String toString() {
        return id;
    }
}