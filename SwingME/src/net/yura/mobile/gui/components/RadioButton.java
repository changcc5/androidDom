/*
 *  This file is part of 'yura.net Swing ME'.
 *
 *  'yura.net Swing ME' is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  'yura.net Swing ME' is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with 'yura.net Swing ME'. If not, see <http://www.gnu.org/licenses/>.
 */
package net.yura.mobile.gui.components;

import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JRadioButton
 */
public class RadioButton extends Button {

	protected Icon selectedImage;
	protected Icon disabledSelectedImage;
	protected Icon focusedSelectedImage;

	/**
	 * @see javax.swing.JRadioButton#JRadioButton() JRadioButton.JRadioButton
	 */
	public RadioButton() {
		this("");
	}

	/**
	 * @param label
	 * @see javax.swing.JRadioButton#JRadioButton(java.lang.String) JRadioButton.JRadioButton
	 */
	public RadioButton(String label) {
		this(label, false);
	}

	/**
	 * @param string
	 * @param b
	 * @see javax.swing.JRadioButton#JRadioButton(java.lang.String, boolean) JRadioButton.JRadioButton
	 */
	public RadioButton(String string, boolean b) {
		super(string);
		setSelected(b);
		setHorizontalAlignment(Graphics.LEFT);
	}

	protected void toggleSelection() {
		setSelected(true);
	}

	/**
	 * @see javax.swing.AbstractButton#getRolloverSelectedIcon() AbstractButton.getRolloverSelectedIcon
	 */
	public Icon getRolloverSelectedIcon() {
            if (focusedSelectedImage!=null) {
		return focusedSelectedImage;
            }
            return (Icon) theme.getProperty("icon",  Style.SELECTED | Style.FOCUSED);
	}

	/**
	 * @see javax.swing.AbstractButton#setRolloverSelectedIcon() AbstractButton.setRolloverSelectedIcon
	 */
	public void setRolloverSelectedIcon(Icon focusedSelectedImage) {
		this.focusedSelectedImage = focusedSelectedImage;
	}

	/**
	 * @see javax.swing.AbstractButton#getSelectedIcon() AbstractButton.getSelectedIcon
	 */
	public Icon getSelectedIcon() {
            if (selectedImage!=null) {
		return selectedImage;
            }
            return (Icon) theme.getProperty("icon", Style.SELECTED);
	}

	/**
	 * @see javax.swing.AbstractButton#setSelectedIcon(javax.swing.Icon) AbstractButton.setSelectedIcon
	 */
	public void setSelectedIcon(Icon selectedImage) {
		this.selectedImage = selectedImage;
	}

	/**
	 * @see javax.swing.AbstractButton#setDisabledSelectedIcon(javax.swing.Icon) AbstractButton.setDisabledSelectedIcon
	 */
	public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
		disabledSelectedImage = disabledSelectedIcon;
	}

	/**
	 * @see javax.swing.AbstractButton#getDisabledSelectedIcon() AbstractButton.getDisabledSelectedIcon
	 */
	public Icon getDisabledSelectedIcon() {
            if (disabledSelectedImage!=null) {
                return disabledSelectedImage;
            }
            return (Icon) theme.getProperty("icon", Style.DISABLED | Style.SELECTED);
	}

	public String getDefaultName() {
		return "RadioButton";
	}

	protected void paintIcon(Graphics2D g, int x, int y) {

                int cState = getCurrentState();

                // we need to do the check for isSelected()
                // instead of (cState&Style.SELECTED)!=0
                // as setState that is used by cell renderers
                // should not affect the "value" of this item
                // is this case the selected state is a display of the value

                // we need to use the state here as when we pass this component though to the icon, if the icon
                // use the getState it will differ from what state we have here, this problem comes up in Android Native Theme

                boolean selected = (cState&Style.SELECTED)!=0; // isSelected();

                Icon disabledSelectedImage = getDisabledSelectedIcon();
                Icon focusedSelectedImage = getRolloverSelectedIcon();
                Icon selectedImage = getSelectedIcon();
                
		if (selected && (cState&Style.DISABLED)!=0 && disabledSelectedImage != null) {
			disabledSelectedImage.paintIcon(this, g, x, y);
		}
                else if(selected && (cState&Style.FOCUSED)!=0 && focusedSelectedImage!=null){
			focusedSelectedImage.paintIcon(this, g, x, y);
		}
                else if (selected && selectedImage!=null) {
			selectedImage.paintIcon(this, g, x, y);
		}
                else {
			super.paintIcon(g, x, y);
		}
	}
}
