package net.guerkhd.minecraftrequiem.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBinding
{
    public static final String KEY_CATEGORY_STAND = "key.category.minecraftrequiem.stand";
    public static final String KEY_SUMMON_STAND = "key.minecraftrequiem.summon_stand";
    public static final String KEY_STAND_ABILITY = "key.minecraftrequiem.stand_ability";

    public static final net.minecraft.client.settings.KeyBinding SUMMONING_KEY =
            new net.minecraft.client.settings.KeyBinding(KEY_SUMMON_STAND, Keyboard.KEY_G, KEY_CATEGORY_STAND);
    public static final net.minecraft.client.settings.KeyBinding ABILITY_KEY =
            new net.minecraft.client.settings.KeyBinding(KEY_STAND_ABILITY, Keyboard.KEY_V, KEY_CATEGORY_STAND);
}
