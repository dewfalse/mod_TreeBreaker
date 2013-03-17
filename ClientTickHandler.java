package treebreaker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;
import net.minecraft.util.EnumMovingObjectType;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class ClientTickHandler implements ITickHandler {

	int prev_blockHitWait = 0;
	int targetBlockId = 0;
	int targetBlockMetadata = 0;
	Coord blockCoord = new Coord();
	int sideHit = 0;

	Queue<Coord> nextTarget = new LinkedList<Coord>();
	Set<Coord> vectors = new LinkedHashSet();

	int count = 0;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if (type.equals(EnumSet.of(TickType.CLIENT))) {
			GuiScreen guiscreen = Minecraft.getMinecraft().currentScreen;
			if (guiscreen == null) {
				onTickInGame();
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

	@Override
	public String getLabel() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	public void onTickInGame() {
		if(isBreakBlock()) {
			count = 0;
			startBreak();
		}
		if(continueBreak() == false) {
		}
		if(isValidTool(targetBlockId, targetBlockMetadata) == false) {
			nextTarget.clear();
		}
		if(nextTarget.size() == 0) {
			updateBlockInfo();
		}
		// System.out.println("onTickInGame");
	}

	private void startBreak() {
		isValidStatus();
		isValidMode();

		if(isValidTarget(targetBlockId, targetBlockMetadata) == false) {
			nextTarget.clear();
			return;
		}

		vectors = getVector(sideHit, TreeBreaker.config.mode);
		nextTarget.addAll(getNextTarget(blockCoord, vectors));
	}

	private Set<Coord> getVector(int sideHit, EnumMode mode) {
		Set<Coord> set = new LinkedHashSet();
		switch(mode) {
		case off:
			break;
		case logs:
		case leaves:
		case all:
			set.add(Coord.east);set.add(Coord.ne);
			set.add(Coord.north);set.add(Coord.nw);
			set.add(Coord.west);set.add(Coord.sw);
			set.add(Coord.south);set.add(Coord.se);
			set.add(Coord.use);set.add(Coord.us);set.add(Coord.usw);
			set.add(Coord.ue);set.add(Coord.up);set.add(Coord.uw);
			set.add(Coord.une);set.add(Coord.un);set.add(Coord.unw);
			set.add(Coord.dse);set.add(Coord.ds);set.add(Coord.dsw);
			set.add(Coord.de);set.add(Coord.down);set.add(Coord.dw);
			set.add(Coord.dne);set.add(Coord.dn);set.add(Coord.dnw);
			break;
		}
		return set;
	}

	private Set<Coord> getNextTarget(Coord blockCoord,Set<Coord> vec) {
		Set<Coord> set = new LinkedHashSet();
		for(Coord direction : vec) {
			set.add(blockCoord.addVector(direction));
		}
		return set;
	}

	private boolean isValidMode() {
		return true;
	}

	private boolean isValidStatus() {
		return true;
	}

	private boolean isValidTool(int blockId, int metadata) {

		Minecraft mc = Minecraft.getMinecraft();

		// air
		if(blockId == 0) return false;
		// bedrock
		if(blockId == Block.bedrock.blockID) return false;

		Block block = Block.blocksList[blockId];
		if(block == null) return false;
		// liquid or air
		if(block.blockMaterial.isLiquid() || block.blockMaterial == Material.air) return false;

		//int metadata = b.metadata == -1 ? 0 : b.metadata;

		// this is not a target block type

		if(targetBlockId != blockId) return false;

		ItemStack itemStack = mc.thePlayer.getCurrentEquippedItem();
		// held no item
		if(itemStack == null) return false;

		Item item = Item.itemsList[itemStack.itemID];
		String itemName = Item.itemsList[itemStack.itemID].getUnlocalizedName();
		// if tool or shears or specific item, check effective
		if(item instanceof ItemAxe || item instanceof ItemShears || TreeBreaker.config.tools.contains(itemName)) {
			if(TreeBreaker.config.effective_tool_only) {
				if(TreeBreaker.config.logs.contains(blockId)) {
					if(item.getStrVsBlock(itemStack, block, metadata) <= 1.0F) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}

	private boolean isValidTarget(int blockId, int metadata) {
		switch(TreeBreaker.config.getMode()) {
		case off:
			return false;
		case logs:
			return TreeBreaker.config.logs.contains(blockId);
		case leaves:
			return TreeBreaker.config.leaves.contains(blockId);
		case all:
			if(TreeBreaker.config.logs.contains(blockId) == false && TreeBreaker.config.leaves.contains(blockId) == false) {
				return false;
			}
		}
		return true;
	}

	private boolean continueBreak() {
		for(int i = 0; i < 10; ++i) {
			Coord target = nextTarget.poll();
			if(target == null) {
				return false;
			}
			if(isValidStatus() == false) {
				nextTarget.clear();
				return false;
			}
			if(breakBlock(target)) {
				count++;
				nextTarget.addAll(getNextTarget(target, vectors));
			}
		}
		return true;
	}

	// true if break success
	private boolean breakBlock(Coord pos) {

		int x = pos.x;
		int y = pos.y;
		int z = pos.z;

		// height limit
		if(y < 1 || 255 < y) return false;

		Minecraft mc = Minecraft.getMinecraft();

		int blockId = mc.theWorld.getBlockId(x, y, z);
		// air
		if(blockId == 0) return false;
		// bedrock
		if(blockId == Block.bedrock.blockID) return false;

		Block block = Block.blocksList[blockId];
		if(block == null) return false;
		// liquid or air
		if(block.blockMaterial.isLiquid() || block.blockMaterial == Material.air) return false;

		int metadata = mc.theWorld.getBlockMetadata(x, y, z);

		// this is not a target block type
		switch(TreeBreaker.config.getMode()) {
		case off:
			return false;
		case logs:
			if(TreeBreaker.config.logs.contains(targetBlockId) == false) {
				return false;
			}
			if(TreeBreaker.config.logs.contains(blockId) == false) {
				return false;
			}
			break;
		case leaves:
			if(TreeBreaker.config.leaves.contains(targetBlockId) == false) {
				return false;
			}
			if(TreeBreaker.config.leaves.contains(blockId) == false) {
				return false;
			}
			break;
		case all:
			if(TreeBreaker.config.logs.contains(targetBlockId) == false && TreeBreaker.config.leaves.contains(targetBlockId) == false) {
				return false;
			}
			if(TreeBreaker.config.logs.contains(blockId) == false && TreeBreaker.config.leaves.contains(blockId) == false) {
				return false;
			}
			break;
		}

		if(isValidTool(blockId, metadata) == false) {
			return false;
		}

		ItemStack itemStack = mc.thePlayer.getCurrentEquippedItem();
		// held no item
		if(itemStack == null) return false;

		block.onBlockDestroyedByPlayer(mc.theWorld, x, y, z, blockId);
		mc.thePlayer.sendQueue.addToSendQueue(new Packet14BlockDig(2, x, y, z, 0));
		mc.theWorld.playAuxSFX(2001, x, y, z, block.blockID + (mc.theWorld.getBlockMetadata(x, y, z) << 12));
		boolean flag = mc.theWorld.setBlockAndMetadataWithNotify(x, y, z, 0, 0, 3);

		if (block != null && flag) {
			block.removeBlockByPlayer(mc.theWorld, mc.thePlayer, x, y, z);
			mc.theWorld.markBlockForUpdate(x, y, z);
		}

		itemStack.onBlockDestroyed(mc.theWorld, blockId, x, y, z, mc.thePlayer);
		sendPacket(EnumCommand.BREAK, pos);
		if (itemStack.stackSize == 0) {
			// itemstack.onItemDestroyedByUse(minecraft.thePlayer);
			mc.thePlayer.destroyCurrentEquippedItem();
			//ret = false;
		}

		boolean flag1 = mc.thePlayer.canHarvestBlock(block);

		if (flag1) {
			if(TreeBreaker.config.drop_to_player) {
				block.harvestBlock(mc.theWorld, mc.thePlayer, (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ, metadata);
			}
			else {
				block.harvestBlock(mc.theWorld, mc.thePlayer, x, y, z, metadata);
			}
		}

		return true;
	}

	private void sendPacket(EnumCommand command, Coord pos) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(bytes);
		try {
			stream.writeUTF(command.toString());
			stream.writeInt(pos.x);
			stream.writeInt(pos.y);
			stream.writeInt(pos.z);

			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = Config.channel;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;
			Minecraft mc = Minecraft.getMinecraft();
			mc.thePlayer.sendQueue.addToSendQueue(packet);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	void updateBlockInfo() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.objectMouseOver == null) {
			return;
		}

		if (mc.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {
			blockCoord.x = mc.objectMouseOver.blockX;
			blockCoord.y = mc.objectMouseOver.blockY;
			blockCoord.z = mc.objectMouseOver.blockZ;
			sideHit  = mc.objectMouseOver.sideHit;
			targetBlockId = mc.theWorld.getBlockId(blockCoord.x, blockCoord.y, blockCoord.z);
			targetBlockMetadata  = mc.theWorld.getBlockMetadata(blockCoord.x, blockCoord.y, blockCoord.z);
		}
	}

	boolean isBreakBlock() {

		boolean isBreak = false;
		Minecraft mc = Minecraft.getMinecraft();
		try {
			int blockHitWait = (Integer) ModLoader.getPrivateValue(PlayerControllerMP.class, mc.playerController, 8);
			isBreak = (blockHitWait == 5 && blockHitWait != prev_blockHitWait );
			prev_blockHitWait = blockHitWait;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return isBreak;
	}
}
