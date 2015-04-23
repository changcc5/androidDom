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

package net.yura.mobile.gui.plaf;

import java.util.Enumeration;
import java.util.Hashtable;
import net.yura.mobile.gui.Font;
import net.yura.mobile.gui.border.Border;
import net.yura.mobile.util.SystemUtil;

/**
 * @author Yura Mamyrin
 * @see javax.swing.plaf.synth.SynthStyle
 * @see javax.swing.plaf.ComponentUI
 */
public class Style {

    public static final int NO_COLOR = 0;

    public static final int ALL = 0;
    public static final int SELECTED = 8;
    //public static final int ENABLED = 1;
    public static final int DISABLED = 2;
    public static final int FOCUSED = 4;
    private static final int[] searchOrder = new int[] { SELECTED , FOCUSED , DISABLED };
    
    private Font font;
    private Border border;
    private Integer background;
    private Integer foreground;
    private Hashtable properties;

    private Hashtable fontStates;
    private Hashtable borderStates;
    private Hashtable backgroundStates;
    private Hashtable foregroundStates;
    private Hashtable propertiesStates;

    public Style() {
    }
    
    public Style(Style st) {
        putAll(st);
    }

    public void putAll(Style st) {

        if (st.font!=null) {
            font = st.font;
        }
        if (st.border!=null) {
            border = st.border;
        }
        if (st.background!=null) {
            background = st.background;
        }
        if (st.foreground!=null) {
            foreground = st.foreground;
        }
        if (st.properties!=null) {
            if (properties==null) properties = new Hashtable();
            SystemUtil.hashtablePutAll(st.properties,properties);
        }




        if (st.fontStates!=null) {
            if (fontStates==null) fontStates = new Hashtable();
            SystemUtil.hashtablePutAll(st.fontStates,fontStates);
        }
        if (st.borderStates!=null) {
            if (borderStates==null) borderStates = new Hashtable();
            SystemUtil.hashtablePutAll(st.borderStates,borderStates);
        }
        if (st.backgroundStates!=null) {
            if (backgroundStates==null) backgroundStates = new Hashtable();
            SystemUtil.hashtablePutAll(st.backgroundStates,backgroundStates);
        }
        if (st.foregroundStates!=null) {
            if (foregroundStates==null) foregroundStates = new Hashtable();
            SystemUtil.hashtablePutAll(st.foregroundStates,foregroundStates);
        }
        if (st.propertiesStates!=null) {
            if (propertiesStates==null) propertiesStates = new Hashtable();
            Enumeration en = st.propertiesStates.keys();
            while (en.hasMoreElements()) {
                Object key = en.nextElement();
                Hashtable src = (Hashtable)st.propertiesStates.get(key);
                Hashtable dest = (Hashtable)propertiesStates.get(key);
                if (dest==null) {
                    dest = new Hashtable();
                    propertiesStates.put(key, dest);
                }
                SystemUtil.hashtablePutAll(src,dest);
            }
        }

    }


    private Object getValueFromMap(Hashtable t,int state,Object defaultObject) {
        if (state==ALL || t==null) return defaultObject;
        Object a;
        a = t.get(new Integer(state));
        
        // as to indicate that something is selected is quite important
        // we will try and get the value of this
        for (int c=0;c<searchOrder.length;c++) {
            if (a!=null) {
                break;
            }
            if ((state & searchOrder[c])!=0) {
                a = t.get(new Integer(searchOrder[c]));
            }
        }

        return a==null?defaultObject:a;
    }
    
    public void addBorder(Border b, int state) {
        if (state==ALL) { border=b; return; }
        if (borderStates==null) {
            borderStates = new Hashtable();
        }
        borderStates.put(new Integer(state), b);
    }
    public Border getBorder(int state) {
        return (Border)getValueFromMap(borderStates,state,border);
    }
    
    public void addFont(Font b, int state) {
        if (state==ALL) { font=b; return; }
        if (fontStates==null) {
            fontStates = new Hashtable();
        }
        fontStates.put(new Integer(state), b);
    }
    public Font getFont(int state) {
        return (Font)getValueFromMap(fontStates,state,font);
    }
    
    
    public void addBackground(int b, int state) {
        Integer i = new Integer(b);
        if (state==ALL) { background=i; return; }
        if (backgroundStates==null) {
            backgroundStates = new Hashtable();
        }
        backgroundStates.put(new Integer(state), i);
    }
    public int getBackground(int state) {
        Integer i = (Integer)getValueFromMap(backgroundStates,state,background);
        return i==null?NO_COLOR:i.intValue();
    }
    
    public void addForeground(int b, int state) {
        Integer i = new Integer(b);
        if (state==ALL) { foreground=i; return; }
        if (foregroundStates==null) {
            foregroundStates = new Hashtable();
        }
        foregroundStates.put(new Integer(state), i);
    }
    public int getForeground(int state) {
        Integer i = (Integer)getValueFromMap(foregroundStates,state,foreground);
        return i==null?NO_COLOR:i.intValue();
    }
    
    public void addProperty(Object b, String key, int state) {
        if (state==ALL) { 
            if (properties==null) {
                properties = new Hashtable();
            }
            properties.put(key, b);
            return;
        }
        if (propertiesStates==null) {
            propertiesStates = new Hashtable();
        }
        Hashtable table = (Hashtable)propertiesStates.get(new Integer(state));
        if (table==null) {
            table = new Hashtable();
            propertiesStates.put(new Integer(state), table);
        }
        
        table.put(key, b);
    }
    
    public int getIntProperty(String key, int state) {
        
        Integer value = (Integer)getProperty(key, state);
        
        if(value != null) {
            return value.intValue();
        }
        
        return 0; // Yes, its zero. If I've not set a value, it's zero. ZERO! Not (-1), its totally and utterly zero.
    }
    
    public Object getProperty(String key,int state) {
        if (state==ALL || propertiesStates==null) {
            if (properties==null) {
                return null;
            }
            return properties.get(key);
        }
        
        Object a=null;
        Hashtable t = (Hashtable)propertiesStates.get(new Integer(state));
        if (t!=null) {
            a = t.get(key);
        }

        // as to indicate that something is selected is quite important
        // we will try and get the value of this
        for (int c=0;c<searchOrder.length;c++) {
            if (a!=null) {
                break;
            }
            if ((state & searchOrder[c])!=0) {
                t = (Hashtable)propertiesStates.get(new Integer(searchOrder[c]));
                if (t!=null) {
                    a = t.get(key);
                }
            }
        }

        return a;
    }

    protected void reset() {
        font = null;
        border= null;
        background = null;
        foreground = null;
        properties = null;

        fontStates = null;
        borderStates = null;
        backgroundStates = null;
        foregroundStates = null;
        propertiesStates = null;
    }

    //#mdebug debug
    public String toString() {
        return "Style "+font+" "+border+" "+background+" "+foreground+" "+properties+" "+fontStates+" "+borderStates+" "+backgroundStates+" "+foregroundStates+" "+propertiesStates;
    }
    //#enddebug

}
