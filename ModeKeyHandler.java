package treebreaker;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class ModeKeyHandler extends KeyHandler {

	static KeyBinding modeKeyBinding = new KeyBinding("TreeBreaker.Mode", Keyboard.KEY_M);

	public ModeKeyHandler() {
		super(new KeyBinding[] { modeKeyBinding }, new boolean[] { false });
	}
	@Override
	public String getLabel() {
		return "TreeBreaker.Mode";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		Minecraft mc = Minecraft.getMinecraft();
		if(kb != this.modeKeyBinding) return;
		if(tickEnd == false) return;
		if(mc.currentScreen != null) return;
		if(mc.ingameGUI.getChatGUI().getChatOpen()) return;

		TreeBreaker.config.toggleMode();
		mc.ingameGUI.getChatGUI().printChatMessage("TreeBreaker Mode = " + TreeBreaker.config.getMode().toString());
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
