package treebreaker;

import net.minecraft.block.Block;

public class BlockInfo {
	public int blockId = 0;
	public int metadata = -1;

	public BlockInfo(int blockId) {
		this.blockId = blockId;
	}

	public BlockInfo(int blockId, int metadata) {
		this.blockId = blockId;
		this.metadata = metadata;
	}

	public BlockInfo(BlockInfo b) {
		this.blockId = b.blockId;
		this.metadata = b.metadata;
	}

	@Override
	public int hashCode() {
		return 13 * 13 * blockId + metadata;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof BlockInfo) {
			BlockInfo b = (BlockInfo) obj;
			if (blockId == b.blockId) {
				if(metadata == -1 || b.metadata == -1) {
					return true;
				}
				return metadata == b.metadata;
			}
			if (blockId == Block.dirt.blockID && b.blockId == Block.grass.blockID) {
				return true;
			}
			if (blockId == Block.grass.blockID && b.blockId == Block.dirt.blockID) {
				return true;
			}
			if (blockId == Block.oreRedstone.blockID && b.blockId == Block.oreRedstoneGlowing.blockID) {
				return true;
			}
			if (blockId == Block.oreRedstoneGlowing.blockID && b.blockId == Block.oreRedstone.blockID) {
				return true;
			}
		}
		return false;
	}

}
