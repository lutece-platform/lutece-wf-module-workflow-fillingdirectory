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
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service;

import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 *
 * IRecordFieldHistoryService
 *
 */
public interface IRecordFieldHistoryService
{
    /**
    * Creation of an instance of record field
    * @param recordField The instance of the record field which contains the informations to store
    * @param nIdHistory the history id
    * @param nIdTask the task id
    * @param plugin the Plugin
    */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void create( int nIdHistory, int nIdTask, RecordField recordField, Plugin plugin );

    /**
     * Delete the record field by task
     * @param nIdTask the task id
     * @param plugin the Plugin
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByTask( int nIdTask, Plugin plugin );

    /**
     * Delete the record field by history
     * @param nIdHistory the history id
     * @param nIdTask the task id
     * @param plugin the Plugin
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin );

    // Finders

    /**
     * Returns an instance of a Record field  by history and id task
     * @param nIdHistory the history id
     * @param nIdTask the task id
     * @param plugin the Plugin
     * @return an instance of Record field
     */
    List<RecordField> getListByHistory( int nIdHistory, int nIdTask, Plugin plugin );
}
