package net.guerkhd.minecraftrequiem.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.minecraft.block.BlockFurnace;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Stand Remover Arrow's recipe: a Requiem Arrow in a vanilla furnace with a Milk Bucket as fuel.
 *
 * The milk works like a Lava Bucket, but only for the arrow:
 * - It does nothing until a Requiem Arrow lies on top (and the output is free).
 * - Then it is burned like lava: the bucket empties at once, and the heat lasts exactly one conversion (200 ticks).
 * - A furnace still glowing from other fuel burns out first; the arrow never cooks with other heat.
 * - Milk heat cooks nothing else. Taking the arrow out resets its progress, as with any smelting.
 *
 * Vanilla asks two separate questions: does the item on top have a smelting result, and does the item in the fuel slot
 * burn? Neither side knows the other, and 1.7.10 has no event where a mod could join them. So this runs after the
 * furnaces' own update every tick and handles the arrow itself. Which furnaces burn milk heat is kept in the world's
 * saved data, so it survives unloaded chunks and restarts.
 */
public class RemoverArrowFurnace
{
    private static final int CONVERSION_TIME = 200;
    /** +1: the conversion counts the ticks after ignition, the furnace counts down before that. */
    private static final int MILK_BURN_TIME = CONVERSION_TIME + 1;

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event)
    {
        if(event.phase != TickEvent.Phase.END || event.world.isRemote) return;

        World world = event.world;
        MilkFurnaces milkFurnaces = MilkFurnaces.get(world);

        // Collect first: lighting a furnace re-adds its tile entity to the list we would be iterating.
        List<TileEntityFurnace> furnaces = new ArrayList<TileEntityFurnace>();
        for(Object o : world.loadedTileEntityList)
        {
            // Only the vanilla furnace; other mods' furnaces may use their slots differently.
            if(o == null || o.getClass() != TileEntityFurnace.class) continue;
            TileEntityFurnace furnace = (TileEntityFurnace) o;
            if(!furnace.isInvalid() && (isRequiemArrow(furnace.getStackInSlot(0)) || milkFurnaces.get(furnace) != null)) furnaces.add(furnace);
        }

        for(TileEntityFurnace furnace : furnaces) update(world, milkFurnaces, furnace);

        if(world.getTotalWorldTime() % 100 == 0) milkFurnaces.removeMissing(world);
    }

    private static void update(World world, MilkFurnaces milkFurnaces, TileEntityFurnace furnace)
    {
        Integer progress = milkFurnaces.get(furnace);
        boolean arrowReady = isRequiemArrow(furnace.getStackInSlot(0)) && furnace.getStackInSlot(2) == null;

        // Milk heat: only the arrow cooks. Anything else gets its cook time reset, so it never finishes.
        if(progress != null && furnace.furnaceBurnTime > 0)
        {
            progress = arrowReady ? progress + 1 : 0;

            if(progress >= CONVERSION_TIME)
            {
                furnace.decrStackSize(0, 1);
                furnace.setInventorySlotContents(2, new ItemStack(ModItems.REMOVER_ARROW));
                furnace.markDirty();
                progress = 0;
            }

            // The furnace knows no recipe for the arrow and resets its cook time every tick; set again for the GUI.
            furnace.furnaceCookTime = progress;
            milkFurnaces.put(furnace, progress);
        }

        if(furnace.furnaceBurnTime > 0) return;

        // The furnace is out: light the milk if there is an arrow to convert (progress carries over, as between fuels).
        ItemStack fuel = furnace.getStackInSlot(1);
        if(arrowReady && fuel != null && fuel.getItem() == Items.milk_bucket)
        {
            furnace.setInventorySlotContents(1, new ItemStack(Items.bucket));
            furnace.furnaceBurnTime = MILK_BURN_TIME;
            furnace.currentItemBurnTime = MILK_BURN_TIME;
            furnace.markDirty();
            light(world, furnace);
            milkFurnaces.put(furnace, progress != null ? progress : 0);
        }
        else if(progress != null)
        {
            milkFurnaces.remove(furnace);
        }
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

    /**
     * Furnaces burning milk heat and their conversion progress, saved with the world (data/minecraftrequiem_milk_furnaces.dat).
     */
    public static class MilkFurnaces extends WorldSavedData
    {
        private static final String NAME = MinecraftRequiem.MOD_ID + "_milk_furnaces";

        private final Map<ChunkCoordinates, Integer> progress = new HashMap<ChunkCoordinates, Integer>();

        public MilkFurnaces(String name)
        {
            super(name);
        }

        static MilkFurnaces get(World world)
        {
            MilkFurnaces data = (MilkFurnaces) world.perWorldStorage.loadData(MilkFurnaces.class, NAME);
            if(data == null)
            {
                data = new MilkFurnaces(NAME);
                world.perWorldStorage.setData(NAME, data);
            }
            return data;
        }

        Integer get(TileEntity furnace)
        {
            return progress.get(position(furnace));
        }

        void put(TileEntity furnace, int value)
        {
            Integer old = progress.put(position(furnace), value);
            if(old == null || old != value) markDirty();
        }

        void remove(TileEntity furnace)
        {
            if(progress.remove(position(furnace)) != null) markDirty();
        }

        /**
         * Forgets furnaces that were broken or replaced (checked only where the chunk is loaded).
         */
        void removeMissing(World world)
        {
            for(Iterator<ChunkCoordinates> it = progress.keySet().iterator(); it.hasNext();)
            {
                ChunkCoordinates pos = it.next();
                if(!world.blockExists(pos.posX, pos.posY, pos.posZ)) continue;

                TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
                if(te == null || te.getClass() != TileEntityFurnace.class)
                {
                    it.remove();
                    markDirty();
                }
            }
        }

        private static ChunkCoordinates position(TileEntity te)
        {
            return new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord);
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt)
        {
            progress.clear();
            NBTTagList list = nbt.getTagList("furnaces", 10);
            for(int i = 0; i < list.tagCount(); i++)
            {
                NBTTagCompound entry = list.getCompoundTagAt(i);
                progress.put(new ChunkCoordinates(entry.getInteger("x"), entry.getInteger("y"), entry.getInteger("z")), entry.getInteger("progress"));
            }
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt)
        {
            NBTTagList list = new NBTTagList();
            for(Map.Entry<ChunkCoordinates, Integer> e : progress.entrySet())
            {
                NBTTagCompound entry = new NBTTagCompound();
                entry.setInteger("x", e.getKey().posX);
                entry.setInteger("y", e.getKey().posY);
                entry.setInteger("z", e.getKey().posZ);
                entry.setInteger("progress", e.getValue());
                list.appendTag(entry);
            }
            nbt.setTag("furnaces", list);
        }
    }
}
