/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.FillingDirectoryPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, f...) for Record field objects
 */
public final class RecordFieldHistoryHome
{
    // Static variable pointed at the DAO instance
    private static IRecordFieldHistoryDAO _dao = (IRecordFieldHistoryDAO) SpringContextService.getPluginBean( FillingDirectoryPlugin.PLUGIN_NAME,
            "fillingDirectoryRecordFieldHistoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private RecordFieldHistoryHome(  )
    {
    }

    /**
     * Creation of an instance of record field
     *
     * @param recordField The instance of the record field which contains the informations to store
     * @param nIdHistory the history id
     * @param nIdTask the task id
     * @param plugin the Plugin
     *
     */
    public static void create( int nIdHistory, int nIdTask, RecordField recordField, Plugin plugin )
    {
        if ( recordField.getFile(  ) != null )
        {
            recordField.getFile(  ).setIdFile( FileHistoryHome.create( recordField.getFile(  ), plugin ) );
        }

        _dao.insert( nIdHistory, nIdTask, recordField, plugin );
    }

    /**
     *Delete the record field by task
     *
    
     * @param nIdTask the task id
     * @param plugin the Plugin
     */
    public static void removeByTask( int nIdTask, Plugin plugin )
    {
        List<RecordField> listRecordField = _dao.selectByTask( nIdTask, plugin );

        for ( RecordField recordField : listRecordField )
        {
            if ( ( recordField != null ) && ( recordField.getFile(  ) != null ) )
            {
                FileHistoryHome.remove( recordField.getFile(  ).getIdFile(  ), plugin );
            }
        }

        _dao.deleteByTask( nIdTask, plugin );
    }

    /**
     *Delete the record field by history
     *
     * @param nIdHistory the history id
     * @param nIdTask the task id
     * @param plugin the Plugin
     */
    public static void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        List<RecordField> listRecordField = _dao.selectByHistory( nIdHistory, nIdTask, plugin );

        for ( RecordField recordField : listRecordField )
        {
            if ( ( recordField != null ) && ( recordField.getFile(  ) != null ) )
            {
                FileHistoryHome.remove( recordField.getFile(  ).getIdFile(  ), plugin );
            }
        }

        _dao.deleteByHistory( nIdHistory, nIdTask, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Record field  by history and id task
     *
     * @param nIdHistory the history id
     * @param nIdTask the task id
     * @param plugin the Plugin
     * @return an instance of Record field
     */
    public static List<RecordField> getListByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        List<RecordField> listRecordField = _dao.selectByHistory( nIdHistory, nIdTask, plugin );

        for ( RecordField recordField : listRecordField )
        {
            if ( ( recordField != null ) && ( recordField.getFile(  ) != null ) )
            {
                recordField.setFile( FileHistoryHome.findByPrimaryKey( recordField.getFile(  ).getIdFile(  ), plugin ) );
            }

            if ( ( recordField != null ) && ( recordField.getField(  ) != null ) )
            {
                recordField.setField( FieldHome.findByPrimaryKey( recordField.getField(  ).getIdField(  ), plugin ) );
            }
        }

        return listRecordField;
    }
}
