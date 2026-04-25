package dev.aviatorsofcreate.gearsandjets.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum MachineState implements StringRepresentable {
    OFF("off"),
    IDLING("idling"),
    RUNNING("running");

    private final String name;

    MachineState(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}