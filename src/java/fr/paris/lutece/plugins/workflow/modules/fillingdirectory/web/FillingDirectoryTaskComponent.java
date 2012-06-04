/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
 *     task software without specific prior written permission.
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
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.web;

import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.EntryTypeFileHistory;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.EntryTypeImgHistory;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskFillingDirectoryConfig;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.FillingDirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.IRecordFieldHistoryService;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ITaskFillingDirectoryConfigService;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.utils.TaskFillingDirectoryUtils;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.web.task.TaskComponent;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 *
 * FillingDirectoryTaskComponent
 *
 */
public class FillingDirectoryTaskComponent extends TaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_FILLING_DIRECTORY_CONFIG = "admin/plugins/workflow/modules/fillingdirectory/task_filling_directory_config.html";
    private static final String TEMPLATE_TASK_FILLING_DIRECTORY_FORM = "admin/plugins/workflow/modules/fillingdirectory/task_filling_directory_form.html";
    private static final String TEMPLATE_TASK_EVALUATION_INFORMATION = "admin/plugins/workflow/modules/fillingdirectory/task_filling_directory_information.html";

    // MARKS
    private static final String MARK_CONFIG = "config";
    private static final String MARK_DIRECTORY_LIST = "list_directory";
    private static final String MARK_TASK_LIST = "list_task";
    private static final String MARK_DIRECTORY_ENTRY_LIST = "list_entry_directory";
    private static final String MARK_TASK_ENTRY_LIST = "list_entry_task";
    private static final String MARK_LIST_RECORD_FIELD = "list_record_field";
    private static final String MARK_ENTRY_DIRECTORY = "entry_directory";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";

    // PARAMETERS
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_ENTRY_DIRECTORY = "id_entry_directory";
    private static final String PARAMETER_ID_TASK_ENTRY = "id_task_entry";
    private static final String PARAMETER_ENTRY_PARAMETER = "entry_parameter";
    private static final String PARAMETER_USED_FIELD = "field_used";
    private static final String PARAMETER_ADD_NEW_VALUE = "add_new_value";

    // PROPERTIES
    private static final String FIELD_TASK_DIRECTORY = "module.workflow.fillingdirectory.task_filling_directory_config.label_task_directory";
    private static final String FIELD_TASK_ENTRY_DIRECTORY = "module.workflow.fillingdirectory.task_filling_directory_config.label_task_entry_directory";
    private static final String FIELD_TASK_ENTRY_TASK = "module.workflow.fillingdirectory.task_filling_directory_config.label_task_entry";
    private static final String FIELD_TASK_ENTRY_PARAMETER = "module.workflow.fillingdirectory.task_filling_directory_config.label_task_entry_parameter";
    private static final String FIELD_USED_FIELD = "module.workflow.fillingdirectory.task_filling_directory_config.label_used";

    // MESSAGES
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.fillingdirectory.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_EVALUATION_EXPERT = "module.workflow.fillingdirectory.message.no_configuration_for_task_filling_directory";
    private static final String MESSAGE_DIRECTORY_ERROR = "module.workflow.fillingdirectory.message.directory_error";
    private static final String MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD = "module.workflow.fillingdirectory.message.directory_error.mandatory.field";

    // XML TAGS
    private static final String TAG_FILLING_DIRECTORY = "filling-directory";

    // PROPERTIES
    private static final String PROPERTY_ID_ENTRY_TYPE_FILE = "workflow-fillingdirectory.id_entry_type_file";
    private static final String PROPERTY_ID_ENTRY_TYPE_IMG = "workflow-fillingdirectory.id_entry_type_img";
    private static final String PROPERTY_ID_ENTRY_TYPE_CHECKBOX = "workflow-fillingdirectory.id_entry_type_checkbox";
    private static final String PROPERTY_ACCEPT_DIRECTORY_TYPE = "workflow-fillingdirectory.accept_directory_type";
    private static final String LABEL_REFERENCE_DIRECTORY = "module.workflow.fillingdirectory.task_filling_directory_config.label_reference_directory";

    // SERVICES
    @Inject
    private ITaskFillingDirectoryConfigService _taskFillingDirectoryConfigService;
    @Inject
    private ITaskService _taskService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IRecordFieldHistoryService _recordFieldHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        int nUsed = DirectoryUtils.convertStringToInt( request.getParameter( PARAMETER_USED_FIELD ) );
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strPositionEntryDirectory = request.getParameter( PARAMETER_ID_ENTRY_DIRECTORY );
        String strIdTaskEntry = request.getParameter( PARAMETER_ID_TASK_ENTRY );
        String strEntryParameter = request.getParameter( PARAMETER_ENTRY_PARAMETER );
        int nIdTaskEntry = WorkflowUtils.convertStringToInt( strIdTaskEntry );
        int nPositionEntryDirectory = WorkflowUtils.convertStringToInt( strPositionEntryDirectory );
        String strAddNewValue = request.getParameter( PARAMETER_ADD_NEW_VALUE );

        String strError = StringUtils.EMPTY;

        if ( nUsed == WorkflowUtils.CONSTANT_ID_NULL )
        {
            strError = FIELD_USED_FIELD;
        }
        else if ( ( strIdDirectory == null ) || strIdDirectory.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_TASK_DIRECTORY;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) &&
                ( nPositionEntryDirectory == WorkflowUtils.CONSTANT_ID_NULL ) )
        {
            strError = FIELD_TASK_ENTRY_DIRECTORY;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) && ( nUsed == 2 ) &&
                ( nIdTaskEntry == WorkflowUtils.CONSTANT_ID_NULL ) )
        {
            strError = FIELD_TASK_ENTRY_TASK;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) && ( nUsed == 2 ) &&
                ( ( strEntryParameter == null ) || strEntryParameter.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) ) )
        {
            strError = FIELD_TASK_ENTRY_PARAMETER;
        }
        else if ( ( request.getParameter( PARAMETER_APPLY ) == null ) && ( ( nUsed == 2 ) || ( nUsed == 1 ) ) )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            String strAcceptEntryType = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE );
            String[] strTabAcceptEntryType = strAcceptEntryType.split( "," );

            IEntry entryDirectory = null;
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setPosition( nPositionEntryDirectory );
            entryFilter.setIdDirectory( DirectoryUtils.convertStringToInt( strIdDirectory ) );

            List<IEntry> entryList = EntryHome.getEntryList( entryFilter, pluginDirectory );

            if ( ( entryList != null ) && !entryList.isEmpty(  ) )
            {
                entryDirectory = EntryHome.findByPrimaryKey( entryList.get( 0 ).getIdEntry(  ), pluginDirectory );

                boolean isAcceptType = false;

                for ( int i = 0; i < strTabAcceptEntryType.length; i++ )
                {
                    if ( entryDirectory.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryType[i] ) )
                    {
                        isAcceptType = true;
                    }
                }

                if ( !isAcceptType )
                {
                    Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

                    return AdminMessageService.getMessageUrl( request, FIELD_TASK_ENTRY_DIRECTORY, tabRequiredFields,
                        AdminMessage.TYPE_STOP );
                }
            }
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );

        TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( task.getId(  ), plugin );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskFillingDirectoryConfig(  );
            config.setIdTask( task.getId(  ) );
            bCreate = true;
        }

        config.setIdDirectory( DirectoryUtils.convertStringToInt( strIdDirectory ) );
        config.setPositionEntryDirectory( nPositionEntryDirectory );
        config.setIdTask( task.getId(  ) );
        config.setUsedTaskEntry( nUsed == 2 );
        config.setUsedUser( nUsed == 1 );
        config.setIdTaskEntry( nIdTaskEntry );
        config.setEntryParameter( strEntryParameter );
        config.setAddNewValue( strAddNewValue != null );

        if ( bCreate )
        {
            _taskFillingDirectoryConfigService.create( config, plugin );
        }
        else
        {
            _taskFillingDirectoryConfigService.update( config, plugin );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( task.getId(  ), plugin );
        String strIdEntryTypeFile = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_FILE );
        String strIdEntryTypeImg = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_IMG );
        String strIdEntryTypeCheckBox = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_CHECKBOX );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_EVALUATION_EXPERT,
                AdminMessage.TYPE_STOP );
        }

        if ( strResourceType.equals( Record.WORKFLOW_RESOURCE_TYPE ) )
        {
            Record record = RecordHome.findByPrimaryKey( nIdResource, pluginDirectory );

            IEntry entry = null;
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setPosition( config.getPositionEntryDirectory(  ) );
            entryFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

            List<IEntry> entryList = EntryHome.getEntryList( entryFilter, pluginDirectory );

            if ( ( entryList != null ) && !entryList.isEmpty(  ) )
            {
                entry = EntryHome.findByPrimaryKey( entryList.get( 0 ).getIdEntry(  ), pluginDirectory );
            }

            if ( ( entry != null ) )
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
                            entry.getRecordFieldData( record,
                                TaskFillingDirectoryUtils.getParameterValue( request, config.getEntryParameter(  ) ),
                                true, config.isAddNewValue(  ), listRecordFieldResult, locale );
                        }
                    }

                    // Delete unwanted files that are generated from entry type download url
                    TaskFillingDirectoryUtils.doDeleteTempFile( entry, listRecordFieldResult, pluginDirectory );
                }
                catch ( DirectoryErrorException error )
                {
                    String strErrorMessage = DirectoryUtils.EMPTY_STRING;

                    if ( error.isMandatoryError(  ) )
                    {
                        Object[] tabRequiredFields = { error.getTitleField(  ) };
                        strErrorMessage = AdminMessageService.getMessageUrl( request,
                                MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
                    }
                    else
                    {
                        Object[] tabRequiredFields = { error.getTitleField(  ), error.getErrorMessage(  ) };
                        strErrorMessage = AdminMessageService.getMessageUrl( request, MESSAGE_DIRECTORY_ERROR,
                                tabRequiredFields, AdminMessage.TYPE_STOP );
                    }

                    // Delete unwanted files that are generated from entry type download url
                    TaskFillingDirectoryUtils.doDeleteTempFile( entry, listRecordFieldResult, pluginDirectory );

                    return strErrorMessage;
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask taskRef )
    {
        String strAcceptEntryType = AppPropertiesService.getProperty( PROPERTY_ACCEPT_DIRECTORY_TYPE );
        String[] strTabAcceptEntryType = strAcceptEntryType.split( "," );

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Map<String, Object> model = new HashMap<String, Object>(  );
        ReferenceList entryList = null;
        ReferenceList entryTaskList = null;
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( taskRef.getId(  ),
                plugin );

        ReferenceList directoryList = DirectoryHome.getDirectoryList( pluginDirectory );
        ReferenceList taskReferenceListDirectory = new ReferenceList(  );
        taskReferenceListDirectory.addItem( DirectoryUtils.CONSTANT_ID_NULL, "" );

        if ( directoryList != null )
        {
            taskReferenceListDirectory.addAll( directoryList );
        }

        if ( ( config != null ) && ( config.getIdDirectory(  ) != DirectoryUtils.CONSTANT_ID_NULL ) )
        {
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( config.getIdDirectory(  ) );
            entryFilter.setIsGroup( EntryFilter.FILTER_FALSE );
            entryFilter.setIsComment( EntryFilter.FILTER_FALSE );

            entryList = new ReferenceList(  );

            for ( IEntry entry : EntryHome.getEntryList( entryFilter, pluginDirectory ) )
            {
                if ( config.isUsedTaskEntry(  ) || config.isUsedUser(  ) )
                {
                    for ( int i = 0; i < strTabAcceptEntryType.length; i++ )
                    {
                        if ( entry.getEntryType(  ).getIdType(  ) == Integer.parseInt( strTabAcceptEntryType[i] ) )
                        {
                            entryList.addItem( entry.getPosition(  ),
                                String.valueOf( entry.getPosition(  ) ) + " (" +
                                I18nService.getLocalizedString( LABEL_REFERENCE_DIRECTORY, locale ) +
                                entry.getTitle(  ) + " - " +
                                I18nService.getLocalizedString( entry.getEntryType(  ).getTitleI18nKey(  ), locale ) +
                                ")" );
                        }
                    }
                }
                else
                {
                    entryList.addItem( entry.getPosition(  ),
                        String.valueOf( entry.getPosition(  ) ) + " (" +
                        I18nService.getLocalizedString( LABEL_REFERENCE_DIRECTORY, locale ) + entry.getTitle(  ) +
                        " - " + I18nService.getLocalizedString( entry.getEntryType(  ).getTitleI18nKey(  ), locale ) +
                        ")" );
                }
            }

            if ( config.getIdTaskEntry(  ) != WorkflowUtils.CONSTANT_ID_NULL )
            {
                //get entry task list
                ITask taskEntry = _taskService.findByPrimaryKey( config.getIdTaskEntry(  ), locale );

                if ( taskEntry != null )
                {
                    entryTaskList = ReferenceList.convert( taskEntry.getTaskFormEntries( locale ) );
                }
            }
        }

        ReferenceList taskReferenceListEntry = new ReferenceList(  );
        taskReferenceListEntry.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );

        if ( entryList != null )
        {
            taskReferenceListEntry.addAll( entryList );
        }

        if ( config == null )
        {
            config = new TaskFillingDirectoryConfig(  );
            config.setIdDirectory( DirectoryUtils.CONSTANT_ID_NULL );
            config.setPositionEntryDirectory( DirectoryUtils.CONSTANT_ID_NULL );
        }

        //get task list
        ReferenceList refListActionTasks = new ReferenceList(  );
        refListActionTasks.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );

        for ( ITask task : _taskService.getListTaskByIdAction( taskRef.getAction(  ).getId(  ), locale ) )
        {
            if ( task.getId(  ) != taskRef.getId(  ) )
            {
                ITask taskEntry = _taskService.findByPrimaryKey( task.getId(  ), locale );

                if ( taskEntry != null )
                {
                    if ( taskEntry.getTaskFormEntries( locale ) != null )
                    {
                        refListActionTasks.addItem( task.getId(  ), task.getTitle( locale ) );
                    }
                }
            }
        }

        model.put( MARK_CONFIG, config );
        model.put( MARK_DIRECTORY_ENTRY_LIST, taskReferenceListEntry );
        model.put( MARK_DIRECTORY_LIST, directoryList );
        model.put( MARK_TASK_LIST, refListActionTasks );
        model.put( MARK_TASK_ENTRY_LIST, entryTaskList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FILLING_DIRECTORY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( strResourceType.equals( Record.WORKFLOW_RESOURCE_TYPE ) )
        {
            Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
            TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( task.getId(  ),
                    plugin );
            Record record = RecordHome.findByPrimaryKey( nIdResource, pluginDirectory );
            IEntry entry = null;

            if ( ( config != null ) && !( config.isUsedTaskEntry(  ) || config.isUsedUser(  ) ) )
            {
                EntryFilter entryFilter = new EntryFilter(  );
                entryFilter.setPosition( config.getPositionEntryDirectory(  ) );
                entryFilter.setIdDirectory( record.getDirectory(  ).getIdDirectory(  ) );

                List<IEntry> entryList = EntryHome.getEntryList( entryFilter, pluginDirectory );

                if ( ( entryList != null ) && !entryList.isEmpty(  ) )
                {
                    entry = EntryHome.findByPrimaryKey( entryList.get( 0 ).getIdEntry(  ), pluginDirectory );

                    /**
                     * Map of <idEntry, RecordFields>
                     *         1) The user has uploaded/deleted a file
                     *                 - The updated map is stored in the session
                     *  2) The user has not uploaded/delete a file
                     *          - The map is filled with the data from the database
                     *          - The asynchronous uploaded files map is reinitialized
                     */
                    Map<String, List<RecordField>> mapRecordFields = null;

                    /** 1) Case when the user has uploaded a file, the the map is stored in the session */
                    HttpSession session = request.getSession(  );

                    String strUploadAction = DirectoryAsynchronousUploadHandler.getHandler(  ).getUploadAction( request );

                    if ( StringUtils.isBlank( strUploadAction ) )
                    {
                        // IMPORTANT : Remove the map from the session if the button upload is not pushed
                        session.removeAttribute( DirectoryUtils.SESSION_DIRECTORY_TASKS_SUBMITTED_RECORD_FIELDS );
                    }
                    else
                    {
                        mapRecordFields = (Map<String, List<RecordField>>) session.getAttribute( DirectoryUtils.SESSION_DIRECTORY_TASKS_SUBMITTED_RECORD_FIELDS );
                    }

                    /** 2) The user has not uploaded/delete a file */
                    if ( mapRecordFields == null )
                    {
                        mapRecordFields = new HashMap<String, List<RecordField>>(  );
                        // Reinit the asynchronous uploaded files map
                        DirectoryAsynchronousUploadHandler.getHandler(  ).reinitMap( request, mapRecordFields, plugin );
                    }

                    model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, mapRecordFields );

                    model.put( MARK_ENTRY_DIRECTORY, entry );
                    model.put( MARK_LOCALE, locale );

                    HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_FILLING_DIRECTORY_FORM,
                            locale, model );

                    return template.getHtml(  );
                }
            }
        }

        return DirectoryUtils.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        TaskFillingDirectoryConfig config = _taskFillingDirectoryConfigService.findByPrimaryKey( task.getId(  ), plugin );

        if ( ( config != null ) && !config.isUsedTaskEntry(  ) )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            String strIdEntryTypeFile = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_FILE );
            String strIdEntryTypeImg = AppPropertiesService.getProperty( PROPERTY_ID_ENTRY_TYPE_IMG );

            ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
            Map<String, Object> model = new HashMap<String, Object>(  );
            IEntry entry = null;
            List<RecordField> recordFieldList = null;

            if ( ( resourceHistory != null ) &&
                    resourceHistory.getResourceType(  ).equals( Record.WORKFLOW_RESOURCE_TYPE ) )
            {
                recordFieldList = _recordFieldHistoryService.getListByHistory( nIdHistory, task.getId(  ), plugin );
            }

            if ( ( recordFieldList != null ) && ( recordFieldList.size(  ) > 0 ) )
            {
                entry = EntryHome.findByPrimaryKey( recordFieldList.get( 0 ).getEntry(  ).getIdEntry(  ),
                        pluginDirectory );

                if ( entry.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( strIdEntryTypeFile ) )
                {
                    entry = new EntryTypeFileHistory( entry );
                }
                else if ( entry.getEntryType(  ).getIdType(  ) == DirectoryUtils.convertStringToInt( strIdEntryTypeImg ) )
                {
                    entry = new EntryTypeImgHistory( entry );
                }
            }

            model.put( MARK_ENTRY_DIRECTORY, entry );
            model.put( MARK_LIST_RECORD_FIELD, recordFieldList );
            model.put( MARK_LOCALE, locale );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_INFORMATION, locale, model );

            return template.getHtml(  );
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        StringBuffer strXml = new StringBuffer(  );

        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Plugin plugin = PluginService.getPlugin( FillingDirectoryPlugin.PLUGIN_NAME );
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        IEntry entry = null;
        List<RecordField> listRecordField = null;

        if ( ( resourceHistory != null ) &&
                resourceHistory.getResourceType(  ).equals( Record.WORKFLOW_RESOURCE_TYPE ) )
        {
            listRecordField = _recordFieldHistoryService.getListByHistory( nIdHistory, task.getId(  ), plugin );
        }

        if ( ( listRecordField != null ) && ( listRecordField.size(  ) > 0 ) )
        {
            entry = EntryHome.findByPrimaryKey( listRecordField.get( 0 ).getEntry(  ).getIdEntry(  ), pluginDirectory );
        }

        XmlUtil.beginElement( strXml, TAG_FILLING_DIRECTORY );

        if ( entry != null )
        {
            Map<String, Object> model = new HashMap<String, Object>(  );
            model.put( Entry.ATTRIBUTE_ENTRY_ID, String.valueOf( entry.getIdEntry(  ) ) );
            XmlUtil.beginElement( strXml, Entry.TAG_ENTRY, model );
            XmlUtil.beginElement( strXml, Record.TAG_LIST_RECORD_FIELD );

            if ( listRecordField != null )
            {
                for ( RecordField recordField : listRecordField )
                {
                    XmlUtil.beginElement( strXml, Record.TAG_RECORD_FIELD );

                    if ( recordField.getFile(  ) != null )
                    {
                        XmlUtil.addEmptyElement( strXml, Record.TAG_RECORD_FIELD_VALUE, null );
                        strXml.append( recordField.getFile(  )
                                                  .getXml( plugin, locale, entry.getEntryType(  ).getIdType(  ),
                                entry.getDisplayWidth(  ), entry.getDisplayHeight(  ) ) );
                    }
                    else
                    {
                        XmlUtil.addElementHtml( strXml, Record.TAG_RECORD_FIELD_VALUE,
                            DirectoryUtils.substituteSpecialCaractersForExport( 
                                recordField.getEntry(  )
                                           .convertRecordFieldValueToString( recordField, locale, false, false ) ) );
                        XmlUtil.addEmptyElement( strXml, File.TAG_FILE, null );
                    }

                    XmlUtil.endElement( strXml, Record.TAG_RECORD_FIELD );
                }
            }

            XmlUtil.endElement( strXml, Record.TAG_LIST_RECORD_FIELD );
            XmlUtil.endElement( strXml, Entry.TAG_ENTRY );
        }

        XmlUtil.endElement( strXml, TAG_FILLING_DIRECTORY );

        return strXml.toString(  );
    }
}
