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

import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.ITaskHelpFillingConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskHelpFillingConfig;
import fr.paris.lutece.plugins.workflowcore.service.config.TaskConfigService;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;


/**
 *
 * TaskFillingDirectoryConfigService
 *
 */

public class TaskHelpFillingConfigService extends TaskConfigService implements ITaskHelpFillingConfigService
{
    public static final String BEAN_SERVICE = "workflow-fillingdirectory.taskHelpFillingConfigService";
    @Inject
    private ITaskHelpFillingConfigDAO _taskHelpFillingConfigDAO;
   
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskHelpFillingConfig> findAll(  )
    {
        return _taskHelpFillingConfigDAO.selectAll(  );
    }
    
    @Override
    public void insertListEntries( List<ListEntries> list, Plugin plugin )
    {
    	
    	_taskHelpFillingConfigDAO.insertListEntries( list,plugin );
    	
    }
    
    @Override
	public void selectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry ) 
    {
		
    	_taskHelpFillingConfigDAO.selectUpdate( config, strIdEntry, idParentEntry );
	}
    
    @Override
	public void unSelectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry ) 
    {
		
    	_taskHelpFillingConfigDAO.unSelectUpdate( config, strIdEntry, idParentEntry );
	}
    
    @Override
    public Map <Integer,List<Integer>> loadSelectedList( int idTask, int idDirectory )
    {
    	return _taskHelpFillingConfigDAO.loadSelectedList( idTask, idDirectory );
    }
    
    @Override
    public void selectedRecords( TaskHelpFillingConfig config )
    {
    	_taskHelpFillingConfigDAO.selectedRecords(  config ) ;
    }
    @Override
    public void loadListEntriesTmp( int idTask, int idDirectory )
    {
    	_taskHelpFillingConfigDAO.loadListEntriesTmp( idTask, idDirectory );
    }
}


