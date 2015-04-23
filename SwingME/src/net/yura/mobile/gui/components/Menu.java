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

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import net.yura.mobile.gui.DesktopPane;
import net.yura.mobile.gui.Graphics2D;
import net.yura.mobile.gui.Icon;
import net.yura.mobile.gui.KeyEvent;
import net.yura.mobile.gui.Midlet;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.JMenu
 */
public class Menu extends Button {

        // this does not work on BlackBerry as when you override it, it gets reset back to true
        // http://wiki.softartisans.com/display/BLOGS/2010/07/12/Static+initialization+in+the+BlackBerry+JVM
        // public static boolean LOOP_MENU = true; // this is now in DesktopPane#UIManager
    
        //private boolean useAnimation=true;
        private boolean open;
        private int slide = Graphics.BOTTOM;
        private int destX;
        private int destY;
        private Icon arrowDirection;

        public Menu() {
            popup = makePopup();

            // TODO ???
            //arrowDirection = Graphics.RIGHT;
            //setHorizontalAlignment(Graphics.LEFT);
        }

        /**
         * @param string the text for the menu label
         * @see javax.swing.JMenu#JMenu(java.lang.String) JMenu.JMenu
         */
        public Menu(String string) {
            this();
            setText(string);
        }

/*
        public void processMouseEvent(int type, int x, int y, KeyEvent keys) {
            super.processMouseEvent(type, x, y, keys);
            // do not wait for the mouse up. we want to fire right away
            if (isSelected()) {
                // can not do this, as fireActionPerformed also makes a button unselected!
                fireActionPerformed();
            }
        }
*/

        public void fireActionPerformed() {
            super.fireActionPerformed();
            setPopupMenuVisible(true);
        }

        /**
         * @param c The component to append to the menu
         * @see javax.swing.JMenu#add(java.awt.Component) JMenu.add
         */
        protected void addImpl(Component c, Object cons, int index) {
            getPopupMenu(popup).insert(c,index);
        }

        /**
         * @see javax.swing.JMenu#removeAll() JMenu.removeAll
         */
        public void removeAll() {
            getPopupMenu(popup).removeAll();
        }

        public Button findMneonicButton(int mnu) {
            return getPopupMenu(popup).findMneonicButton(mnu);
        }

	protected void workoutMinimumSize() {
            super.workoutMinimumSize();

            if (!isTopLevelMenu()) {
                width = width + (arrowDirection!=null?(arrowDirection.getIconWidth()+gap):0);
            }
	}

	public void paintComponent(Graphics2D g) {
            super.paintComponent(g);
            
            if (!isTopLevelMenu()) {
                if (arrowDirection!=null) {
                    arrowDirection.paintIcon(this, g, width-padding-arrowDirection.getIconWidth(), (height - arrowDirection.getIconHeight())/2 );
                }
            }
	}

        public void setMenuRenderer(ListCellRenderer renderer) {
            getPopupMenu(popup).setCellRenderer(renderer);
        }

        public void updateUI() {
            super.updateUI();
            arrowDirection = (Icon)theme.getProperty("arrow", Style.ALL);
        }

        /**
         * @see javax.swing.JMenu#addSeparator() JMenu.addSeparator
         */
        public void addSeparator() {
            add( makeSeparator() );
        }

        /**
         * @see javax.swing.JMenu#getMenuComponents() JMenu.getMenuComponents
         */
        public Vector getMenuComponents() {
            MenuBar bar = getPopupMenu(popup);
            return bar.getItems();
        }

        /**
         * @see javax.swing.JSeparator
         */
        public static Component makeSeparator() {
            Label separator = new Label();
            separator.setPreferredSize(-1, 1);
            separator.setName("Separator");
            return separator;
        }

        /**
         * @see javax.swing.JMenu#getItemCount() JMenu.getItemCount
         */
        public int getItemCount() {
            return getPopupMenu(popup).getComponentCount();
        }

        /**
         * @see java.awt.Container#getComponents() Container.getComponents
         */
        public Vector getComponents() {
            return getPopupMenu(popup).getItems();
        }

        /**
         * @see javax.swing.JMenu#remove(java.awt.Component) JMenu.remove
         */
        public void remove(Component c) {
            getPopupMenu(popup).remove(c);
        }

        /**
         * @see javax.swing.JPopupMenu
         */
        public static Window makePopup() {

            Window popup = new Window();
            popup.setCloseOnFocusLost(true);

            MenuBar menuItems = new MenuBar();
            menuItems.setLayoutOrientation(List.VERTICAL);
            menuItems.setLoop( (!Boolean.FALSE.equals( DesktopPane.get("LOOP_MENU") )) ); // true by default

            popup.addWindowListener(menuItems);

            popup.add(new ScrollPane(menuItems));
            popup.setName("Menu");

            // SETUP RIGHT/BACK SOFT KEY

            Button cancel = new Button( (String)DesktopPane.get("cancelText") );
            cancel.setActionCommand(Frame.CMD_CLOSE);
            cancel.addActionListener(menuItems);
            cancel.setMnemonic(KeyEvent.KEY_END);
            popup.addCommand(cancel);

            // SETUP LEFT/MENU SOFT KEY

            if (Midlet.getPlatform()==Midlet.PLATFORM_ANDROID) {
                Button cancel2 = new Button( (String)DesktopPane.get("menuText") );
                cancel2.setActionCommand(Frame.CMD_CLOSE);
                cancel2.addActionListener(menuItems);
                cancel2.setMnemonic(KeyEvent.KEY_MENU);
                popup.addCommand(cancel2);
            }
            else {
                menuItems.setUseSelectButton(true);
            }

            return popup;
        }

        public static MenuBar getPopupMenu(Window popup) {
            return (MenuBar) ((ScrollPane)popup.getComponents().firstElement()).getView();
        }

        /**
         * @see javax.swing.JMenu#setPopupMenuVisible(boolean) JMenu#setPopupMenuVisible
         */
        public void setPopupMenuVisible(boolean vis) {
            if (vis) {

                // setup the owner of this popup
                Component parent1 = getParent();
                if (parent1 instanceof MenuBar) {
                    getPopupMenu(popup).owner = (MenuBar)parent1;
                }

                // just a help to make sure we open always on the correct DesktopPane, in the case there are more then one
                popup.setDesktopPane( getDesktopPane() );

                setupSize(popup);

                Border insets=getInsets();
                positionMenuRelativeTo(
                        popup,
                        getXOnScreen() - insets.getLeft(), getYOnScreen()- insets.getTop(), getWidthWithBorder(), getHeightWithBorder(),
                        isTopLevelMenu()?Graphics.TOP:Graphics.RIGHT
                    );

                setupSnap(popup);

                openMenuAtLocation();
            }
            // TODO else
        }

        private boolean isTopLevelMenu() {
            Component parent1 = getParent();
            // return true if we are NOT in a mneubar, or if the menubar is horizontal
            return !(parent1 instanceof MenuBar) || ((MenuBar)parent1).getLayoutOrientation() == List.HORIZONTAL;
        }

        public static void setupSize(Window window) {

            window.pack();

            DesktopPane dp = window.getDesktopPane();

            int w = window.getWidthWithBorder();
            int h = window.getHeightWithBorder();

            final int maxh = dp.getHeight() - dp.getMenuHeight()*2;

            if (h > maxh) {
                h = maxh;
                w = w + extraWidth(window);
            }

            // resize the popup if its bigger then the screen! if it is then shrink it
            if (w > dp.getWidth()) {
                w = dp.getWidth();
            }

            Border insets=window.getInsets();
            window.setSize(w-insets.getLeft()-insets.getRight(), h-insets.getTop()-insets.getBottom() );

        }

        public static void positionMenuRelativeTo(Window window,int x, int y, int width, int height,int direction) {

            DesktopPane dp = window.getDesktopPane();

            int w = window.getWidthWithBorder();
            int h = window.getHeightWithBorder();

            if (direction!=Graphics.RIGHT) {
                // the right x position of whatever opended me!
                int right = dp.getWidth() - x - width;
                //int bottom = dp.getHeight() - y - height;
                boolean up = (y+height/2 > dp.getHeight()/2);

                if (up) {
                    y = y-h;
                }
                else {
                    y = y+height;
                }

                // if the menu is on the right softkey, but does not touch the right side
                // when its open as its narrower then the softkey, its pushed to the edge
                if (x>0 && right==0) {
                    x = dp.getWidth() - w;
                }
                //else {
                //    x = x;
                //}
            }
            else {
                x = x+width;
            }

            Border insets=window.getInsets();
            window.setLocation(x+insets.getLeft(), y+insets.getTop());

            // will adjust the location to make sure it is on screen
            window.makeVisible();

        }

        private static int extraWidth(Panel p) {
            Vector children = p.getComponents();
            for (int c=0;c<children.size();c++) {
                Component comp = (Component)children.elementAt(c);
                if (comp instanceof ScrollPane) {
                    return ((ScrollPane)comp).getBarThickness();
                }
                else if (comp instanceof Panel) {
                    int e = extraWidth( (Panel)comp );
                    if (e>0) {
                        return e;
                    }
                }
            }
            return 0;
        }

        private static void setupSnap(Window popup) {

            DesktopPane dp = DesktopPane.getDesktopPane();

            boolean left = popup.getXWithBorder()==0;
            boolean top = popup.getYWithBorder()==0;
            boolean right = popup.getXWithBorder()+popup.getWidthWithBorder()==dp.getWidth();
            boolean bottom = popup.getYWithBorder()+popup.getHeightWithBorder()==dp.getHeight();
            //System.out.println("left="+left+" top="+top+" right="+right+" bottom="+bottom);

            popup.snap = (left?Graphics.LEFT:0) | (top?Graphics.TOP:0) | (right?Graphics.RIGHT:0) | (bottom?Graphics.BOTTOM:0);

        }

        /**
         * there is no method for this in Swing, Swing uses:
         * popup.show(parent, (invokerSize.width - popupSize.width) / 2, (invokerSize.height - popupSize.height) / 2);
         */
        public void openMenuInCentre() {

            //menuItems.workoutSize(); // what out what the needed size is
            //scroll.setPreferredSize(menuItems.getWidth(), menuItems.getHeight());
            popup.pack();

            popup.setLocationRelativeTo(null);

            // TODO, make sure it does not go over the edges
            // should be only 1 place that does this, optionpane already does this

            openMenuAtLocation();
        }

        private void openMenuAtLocation() {

            DesktopPane dp = getDesktopPane();

            // if this menu button is hidden then animate in, otherwise just show
            if ( !isVisible() ) {

                int x = popup.getXWithBorder();
                int y = popup.getYWithBorder();
                int w = popup.getInsets().getLeft();
                int h = popup.getInsets().getTop();

                if (slide==Graphics.BOTTOM) {
                    popup.setLocation(w+x,h+ dp.getHeight());
                }
                if (slide==Graphics.TOP) {
                    popup.setLocation(w+x, h-popup.getHeightWithBorder());
                }
                if (slide==Graphics.RIGHT) {
                    popup.setLocation(w+dp.getWidth(), h+y);
                }
                if (slide==Graphics.LEFT) {
                    popup.setLocation(w-popup.getWidthWithBorder(), h+y);
                }

                int offsetX = popup.getX() - popup.getXWithBorder();
                int offsetY = popup.getY() - popup.getYWithBorder();

                destX = x + offsetX;
                destY = y + offsetY;

                open = false;

                dp.animateComponent(this);
            }

            dp.add(popup);
        }

        public void run() throws InterruptedException {

            try {

                int travelDistance = 0;
                if (slide==Graphics.BOTTOM) {
                    travelDistance = popup.getY() - destY;
                }
                else if (slide==Graphics.TOP) {
                    travelDistance = destY - popup.getY();
                }
                else if (slide==Graphics.RIGHT) {
                    travelDistance = popup.getX() - destX;
                }
                else if (slide==Graphics.LEFT) {
                    travelDistance = destX - popup.getX();
                }

            	int menuMoveSpeed = travelDistance/3 + 1;
		int step = menuMoveSpeed/10 + 1;

                while (true) {

			menuMoveSpeed = menuMoveSpeed-step;

			if(menuMoveSpeed < step) {

				menuMoveSpeed = step;
			}

			if(open) {
				// TODO close the menu and fire the action
			}
			else {

				int pX = popup.getX(),pY = popup.getY();
                                if (slide==Graphics.BOTTOM) {
                                    pY = pY - menuMoveSpeed;
                                    if (pY < destY) { pY = destY; }
                                }
                                else if (slide==Graphics.TOP) {
                                    pY = pY + menuMoveSpeed;
                                    if (pY > destY) { pY = destY; }
                                }
                                else if (slide==Graphics.RIGHT) {
                                    pX = pX - menuMoveSpeed;
                                    if (pX < destX) { pX = destX; }
                                }
                                else if (slide==Graphics.LEFT) {
                                    pX = pX + menuMoveSpeed;
                                    if (pX > destX) { pX = destX; }
                                }

                                popup.getDesktopPane().repaintHole(popup);
                                popup.setLocation(pX, pY);
				popup.repaint();

				if(pY==destY && pX == destX) {
                                    break;
				}



			}

			wait(50);

		}

            }
            finally {
                if(open) {
                    // TODO
                }
                else {
                    open=true;

                    popup.getDesktopPane().repaintHole(popup);
                    popup.setLocation(destX, destY);
                    // this is not good enough as during the animation images may have been loaded
                    // or the size of the menu could have changed, so our destination is not good enough
                    // instead what we need to do is just make sure it is on the screen
                    popup.makeVisible();
                    popup.repaint();
                }
            }

        }

}
