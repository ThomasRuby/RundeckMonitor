package com.github.sbugat.rundeckmonitor.wizard;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.rundeck.api.RundeckClient;
import org.rundeck.api.domain.RundeckProject;

import com.github.sbugat.rundeckmonitor.configuration.RundeckMonitorConfiguration;
import com.github.sbugat.rundeckmonitor.tools.RundeckClientTools;

/**
 * Project wizard panel.
 *
 * @author Sylvain Bugat
 *
 */
public final class ProjectConfigurationWizardPanelDescriptor extends WizardPanelDescriptor {

	/** Main container. */
	private final Container container = new Container();

	/** Combo box for selecting the RunDeck project. */
	private final JComboBox<String> rundeckProjectNameTextField = new JComboBox<>();

	/** Combo box for selecting the RunDeck API version. */
	private final JComboBox<RundeckAPIVersion> rundeckRundeckAPIVersionTextField = new JComboBox<>();

	/**
	 * Copy arguments and initialize the RunDeck project configuration wizard panel.
	 *
	 * @param backArg previous panel
	 * @param nextArg next panel
	 * @param rundeckMonitorConfigurationArg RunDeck monitor common configuration
	 */
	public ProjectConfigurationWizardPanelDescriptor(final ConfigurationWizardStep backArg, final ConfigurationWizardStep nextArg, final RundeckMonitorConfiguration rundeckMonitorConfigurationArg) {
		super(ConfigurationWizardStep.PROJECT_STEP, backArg, nextArg, rundeckMonitorConfigurationArg);

		container.setLayout(new GridBagLayout());
		final JLabel rundeckProjectlabel = new JLabel("Rundeck project:"); //$NON-NLS-1$
		final JLabel rundeckApiVersionlabel = new JLabel("Rundeck API version:"); //$NON-NLS-1$

		final GridBagConstraints gridBagConstraits = new GridBagConstraints();
		gridBagConstraits.insets = new Insets(2, 2, 2, 2);
		gridBagConstraits.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraits.gridwidth = 1;

		gridBagConstraits.gridx = 0;
		gridBagConstraits.gridy = 0;
		container.add(rundeckProjectlabel, gridBagConstraits);
		gridBagConstraits.gridx = 1;
		container.add(rundeckProjectNameTextField, gridBagConstraits);

		gridBagConstraits.gridx = 0;
		gridBagConstraits.gridy = 1;
		container.add(rundeckApiVersionlabel, gridBagConstraits);
		gridBagConstraits.gridx = 1;
		container.add(rundeckRundeckAPIVersionTextField, gridBagConstraits);
	}

	@Override
	public Component getPanelComponent() {

		return container;
	}

	@Override
	public void aboutToDisplayPanel() {

		// Initialize the rundeck client with the minimal rundeck version (1)
		final RundeckClient rundeckClient = RundeckClientTools.buildMinimalRundeckClient(getRundeckMonitorConfiguration());

		rundeckProjectNameTextField.removeAllItems();
		// Check if the configured project exists
		boolean existingOldConfiguredProject = false;
		for (final RundeckProject rundeckProject : rundeckClient.getProjects()) {

			final String currentProjectName = rundeckProject.getName();
			rundeckProjectNameTextField.addItem(currentProjectName);

			if (!currentProjectName.isEmpty() && currentProjectName.equals(getRundeckMonitorConfiguration().getRundeckProject())) {

				existingOldConfiguredProject = true;
			}
		}

		if (existingOldConfiguredProject) {
			rundeckProjectNameTextField.setSelectedItem(getRundeckMonitorConfiguration().getRundeckProject());
		}

		final String rundeckVersion = rundeckClient.getSystemInfo().getVersion();

		rundeckRundeckAPIVersionTextField.removeAllItems();
		RundeckAPIVersion oldApiVersion = null;
		for (final RundeckAPIVersion version : RundeckAPIVersion.values()) {

			if (rundeckVersion.compareTo(version.getSinceReturnVersion()) >= 0) {
				rundeckRundeckAPIVersionTextField.addItem(version);

				if (getRundeckMonitorConfiguration().getRundeckAPIversion() > 0) {
					if (version.getVersion().getVersionNumber() == getRundeckMonitorConfiguration().getRundeckAPIversion()) {

						oldApiVersion = version;
					}
				}
				else if (version.getVersion().getVersionNumber() <= RundeckAPIVersion.RUNDECK_APIVERSION_13.getVersion().getVersionNumber()) {

					oldApiVersion = version;
				}
			}
		}

		if (null != oldApiVersion) {
			rundeckRundeckAPIVersionTextField.setSelectedItem(oldApiVersion);
		}
	}

	@Override
	public boolean validate() {

		// Initialize the rundeck client
		final RundeckClient minimalRundeckClient = RundeckClientTools.buildMinimalRundeckClient(getRundeckMonitorConfiguration());

		// Check if the configured project exists
		boolean existingProject = false;
		for (final RundeckProject rundeckProject : minimalRundeckClient.getProjects()) {

			if (rundeckProjectNameTextField.getSelectedItem().equals(rundeckProject.getName())) {
				existingProject = true;
				break;
			}
		}

		if (!existingProject) {
			JOptionPane.showMessageDialog(null, "Unknown Rundeck project," + System.lineSeparator() + "check and change this project name:" + System.lineSeparator() + '"' + rundeckProjectNameTextField.getSelectedItem() + "\".", "RundeckMonitor wizard error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return false;
		}

		// Initialize the rundeck client
		final int selectedRundeckAPIVersion = rundeckRundeckAPIVersionTextField.getItemAt(rundeckRundeckAPIVersionTextField.getSelectedIndex()).getVersion().getVersionNumber();

		try {
			RundeckClientTools.buildRundeckClient(selectedRundeckAPIVersion, getRundeckMonitorConfiguration());
		}
		catch (final Exception exception) {
			JOptionPane.showMessageDialog(null, "Invalid Rundeck API version," + System.lineSeparator() + "check and change the selected Rundeck API version:" + System.lineSeparator() + '"' + rundeckRundeckAPIVersionTextField.getSelectedItem() + "\".", "RundeckMonitor wizard error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return false;
		}

		getRundeckMonitorConfiguration().setRundeckProject(rundeckProjectNameTextField.getItemAt(rundeckProjectNameTextField.getSelectedIndex()));
		getRundeckMonitorConfiguration().setRundeckAPIversion(selectedRundeckAPIVersion);

		return true;
	}
}
