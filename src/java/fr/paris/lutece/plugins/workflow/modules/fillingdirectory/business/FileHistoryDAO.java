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

import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * This class provides Data Access methods for Field objects
 */
public final class FileHistoryDAO implements IFileHistoryDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_file ) FROM tf_directory_file";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_file,title,id_physical_file,file_size,mime_type" +
        " FROM tf_directory_file WHERE id_file = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO tf_directory_file(id_file,title,id_physical_file,file_size,mime_type)" +
        " VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM tf_directory_file WHERE id_file = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  tf_directory_file SET " +
        "id_file=?,title=?,id_physical_file=?,file_size=?,mime_type=? WHERE id_file = ?";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IFileHistoryDAO#newPrimaryKey(fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public int newPrimaryKey( Plugin plugin )
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IFileHistoryDAO#insert(fr.paris.lutece.plugins.directory.business.File, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public synchronized int insert( File file, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setString( 2, file.getTitle(  ) );

        if ( file.getPhysicalFile(  ) != null )
        {
            daoUtil.setInt( 3, file.getPhysicalFile(  ).getIdPhysicalFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( 3 );
        }

        daoUtil.setInt( 4, file.getSize(  ) );
        daoUtil.setString( 5, file.getMimeType(  ) );

        file.setIdFile( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, file.getIdFile(  ) );
        daoUtil.executeUpdate(  );

        daoUtil.free(  );

        return file.getIdFile(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IFileHistoryDAO#load(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public File load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        File file = null;
        PhysicalFile physicalFile = null;

        if ( daoUtil.next(  ) )
        {
            file = new File(  );
            file.setIdFile( daoUtil.getInt( 1 ) );
            file.setTitle( daoUtil.getString( 2 ) );

            if ( daoUtil.getObject( 3 ) != null )
            {
                physicalFile = new PhysicalFile(  );
                physicalFile.setIdPhysicalFile( daoUtil.getInt( 3 ) );
                file.setPhysicalFile( physicalFile );
            }

            file.setSize( daoUtil.getInt( 4 ) );
            file.setMimeType( daoUtil.getString( 5 ) );
        }

        daoUtil.free(  );

        return file;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IFileHistoryDAO#delete(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void delete( int nIdFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdFile );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IFileHistoryDAO#store(fr.paris.lutece.plugins.directory.business.File, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void store( File file, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, file.getIdFile(  ) );
        daoUtil.setString( 2, file.getTitle(  ) );

        if ( file.getPhysicalFile(  ) != null )
        {
            daoUtil.setInt( 3, file.getPhysicalFile(  ).getIdPhysicalFile(  ) );
        }
        else
        {
            daoUtil.setIntNull( 3 );
        }

        daoUtil.setInt( 4, file.getSize(  ) );
        daoUtil.setString( 5, file.getMimeType(  ) );
        daoUtil.setInt( 6, file.getIdFile(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
