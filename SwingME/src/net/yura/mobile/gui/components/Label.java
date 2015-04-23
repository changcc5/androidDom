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
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.plaf.Style;
import net.yura.mobile.util.Option;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JLabel
 */
public class Label extends Component {

	public static String extension = "...";

	protected Font font;

	protected String string;
	protected Icon icon;
	protected int padding=2;
	protected int gap=2;

        protected int alignment;
        protected int textPosition;

        /**
         * @see javax.swing.JLabel#JLabel() JLabel.JLabel
         */
        public Label() {
            this((String)null);
        }

        /**
         * @param text The text to be displayed by the label
         * @see javax.swing.JLabel#JLabel(java.lang.String) JLabel.JLabel
         */
	public Label(String text) {

		focusable = false;

                // swing will set a empty string as the value if you pass in null to the constructor
                // but if u call setText(null) then the text will be null, (getText() will return null)
                // but it will still display as a empty string)
                // in J2SE Swing this does NOT call setText(), so here it does not either!
		string = text==null?"":text;

                alignment = Graphics.VCENTER | Graphics.LEFT;

                textPosition = Graphics.VCENTER | Graphics.RIGHT;

	}

        /**
         * @param icon The image to be displayed by the label
         * @see javax.swing.JLabel#JLabel(javax.swing.Icon) JLabel.JLabel
         */
	public Label(Icon icon) {
		this();
                setIcon(icon);
		setHorizontalAlignment(Graphics.HCENTER);
	}

        /**
         * @param text The text to be displayed by the label
         * @param icon The image to be displayed by the label
         * @see javax.swing.JLabel#JLabel(java.lang.String, javax.swing.Icon, int) JLabel.JLabel
         */
        public Label(String text,Icon icon) {
                this(text);
                setIcon(icon);
        }

        /**
         * @see TextComponent#setMargin(int)
         * @see javax.swing.AbstractButton#setMargin(java.awt.Insets) AbstractButton.setMargin
         */
        public void setMargin(int padding) {
            this.padding = padding;
        }

        /**
         * @return The padding of the label
         * @see TextComponent#getMargin()
         * @see javax.swing.AbstractButton#getMargin() AbstractButton.getMargin
         */
        public int getMargin() {
            return padding;
        }

	protected void workoutMinimumSize() {

		int w = getCombinedWidth();
		int h = getCombinedHeight();

		if (w==0 && h==0) {
			width=0;
			height=0;
		}
		else {
                    width = w + (padding*2);
                    height = h + (padding*2);

                    int max = getMaxWidth();
                    if (width > max) {
                        width = max;
                    }
		}

	}

	public void paintComponent(Graphics2D g) {

		int x=padding;
		int y=padding;
                int iconWidth = getIconWidth();

                String drawString = this.string;
                if ("".equals(drawString)) { drawString=null; } // when you draw, null or empty string are the same thing
                
		int combinedwidth = getCombinedWidth();

                if (font!=null && drawString!=null && combinedwidth > width-(padding*2)) {
                    combinedwidth = width-(padding*2);
                    int w = ((((textPosition & Graphics.HCENTER) == 0) && iconWidth>0)?(combinedwidth-iconWidth-gap):combinedwidth) - font.getWidth(extension);
                    int a = TextArea.searchStringCharOffset(drawString, font, w);
                    drawString = drawString.substring(0, a)+extension;
                }

		int combinedheight = getCombinedHeight();

		if ((alignment & Graphics.HCENTER) != 0) {
			x = (width - combinedwidth)/2;
		}
		else if ((alignment & Graphics.RIGHT) != 0) {
			x = (width - combinedwidth) -padding;
		}

		if ((alignment & Graphics.VCENTER) != 0) {
			y = (height - combinedheight)/2;
		}
		else if ((alignment & Graphics.BOTTOM) != 0) {
			y = (height - combinedheight) -padding;
		}


		if (iconWidth>0) {

			int ix=x;
			int iy=y;

			if ((textPosition & Graphics.HCENTER) != 0) {

				ix = x + (combinedwidth - iconWidth)/2;

			}
			else if ((textPosition & Graphics.LEFT) != 0 && font!=null && drawString!=null) {

				ix = x + font.getWidth(drawString)+gap;

			}

			if ((textPosition & Graphics.VCENTER) != 0 || (textPosition & Graphics.RIGHT) != 0 || (textPosition & Graphics.LEFT) != 0 ) {

				iy = y + (combinedheight - getIconHeight())/2;

			}
			else if ((textPosition & Graphics.TOP) != 0 && font!=null && drawString!=null) {

				iy = y + font.getHeight()+gap;

			}

			paintIcon( g, ix, iy  );
		}

		if (font!=null && drawString!=null) {

			int tx = x;
			int ty = y;

			if ((textPosition & Graphics.HCENTER) != 0) {

				tx = x + (combinedwidth - font.getWidth(drawString))/2;

			}
			else if ((textPosition & Graphics.RIGHT) != 0 && iconWidth>0) {

				tx = x + iconWidth+gap;

			}

			if ((textPosition & Graphics.VCENTER) != 0) {

				ty = y + (combinedheight - font.getHeight())/2;

			}
			else if ((textPosition & Graphics.BOTTOM) != 0 && iconWidth>0) {

				ty = y + getIconHeight()+gap;

			}


			g.setColor( getForeground() );
                        g.setFont(font);
			g.drawString( drawString, tx,ty );
		}

	}

        protected void paintIcon(Graphics2D g, int x, int y) {
            getIcon().paintIcon(this, g, x, y);
        }

	protected int getCombinedWidth() {
            return getCombinedWidth(string,getIconWidth());
	}

        protected int getCombinedWidth(String string,int iconWidth) {
            	int fw = (font!=null&&string!=null)?font.getWidth(string):0;
		if ((textPosition & Graphics.HCENTER) != 0) {
			if (iconWidth<=0 && font == null) return 0;
			if (iconWidth>0) {
				return (iconWidth > fw)?iconWidth:fw;
			}
			return fw;
		}
		else {
			if (iconWidth>0) { fw=    ((fw==0)?0:gap)     +fw+iconWidth; }
			return fw;
		}
        }

        protected int getCombinedHeight() {
            return getCombinedHeight(getIconHeight());
        }

	protected int getCombinedHeight(int iconHeight) {

            // how to work out height based on Text Position
            //  M=Max S=Sum

            //    M--S--M
            //    M--M--M
            //    M--S--M

		int fw = (font!=null && !"".equals(string) && string != null)?font.getHeight():0;
		if ((textPosition & Graphics.VCENTER)!= 0 || ((textPosition & Graphics.RIGHT)!= 0) || ((textPosition & Graphics.LEFT)!= 0) ) {
			if (iconHeight<=0 && font == null) return 0;
			if (iconHeight>0) {
				return (iconHeight > fw)?iconHeight:fw;
			}
			return fw;
		}
                else { // only add if HCENTER AND (TOP OR BOTTOM)
			if (iconHeight>0) { fw=    ((fw==0)?0:gap)     +fw+iconHeight; }
			return fw;
		}
	}

        /**
         * @param a The text of the label
         * @see javax.swing.JLabel#setText(java.lang.String) JLabel.setText
         */
	public void setText(String a) {
            String oldValue = string;
            string = a;
            // this is taken from real Swing
            if (string == null || oldValue == null || !string.equals(oldValue)) {
                repaint();
            }
	}

        /**
         * @return The text of the label
         * @see javax.swing.JLabel#getText() JLabel.getText
         */
        public String getText() {
		return string;
	}

        /**
         * @return The font of the label
         * @see java.awt.Component#getFont() Component.getFont
         */
	public Font getFont() {
		return font;
	}

        /**
         * @param font The font of the label
         * @see javax.swing.JComponent#setFont(java.awt.Font) JComponent.setFont
         */
	public void setFont(Font font) {
		this.font = font;

	}

        /**
         * @return The icon of the label
         * @see javax.swing.JLabel#getIcon() JLabel.getIcon
         */
	public Icon getIcon() {
            if (icon!=null) {
		return icon;
            }
            return (Icon) theme.getProperty("icon", Style.ALL);
	}

        /**
         * @param icon The icon of the label
         * @see javax.swing.JLabel#setIcon(javax.swing.Icon) JLabel.setIcon
         */
	public void setIcon(Icon icon) {
            Icon oldValue = this.icon;
            this.icon = icon;
            // this is taken from real Swing
            if (this.icon != oldValue) {
                repaint();
            }
	}

	/**
         * @param a One of the following constants defined in Graphics: LEFT, HCENTER, RIGHT (the default)
         * @see javax.swing.JLabel#setHorizontalTextPosition(int) JLabel.setHorizontalTextPosition
         */
	public void setHorizontalTextPosition(int a) {
		textPosition = getVerticalTextPosition() | a;
	}

        /**
         * @param a One of the following constants defined in Graphics: TOP, VCENTER (the default), or BOTTOM
         * @see javax.swing.JLabel#setVerticalTextPosition(int) JLabel.setVerticalTextPosition
         */
	public void setVerticalTextPosition(int a) {
		textPosition = a | getHorizontalTextPosition();
	}

        /**
         * @param a One of the following constants defined in Graphics: LEFT (the default for text-only labels), HCENTER (the default for image-only labels), RIGHT
         * @see javax.swing.JLabel#setHorizontalAlignment(int) JLabel.setHorizontalAlignment
         */
        public void setHorizontalAlignment(int a) {
            alignment = getVerticalAlignment() | a;
        }

        /**
         * @param a One of the following constants defined in Graphics: TOP, VCENTER (the default), or BOTTOM
         * @see javax.swing.JLabel#setVerticalAlignment(int) JLabel.setVerticalAlignment
         */
        public void setVerticalAlignment(int a) {
            alignment = a | getHorizontalAlignment();
        }

        /**
         * @param iconTextGap The gap, default is 2
         * @see javax.swing.JLabel#setIconTextGap(int) JLabel.setIconTextGap
         */
        public void setIconTextGap(int iconTextGap) {
            gap = iconTextGap;
        }

        /**
         * @see javax.swing.JLabel#getHorizontalTextPosition() JLabel.getHorizontalTextPosition
         */
        public int getHorizontalTextPosition() {
            return ((textPosition&Graphics.LEFT)!=0?Graphics.LEFT:((textPosition&Graphics.RIGHT)!=0?Graphics.RIGHT:Graphics.HCENTER));
        }

        /**
         * @see javax.swing.JLabel#getVerticalTextPosition() JLabel.getVerticalTextPosition
         */
        public int getVerticalTextPosition() {
            return ((textPosition&Graphics.TOP)!=0?Graphics.TOP:((textPosition&Graphics.BOTTOM)!=0?Graphics.BOTTOM:Graphics.VCENTER));
        }

        /**
         * @see javax.swing.JLabel#getHorizontalAlignment() JLabel.getHorizontalAlignment
         */
        public int getHorizontalAlignment() {
            return ((alignment&Graphics.LEFT)!=0?Graphics.LEFT:((alignment&Graphics.RIGHT)!=0?Graphics.RIGHT:Graphics.HCENTER));
        }

        /**
         * @see javax.swing.JLabel#getVerticalAlignment() JLabel.getVerticalAlignment
         */
        public int getVerticalAlignment() {
            return ((alignment&Graphics.TOP)!=0?Graphics.TOP:((alignment&Graphics.BOTTOM)!=0?Graphics.BOTTOM:Graphics.VCENTER));
        }

        public void setValue(Object obj) {

            String drawString=null;
            Icon image=null;
            String tip=null;

            if (obj instanceof Option) {
                Option option = ((Option)obj);
                drawString = option.getValue();
                image = option.getIcon();
                tip = option.getToolTip();
            }
            else if (obj!=null) {
                drawString = String.valueOf(obj);
            }

//            if (drawString!=null) {
//                int a = getCombinedWidth(drawString, image!=null?image.getWidth():0);
//                int w = getMaxTextWidth();
//
//                if (a > w) {
//                    int maxCharacters = Math.min((w/getFont().getWidth('0')),drawString.length());
//                    drawString = drawString.substring(0, maxCharacters) + extension;
//                }
//            }

            // calling setText causes thing like spinner that throw in the setText to stop working
            string = drawString; // dont want to recalc everything twice
            setIcon(image);
            setToolTipText(tip);
        }

        public int getMaxWidth() {

            // if the width has not been set yet
            // we will assume as can take the default amount

            Border b = getInsets();

            int resonableTextLength = getDesktopPane().getWidth() - ( b.getLeft()+b.getRight() );
            int minimumIconWidth = getIconWidth() + padding *2;

            return Math.max(resonableTextLength,minimumIconWidth);

        }

        public void updateUI() {
            super.updateUI();
            font = theme.getFont(Style.ALL);
            Integer iconTextGap = (Integer)theme.getProperty("iconTextGap", Style.ALL);
            if (iconTextGap!=null) {
                gap = iconTextGap.intValue();
            }
        }

        private int getIconWidth() {
            Icon icon = getIcon();
            return icon!=null?icon.getIconWidth():0;
        }

        private int getIconHeight() {
            Icon icon = getIcon();
            return icon!=null?icon.getIconHeight():0;
        }

        protected String getDefaultName() {
            return "Label";
        }

        //#mdebug debug
	public String toString() {
            return super.toString() +"("+string+")";
        }
        //#enddebug

}
