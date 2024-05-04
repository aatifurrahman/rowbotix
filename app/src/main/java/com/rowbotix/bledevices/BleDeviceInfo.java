package com.rowbotix.bledevices;

import java.util.Objects;

public class BleDeviceInfo {
    private final String name;
    private final String address;

    public BleDeviceInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BleDeviceInfo otherDevice = (BleDeviceInfo) obj;
        return address != null && address.equals(otherDevice.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}

