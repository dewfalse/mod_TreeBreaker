package treebreaker;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {

	@Override
	void init() {
		TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		KeyBindingRegistry.registerKeyBinding(new ModeKeyHandler());
	}
}
