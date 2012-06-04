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

import fr.paris.lutece.plugins.directory.business.PhysicalFile;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.IPhysicalFileHistoryDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


/**
 *
 * PhysicalFileHistoryService
 *
 */
public class PhysicalFileHistoryService implements IPhysicalFileHistoryService
{
    public static final String BEAN_SERVICE = "workflow-fillingdirectory.physicalFileHistoryService";
    @Inject
    private IPhysicalFileHistoryDAO _physicalFileHistoryDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public int create( PhysicalFile physicalFile, Plugin plugin )
    {
        return _physicalFileHistoryDAO.insert( physicalFile, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void update( PhysicalFile physicalFile, Plugin plugin )
    {
        _physicalFileHistoryDAO.store( physicalFile, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    public void remove( int nIdPhysicalFile, Plugin plugin )
    {
        _physicalFileHistoryDAO.delete( nIdPhysicalFile, plugin );
    }

    // Finders

    /**
     * {@inheritDoc}
     */
    @Override
    public PhysicalFile findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _physicalFileHistoryDAO.load( nKey, plugin );
    }
}
