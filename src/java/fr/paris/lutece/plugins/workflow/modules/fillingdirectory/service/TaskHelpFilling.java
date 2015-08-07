package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryResourceIdService;
import fr.paris.lutece.plugins.directory.service.record.IRecordService;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * 
 * TaskHelpFilling Task
 *
 */
public class TaskHelpFilling extends SimpleTask 
{
	   
    private static final String MESSAGE_ACCESS_DENIED = "Acces denied";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD = DirectoryUtils.MESSAGE_DIRECTORY_ERROR_MANDATORY_FIELD;
    private static final String MESSAGE_DIRECTORY_ERROR = DirectoryUtils.MESSAGE_DIRECTORY_ERROR;
    private static final String JSP_CREATE_DIRECTORY_RECORD = "jsp/admin/plugins/directory/CreateDirectoryRecord.jsp";
    private static final String PARAMETER_ID_DIRECTORY = DirectoryUtils.PARAMETER_ID_DIRECTORY;
    private static final String PARAMETER_ID_DIRECTORY_RECORD = "id_directory_record";
    
    //MARK
    private static final String MARK_POPUP_ACTIVE = "popup_active";
    private static final String MARK_TRUE = "true";
    private static final String MARK_CONSTANT_STR_NULL = "";
    
    //PARAMETERS
    private static boolean _bIn ;
    // SERVICES
    @Inject
    @Named( TaskHelpFillingConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskHelpFillingConfigService;
    @Inject
    private IRecordService _recordService ;
    
	/**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
    	String strIdDirectoryRecord = request.getParameter( PARAMETER_ID_DIRECTORY_RECORD );
    	String strPopupActive = request.getParameter( MARK_POPUP_ACTIVE ) == null ? String.valueOf( MARK_CONSTANT_STR_NULL ) : request.getParameter( MARK_POPUP_ACTIVE ) ;
        int nIdDirectoryRecord = DirectoryUtils.convertStringToInt( strIdDirectoryRecord );
        
    	if ( !( strPopupActive.equals( MARK_TRUE ) ) && !_bIn )
    	{
    		_bIn = true;
	    	Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
	        
	        Record record = _recordService.findByPrimaryKey( nIdDirectoryRecord, pluginDirectory );
	        
	        record.setListRecordField( record.getListRecordField(  ) == null? new ArrayList<RecordField>( ) :record.getListRecordField(  ) );
	
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
	
	        if ( request.getParameter( PARAMETER_CANCEL ) == null )
	        {
	        	 String strRedirectUrl = getDirectoryRecordData( record, request,locale, pluginDirectory );
	
	             if ( StringUtils.isNotBlank( strRedirectUrl ) )
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
	             else
	             {
		            try
		            {
		                _recordService.updateWidthRecordField( record, pluginDirectory );
		            }
		            catch ( Exception ex )
		            {
		                // something very wrong happened... a database check might be needed
		                AppLogService.error( ex.getMessage(  ) + " when updating Record " + record.getIdRecord(  ), ex );
		            }
	             }
	        }
	    }
    }
    
    /**
     * 
     * @param record the record to fill
     * @param request the HTTP request
     * @param locale the Locale local
     * @param plugin the Plugin plugin 
     * @return directory record
     */
    private String getDirectoryRecordData( Record record, HttpServletRequest request, Locale locale, Plugin plugin )
    {

        String strUploadAction = DirectoryAsynchronousUploadHandler.getHandler(  ).getUploadAction( request );

        try
        {
            DirectoryUtils.getDirectoryRecordData( request, record, plugin , locale );
        }
        catch ( DirectoryErrorException error )
        {
            // Case if the user does not upload a file, then throw the error message
            if ( StringUtils.isBlank( strUploadAction ) )
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

                return strErrorMessage;
            }
        }

        // Special case for upload fields : if no action is specified, a submit
        // button associated with an upload might have been pressed :
        if ( StringUtils.isNotBlank( strUploadAction ) )
        {
            Map<String, List<RecordField>> mapListRecordFields = DirectoryUtils.buildMapIdEntryListRecordField( record );

            // Upload the file
            try
            {
                DirectoryAsynchronousUploadHandler.getHandler(  )
                                                  .doUploadAction( request, strUploadAction, mapListRecordFields,
                    record, plugin );
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

                return strErrorMessage;
            }

            // Put the map <idEntry, RecordFields> in the session
            request.getSession(  )
                   .setAttribute( DirectoryUtils.SESSION_DIRECTORY_LIST_SUBMITTED_RECORD_FIELDS, mapListRecordFields );

            return getJspCreateDirectoryRecord( request, record.getDirectory(  ).getIdDirectory(  ) );
        }

        return StringUtils.EMPTY;
    
    }
    /**
     * 
     * @param request the HTTP request
     * @param nIdDirectory Id directory
     * @return url path
     */
    private String getJspCreateDirectoryRecord( HttpServletRequest request, int nIdDirectory )
    {
        return AppPathService.getBaseUrl( request ) + JSP_CREATE_DIRECTORY_RECORD + "?" + PARAMETER_ID_DIRECTORY + "=" +
        nIdDirectory;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig(  )
    {
         _taskHelpFillingConfigService.remove( this.getId(  ) );
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {

        return StringUtils.EMPTY;
    }

}
