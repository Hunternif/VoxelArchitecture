package hunternif.voxarch.mc;

import hunternif.voxarch.util.BlockOrientation;
import hunternif.voxarch.util.MathUtil;
import hunternif.voxarch.util.OrientableBlockData;
import coolalias.structuregenapi.util.GenHelper;

public class OrientableBlockDataMC extends OrientableBlockData {

	public OrientableBlockDataMC(int id, int metadata) {
		super(id, metadata);
	}

	@Override
	public void setOrientaion(BlockOrientation orient) {
		if (orient == BlockOrientation.NONE) {
			// Is this safe?
			metadata = 0;
		} else {
			//TODO: test this in Minecraft
			// The number of rotations the method GenHelper.getMetadata() accepts is clockwise:
			int rotations = (int) MathUtil.clampAngle(getOrientaion().angle - orient.angle) / 90;
			metadata = GenHelper.getMetadata(rotations, id, metadata);
		}
		
		super.setOrientaion(orient);
	}

}
