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
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.record.RecordService;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ITaskHelpFillingConfigService;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.TaskHelpFillingConfigService;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.resource.ExtendableResourcePluginActionManager;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides the user interface to manage form features (
 * modify directory and to display help filling)
 */
public class HelpFillingJspBean extends PluginAdminPageJspBean 
{
	/**
	 * Generated serial version UID
	 */
	private static final long serialVersionUID = -8417121042985481292L;

	// Templates
	private static final String TEMPLATE_MODIFY_DIRECTORY_RECORD_POPUP = "admin/plugins/workflow/modules/helpfilling/popup.html";
	// Messages (I18n keys)
	private static final String MESSAGE_ACCESS_DENIED = "Acces denied";
	// properties
	private static final String PROPERTY_MODIFY_DIRECTORY_RECORD_PAGE_TITLE = "directory.modify_directory_record.page_title";
	// Markers
	private static final String MARK_WEBAPP_URL = "webapp_url";
	private static final String MARK_LOCALE = "locale";
	// private static final String MARK_DIRECTORY_RECORD_LIST =
	// "directory_record_list";
	private static final String MARK_DIRECTORY = "directory";
	private static final String MARK_DIRECTORY_RECORD = "directory_record";
	private static final String MARK_ENTRY_LIST = "entry_list";
	private static final String MARK_ROLE_REF_LIST = "role_list";
	private static final String MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD = "map_id_entry_list_record_field";
	private static final String MARK_ID_TASK = "idTask";
	// Parameters
	private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";

	// session fields
	private IRecordService _recordService = SpringContextService.getBean( RecordService.BEAN_SERVICE );
	private ITaskHelpFillingConfigService _listEntriesService = SpringContextService.getBean( TaskHelpFillingConfigService.BEAN_SERVICE );

	// popup
	/**
	 * 
	 * @param request the HTTP request
	 * @return Html template modify directory record
	 * @throws AccessDeniedException Access Exception
	 */
	public String getModifyDirectoryRecordPopup( HttpServletRequest request ) throws AccessDeniedException 
	{
		String strIdTask = request.getParameter( MARK_ID_TASK );
		String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
		int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
		int nIdTask = DirectoryUtils.convertStringToInt( strIdTask ) ;

		Record record = _recordService.findByPrimaryKey( nIdDirectoryRecord,	getPlugin(  ) );
		if ( ( record == null )
				|| !RBACService.isAuthorized( Directory.RESOURCE_TYPE, record.getDirectory( ).getIdDirectory( )
						+ DirectoryUtils.EMPTY_STRING,
						DirectoryResourceIdService.PERMISSION_MODIFY_RECORD,
						getUser( ) ) ) 
		{
			throw new AccessDeniedException( MESSAGE_ACCESS_DENIED );
		}

		// List of entries to display
		List<IEntry> listEntry = DirectoryUtils.getFormEntries( record.getDirectory().getIdDirectory(), getPlugin(), getUser( ) );

		/**
		 * Map of <idEntry, RecordFields> 1) The user has uploaded/deleted a
		 * file - The updated map is stored in the session 2) The user has not
		 * uploaded/delete a file - The map is filled with the data from the
		 * database - The asynchronous uploaded files map is reinitialized
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
			map = ( Map<String, List<RecordField>> ) session
					.getAttribute( DirectoryUtils.SESSION_DIRECTORY_LIST_SUBMITTED_RECORD_FIELDS );

			// IMPORTANT : Remove the map from the session
			session.removeAttribute( DirectoryUtils.SESSION_DIRECTORY_LIST_SUBMITTED_RECORD_FIELDS );
		}

		// Get the map <idEntry, RecordFields> classically from the database
		/** 2) The user has not uploaded/delete a file */
		if ( map == null ) 
		{
			map = DirectoryUtils.getMapIdEntryListRecordField( listEntry, nIdDirectoryRecord, getPlugin( ) );
			// Reinit the asynchronous uploaded file map
			DirectoryAsynchronousUploadHandler.getHandler().reinitMap( request,map, getPlugin( ) );
		}

		Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory( ).getIdDirectory( ), getPlugin( ) );

		Map<Integer, List<Integer>> listSelectedEntries = _listEntriesService.loadSelectedList( nIdTask, directory.getIdDirectory( ) );

		List<String> llist = new ArrayList<String>( );
		for ( Integer val : listSelectedEntries.keySet( ) ) 
		{
			llist.add( String.valueOf( val ) );
			for ( Integer e : listSelectedEntries.get( val ) )
			{
				llist.add( String.valueOf( e ) );
			}
		}
		Map<String, List<RecordField>> tabNewMap = new HashMap<String, List<RecordField>>( );

		for ( String val : llist ) 
		{
			if ( map.containsKey( val ) ) 
			{
				tabNewMap.put( val, map.get( val ) );
			}
		}
		
		Map<String, Object> model = new HashMap<String, Object>();

		model.put( MARK_ENTRY_LIST, listEntry );
		model.put( MARK_MAP_ID_ENTRY_LIST_RECORD_FIELD, tabNewMap );
		model.put( MARK_DIRECTORY, directory );

		if ( PortalService.isExtendActivated( ) )
		{
			ExtendableResourcePluginActionManager.fillModel( request, AdminUserService.getAdminUser( request ), model, 
					record.getIdExtendableResource( ),record.getExtendableResourceType( ) );
		}

		if ( SecurityService.isAuthenticationEnable( ) ) 
		{
			model.put( MARK_ROLE_REF_LIST, RoleHome.getRolesList( ) );
		}

		model.put( MARK_DIRECTORY_RECORD, record );
		model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
		model.put( MARK_LOCALE, getLocale() );
		setPageTitleProperty( PROPERTY_MODIFY_DIRECTORY_RECORD_PAGE_TITLE );

		HtmlTemplate template = AppTemplateService.getTemplate(
				TEMPLATE_MODIFY_DIRECTORY_RECORD_POPUP, getLocale(), model );

		return getAdminPage( template.getHtml( ) );
	}

}