/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskHelpFillingConfig;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ITaskHelpFillingConfigService;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ListEntries;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.TaskHelpFillingConfigService;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflow.web.task.TaskComponentManager;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.resource.ExtendableResourcePluginActionManager;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
/**
 *
 * NotificationTaskComponent
 *
 */
public class HelpFillingTaskComponent extends AbstractTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_HELP_FILLING_CONFIG = "admin/plugins/workflow/modules/helpfilling/task_helpFilling_config.html";
    //Parameters
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_SAVE = "save";
    // MARKS
    private static final String TEMPLATE_MODIFY_DIRECTORY_RECORD = "admin/plugins/workflow/modules/helpfilling/modify_record_directory.html";
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    private static final String MARK_ID_DIRECTORY = "id_directory";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MESSAGE_ACCESS_DENIED = "Acces denied";
    private static final String MARK_DIRECTORY = "directory";
    private static final String MARK_DIRECTORY_RECORD = "directory_record";
    private static final String MARK_ENTRY_LIST = "entry_list";
    private static final String MARK_ROLE_REF_LIST = "role_list";
    private static final String MARK_LIST_ENTRIES = "entry_list";
    private static final String MARK_HELP_TYPE = "help_type";
    private static final String MARK_IS_ACTIVE = "is_active";
    private static final String MARK_CONFIG = "config" ;
    private static final String MARK_DIRECTORY_LIST = "list_directory";
    private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
    private static final String MARK_AVAILABLE_ITEMS = "available_items";
    private static final String MARK_SELECTED_ITEMS = "selected_items";
    private static final String MARK_ITEMS_TITLES = "titles_items";
    private static final String MARK_TYPE_POPUP = "popup";
    private static final String MARK_HIDDEN_INPUT_TAB = "hidden_input" ;
    private static final String MARK_HIDDEN_INPUT = "unselectedSelected_" ;
    private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";
    private static final String PARAMETER_ID_TASK = "id_task";
    
    @Inject
    @Named ( TaskComponentManager.BEAN_MANAGER )
    private ITaskComponentManager _taskComponentManager;
    @Inject
    private IRecordService _recordService ;
    
    @Inject
    @Named( TaskHelpFillingConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskHelpFillingConfigService;

    @Inject
    @Named( TaskHelpFillingConfigService.BEAN_SERVICE )
    private ITaskHelpFillingConfigService _listEntriesService ;

	/**
	 * create ListEntries 
	 * @param request HTTP
	 * @param config task
	 * @param task workflow
	 * @param nIdDirectory directory
	 * @return List ListEntries
	 */
    private List<ListEntries> createListEntries( HttpServletRequest request, TaskHelpFillingConfig config, ITask task, int nIdDirectory )
    {
    	List<ListEntries> listEntries = new ArrayList<ListEntries>();
    	ListEntries list = null;
        
    	Map<String, ReferenceList> references = getAvailableItems( request, config ) ;
        if( references != null && !references.isEmpty() )
        {
            Set <String> keys = references.keySet();
            for( String title: keys )
            {
            	int idParentEntry = DirectoryUtils.convertStringToInt( getTitleItems( references.get( title ) ) );
            	
            	for ( ReferenceItem strCle : references.get( title ) )
            	{
            		if( !strCle.getCode( ).equals( "-1" ) )
            		{
	            		list = new ListEntries();
	            		int idEntry = DirectoryUtils.convertStringToInt( strCle.getCode( ) );
	            		list.setIdEntry( idEntry );
	            		list.setIdParentEntry( idParentEntry );
	            		list.setIdTaskEntries( task.getId(  ) );
	            		list.setState( false );
	            		list.setTempState( false );
	            		list.setIdDirectory( nIdDirectory );
	            		listEntries.add( list );
            		}
            	}
            }
        }
        return listEntries;
    }
	/**
     * {@inheritDoc}
     */
    @Override
    public String doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
    	String strIdDirectory = request.getParameter( MARK_ID_DIRECTORY );
    	String strHelpType = request.getParameter( MARK_HELP_TYPE ) ;
    	String strIsActive = request.getParameter( MARK_IS_ACTIVE ) ;
    	boolean bIsIn = false;
    	int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
    	
    	TaskHelpFillingConfig config = _taskHelpFillingConfigService.findByPrimaryKey( task.getId(  ) );
    	if( config!= null )
    	{
    		config.setIdDirectory( nIdDirectory );
    		config.setIdTask( task.getId(  ) );
            config.setstrType( strHelpType );
            config.setIsActive( Boolean.valueOf( strIsActive ) );
    	}
    	
    	Boolean bCreate = false;
    	List<ListEntries> listEntries ;
    	
    	if( config == null )
    	{
                config = new TaskHelpFillingConfig(  );
                config.setIdTask( task.getId(  ) );
                config.setIdDirectory( nIdDirectory );
                config.setstrType( MARK_TYPE_POPUP );
                config.setIsActive( true );
                listEntries = createListEntries( request, config, task, nIdDirectory );
                config.setList( listEntries ) ;
                bCreate = true;
    	}
    	else
    	{
            Map<String, ReferenceList> references = getAvailableItems( request, config );
            listEntries = createListEntries( request, config, task, nIdDirectory );
            config.setList( listEntries ) ;
            
            Set <String> listIt = references.keySet( );
            String strInputId = null ;
            
            for( int i = 0 ;i<listIt.size( );i++ )
            {
            	String strAction = PARAMETER_APPLY+"_" + i ;
            	
            	if ( request.getParameter( strAction ) != null )
            	{
            		bIsIn = true;
            		strInputId = request.getParameter( MARK_HIDDEN_INPUT + i );
            		int idParentEntry = DirectoryUtils.convertStringToInt( strInputId );
            		doActionList( request,i,idParentEntry,config );
            	}
            }
        	if ( bIsIn )
    	    {
        		
        		return AppPathService.getBaseUrl( request ) + JSP_MODIFY_TASK + "?" + PARAMETER_ID_TASK + "=" + task.getId()+"&"+PARAMETER_APPLY+"=true";
    	    }
            
    	}

    	_listEntriesService.selectedRecords( config );
	        if ( bCreate )
	        {
	        	_taskHelpFillingConfigService.create( config );
	        }
	        else
	        {
	        	_taskHelpFillingConfigService.update( config );
	        	
	        }
        return null;
    }
    /**
     * Load directory items records
     * @param request HTTP
     * @param config task management
     * @return map items
     */
    private Map<String, ReferenceList> getAvailableItems( HttpServletRequest request, TaskHelpFillingConfig config  )
    {
    	Map<String, ReferenceList> elements =  new HashMap<String, ReferenceList>(  );  
    	if ( config!=null && config.getIdDirectory( )!=-1 )
 	    {
	    	List<IEntry>listEntries = DirectoryUtils.getFormEntries( config.getIdDirectory(), null,  AdminUserService.getAdminUser( request ) );
	       
	        for ( IEntry  e : listEntries )
	        {
	        	if ( e.getTitle() == null )
	        	{
	        		e.setTitle( "---" );
	        	}
	    		ReferenceList availableItems = new ReferenceList(  );
	    		for ( IEntry  ch : e.getChildren( ) )
	            {
	    			availableItems.addItem( ch.getIdEntry( ), ch.getTitle( ) );
	            }
	    		if ( availableItems.size( ) > 0 )
	    		{
	    			availableItems.addItem( -1,String.valueOf( e.getIdEntry( ) ) );
	    			elements.put( e.getTitle( ), availableItems );
	    		}
	        }
 	    }
    	return elements;
    }
    /**
     * Get directory items without titles
     * @param refList list reference
     * @return reference list
     */
    private ReferenceList  getAvailableItemsWithoutTitle( ReferenceList refList )
    {
    	Iterator<ReferenceItem> e = refList.iterator();
    	for( ;e.hasNext( ); )
    	{
    		ReferenceItem v = e.next();
    		if ( v.getCode( ).equals( "-1" ) )
    		{
    			e.remove( );
    		}
    	}
    	return refList;
    }
    /**
     * Get Title items
     * @param refList List reference
     * @return title items
     */
    private String getTitleItems( ReferenceList refList )
    {
    	String strTitle = "";
    	Iterator<ReferenceItem> e = refList.iterator( );
    	for( ;e.hasNext( ); )
    	{
    		ReferenceItem v = e.next( );
    		if ( v.getCode( ).equals( "-1" ) )
    		{
    			strTitle = v.getName();
    		}
    	}
    	return strTitle; 
    }
    /**
     * Update entry state
     * @param request the HTTP request
     * @param nName the action name
     * @param idParentEntry idParent
     * @param config the workflow management
     */
    private void doActionList( HttpServletRequest request,int nName, int idParentEntry, TaskHelpFillingConfig config )
    {
    	String [] tabSelectedElements = request.getParameterValues( "select_"+nName );
    	String [] tabUnSelectedElements = request.getParameterValues( "unselect_"+nName );
    	int n1 = tabSelectedElements == null? 0 : tabSelectedElements.length;
    	int n2 = tabUnSelectedElements == null? 0 : tabUnSelectedElements.length;
    	if( n1 > 0 )
    	{
	    	for( int i = 0 ; i < n1 ; i++ )
	    	{
	    		_listEntriesService.selectUpdate( config, tabSelectedElements[i], idParentEntry );
	    		
	    	}
    	}
    	if( n2 > 0 )
    	{
	    	for( int i = 0 ; i < n2 ; i++ )
	    	{
	    		_listEntriesService.unSelectUpdate( config, tabUnSelectedElements[i], idParentEntry );
	    	}
    	}
    }
        
	@Override
	public String getDisplayConfigForm( HttpServletRequest request,Locale locale, ITask task ) 
	{
		String strIdDirectory = request.getParameter( MARK_ID_DIRECTORY ) == null ? String.valueOf( DirectoryUtils.CONSTANT_ID_NULL ) : request.getParameter( MARK_ID_DIRECTORY );
		int nIdDirectory = DirectoryUtils.convertStringToInt( strIdDirectory );
		Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
	    Map<String, Object> model = new HashMap<String, Object>(  );
	    Map<String, List<RecordField>> map = null;
	    List<IEntry> listEntries = null;
	    //get list_directory
	    ReferenceList directoryList = DirectoryHome.getDirectoryList( pluginDirectory );
	    ReferenceList taskReferenceListDirectory = new ReferenceList(  );
        taskReferenceListDirectory.addItem( DirectoryUtils.CONSTANT_ID_NULL, "" );

        if ( directoryList != null )
        {
            taskReferenceListDirectory.addAll( directoryList );
        }
	    TaskHelpFillingConfig config = _taskHelpFillingConfigService.findByPrimaryKey( task.getId(  ) );
	    if ( ! ( request.getParameter( PARAMETER_SAVE ) != null || request.getParameter( PARAMETER_APPLY ) != null )  && config!=null )
    	{
	    	_listEntriesService.loadListEntriesTmp( task.getId(), config.getIdDirectory( ) );
	    	 config = _taskHelpFillingConfigService.findByPrimaryKey( task.getId(  ) );
    	}
	    //initialiser la configuration
	    
	    if ( config == null )
        {
            config = new TaskHelpFillingConfig(  );
            config.setIdDirectory( nIdDirectory );
            config.setIdTask( task.getId(  ) );
            config.setstrType( MARK_TYPE_POPUP );	
            config.setIsActive( true );
        }
	    
	    if ( config!=null && config.getIdDirectory( )!=-1 )
	    {
	    	Record record = _recordService.findByPrimaryKey( config.getIdDirectory(), null );
	    	// List of entries to display
	        listEntries = DirectoryUtils.getFormEntries( record.getDirectory(  ).getIdDirectory(  ), null,  AdminUserService.getAdminUser( request ) );
		   
		        if ( map == null )
		        {
		            map = DirectoryUtils.getMapIdEntryListRecordField( listEntries, config.getIdDirectory(), pluginDirectory );
		            // Reinit the asynchronous uploaded file map
		            DirectoryAsynchronousUploadHandler.getHandler(  ).reinitMap( request, map, pluginDirectory );
		        }
	    }
	    	   
	    // Chargement des deux listes
	    Map<String, ReferenceList> references = getAvailableItems( request, config );
	    if( references != null && !references.isEmpty( ) )
	    {
			List <String> tabTitleId = getTitles( references );
			Set <String> strKeysRef = references.keySet();
			List<ReferenceList> refItems = new ArrayList<ReferenceList>();
			
			for ( String strCle : strKeysRef )
			{
				refItems.add( references.get( strCle ) );
			}
			
		    List<ReferenceList> leftList = new ArrayList<ReferenceList>();
			List<ReferenceList> rightList = new ArrayList<ReferenceList>();
			List <ListEntries> getlistEntries = config.getList();
			
			ReferenceList rightReflist = null;
			ReferenceList leftReflist = null;
			ReferenceItem item = null;
			for( String idParent : tabTitleId )
			{
				rightReflist = new ReferenceList();
				leftReflist = new ReferenceList();
				
				for ( ListEntries e : getlistEntries )
				{
					if( e.getIdParentEntry( ) == ( DirectoryUtils.convertStringToInt ( idParent ) ) )
					{
							item = new ReferenceItem( );
							item.setCode( String.valueOf( e.getIdEntry() ) );
							item.setName( getNameByCode  ( refItems, String.valueOf( e.getIdEntry() ) ) );
							
							if( e.getTempState() && !rightReflist.contains( item )  )
							{
								rightReflist.add( item );
							}
							else if( !e.getTempState() && !leftReflist.contains( item ) )
							{
								leftReflist.add( item );
							}
					}
				}
				rightList.add( rightReflist );
				leftList.add( leftReflist );
			}
			model.put( MARK_AVAILABLE_ITEMS, leftList );
			model.put( MARK_SELECTED_ITEMS, rightList );
			model.put( MARK_HIDDEN_INPUT_TAB, tabTitleId );
			getRefencesWithoutTitle( references );
			Set <String> strKeys = references.keySet();
	        model.put( MARK_ITEMS_TITLES, strKeys );
	    }
			model.put( MARK_DIRECTORY_LIST, directoryList );
			model.put( MARK_CONFIG, config );
			model.put( MARK_LIST_ENTRIES, listEntries );
			model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, map );
	        
	        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_HELP_FILLING_CONFIG, locale, model );
        
        return template.getHtml(  );
	}
	/**
	 * Get name reference by code
	 * @param refList reference list
	 * @param code reference
	 * @return name reference
	 */
	private String getNameByCode( List<ReferenceList> refList,String code )
	{
		String name = "";
		for( ReferenceList listItems : refList )
		{
			Iterator<ReferenceItem> e = listItems.iterator( );
			
	    	for( ;e.hasNext(); )
	    	{
	    		ReferenceItem v = e.next();
	    		if( v.getCode( ).equals( code ) )
	    		{
	    			name = v.getName( );
	    		}
	    	}
		}
    	return name;
	}
	/**
	 * Get titles
	 * @param refs reference records
	 * @return list Titles
	 */
	private List <String> getTitles( Map<String, ReferenceList> refs )
	{
		List <String> tabTitleId = new ArrayList<String>();
		for ( String strCle : refs.keySet( ) )
		{
			tabTitleId.add( getTitleItems( refs.get( strCle ) ) );
		}
		return tabTitleId;
	}
	/**
	 * Get References without titles
	 * @param refs reference records
	 */
	private void getRefencesWithoutTitle( Map<String, ReferenceList> refs )
	{
		for ( String strCle : refs.keySet( ) )
		{
			refs.put( strCle, getAvailableItemsWithoutTitle( refs.get( strCle ) ) );
		}
	}
	
	@Override
	public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task ) 
	{
    	Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
		String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        Record record = _recordService.findByPrimaryKey( nIdDirectoryRecord, pluginDirectory );

        if ( ( record == null ) ||
                !RBACService.isAuthorized( Directory.RESOURCE_TYPE,
                    record.getDirectory(  ).getIdDirectory(  ) + DirectoryUtils.EMPTY_STRING,
                    DirectoryResourceIdService.PERMISSION_MODIFY_RECORD, AdminUserService.getAdminUser( request ) ) )
        {
            try 
            {
				throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
			} 
            catch ( AccessDeniedException e ) 
            {
				AppLogService.error( e );
			}
        }

        // List of entries to display
        List<IEntry> listEntry = DirectoryUtils.getFormEntries( record.getDirectory(  ).getIdDirectory(  ),
        		pluginDirectory, AdminUserService.getAdminUser( request ) );

        /**
         * Map of <idEntry, RecordFields>
         * 1) The user has uploaded/deleted a file
         * - The updated map is stored in the session
         * 2) The user has not uploaded/delete a file
         * - The map is filled with the data from the database
         * - The asynchronous uploaded files map is reinitialized
         */
        Map<String, List<RecordField>> map = null;

        // Get the map of <idEntry, RecordFields from session if it exists : 
        /**
         * 1) Case when the user has uploaded a file, the the map is stored in
         * the session
         */
        HttpSession session = request.getSession( false );

        if ( session != null )
        {
            map = (Map<String, List<RecordField>>) session.getAttribute( DirectoryUtils.SESSION_DIRECTORY_LIST_SUBMITTED_RECORD_FIELDS );
            // IMPORTANT : Remove the map from the session
            session.removeAttribute( DirectoryUtils.SESSION_DIRECTORY_LIST_SUBMITTED_RECORD_FIELDS );
        }

        // Get the map <idEntry, RecordFields> classically from the database
        /** 2) The user has not uploaded/delete a file */
        if ( map == null )
        {
            map = DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdDirectoryRecord, pluginDirectory );
            // Reinit the asynchronous uploaded file map
            DirectoryAsynchronousUploadHandler.getHandler(  ).reinitMap( request, map, pluginDirectory );
        }

        Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ), pluginDirectory );
        TaskHelpFillingConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        
        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_ENTRY_LIST, listEntry );
        model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, map );
        model.put( MARK_DIRECTORY, directory );
        model.put( MARK_CONFIG, config );
        
        if ( PortalService.isExtendActivated(  ) )
        {
            ExtendableResourcePluginActionManager.fillModel( request, AdminUserService.getAdminUser( request ), model,
                record.getIdExtendableResource(  ), record.getExtendableResourceType(  ) );
        }

        if ( SecurityService.isAuthenticationEnable(  ) )
        {
            model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList(  ) );
        }

        model.put( MARK_DIRECTORY_RECORD, record );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, locale );
        //setPageTitleProperty( PROPERTY_MODIFY_DIRECTORY_RECORD_PAGE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_DIRECTORY_RECORD, locale, model );

        return template.getHtml(  ) ;
	}
	
	@Override
	public String getDisplayTaskInformation( int nIdHistory,HttpServletRequest request, Locale locale, ITask task ) 
	{
        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskHelpFillingConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( "TEMPLATE_TASK_COMMENT_INFORMATION", locale, model );

        return template.getHtml(  );
	}
	
	@Override
	public String getTaskInformationXml( int nIdHistory,HttpServletRequest request, Locale locale, ITask task ) 
	{
		return null;
	}
	
	@Override
	public String doValidateTask( int nIdResource, String strResourceType,HttpServletRequest request, Locale locale, ITask task ) 
	{
		return null;
	}
	
}
