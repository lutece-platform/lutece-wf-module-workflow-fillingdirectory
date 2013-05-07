/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service;

import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.IndexerAction;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.directorysearch.DirectorySearchService;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.task.TaskRemovalListenerService;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskFillingDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskFillingRemovalListener;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.utils.TaskFillingDirectoryUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskFillingDirectory
 *
 */
public class TaskFillingDirectory extends Task
{
    private static final String COMMA = ", ";
    private static TaskFillingRemovalListener _listenerTaskFillingRemovalListener;

    // PROPERTIES
    private static final String PROPERTY_ID_ENTRY_TYPE_FILE = "workflow-fillingdirectory.id_entry_type_file";
    private static final String PROPERTY_ID_ENTRY_TYPE_IMG = "workflow-fillingdirectory.id_entry_type_img";
    private static final String PROPERTY_ID_ENTRY_TYPE_CHECKBOX = "workflow-fillingdirectory.id_entry_type_checkbox";

    // SERVICES
    @Inject
    @Named( TaskFillingDirectoryConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskFillingDirectoryConfigService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IRecordFieldHistoryService _recordFieldHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(  )
    {
        // Create removal listener and register it
        if ( _listenerTaskFillingRemovalListener == null )
        {
            _listenerTaskFillingRemovalListener = new TaskFillingRemovalListener(  );
            TaskRemovalListenerService.getService(  ).registerListener( _listenerTaskFillingRemovalListener );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( this.getId(  ) );
        String strIdEntryTypeFile = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_FILE );
        String strIdEntryTypeImg = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_IMG );
        String strIdEntryTypeCheckBox = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_CHECKBOX );
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( ( config != null ) && ( resourceHistory != null ) &&
                resourceHistory.getResourceType(  ).equals( Record.WORKFLOW_RESOURCE_TYPE ) )
        {
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

            IEntry entry = null;
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setPosition( config.getPositionEntryDirectory(  ) );
            entryFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

            List<IEntry> entryList = EntryHome.getEntryList( entryFilter, pluginDirectory );

            if ( ( entryList != null ) && !entryList.isEmpty(  ) )
            {
                entry = EntryHome.findByPrimaryKey( entryList.get( 0 ).getIdEntry(  ), pluginDirectory );
            }

            if ( entry != null )
            {
                List<RecordField> listRecordFieldResult = new ArrayList<RecordField>(  );

                try
                {
                    if ( config.isUsedUser(  ) )
                    {
                        List<String> listString = new ArrayList<String>(  );
                        listString.add( AdminUserService.getAdminUser( request ).getAccessCode(  ) );
                        entry.getRecordFieldData( record, listString, true, config.isAddNewValue(  ),
                            listRecordFieldResult, locale );
                    }
                    else if ( !config.isUsedTaskEntry(  ) )
                    {
                        entry.getRecordFieldData( record, request, true, config.isAddNewValue(  ),
                            listRecordFieldResult, locale );
                    }
                    else
                    {
                        if ( ( entry.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( 
                                    strIdEntryTypeFile ) ) ||
                                ( entry.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( 
                                    strIdEntryTypeImg ) ) )
                        {
                            entry.getRecordFieldData( record,
                                TaskFillingDirectoryUtils.getParameterValue( request, config.getEntryParameter(  ) ),
                                true, config.isAddNewValue(  ), listRecordFieldResult, locale );
                        }
                        else if ( ( entry.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( 
                                    strIdEntryTypeCheckBox ) ) )
                        {
                            entry.getRecordFieldData( record,
                                TaskFillingDirectoryUtils.getParameterValuesTypeMultipleChoice( request,
                                    config.getEntryParameter(  ) ), true, config.isAddNewValue(  ),
                                listRecordFieldResult, locale );
                        }
                        else
                        {
                            // If the config has a criteria of the type checkbox/select that provides multiple choices, 
                            // we must display the output in a single line
                            List<String> listParameterValues = TaskFillingDirectoryUtils.getParameterValuesTypeMultipleChoice( request,
                                    config.getEntryParameter(  ) );

                            if ( listParameterValues.size(  ) > 1 )
                            {
                                StringBuilder sbParameterValue = new StringBuilder(  );

                                for ( String strParameterValue : listParameterValues )
                                {
                                    sbParameterValue.append( strParameterValue + COMMA );
                                }

                                sbParameterValue.delete( sbParameterValue.length(  ) - COMMA.length(  ),
                                    sbParameterValue.length(  ) );

                                List<String> list = new ArrayList<String>(  );
                                list.add( sbParameterValue.toString(  ) );
                                entry.getRecordFieldData( record, list, true, config.isAddNewValue(  ),
                                    listRecordFieldResult, locale );
                            }
                            else
                            {
                                entry.getRecordFieldData( record,
                                    TaskFillingDirectoryUtils.getParameterValue( request, config.getEntryParameter(  ) ),
                                    true, config.isAddNewValue(  ), listRecordFieldResult, locale );
                            }
                        }
                    }

                    //add Indexer action
                    DirectorySearchService.getInstance(  )
                                          .addIndexerAction( record.getIdRecord(  ), IndexerAction.TASK_MODIFY, plugin );

                    //delete all record field in database associate to the entry and the record
                    RecordFieldFilter filter = new RecordFieldFilter(  );
                    filter.setIdRecord( record.getIdRecord(  ) );
                    filter.setIdEntry( entry.getIdEntry(  ) );
                    RecordFieldHome.removeByFilter( filter, plugin );

                    //insert the new record Field
                    for ( RecordField recordField : listRecordFieldResult )
                    {
                        recordField.setRecord( record );
                        RecordFieldHome.create( recordField, plugin );
                        //insert new History recordField
                        _recordFieldHistoryService.create( nIdResourceHistory, this.getId(  ), recordField, plugin );
                    }
                }
                catch ( DirectoryErrorException error )
                {
                    throw new RuntimeException( error );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        _taskFillingDirectoryConfigService.remove( this.getId(  ) );
        // Remove task information
        _recordFieldHistoryService.removeByTask( this.getId(  ), plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        _recordFieldHistoryService.removeByHistory( nIdHistory, this.getId(  ), plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( this.getId(  ) );

        if ( config != null )
        {
            IEntry entry = null;
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setPosition( config.getPositionEntryDirectory(  ) );
            entryFilter.setIdDirectory( config.getIdDirectory(  ) );

            List<IEntry> entryList = EntryHome.getEntryList( entryFilter, pluginDirectory );

            if ( ( entryList != null ) && !entryList.isEmpty(  ) )
            {
                entry = EntryHome.findByPrimaryKey( entryList.get( 0 ).getIdEntry(  ), pluginDirectory );

                return entry.getTitle(  );
            }
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        return null;
    }
}
