package hunternif.voxarch.gen.impl;

import hunternif.voxarch.gen.ElementGenerator;
import hunternif.voxarch.gen.Materials;
import hunternif.voxarch.plan.Prop;
import hunternif.voxarch.storage.BlockData;
import hunternif.voxarch.storage.IBlockStorage;

public class OneBlockPropGen implements ElementGenerator.Prop {

	private final String blockName;
	
	public OneBlockPropGen(String blockName) {
		super();
		this.blockName = blockName;
	}

	@Override
	public void generateProp(IBlockStorage dest, Prop prop, Materials materials) {
		BlockData block = materials.oneBlockProp(blockName);
		if (block != null) {
			dest.setBlock(0, 0, 0, block);
		}
	}

}
