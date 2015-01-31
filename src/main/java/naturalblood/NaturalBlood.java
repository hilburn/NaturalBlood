package naturalblood;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import WayofTime.alchemicalWizardry.api.event.ItemBindEvent;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBindable;
import WayofTime.alchemicalWizardry.common.items.EnergyBattery;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import naturalblood.reference.Metadata;
import naturalblood.reference.Reference;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import thaumcraft.common.lib.FakeThaumcraftPlayer;

/**
 * NaturalBlood Mod
 *
 * @author Charlie Paterson
 * @license DBaJ non-commercial care-free license. (https://github.com/hilburn/NaturalBlood/blob/master/LICENSE.md)
 **/

@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL, dependencies = "required-after:AWWayofTime")
public class NaturalBlood
{
    private static boolean thaumcraft = Loader.isModLoaded("Thaumcraft");

    @Instance(Reference.ID)
    public static NaturalBlood instance = new NaturalBlood();

    @Mod.Metadata(Reference.ID)
    public static ModMetadata metadata;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        metadata = Metadata.init(metadata);
        config(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new RealPlayerHandler());
    }

    public static void config(File file)
    {
        Configuration config = new Configuration(file);
        config.get("General", "sticks", false, "Sticks?");
        config.save();
    }

    @NetworkCheckHandler
    public final boolean networkCheck(Map<String, String> remoteVersions, Side side)
    {
        if (side.isClient()) return true;
        else return remoteVersions.containsKey(Reference.ID);
    }

    public class RealPlayerHandler
    {
        @SubscribeEvent
        public void linkPlayer(ItemBindEvent e)
        {
            if (e.player instanceof FakePlayer || (thaumcraft && e.player instanceof FakeThaumcraftPlayer))
            {
                e.key = "";
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public void renderToolTip(ItemTooltipEvent e)
        {
            if (e.itemStack.getItem() instanceof IBindable)
            {
                for (Iterator<String> itr = e.toolTip.iterator(); itr.hasNext();)
                {
                    if (itr.next().equals("Current owner: ")) itr.remove();
                }
            }
        }
    }
}