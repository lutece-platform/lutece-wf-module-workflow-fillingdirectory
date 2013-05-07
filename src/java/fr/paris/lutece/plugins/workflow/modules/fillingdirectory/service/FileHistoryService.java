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

import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IFileHistoryDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


/**
 *
 * FileHistoryService
 *
 */
public class FileHistoryService implements IFileHistoryService
{
    public static final String BEAN_SERVICE = "workflow-fillingdirectory.fileHistoryService";
    @Inject
    private IFileHistoryDAO _fileHistoryDAO;
    @Inject
    private IPhysicalFileHistoryService _physicalFileHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public int create( File file, Plugin plugin )
    {
        if ( file.getPhysicalFile(  ) != null )
        {
            file.getPhysicalFile(  )
                .setIdPhysicalFile( _physicalFileHistoryService.create( file.getPhysicalFile(  ), plugin ) );
        }

        return _fileHistoryDAO.insert( file, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void update( File file, Plugin plugin )
    {
        if ( file.getPhysicalFile(  ) != null )
        {
            _physicalFileHistoryService.update( file.getPhysicalFile(  ), plugin );
        }

        _fileHistoryDAO.store( file, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void remove( int nIdFile, Plugin plugin )
    {
        File file = this.findByPrimaryKey( nIdFile, plugin );

        if ( file.getPhysicalFile(  ) != null )
        {
            _physicalFileHistoryService.remove( file.getPhysicalFile(  ).getIdPhysicalFile(  ), plugin );
        }

        _fileHistoryDAO.delete( nIdFile, plugin );
    }

    // Finders

    /**
     * {@inheritDoc}
     */
    @Override
    public File findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _fileHistoryDAO.load( nKey, plugin );
    }
}
