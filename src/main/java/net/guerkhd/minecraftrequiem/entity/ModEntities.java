package net.guerkhd.minecraftrequiem.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;

public class ModEntities
{
    public static void register()
    {
        EntityRegistry.registerModEntity(EntityStand.class, "stand", 0, MinecraftRequiem.instance, 64, 1, true);
        EntityRegistry.registerModEntity(EntitySheerHeartAttack.class, "sheer_heart_attack", 1, MinecraftRequiem.instance, 64, 10, false);
    }
}
