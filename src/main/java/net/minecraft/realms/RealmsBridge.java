package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBridge extends RealmsScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private GuiScreen previousScreen;

    public void switchToRealms(GuiScreen p_switchToRealms_1_)
    {
        this.previousScreen = p_switchToRealms_1_;
    }

    public void init()
    {
        Minecraft.getMinecraft().displayGuiScreen(this.previousScreen);
    }
}
