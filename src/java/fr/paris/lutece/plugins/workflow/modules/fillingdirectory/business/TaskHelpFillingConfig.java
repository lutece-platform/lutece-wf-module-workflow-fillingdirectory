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

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ListEntries;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;


/**
 *
 *TaskNotificationtConfig
 *
 */
public class TaskHelpFillingConfig extends TaskConfig
{
	 	@NotNull
	    @Min( 1 )
	    private int _nIdDirectory;
	    private String _strHelpType;
	    private boolean _bIsActive; 
	    private List<ListEntries> _listEntries;

	    /**
	    *
	    * @return id directory
	    */
	    public int getIdDirectory(  )
	    {
	        return _nIdDirectory;
	    }

	    /**
	     * Set id directory
	     * @param idDirectory id directory
	     */
	    public void setIdDirectory( int idDirectory )
	    {
	        _nIdDirectory = idDirectory;
	    }
		/**
		 * 
		 * @return Help type
		 */
	    public String getstrType( ) 
	    {
			return _strHelpType;
		}
		/**
		 * Set help type
		 * @param strType type
		 */
	    public void setstrType( String strType ) 
	    {
			this._strHelpType = strType;
		}
	    /**
	     * 
	     * @return is Active
	     */
		public boolean isActive() 
		{
			return _bIsActive;
		}
		/**
		 * Set is Active
		 * @param bIsActive isActive
		 */
		public void setIsActive( boolean bIsActive )
		{
			this._bIsActive = bIsActive;
		}
		/**
		 * 
		 * @return List Entries
		 */
		public List<ListEntries> getList() 
		{
			return _listEntries;
		}
		/**
		 * Set List Entries
		 * @param list listEntries
		 */
		public void setList( List<ListEntries> list ) 
		{
			this._listEntries = list;
		}
	    
		
    
}
