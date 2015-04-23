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

package net.yura.mobile.gui.celleditor;

import net.yura.mobile.gui.cellrenderer.ListCellRenderer;
import net.yura.mobile.gui.components.Component;
import net.yura.mobile.gui.components.Button;
import net.yura.mobile.gui.components.Table;
import net.yura.mobile.gui.plaf.Style;

/**
 * @author Yura Mamyrin
 * @see javax.swing.DefaultCellEditor
 */
public class DefaultCellEditor implements TableCellEditor,ListCellRenderer {

    protected Component component;

    /**
     * @see javax.swing.DefaultCellEditor#DefaultCellEditor(javax.swing.JCheckBox) DefaultCellEditor.DefaultCellEditor
     * @see javax.swing.DefaultCellEditor#DefaultCellEditor(javax.swing.JComboBox) DefaultCellEditor.DefaultCellEditor
     * @see javax.swing.DefaultCellEditor#DefaultCellEditor(javax.swing.JTextField) DefaultCellEditor.DefaultCellEditor
     */
    public DefaultCellEditor(Component c) {
        component = c;
    }

    public Component getTableCellEditorComponent(Table table, Object value, boolean isSelected, int row, int column) {
        component.setValue(value);
        return component;
    }

    public Object getCellEditorValue() {
        return component.getValue();
    }

    public Component getListCellRendererComponent(Component list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        component.setValue(value);
        // part of the value of the button is its selected state, and we do want to lose that
        component.setupState(list, component instanceof Button?((Button)component).isSelected():isSelected, cellHasFocus);
        if (component.getForeground()==Style.NO_COLOR && list!=null) {// if our theme does not give us a foreground, then fall back to parent
            component.setForeground( list.getForeground() );
        }
        return component;
    }

    public void updateUI() {
        component.updateUI();
    }

}
