package hunternif.voxarch.util;

/**
 * Can be useful for removing non-terrain blocks in the construction area, such
 * as trees and grass.
 * @author Hunternif
 */
public interface IBlockFilter {
	boolean accept(BlockData block);
}
