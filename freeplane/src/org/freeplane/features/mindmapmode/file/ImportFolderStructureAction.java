/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
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
package org.freeplane.features.mindmapmode.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import javax.swing.JFileChooser;

import org.freeplane.core.Compat;
import org.freeplane.core.actions.IFreeplaneAction;
import org.freeplane.core.controller.Controller;
import org.freeplane.core.frame.ViewController;
import org.freeplane.core.modecontroller.ModeController;
import org.freeplane.core.model.NodeModel;
import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.util.LogTool;
import org.freeplane.features.common.link.LinkController;
import org.freeplane.features.common.text.TextController;
import org.freeplane.features.mindmapmode.MMapController;
import org.freeplane.features.mindmapmode.link.MLinkController;
import org.freeplane.features.mindmapmode.text.MTextController;

class ImportFolderStructureAction extends AFreeplaneAction implements IFreeplaneAction{
	
	private static final String NAME = "importFolderStructure";

	private static final long serialVersionUID = 2655627952066324326L;

	public ImportFolderStructureAction(final Controller controller) {
		super(controller, "import_folder_structure");
	}

	public void actionPerformed(final ActionEvent e) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		ModeController r = getModeController();
		chooser.setDialogTitle(ResourceController.getText("select_folder_for_importing"));
		final ViewController viewController = getController().getViewController();
		final int returnVal = chooser.showOpenDialog(viewController.getContentPane());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File folder = chooser.getSelectedFile();
			viewController.out("Importing folder structure ...");
			try {
				importFolderStructure(folder, getModeController().getMapController().getSelectedNode(),
				/*redisplay=*/true);
			}
			catch (final Exception ex) {
				LogTool.logException(ex);
			}
			viewController.out("Folder structure imported.");
		}
	}

	/**
	 */
	private NodeModel addNode(final NodeModel target, final String nodeContent, final String link) {
		final NodeModel node = ((MMapController) getModeController().getMapController()).addNewNode(target, target
		    .getChildCount(), target.isNewChildLeft());
		((MTextController) TextController.getController(getModeController())).setNodeText(node, nodeContent);
		((MLinkController) LinkController.getController(getModeController())).setLink(node, link);
		return node;
	}

	public void importFolderStructure(final File folder, final NodeModel target, final boolean redisplay)
	        throws MalformedURLException {
		Logger.global.warning("Entering folder: " + folder);
		if (folder.isDirectory()) {
			final File[] list = folder.listFiles();
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					final NodeModel node = addNode(target, list[i].getName(), Compat.fileToUrl(list[i]).toString());
					importFolderStructure(list[i], node, false);
				}
			}
			for (int i = 0; i < list.length; i++) {
				if (!list[i].isDirectory()) {
					addNode(target, list[i].getName(), Compat.fileToUrl(list[i]).toString());
				}
			}
		}
		getModeController().getMapController().setFolded(target, true);
	}

	public String getName() {
	    return NAME;
    }
}
