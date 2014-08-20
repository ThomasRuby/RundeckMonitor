package com.github.sbugat.rundeckmonitor.wizard;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.sbugat.rundeckmonitor.configuration.RundeckMonitorConfiguration;

public class MonitorConfigurationWizardPanelDescriptor extends WizardPanelDescriptor {

	final Container container = new Container();
	final GridBagLayout layout = new GridBagLayout();

	final JTextField rundeckMonitorName = new JTextField( 20 );
	final JComboBox<RefreshDelay> rundeckMonitorRefreshDelay = new JComboBox<>();
	final JComboBox<LateExecutionThreshold> rundeckMonitorLateExecutionThreshold = new JComboBox<>();
	final JComboBox<FailedJobsNumber> rundeckMonitorFailedJobNumber = new JComboBox<>();
	final JComboBox<DateFormat> rundeckMonitorDateFormat = new JComboBox<>();

	public MonitorConfigurationWizardPanelDescriptor( final ConfigurationWizardStep panelIdentifierArg, final ConfigurationWizardStep backArg, final ConfigurationWizardStep nextArg, final RundeckMonitorConfiguration rundeckMonitorConfigurationArg ) {
		super( panelIdentifierArg, backArg, nextArg, rundeckMonitorConfigurationArg );

		container.setLayout( layout );
		final JLabel rundeckMonitorNameLabel = new JLabel( "Tray-icon monitor name:" ); //$NON-NLS-1$
		final JLabel rundeckMonitorRefreshDelayLabel = new JLabel( "Failed/late jobs refresh delay:" ); //$NON-NLS-1$
		final JLabel rundeckMonitorLateExecutionThresholdLabel = new JLabel( "Late execution detection threshold:" ); //$NON-NLS-1$
		final JLabel rundeckMonitorFailedJobNumberLabel = new JLabel( "Number of failed/late jobs to display:" ); //$NON-NLS-1$
		final JLabel rundeckMonitorDateFormatLabel = new JLabel( "Failed/late jobs displayed date format:" ); //$NON-NLS-1$

		//Fields initialization
		if( null != rundeckMonitorConfiguration.getRundeckMonitorName() && rundeckMonitorConfiguration.getRundeckMonitorName().isEmpty() ) {
			rundeckMonitorName.setText( RundeckMonitorConfiguration.RUNDECK_MONITOR_PROPERTY_NAME_DEFAULT_VALUE );
		}
		else {
			rundeckMonitorName.setText( rundeckMonitorConfiguration.getRundeckMonitorName() );
		}

		RefreshDelay oldConfiguredRefreshDelay = null;
		for( final RefreshDelay refreshDelay : RefreshDelay.values() ) {

			rundeckMonitorRefreshDelay.addItem( refreshDelay );
			if( refreshDelay.getDelay() == rundeckMonitorConfiguration.getRefreshDelay() ) {
				oldConfiguredRefreshDelay = refreshDelay;
			}
		}

		if( null != oldConfiguredRefreshDelay ) {
			rundeckMonitorRefreshDelay.setSelectedItem( oldConfiguredRefreshDelay );
		}
		else {
			rundeckMonitorRefreshDelay.setSelectedItem( RefreshDelay.REFRESH_DELAY_1M );
		}

		LateExecutionThreshold oldLateExecutionThreshold = null;
		for( final LateExecutionThreshold lateExecutionThreshold : LateExecutionThreshold.values() ) {

			rundeckMonitorLateExecutionThreshold.addItem( lateExecutionThreshold );
			if( lateExecutionThreshold.getThreshold() == rundeckMonitorConfiguration.getLateThreshold() ) {
				oldLateExecutionThreshold = lateExecutionThreshold;
			}
		}

		if( null != oldLateExecutionThreshold ) {
			rundeckMonitorLateExecutionThreshold.setSelectedItem( oldLateExecutionThreshold );
		}
		else {
			rundeckMonitorLateExecutionThreshold.setSelectedItem( LateExecutionThreshold.LATE_EXECUTION_THRESHOLD_30M );
		}


		FailedJobsNumber oldFailedJobsNumber = null;
		for( final FailedJobsNumber failedJobsNumber : FailedJobsNumber.values() ) {

			rundeckMonitorFailedJobNumber.addItem( failedJobsNumber );
			if( failedJobsNumber.getFailedJobsNumber() == rundeckMonitorConfiguration.getFailedJobNumber() ) {
				oldFailedJobsNumber = failedJobsNumber;
			}
		}

		if( null != oldFailedJobsNumber ) {
			rundeckMonitorFailedJobNumber.setSelectedItem( oldFailedJobsNumber );
		}
		else {
			rundeckMonitorFailedJobNumber.setSelectedItem( FailedJobsNumber.FAILED_JOBS_10 );
		}

		DateFormat oldDateFormat = null;
		for( final DateFormat dateFormat : DateFormat.values() ) {

			rundeckMonitorDateFormat.addItem( dateFormat );

			if( dateFormat.getDateFormat().equals( rundeckMonitorConfiguration.getDateFormat() ) ) {
				oldDateFormat = dateFormat;
			}
		}

		if( null != oldDateFormat ) {
			rundeckMonitorDateFormat.setSelectedItem( oldDateFormat );
		}
		else {
			rundeckMonitorDateFormat.setSelectedItem( DateFormat.DATE_FORMAT_STANDARD );
		}

		final GridBagConstraints gridBagConstraits = new GridBagConstraints();
		gridBagConstraits.insets = new Insets( 2,2,2,2 );
		gridBagConstraits.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraits.gridwidth = 1;

		gridBagConstraits.gridx=0;
		gridBagConstraits.gridy=0;
		container.add( rundeckMonitorNameLabel, gridBagConstraits );
		gridBagConstraits.gridx=1;
		container.add( rundeckMonitorName, gridBagConstraits );

		gridBagConstraits.gridx=0;
		gridBagConstraits.gridy=1;
		container.add( rundeckMonitorRefreshDelayLabel, gridBagConstraits );
		gridBagConstraits.gridx=1;
		container.add( rundeckMonitorRefreshDelay, gridBagConstraits );

		gridBagConstraits.gridx=0;
		gridBagConstraits.gridy=2;
		container.add( rundeckMonitorLateExecutionThresholdLabel, gridBagConstraits );
		gridBagConstraits.gridx=1;
		container.add( rundeckMonitorLateExecutionThreshold, gridBagConstraits );

		gridBagConstraits.gridx=0;
		gridBagConstraits.gridy=3;
		container.add( rundeckMonitorFailedJobNumberLabel, gridBagConstraits );
		gridBagConstraits.gridx=1;
		container.add( rundeckMonitorFailedJobNumber, gridBagConstraits );

		gridBagConstraits.gridx=0;
		gridBagConstraits.gridy=4;
		container.add( rundeckMonitorDateFormatLabel, gridBagConstraits );
		gridBagConstraits.gridx=1;
		container.add( rundeckMonitorDateFormat, gridBagConstraits );
	}

	@Override
	public Component getPanelComponent() {

		return container;
	}

	public void aboutToDisplayPanel() {

	}

	public boolean validate() {

		rundeckMonitorConfiguration.setRundeckMonitorName( rundeckMonitorName.getText() );
		rundeckMonitorConfiguration.setRefreshDelay( rundeckMonitorRefreshDelay.getItemAt( rundeckMonitorRefreshDelay.getSelectedIndex() ).getDelay() );
		rundeckMonitorConfiguration.setLateThreshold( rundeckMonitorLateExecutionThreshold.getItemAt( rundeckMonitorLateExecutionThreshold.getSelectedIndex() ).getThreshold() );
		rundeckMonitorConfiguration.setFailedJobNumber( rundeckMonitorFailedJobNumber.getItemAt( rundeckMonitorFailedJobNumber.getSelectedIndex() ).getFailedJobsNumber() );
		rundeckMonitorConfiguration.setDateFormat( rundeckMonitorDateFormat.getItemAt( rundeckMonitorDateFormat.getSelectedIndex() ).getDateFormat() );

		try {
			rundeckMonitorConfiguration.saveMonitorConfigurationPropertieFile();
		}
		catch( final IOException e ) {
			return false;
		}

		return true;
	}
}
