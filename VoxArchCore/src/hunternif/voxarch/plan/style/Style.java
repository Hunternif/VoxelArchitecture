package hunternif.voxarch.plan.style;

/**
 * An aggregator for all architectural style information.
 * @author Hunternif
 */
public class Style {
	public final Geometry geometry;
	public final Elements elements;
	public final Materials materials;
	
	public Style(Geometry geometry, Elements elements, Materials materials) {
		this.geometry = geometry;
		this.elements = elements;
		this.materials = materials;
	}
}
