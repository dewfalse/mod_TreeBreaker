package treebreaker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;

public class Config {
	public static final String channel = "tb";
	public static Set<Integer> logs = new LinkedHashSet();
	public static Set<Integer> leaves = new LinkedHashSet();
	public static Set<String> tools = new LinkedHashSet();
	public static boolean effective_tool_only = true;
	public static EnumMode mode = EnumMode.off;
	public static List<EnumMode> allowMode = new ArrayList();
	public boolean drop_to_player = true;
	public boolean allow_register = true;

	public void load(File file) {
		Configuration cfg = new Configuration(file);
		try {
			cfg.load();
			String value = cfg.get(Configuration.CATEGORY_GENERAL, "logIDs", "17").getString();
			for(String token : value.split(",")) {
				try {
					Integer blockId = Integer.parseInt(token.trim());
					if(blockId != null) {
						logs.add(blockId.intValue());
					}
				}
				catch(NumberFormatException e) {

				}
			}
			value = cfg.get(Configuration.CATEGORY_GENERAL, "leavesIDs", "18").getString();
			for(String token : value.split(",")) {
				try {
					Integer blockId = Integer.parseInt(token.trim());
					if(blockId != null) {
						leaves.add(blockId.intValue());
					}
				}
				catch(NumberFormatException e) {

				}
			}
			value = cfg.get(Configuration.CATEGORY_GENERAL, "additional_tools", "").getString();
			for(String token : value.split(",")) {
				try {
					tools.add(token);
				}
				catch(NumberFormatException e) {

				}
			}

			effective_tool_only = cfg.get(Configuration.CATEGORY_GENERAL, "effective_tool_only", true).getBoolean(true);
			drop_to_player = cfg.get(Configuration.CATEGORY_GENERAL, "drop_to_player", true).getBoolean(true);
			allow_register = cfg.get(Configuration.CATEGORY_GENERAL, "allow_register", true).getBoolean(true);

			allowMode.add(EnumMode.off);
			if(cfg.get(Configuration.CATEGORY_GENERAL, "enable_treebreaker", true).getBoolean(true)) {
				allowMode.add(EnumMode.logs);
				allowMode.add(EnumMode.leaves);
				allowMode.add(EnumMode.all);
			}
			cfg.save();
		} catch (Exception e) {
			FMLLog.log(Level.SEVERE, e, "TreeBreaker load config exception");
		} finally {
			cfg.save();
		}
	}

	public void toggleMode() {
		for(int i = 0; i < allowMode.size(); ++i) {
			if(allowMode.get(i).equals(mode)) {
				i = (i + 1) % allowMode.size();
				mode = allowMode.get(i);
				break;
			}
		}
	}

	public EnumMode getMode() {
		return mode;
	}

	public void sendTargetToPlayer(INetworkManager manager) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try {
			stream.writeUTF(EnumCommand.TARGET.toString());
			stream.writeInt(TreeBreaker.config.logs.size());
			for(int blockId : TreeBreaker.config.logs) {
				stream.writeInt(blockId);
				stream.writeInt(0);
			}
			stream.writeInt(TreeBreaker.config.leaves.size());
			for(int blockId : TreeBreaker.config.leaves) {
				stream.writeInt(blockId);
				stream.writeInt(0);
			}

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = Config.channel;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;

			manager.addToSendQueue(packet);
		} catch (IOException e) {
			// TODO Ž©“®¶¬‚³‚ê‚½ catch ƒuƒƒbƒN
			e.printStackTrace();
		}
	}

}
