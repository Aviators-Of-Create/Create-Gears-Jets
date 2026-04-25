package dev.aviatorsofcreate.gearsandjets.enums;

import net.minecraft.util.StringRepresentable;

public enum MachineState implements StringRepresentable {
    OFF("off"),
    STARTING("starting"),
    RUNNING("running");

    private final String name;

    MachineState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}