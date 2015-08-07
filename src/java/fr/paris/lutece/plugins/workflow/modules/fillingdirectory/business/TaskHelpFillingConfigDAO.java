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
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.FillingDirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ListEntries;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 *
 * TaskHelpFillingConfigDAO
 *
 */
public class TaskHelpFillingConfigDAO implements ITaskHelpFillingConfigDAO
{
	
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task,id_directory,help_type,is_active " +
        " FROM workflow_helpfilling_cf WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_helpfilling_cf  " +
        "(id_task,id_directory,help_type,is_active)VALUES(?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_helpfilling_cf SET id_task=?,id_directory=?,help_type=?,is_active=?" 
        + " WHERE id_task=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_helpfilling_cf WHERE id_task=? ";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_task,id_directory,help_type,is_active FROM workflow_helpfilling_cf";
    
    
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_list_entries ) FROM workflow_helpfilling_list_entries";
    
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY_LIST = "SELECT id_list_entries, id_task_entry, id_title_parent_entry,id_entry,state,state_tmp,id_directory"
    		+ " FROM workflow_helpfilling_list_entries WHERE id_task_entry=? AND id_directory=?";
    private static final String SQL_QUERY_INSERT_LIST = "INSERT INTO  workflow_helpfilling_list_entries  " +
            "(id_list_entries, id_task_entry, id_title_parent_entry,id_entry,state,state_tmp,id_directory)VALUES(?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE_TMP_LIST = "UPDATE workflow_helpfilling_list_entries SET state_tmp=?" 
            + " WHERE id_task_entry=? AND id_title_parent_entry=? AND id_entry=? ";
    private static final String SQL_QUERY_DELETE_LIST = "DELETE FROM workflow_helpfilling_list_entries WHERE id_task_entry=? ";
        
    private static final String SQL_QUERY_SELECT_SELECTED_LIST = "SELECT id_title_parent_entry, id_entry FROM workflow_helpfilling_list_entries WHERE id_directory=? AND id_task_entry=? AND state=?";
    private static final String SQL_QUERY_SELECT_SELECTED_TITLE_LIST = "SELECT id_title_parent_entry FROM workflow_helpfilling_list_entries WHERE id_directory=? AND id_task_entry=? AND state=? ";
    private static final String SQL_QUERY_UPDATE_RECORDS_LIST = "UPDATE workflow_helpfilling_list_entries SET state=state_tmp WHERE id_task_entry=? AND id_directory=? ";
    private static final String SQL_QUERY_UPDATE_RECORDS_TMP_LIST = "UPDATE workflow_helpfilling_list_entries SET state_tmp=state WHERE id_task_entry=? AND id_directory=?";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( TaskHelpFillingConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, FillingDirectoryPlugin.getPlugin(  ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory() );
        daoUtil.setString( ++nPos, config.getstrType() );
        daoUtil.setBoolean( ++nPos, config.isActive() );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
        
        if( config.getList( )!=null && !config.getList( ).isEmpty( ) && loadListEntries( config.getIdTask(  ), config.getIdDirectory( ) ).isEmpty( ) )
        {
        	insertListEntries( config.getList( ),FillingDirectoryPlugin.getPlugin(  ) );
        }
    }
    
    @Override
    public void loadListEntriesTmp( int idTask, int idDirectory )
    {
    	 DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_RECORDS_TMP_LIST, FillingDirectoryPlugin.getPlugin(  ) );

	        int nPos = 0;

	        daoUtil.setInt( ++nPos, idTask );
	        daoUtil.setInt( ++nPos, idDirectory );
	        daoUtil.executeUpdate(  );
	        daoUtil.free(  );
    }
        
    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insertListEntries( List<ListEntries> list, Plugin plugin )
    {
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LIST, FillingDirectoryPlugin.getPlugin(  ) );
    	
    	for( ListEntries itemList:list )
    	{
    		itemList.setIdListEntries( newPrimaryKey( plugin ) );
    		
    		int nPos = 0;
    		daoUtil.setInt( ++nPos, itemList.getIdListEntries( ) );
    		daoUtil.setInt( ++nPos, itemList.getIdTaskEntries( ) );
    		daoUtil.setInt( ++nPos, itemList.getIdParentEntry( ) );
    		daoUtil.setInt( ++nPos, itemList.getIdEntry( ) );
    		daoUtil.setBoolean( ++nPos, itemList.getState( ) );
    		daoUtil.setBoolean( ++nPos, itemList.getTempState( ) );
    		daoUtil.setInt( ++nPos, itemList.getIdDirectory( ) );
    		
    		daoUtil.executeUpdate(  );
    		
    	}
    	daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( TaskHelpFillingConfig config )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, FillingDirectoryPlugin.getPlugin(  ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory( ) );
        daoUtil.setString( ++nPos, config.getstrType( ) );
        daoUtil.setBoolean( ++nPos, config.isActive( ) );
        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
        
        loadListEntriesTmp( config.getIdTask(  ) , config.getIdDirectory() );
        
        if( config.getList( ) != null && !config.getList ( ).isEmpty( ) && loadListEntries( config.getIdTask(  ), config.getIdDirectory( ) ).isEmpty( ) )
        {
        	
        	insertListEntries( config.getList ( ),FillingDirectoryPlugin.getPlugin(  ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskHelpFillingConfig load( int nIdTask )
    {
    	
    	TaskHelpFillingConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, FillingDirectoryPlugin.getPlugin(  ) );
        
        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new TaskHelpFillingConfig(  );
            config.setIdTask( daoUtil.getInt( ++nPos ) );
            config.setIdDirectory( daoUtil.getInt( ++nPos ) );
            config.setstrType( daoUtil.getString( ++nPos ) );
            config.setIsActive( daoUtil.getBoolean( ++nPos ) );
            config.setList( loadListEntries( nIdTask,daoUtil.getInt( 2 ) ) );
        }

        daoUtil.free(  );
        
        return config;
    }
    /**
     * Load entries
     * @param nIdTask task
     * @param idDirectory directory
     * @return list ListEntries
     */
    private List<ListEntries> loadListEntries( int nIdTask, int idDirectory )
    {
    	
    	List<ListEntries> listItems = new ArrayList<ListEntries> ( );
    	ListEntries list ;
    	
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY_LIST, FillingDirectoryPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.setInt( 2, idDirectory );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
        	list = new ListEntries(  );
            list.setIdListEntries( daoUtil.getInt( 1 ) );
            list.setIdTaskEntries( daoUtil.getInt( 2 ) );
            list.setIdParentEntry( daoUtil.getInt( 3 ) );
            list.setIdEntry( daoUtil.getInt( 4 ) );
            list.setState( daoUtil.getBoolean( 5 ) );
            list.setTempState( daoUtil.getBoolean( 6 ) );
            list.setIdDirectory( daoUtil.getInt( 7 ) );
            listItems.add( list );
        }
        daoUtil.free(  );
        
        return listItems;
    }
    /**
     * Load selected titles 
     * @param idTask task
     * @param idDirectory directory
     * @return list of selected items 
     */
    public List <Integer> loadSelectedTitles( int idTask, int idDirectory )
    {
    	List<Integer> items = new ArrayList<Integer> ( );
    	int nTrue = 1;
    	
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SELECTED_TITLE_LIST, FillingDirectoryPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, idDirectory );
        daoUtil.setInt( 2, idTask );
        daoUtil.setInt( 3, nTrue );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            items.add( daoUtil.getInt( 1 ) );
        }
        daoUtil.free(  );
        
        return items;
    }
    /**
     * Load selected list
     * @param idTask task
     * @param idDirectory directory
     * @return map selected items
     */
    public Map <Integer,List<Integer>> loadSelectedList( int idTask, int idDirectory )
    {
    	List <Integer> titles = loadSelectedTitles ( idTask , idDirectory ) ;
    	Map <Integer,List<Integer>> map = new HashMap <Integer,List<Integer>>() ;
    	int nTrue = 1;
    	for( Integer title: titles )
    	{
    		map.put( title, new ArrayList<Integer>() );
    	}
    	
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SELECTED_LIST, FillingDirectoryPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, idDirectory );
        daoUtil.setInt( 2, idTask );
        daoUtil.setInt( 3, nTrue );
        
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            	 
            int idParent = daoUtil.getInt( 1 ) ;
            int idEntry  = daoUtil.getInt( 2 ) ;
            
            for ( Integer item : map.keySet( ) )
            {
            	if( idParent == item )
            	{
            		map.get( item ).add( idEntry );
            	}
            }
        }
        daoUtil.free(  );
        
        return map;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTask )
    {
    	//delete ListEntries references first
        deleteListEntries( nIdTask );
        
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, FillingDirectoryPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
        //delete ListEntries references first
        deleteListEntries( nIdTask );
    }
    /**
     * Delete ListEntries
     * @param nIdTask task
     */
    private void deleteListEntries( int nIdTask )
    {

    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LIST, FillingDirectoryPlugin.getPlugin(  ) );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
	@Override
	public List<TaskHelpFillingConfig> selectAll() 
	{

		 List<TaskHelpFillingConfig> listTask = new ArrayList<TaskHelpFillingConfig>(  );
	        TaskHelpFillingConfig task;

	        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, FillingDirectoryPlugin.getPlugin(  ) );
	        daoUtil.executeQuery(  );

	        while ( daoUtil.next(  ) )
	        {
	            task = new TaskHelpFillingConfig(  );

	            task.setIdTask( daoUtil.getInt( 1 ) );
	            task.setIdDirectory( daoUtil.getInt( 2 ) );
	            task.setstrType( daoUtil.getString( 3 ) );
	            task.setIsActive( daoUtil.getBoolean( 4 ) );
	            
	            listTask.add( task );
	        }

	        daoUtil.free(  );

	        return listTask;
	}
	@Override
	public void selectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry )  
	{
		
		 DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_TMP_LIST, FillingDirectoryPlugin.getPlugin(  ) );

	        int nPos = 0;

	        daoUtil.setBoolean( ++nPos, true );
	        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
	        daoUtil.setInt( ++nPos, idParentEntry );
	        daoUtil.setInt( ++nPos, DirectoryUtils.convertStringToInt( strIdEntry ) );
	        daoUtil.executeUpdate(  );
	        daoUtil.free(  );
	}
	@Override
	public void unSelectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry )
	{
		
		 DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_TMP_LIST, FillingDirectoryPlugin.getPlugin(  ) );

	        int nPos = 0;

	        daoUtil.setBoolean( ++nPos, false );
	        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
	        daoUtil.setInt( ++nPos, idParentEntry );
	        daoUtil.setInt( ++nPos, DirectoryUtils.convertStringToInt( strIdEntry ) );
	        daoUtil.executeUpdate(  );
	        daoUtil.free(  );
	}
	@Override
	public void selectedRecords( TaskHelpFillingConfig config )
	{
		
	     		
		 DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_RECORDS_LIST, FillingDirectoryPlugin.getPlugin(  ) );

	        int nPos = 0;

	        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
	        daoUtil.setInt( ++nPos, config.getIdDirectory( ) );
	        daoUtil.executeUpdate(  );
	        daoUtil.free(  );
	}
}
