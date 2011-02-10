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

import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for RecordField objects
 */
public final class RecordFieldHistoryDAO implements IRecordFieldHistoryDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_BY_HISTORY = "SELECT " +
        "id_record_field,id_record,record_field_value,id_entry, " + "id_field,id_file FROM tf_record_field_history  " +
        "WHERE id_history=? AND id_task=?";
    private static final String SQL_QUERY_SELECT_BY_TASK = "SELECT " +
        "id_record_field,id_record,record_field_value,id_entry, " + "id_field,id_file FROM tf_record_field_history  " +
        "WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO tf_record_field_history( " +
        "id_record_field,id_record,record_field_value,id_entry,id_field,id_file,id_history,id_task) VALUES(?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE_BY_ID_HISTORY = "DELETE FROM tf_record_field_history WHERE id_history=? AND id_task=? ";
    private static final String SQL_QUERY_DELETE_BY_ID_TASK = "DELETE FROM tf_record_field_history WHERE id_task=? ";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IRecordFieldHistoryDAO#insert(int, int, fr.paris.lutece.plugins.directory.business.RecordField, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void insert( int nIdHistory, int nIdTask, RecordField recordField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        int nPos = 0;

        daoUtil.setInt( ++nPos, recordField.getRecord(  ).getIdRecord(  ) );
        daoUtil.setInt( ++nPos, recordField.getRecord(  ).getIdRecord(  ) );
        daoUtil.setString( ++nPos, recordField.getValue(  ) );
        daoUtil.setInt( ++nPos, recordField.getEntry(  ).getIdEntry(  ) );

        if ( recordField.getField(  ) != null )
        {
            daoUtil.setInt( ++nPos, recordField.getField(  ).getIdField(  ) );
        }
        else
        {
            daoUtil.setIntNull( ++nPos );
        }

        if ( recordField.getFile(  ) != null )
        {
            daoUtil.setInt( ++nPos, recordField.getFile(  ).getIdFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( ++nPos );
        }

        daoUtil.setInt( ++nPos, nIdHistory );
        daoUtil.setInt( ++nPos, nIdTask );
        daoUtil.executeUpdate(  );

        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IRecordFieldHistoryDAO#selectByHistory(int, int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<RecordField> selectByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        int nPos = 0;
        List<RecordField> listRecordFiel = new ArrayList<RecordField>(  );
        RecordField recordField = null;
        File file = null;
        IEntry entry = null;
        Field field = null;
        Record record = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_HISTORY, plugin );
        daoUtil.setInt( ++nPos, nIdHistory );
        daoUtil.setInt( ++nPos, nIdTask );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            recordField = new RecordField(  );
            recordField.setIdRecordField( daoUtil.getInt( ++nPos ) );
            record = new Record(  );

            record.setIdRecord( daoUtil.getInt( ++nPos ) );
            recordField.setRecord( record );
            recordField.setValue( daoUtil.getString( ++nPos ) );

            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( ++nPos ) );
            recordField.setEntry( entry );

            if ( daoUtil.getObject( ++nPos ) != null )
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( nPos ) );
                recordField.setField( field );
            }

            if ( daoUtil.getObject( ++nPos ) != null )
            {
                file = new File(  );
                file.setIdFile( daoUtil.getInt( nPos ) );
                recordField.setFile( file );
            }

            listRecordFiel.add( recordField );
        }

        daoUtil.free(  );

        return listRecordFiel;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IRecordFieldHistoryDAO#selectByTask(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public List<RecordField> selectByTask( int nIdTask, Plugin plugin )
    {
        int nPos = 0;
        List<RecordField> listRecordFiel = new ArrayList<RecordField>(  );
        RecordField recordField = null;
        File file = null;
        IEntry entry = null;
        Field field = null;
        Record record = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TASK, plugin );
        daoUtil.setInt( ++nPos, nIdTask );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            recordField = new RecordField(  );
            recordField.setIdRecordField( daoUtil.getInt( ++nPos ) );
            record = new Record(  );

            record.setIdRecord( daoUtil.getInt( ++nPos ) );
            recordField.setRecord( record );
            recordField.setValue( daoUtil.getString( ++nPos ) );

            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( ++nPos ) );
            recordField.setEntry( entry );

            if ( daoUtil.getObject( ++nPos ) != null )
            {
                field = new Field(  );
                field.setIdField( daoUtil.getInt( nPos ) );
                recordField.setField( field );
            }

            if ( daoUtil.getObject( ++nPos ) != null )
            {
                file = new File(  );
                file.setIdFile( daoUtil.getInt( nPos ) );
                recordField.setFile( file );
            }

            listRecordFiel.add( recordField );
        }

        daoUtil.free(  );

        return listRecordFiel;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IRecordFieldHistoryDAO#delete(int, int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_HISTORY, plugin );
        daoUtil.setInt( 1, nIdHistory );
        daoUtil.setInt( 2, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IRecordFieldHistoryDAO#deleteByHistory(int, int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_TASK, plugin );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
