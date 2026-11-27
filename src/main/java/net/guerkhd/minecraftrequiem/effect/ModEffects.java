package net.guerkhd.minecraftrequiem.effect;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.config.MinecraftRequiemCommonConfig;
import net.minecraft.potion.Potion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModEffects
{
    /**
     * Potion IDs are sent to the client as a signed byte, so everything above 127 breaks.
     */
    private static final int MAX_POTION_ID = 127;
    private static final int FIRST_MOD_POTION_ID = 32;

    private static final List<ModPotion> POTIONS = new ArrayList<ModPotion>();

    public static ModPotion TIME_STOP;
    public static ModPotion BOMB;
    public static ModPotion GREEN_DAY;
    public static ModPotion THREE_FREEZE;
    public static ModPotion EPITAPH;
    public static ModPotion CONSTIPATION;
    public static ModPotion LEVITATION;

    public static void register()
    {
        extendPotionArray(MAX_POTION_ID + 1);

        TIME_STOP = add(new TimeStopEffect(id(MinecraftRequiemCommonConfig.TIME_STOP_ID, "time_stop"), true, 0));
        BOMB = add(new BombEffect(id(MinecraftRequiemCommonConfig.BOMB_ID, "bomb"), true, 0));
        GREEN_DAY = add(new GreenDayEffect(id(MinecraftRequiemCommonConfig.GREEN_DAY_ID, "green_day"), true, 0));
        THREE_FREEZE = add(new ThreeFreezeEffect(id(MinecraftRequiemCommonConfig.THREE_FREEZE_ID, "three_freeze"), true, 0));
        EPITAPH = add(new KCEffect(id(MinecraftRequiemCommonConfig.EPITAPH_ID, "epitaph"), false, 0, "epitaph"));
        CONSTIPATION = add(new ConstipationEffect(id(MinecraftRequiemCommonConfig.CONSTIPATION_ID, "constipation"), false, 0));
        LEVITATION = add(new LevitationEffect(id(MinecraftRequiemCommonConfig.LEVITATION_ID, "levitation"), true, 0));
    }

    public static List<ModPotion> all()
    {
        return Collections.unmodifiableList(POTIONS);
    }

    private static ModPotion add(ModPotion potion)
    {
        POTIONS.add(potion);
        return potion;
    }

    /**
     * Returns the configured ID, or the next free one if another mod already uses it.
     */
    private static int id(int configured, String name)
    {
        if(configured > 0 && configured <= MAX_POTION_ID && Potion.potionTypes[configured] == null) return configured;

        for(int id = FIRST_MOD_POTION_ID; id <= MAX_POTION_ID; id++)
        {
            if(Potion.potionTypes[id] == null)
            {
                MinecraftRequiem.LOGGER.warn("Potion ID {} for {} is not available, using {} instead. Make sure client and server use the same IDs.", configured, name, id);
                return id;
            }
        }
        throw new IllegalStateException("No free potion ID left for " + name);
    }

    /**
     * Vanilla 1.7.10 only has room for 32 potions. Make room for more unless another mod already did.
     */
    private static void extendPotionArray(int size)
    {
        if(Potion.potionTypes.length >= size) return;

        Potion[] extended = new Potion[size];
        System.arraycopy(Potion.potionTypes, 0, extended, 0, Potion.potionTypes.length);

        Field field = ReflectionHelper.findField(Potion.class, "potionTypes", "field_76425_a");
        try
        {
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.setAccessible(true);
            field.set(null, extended);
        }
        catch(Exception e)
        {
            // Newer Java versions hide Field.modifiers; fall back to Unsafe.
            try
            {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                Object unsafe = unsafeField.get(null);
                Object base = unsafeClass.getMethod("staticFieldBase", Field.class).invoke(unsafe, field);
                long offset = (Long) unsafeClass.getMethod("staticFieldOffset", Field.class).invoke(unsafe, field);
                unsafeClass.getMethod("putObject", Object.class, long.class, Object.class).invoke(unsafe, base, offset, extended);
            }
            catch(Exception e2)
            {
                throw new RuntimeException("Could not extend the potion array", e2);
            }
        }

        MinecraftRequiem.LOGGER.info("Extended the potion array to {} entries.", size);
    }
}
