/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry
 *
 *  This file author is Dimitry
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.core.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.freeplane.core.ui.components.IKeyBindingManager;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.mode.Controller;

/**
 * @author Dimitry Polivaev
 * 20.04.2009
 */
public class AccelerateableAction implements IFreeplaneAction {

	final private AFreeplaneAction originalAction;
    private static JDialog setAcceleratorOnNextClickActionDialog;
    private static KeyStroke acceleratorForNextClickedAction;

	public static boolean isNewAcceleratorOnNextClickEnabled() {
		return setAcceleratorOnNextClickActionDialog != null;
	}

	private static final String SET_ACCELERATOR_ON_NEXT_CLICK_ACTION = "set_accelerator_on_next_click_action";

	static void setNewAcceleratorOnNextClick(KeyStroke accelerator) {
		if (AccelerateableAction.isNewAcceleratorOnNextClickEnabled()) {
			return;
		}
        acceleratorForNextClickedAction = accelerator;
        String title = TextUtils.getText("SetAccelerator.dialogTitle");
        String text = TextUtils.getText(SET_ACCELERATOR_ON_NEXT_CLICK_ACTION);
		if(accelerator != null)
			text = TextUtils.format("SetAccelerator.keystrokeDetected", toString(accelerator)) + "\n" + text;
		final Component frame = Controller.getCurrentController().getViewController().getMenuComponent();
		setAcceleratorOnNextClickActionDialog = UITools.createCancelDialog(frame, title, text);
		getAcceleratorOnNextClickActionDialog().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(final ComponentEvent e) {
				setAcceleratorOnNextClickActionDialog = null;
				acceleratorForNextClickedAction = null;
			}
		});
		getAcceleratorOnNextClickActionDialog().setVisible(true);
	}

	public AccelerateableAction(final AFreeplaneAction originalAction) {
		super();
		this.originalAction = originalAction;
	}

	public void actionPerformed(final ActionEvent e) {
		final boolean newAcceleratorOnNextClickEnabled = AccelerateableAction.isNewAcceleratorOnNextClickEnabled();
		final KeyStroke newAccelerator = acceleratorForNextClickedAction;
		if (newAcceleratorOnNextClickEnabled) {
			getAcceleratorOnNextClickActionDialog().setVisible(false);
		}
		final Object source = e.getSource();
		if ((newAcceleratorOnNextClickEnabled || 0 != (e.getModifiers() & ActionEvent.CTRL_MASK))
		        && ! (source instanceof IKeyBindingManager &&((IKeyBindingManager) source).isKeyBindingProcessed())) {
			getAcceleratorManager().newAccelerator(getOriginalAction(), newAccelerator);
			return;
		}
		originalAction.actionPerformed(e);
	}
	 
	public static JDialog getAcceleratorOnNextClickActionDialog() {
		return setAcceleratorOnNextClickActionDialog;
	}
	
	public static KeyStroke getAcceleratorForNextClick() {
		return acceleratorForNextClickedAction;
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		originalAction.addPropertyChangeListener(listener);
	}

	public void afterMapChange(final Object newMap) {
		originalAction.afterMapChange(newMap);
	}

	public Object getValue(final String key) {
		return originalAction.getValue(key);
	}

	public boolean isEnabled() {
		return originalAction.isEnabled();
	}

	public boolean isSelected() {
		return originalAction.isSelected();
	}

	public void setSelected(boolean newValue) {
		originalAction.setSelected(newValue);
	}
	
	public AFreeplaneAction getOriginalAction() {
		return originalAction;
	}

    private static String toString(final KeyStroke newAccelerator) {
        return newAccelerator.toString().replaceFirst("pressed ", "");
    }

	public void putValue(final String key, final Object value) {
		originalAction.putValue(key, value);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		originalAction.removePropertyChangeListener(listener);
	}

	public void setEnabled(final boolean b) {
		originalAction.setEnabled(b);
	}

	public String getIconKey() {
		return originalAction.getIconKey();
	}

	private ActionAcceleratorManager getAcceleratorManager() {
		return Controller.getCurrentModeController().getUserInputListenerFactory().getAcceleratorManager();
	}
}
