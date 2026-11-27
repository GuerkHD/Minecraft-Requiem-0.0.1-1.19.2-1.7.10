package net.guerkhd.minecraftrequiem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * The Stand Remover Arrow's recipe: a Requiem Arrow in a vanilla furnace with a Milk Bucket as fuel.
 *
 * A normal smelting recipe would work with any fuel, and registering milk as fuel would make it burn for everything.
 * 1.7.10 has no furnace hooks, so the conversion runs here instead, after the furnaces' own update:
 * - Requiem Arrow on top, Milk Bucket in the fuel slot, empty output: the furnace lights up and converts the arrow
 *   in the usual 200 ticks, with the flame and the progress arrow running in the furnace GUI.
 * - When it is done, the Milk Bucket turns into an empty bucket, like a Lava Bucket.
 * - Taking out the arrow or the milk stops the conversion; nothing is used up until it is done.
 * - Any other fuel does nothing to the arrow, and milk heats nothing else.
 */
public class RemoverArrowFurnace
{
    private static final int CONVERSION_TIME = 200;

    /** Furnaces that are converting right now. Lost on restart, which only restarts the conversion. */
    private final Map<TileEntityFurnace, Conversion> conversions = new WeakHashMap<TileEntityFurnace, Conversion>();

    private static class Conversion
    {
        int progress;
        /** True if the furnace was cold and the heat is ours; then it goes out again when the conversion ends. */
        boolean ownHeat;
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END || event.world.isRemote) return;

        // Collect first: lighting a furnace re-adds its tile entity to the list we would be iterating.
        List<TileEntityFurnace> furnaces = new ArrayList<TileEntityFurnace>();
        for(Object o : event.world.loadedTileEntityList)
        {
            // Only the vanilla furnace; other mods' furnaces may use their slots differently.
            if(o == null || o.getClass() != TileEntityFurnace.class) continue;
            TileEntityFurnace furnace = (TileEntityFurnace) o;
            if(!furnace.isInvalid() && (isRequiemArrow(furnace.getStackInSlot(0)) || conversions.containsKey(furnace))) furnaces.add(furnace);
        }

        for(TileEntityFurnace furnace : furnaces) update(event.world, furnace);
    }

    private void update(World world, TileEntityFurnace furnace)
    {
        Conversion conversion = conversions.get(furnace);
        ItemStack fuel = furnace.getStackInSlot(1);
        boolean ready = isRequiemArrow(furnace.getStackInSlot(0)) && fuel != null && fuel.getItem() == Items.milk_bucket
                && furnace.getStackInSlot(2) == null;

        if(!ready)
        {
            if(conversion != null) stop(furnace, conversion);
            return;
        }

        if(conversion == null)
        {
            conversion = new Conversion();
            conversions.put(furnace, conversion);
        }

        // A furnace still glowing from other fuel keeps that heat; once it runs out, the milk takes over.
        if(!conversion.ownHeat && furnace.furnaceBurnTime <= 1)
        {
            conversion.ownHeat = true;
            // A burn time of 0 means the furnace block is the unlit one.
            if(furnace.furnaceBurnTime == 0) light(world, furnace);
        }

        conversion.progress++;
        // The furnace resets its cook time every tick (it knows no recipe for the arrow), so it is set again for the GUI.
        furnace.furnaceCookTime = conversion.progress;
        if(conversion.ownHeat)
        {
            // +1: the furnace counts down by one before this runs again, and must not go out on its own.
            furnace.furnaceBurnTime = CONVERSION_TIME - conversion.progress + 1;
            furnace.currentItemBurnTime = CONVERSION_TIME;
        }

        if(conversion.progress >= CONVERSION_TIME)
        {
            furnace.decrStackSize(0, 1);
            furnace.setInventorySlotContents(2, new ItemStack(ModItems.REMOVER_ARROW));
            furnace.setInventorySlotContents(1, new ItemStack(Items.bucket));
            stop(furnace, conversion);
            furnace.markDirty();
        }
    }

    private void stop(TileEntityFurnace furnace, Conversion conversion)
    {
        conversions.remove(furnace);
        furnace.furnaceCookTime = 0;
        // Let the furnace go out by itself on its next update.
        if(conversion.ownHeat && furnace.furnaceBurnTime > 1) furnace.furnaceBurnTime = 1;
    }

    /**
     * Swaps in the lit furnace block, as the furnace does itself when fuel catches fire.
     */
    private static void light(World world, TileEntityFurnace furnace)
    {
        BlockFurnace.updateFurnaceBlockState(true, world, furnace.xCoord, furnace.yCoord, furnace.zCoord);

        // Outside the tile entity update, swapping the block adds the furnace to the update list a second time
        // (inside it, Minecraft checks for that). Remove the extra entry, or the furnace would run twice per tick.
        List list = world.loadedTileEntityList;
        for(int count = Collections.frequency(list, furnace); count > 1; count--) list.remove((TileEntity) furnace);
    }

    private static boolean isRequiemArrow(ItemStack stack)
    {
        return stack != null && stack.getItem() == ModItems.REQUIEM_ARROW;
    }
}
