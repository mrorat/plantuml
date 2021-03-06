/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 *
 * Original Author:  Arnaud Roques
 * 
 *
 */
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.Margins;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.svek.TextBlockBackcolored;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GraphicStrings extends AbstractTextBlock implements IEntityImage {

	private final double margin = 5;

	private final HtmlColor background;

	private final UFont font;

	private final HtmlColor maincolor;

	private final HtmlColor hyperlinkColor = HtmlColorUtils.BLUE;

	private final boolean useUnderlineForHyperlink = true;

	private final List<String> strings;

	private final BufferedImage image;

	private final GraphicPosition position;

	public static IEntityImage createForError(List<String> strings, boolean useRed) {
		if (useRed) {
			return new GraphicStrings(strings, UFont.sansSerif(14).bold(), HtmlColorUtils.BLACK,
					HtmlColorUtils.RED_LIGHT, null, null);
		}
		return new GraphicStrings(strings, UFont.sansSerif(14).bold(), HtmlColorSet.getInstance().getColorIfValid(
				"#33FF02"), HtmlColorUtils.BLACK, null, null);
	}

	public static TextBlockBackcolored createGreenOnBlackMonospaced(List<String> strings) {
		return new GraphicStrings(strings, monospaced14(), HtmlColorUtils.GREEN, HtmlColorUtils.BLACK, null, null);
	}

	public static TextBlockBackcolored createBlackOnWhite(List<String> strings) {
		return new GraphicStrings(strings, sansSerif12(), HtmlColorUtils.BLACK, HtmlColorUtils.WHITE, null, null);
	}

	public static TextBlockBackcolored createBlackOnWhiteMonospaced(List<String> strings) {
		return new GraphicStrings(strings, monospaced14(), HtmlColorUtils.BLACK, HtmlColorUtils.WHITE, null, null);
	}

	public static TextBlockBackcolored createBlackOnWhite(List<String> strings, BufferedImage image,
			GraphicPosition position) {
		return new GraphicStrings(strings, sansSerif12(), HtmlColorUtils.BLACK, HtmlColorUtils.WHITE, image, position);
	}

	private static UFont sansSerif12() {
		return UFont.sansSerif(12);
	}

	private static UFont monospaced14() {
		return UFont.monospaced(14);
	}

	private GraphicStrings(List<String> strings, UFont font, HtmlColor maincolor, HtmlColor background,
			BufferedImage image, GraphicPosition position) {
		this.strings = strings;
		this.font = font;
		this.maincolor = maincolor;
		this.background = background;
		this.image = image;
		this.position = position;
	}

	private TextBlock getTextBlock() {
		TextBlock result = null;
		result = Display.create(strings).create(
				new FontConfiguration(font, maincolor, hyperlinkColor, useUnderlineForHyperlink),
				HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		// result = DateEventUtils.addEvent(result, green);
		return result;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(new UTranslate(margin, margin));
		final Dimension2D size = calculateDimensionInternal(ug.getStringBounder());
		getTextBlock().drawU(ug.apply(new UChangeColor(maincolor)));

		if (image != null) {
			if (position == GraphicPosition.BOTTOM) {
				ug.apply(new UTranslate((size.getWidth() - image.getWidth()) / 2, size.getHeight() - image.getHeight()))
						.draw(new UImage(image));
			} else if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
				ug.apply(new UTranslate(size.getWidth() - image.getWidth(), size.getHeight() - image.getHeight()))
						.draw(new UImage(image));
			} else if (position == GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT) {
				ug.apply(new UTranslate(size.getWidth() - image.getWidth() - 1, 1)).draw(new UImage(image));
			}
		}
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return Dimension2DDouble.delta(calculateDimensionInternal(stringBounder), 2 * margin);
	}

	private Dimension2D calculateDimensionInternal(StringBounder stringBounder) {
		Dimension2D dim = getTextBlock().calculateDimension(stringBounder);
		if (image != null) {
			if (position == GraphicPosition.BOTTOM) {
				dim = new Dimension2DDouble(dim.getWidth(), dim.getHeight() + image.getHeight());
			} else if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
				dim = new Dimension2DDouble(dim.getWidth() + image.getWidth(), dim.getHeight());
			} else if (position == GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT) {
				dim = new Dimension2DDouble(dim.getWidth() + image.getWidth(), dim.getHeight());
			}
		}
		return dim;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public HtmlColor getBackcolor() {
		return background;
	}

	public Margins getShield(StringBounder stringBounder) {
		return Margins.NONE;
	}

	public boolean isHidden() {
		return false;
	}
	
	public double getOverscanX(StringBounder stringBounder) {
		return 0;
	}


}
