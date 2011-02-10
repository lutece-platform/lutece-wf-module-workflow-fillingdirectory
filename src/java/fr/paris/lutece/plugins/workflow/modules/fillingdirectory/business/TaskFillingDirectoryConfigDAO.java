/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *class   TaskCommentConfig
 *
 */
public class TaskFillingDirectoryConfigDAO implements ITaskFillingDirectoryConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task,id_directory,position_directory_entry,is_used_task_entry,id_task_entry,entry_parameter,is_used_user,is_add_new_value " +
        "FROM tf_directory_cf  WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  tf_directory_cf( " +
        "id_task,id_directory,position_directory_entry ,is_used_task_entry,id_task_entry,entry_parameter,is_used_user,is_add_new_value)" +
        "VALUES (?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE tf_directory_cf " +
        "SET id_task=?,id_directory=?,position_directory_entry=?,is_used_task_entry=?,id_task_entry=?,entry_parameter=?,is_used_user=?,is_add_new_value=? " +
        " WHERE id_task=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM tf_directory_cf WHERE id_task=? ";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_task, id_directory, position_directory_entry, is_used_task_entry, " +
        "id_task_entry, entry_parameter, is_used_user, is_add_new_value FROM tf_directory_cf";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.ITaskExportToEntryDirectoryConfigDAO#insert(fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskFillingDirectoryConfig, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized void insert( TaskFillingDirectoryConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory(  ) );
        daoUtil.setInt( ++nPos, config.getPositionEntryDirectory(  ) );
        daoUtil.setBoolean( ++nPos, config.isUsedTaskEntry(  ) );
        daoUtil.setInt( ++nPos, config.getIdTaskEntry(  ) );
        daoUtil.setString( ++nPos, config.getEntryParameter(  ) );
        daoUtil.setBoolean( ++nPos, config.isUsedUser(  ) );
        daoUtil.setBoolean( ++nPos, config.isAddNewValue(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.bourserecherche.business.ITaskEvaluationExpertConfigDAO#store(fr.paris.lutece.plugins.workflow.modules.bourserecherche.business.TaskEvaluationExpertConfig, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( TaskFillingDirectoryConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory(  ) );
        daoUtil.setInt( ++nPos, config.getPositionEntryDirectory(  ) );
        daoUtil.setBoolean( ++nPos, config.isUsedTaskEntry(  ) );
        daoUtil.setInt( ++nPos, config.getIdTaskEntry(  ) );
        daoUtil.setString( ++nPos, config.getEntryParameter(  ) );
        daoUtil.setBoolean( ++nPos, config.isUsedUser(  ) );
        daoUtil.setBoolean( ++nPos, config.isAddNewValue(  ) );
        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.bourserecherche.business.ITaskEvaluationExpertConfigDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public TaskFillingDirectoryConfig load( int nIdTask, Plugin plugin )
    {
        TaskFillingDirectoryConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new TaskFillingDirectoryConfig(  );
            config.setIdTask( daoUtil.getInt( ++nPos ) );

            config.setIdDirectory( daoUtil.getInt( ++nPos ) );
            config.setPositionEntryDirectory( daoUtil.getInt( ++nPos ) );
            config.setUsedTaskEntry( daoUtil.getBoolean( ++nPos ) );
            config.setIdTaskEntry( daoUtil.getInt( ++nPos ) );
            config.setEntryParameter( daoUtil.getString( ++nPos ) );
            config.setUsedUser( daoUtil.getBoolean( ++nPos ) );
            config.setAddNewValue( daoUtil.getBoolean( ++nPos ) );
        }

        daoUtil.free(  );

        return config;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.bourserecherche.business.ITaskEvaluationExpertConfigDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nIdState, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdState );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.ITaskFillingDirectoryConfigDAO#selectAll(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<TaskFillingDirectoryConfig> selectAll( Plugin plugin )
    {
        List<TaskFillingDirectoryConfig> listTask = new ArrayList<TaskFillingDirectoryConfig>(  );
        TaskFillingDirectoryConfig task;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            task = new TaskFillingDirectoryConfig(  );

            task.setIdTask( daoUtil.getInt( 1 ) );
            task.setIdDirectory( daoUtil.getInt( 2 ) );
            task.setPositionEntryDirectory( daoUtil.getInt( 3 ) );
            task.setUsedTaskEntry( daoUtil.getBoolean( 4 ) );
            task.setIdTaskEntry( daoUtil.getInt( 5 ) );
            task.setEntryParameter( daoUtil.getString( 6 ) );
            task.setUsedUser( daoUtil.getBoolean( 7 ) );
            task.setAddNewValue( daoUtil.getBoolean( 8 ) );

            listTask.add( task );
        }

        daoUtil.free(  );

        return listTask;
    }
}
