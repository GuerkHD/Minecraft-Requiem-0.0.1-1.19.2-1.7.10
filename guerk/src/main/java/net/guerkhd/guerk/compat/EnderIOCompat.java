package net.guerkhd.guerk.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.guerkhd.guerk.Guerk;

/**
 * Ender IO: SAG Mill recipes, sent as the same XML format Ender IO uses for its own recipe files.
 * Same numbers as Ender IO's recipes for copper or tin.
 */
class EnderIOCompat
{
    private static final String RAW_GUERK = "modID=\"" + Guerk.MOD_ID + "\" itemName=\"raw_guerk\"";

    static void init()
    {
        String xml = "<SAGMillRecipes>"
                + "<recipeGroup name=\"" + Guerk.MOD_NAME + "\">"
                + "<recipe name=\"GuerkOre\" energyCost=\"3600\">"
                + "<input><itemStack oreDictionary=\"oreGuerk\" /></input>"
                + "<output>"
                + "<itemStack " + RAW_GUERK + " number=\"2\" />"
                + "<itemStack modID=\"minecraft\" itemName=\"cobblestone\" chance=\"0.15\" />"
                + "</output>"
                + "</recipe>"
                + "<recipe name=\"GuerkIngot\" energyCost=\"2400\">"
                + "<input><itemStack oreDictionary=\"ingotGuerk\" /></input>"
                + "<output><itemStack " + RAW_GUERK + " /></output>"
                + "</recipe>"
                + "</recipeGroup>"
                + "</SAGMillRecipes>";

        FMLInterModComms.sendMessage(ModCompat.ENDER_IO, "recipe:sagmill", xml);
    }
}
