package net.guerkhd.minecraftrequiem.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.stand.StandType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * The stand HUD. While a stand is summoned: its head, its name and what the ability key costs, in the top left corner.
 * Right after the Stand Arrow: a menacing "go go go" in the middle of the screen.
 * Both replace the chat messages of the 1.19 version.
 */
@SideOnly(Side.CLIENT)
public class StandHudOverlay
{
    private static final int HEAD_SIZE = 16;   // the 8x8 face of the skin, doubled
    private static final int ICON_SIZE = 9;    // vanilla hunger icon
    private static final long FAILED_FLASH_MS = 1000;

    private static final String GO = "ゴ"; // katakana GO, the sound effect from the series
    private static final int GO_COUNT = 3;
    private static final float GO_SCALE = 4f;
    private static final long GO_DELAY_MS = 200;   // between the characters
    private static final long GO_FADE_IN_MS = 150;
    private static final long GO_FADE_OUT_MS = 800;
    private static final long GO_MS = 3000;
    private static final float OUTLINE_WIDTH = 0.35f; // in font pixels, before scaling
    private static final int[][] OUTLINE = {{-1, -1}, {0, -1}, {1, -1}, {-1, 0}, {1, 0}, {-1, 1}, {0, 1}, {1, 1}};

    private static final Map<StandType, ResourceLocation> SKINS = new EnumMap<StandType, ResourceLocation>(StandType.class);
    private static final Random SHAKE = new Random();

    public static void render(ScaledResolution resolution)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.gameSettings.hideGUI || mc.thePlayer == null) return;

        long now = System.currentTimeMillis();
        // The F3 screen uses the same corner.
        if(ClientStandData.getStandActive() && !mc.gameSettings.showDebugInfo) renderStand(mc, resolution, now);
        renderAwakening(mc, resolution, now);

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    private static void renderStand(Minecraft mc, ScaledResolution resolution, long now)
    {
        int x = resolution.getScaledWidth() / 30;
        int y = resolution.getScaledHeight() / 18;
        StandType standType = StandType.byId(ClientStandData.getStandID());

        int textX = x;
        if(standType.hasStandEntity()) // Highway To Hell has no body and so no face
        {
            drawHead(mc, standType, x, y + 1);
            textX += HEAD_SIZE + 4;
        }

        // The color code has to come first, it resets bold.
        mc.fontRenderer.drawStringWithShadow(standType.getColor() + "" + EnumChatFormatting.BOLD + standType.getHudName(), textX, y, 0xFFFFFF);

        int cost = standType.getCost(ClientStandData.getCosts(), mc.thePlayer.dimension);
        if(cost > 0) drawCost(mc, textX, y + 10, cost, now);
    }

    /**
     * The face of the skin with the hat layer on top, like player heads in later versions' tab list.
     */
    private static void drawHead(Minecraft mc, StandType standType, int x, int y)
    {
        ResourceLocation skin = SKINS.get(standType);
        if(skin == null)
        {
            skin = new ResourceLocation(MinecraftRequiem.MOD_ID, standType.getSkinPath());
            SKINS.put(standType, skin);
        }

        mc.getTextureManager().bindTexture(skin);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Gui.func_152125_a(x, y, 8, 8, 8, 8, HEAD_SIZE, HEAD_SIZE, 64, 64);  // face
        Gui.func_152125_a(x, y, 40, 8, 8, 8, HEAD_SIZE, HEAD_SIZE, 64, 64); // hat layer
    }

    /**
     * The cost as hunger shanks, like the hunger bar: grey while there isn't enough food,
     * shaking and flashing red for a second after the ability failed for that reason.
     */
    private static void drawCost(Minecraft mc, int x, int y, int cost, long now)
    {
        boolean affordable = mc.thePlayer.getFoodStats().getFoodLevel() >= cost;
        long sinceFailed = now - ClientStandData.getAbilityFailedAt();
        boolean failed = ClientStandData.getAbilityFailedAt() != 0 && sinceFailed >= 0 && sinceFailed < FAILED_FLASH_MS;
        boolean red = failed && (sinceFailed / 125) % 2 == 0;

        if(red) GL11.glColor4f(1f, 0.3f, 0.3f, 1f);
        else if(!affordable) GL11.glColor4f(0.45f, 0.45f, 0.45f, 1f);
        else GL11.glColor4f(1f, 1f, 1f, 1f);

        mc.getTextureManager().bindTexture(Gui.icons);
        SHAKE.setSeed(now / 50);

        int shanks = (cost + 1) / 2;
        for(int i = 0; i < shanks; i++)
        {
            int iconX = x + i * 8;
            int iconY = y + (failed ? SHAKE.nextInt(3) - 1 : 0);
            boolean half = i * 2 + 1 == cost;

            Gui.func_146110_a(iconX, iconY, 16, 27, ICON_SIZE, ICON_SIZE, 256, 256);               // outline
            Gui.func_146110_a(iconX, iconY, half ? 61 : 52, 27, ICON_SIZE, ICON_SIZE, 256, 256);  // full or half shank
        }

        GL11.glColor4f(1f, 1f, 1f, 1f);
    }

    /**
     * Three big GO, one after another, slightly trembling, in the new stand's color. It doesn't say which stand it is;
     * that shows once it's summoned.
     */
    private static void renderAwakening(Minecraft mc, ScaledResolution resolution, long now)
    {
        long awakenedAt = ClientStandData.getAwakenedAt();
        long since = now - awakenedAt;
        if(awakenedAt == 0 || since < 0 || since > GO_MS) return;

        FontRenderer font = mc.fontRenderer;
        // Not bold and no font shadow: scaled up, both would smear the glyph. A thin dark outline instead.
        String text = StandType.byId(ClientStandData.getStandID()).getColor() + GO;
        float width = font.getStringWidth(GO) * GO_SCALE;
        float step = width + 4;

        float centerX = resolution.getScaledWidth() / 2f;
        float centerY = resolution.getScaledHeight() / 2f - 12;
        float fadeOut = Math.min(1f, (GO_MS - since) / (float) GO_FADE_OUT_MS);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        SHAKE.setSeed(now / 60);

        for(int i = 0; i < GO_COUNT; i++)
        {
            long shown = since - i * GO_DELAY_MS;
            float alpha = Math.min(1f, shown / (float) GO_FADE_IN_MS) * fadeOut;
            if(shown < 0 || alpha < 0.05f) continue;

            // Left to right and a little downwards, like in the manga.
            float px = centerX + (i - (GO_COUNT - 1) / 2f) * step - width / 2 + SHAKE.nextInt(3) - 1;
            float py = centerY + (i - (GO_COUNT - 1) / 2f) * 8 - 4 * GO_SCALE + SHAKE.nextInt(3) - 1;

            GL11.glPushMatrix();
            GL11.glTranslatef(px, py, 0);
            GL11.glScalef(GO_SCALE, GO_SCALE, 1f);
            int outline = (int) (alpha * 0.7f * 255) << 24;
            for(int[] offset : OUTLINE)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(offset[0] * OUTLINE_WIDTH, offset[1] * OUTLINE_WIDTH, 0);
                font.drawString(GO, 0, 0, outline);
                GL11.glPopMatrix();
            }
            font.drawString(text, 0, 0, ((int) (alpha * 255) << 24) | 0xFFFFFF);
            GL11.glPopMatrix();
        }
    }
}
