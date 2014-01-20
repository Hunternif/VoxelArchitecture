package hunternif.voxarch.mc;

import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.util.BlockOrientation;
import hunternif.voxarch.util.MathUtil;
import coolalias.structuregenapi.util.GenHelper;

/**
 * Minecraft-specific block data. It Uses coolAlias's StructureGenerationAPI
 * to set rotation-based metadata.
 * @author Hunternif
 */
public class BlockDataMC extends BlockData {

	public BlockDataMC(int id, int metadata) {
		super(id, metadata);
	}

	@Override
	public void setOrientaion(BlockOrientation orient) {
		if (orient == BlockOrientation.NONE) {
			// Is this safe?
			setMetadata(0);
		} else {
			//TODO: test this in Minecraft
			// The number of rotations the method GenHelper.getMetadata() accepts is clockwise:
			int rotations = (int) MathUtil.clampAngle(getOrientaion().angle - orient.angle) / 90;
			setMetadata(GenHelper.getMetadata(rotations, getId(), getMetadata()));
		}
		
		super.setOrientaion(orient);
	}

}
