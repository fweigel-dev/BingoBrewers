package com.github.indigopolecat.bingobrewers;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.inventory.ContainerChest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraftforge.common.MinecraftForge;
import java.util.List;
import net.minecraft.inventory.Slot;

public class doneLoading {
    long startTime;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public void onInventoryChanged(ContainerChest containerChest) {
        System.out.println("Inventory Changed!");
        startTime = System.currentTimeMillis();
        List<Slot> slots = containerChest.inventorySlots;
        scheduler.scheduleAtFixedRate(() -> compareContainerSize(slots), 0, 100, TimeUnit.MILLISECONDS);
    }

    public void compareContainerSize(List<Slot> slots) {
        long elapsedTime = System.currentTimeMillis() - startTime;
        Slot slot = slots.get(slots.size() - 1);
        if (slot != null) {
            scheduler.schedule(() -> {
                MinecraftForge.EVENT_BUS.post(new InventoryLoadingDoneEvent());
                scheduler.shutdown();
                System.out.println("Detected final inventory slot to be non-null");
            }, 50, TimeUnit.MILLISECONDS);
        } else if (elapsedTime >= 3000) {
            scheduler.shutdown();
            MinecraftForge.EVENT_BUS.post(new InventoryLoadingDoneEvent());
            System.out.println("Inventory Load Scheduler timeout");
        }
    }

    public static class InventoryLoadingDoneEvent extends Event {
    }
}
