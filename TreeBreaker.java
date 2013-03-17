package treebreaker;

import java.util.logging.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "TreeBreaker", name = "TreeBreaker", version = "1.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = true, channels = { Config.channel }, packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class, versionBounds = "[1.0]")
public class TreeBreaker {
	@SidedProxy(clientSide = "treebreaker.ClientProxy", serverSide = "treebreaker.CommonProxy")
	public static CommonProxy proxy;

	@Instance("TreeBreaker")
	public static TreeBreaker instance;

	public static Logger logger = Logger.getLogger("Minecraft");

	public static Config config = new Config();

	@Mod.Init
	public void load(FMLInitializationEvent event) {
		proxy.init();
	}

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		config.load(event.getSuggestedConfigurationFile());
	}
}
