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
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business;


/**
 *
 * TaskFillingDirectoryConfig
 *
 */
public class TaskFillingDirectoryConfig
{
    private int _nIdTask;
    private int _nIdDirectory;
    private int _nPositionEntryDirectory;
    private int _nIdTaskEntry;
    private String _strEntryParameter;
    private boolean _bUsedTaskEntry;
    private boolean _bUsedUser;
    private boolean _bAddNewValue;

    /**
     * @return id Task
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     * set id Task
     * @param idTask id task
     */
    public void setIdTask( int idTask )
    {
        _nIdTask = idTask;
    }

    /**
     * @return id directory
     */
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * set id directory
     * @param nIdDirectory id directory
     */
    public void setIdDirectory( int nIdDirectory )
    {
        _nIdDirectory = nIdDirectory;
    }

    /**
     * @return id Entry directory
     */
    public int getPositionEntryDirectory(  )
    {
        return _nPositionEntryDirectory;
    }

    /**
     * @param nIdEntryDirectory id entry
     */
    public void setPositionEntryDirectory( int nIdEntryDirectory )
    {
        _nPositionEntryDirectory = nIdEntryDirectory;
    }

    /**
     * @return the task entry
     */
    public int getIdTaskEntry(  )
    {
        return _nIdTaskEntry;
    }

    /**
     * set id Task entry
     * @param nIdTask id of task
     */
    public void setIdTaskEntry( int nIdTask )
    {
        _nIdTaskEntry = nIdTask;
    }

    /**
     * @return entry parameter
     */
    public String getEntryParameter(  )
    {
        return _strEntryParameter;
    }

    /**
     * @param strParameter the parameter
     */
    public void setEntryParameter( String strParameter )
    {
        _strEntryParameter = strParameter;
    }

    /**
     * @param bUsed use task entry
     */
    public void setUsedTaskEntry( boolean bUsed )
    {
        _bUsedTaskEntry = bUsed;
    }

    /**
     * @return true if used task entry
     */
    public boolean isUsedTaskEntry(  )
    {
        return _bUsedTaskEntry;
    }

    /**
     * @param bUsed use user
     */
    public void setUsedUser( boolean bUsed )
    {
        _bUsedUser = bUsed;
    }

    /**
     * @return true if used user
     */
    public boolean isUsedUser(  )
    {
        return _bUsedUser;
    }

    /**
     * @param bAddNewValue true if it must add a new value, false otherwise
     */
    public void setAddNewValue( boolean bAddNewValue )
    {
        _bAddNewValue = bAddNewValue;
    }

    /**
     * @return true if add new value
     */
    public boolean isAddNewValue(  )
    {
        return _bAddNewValue;
    }
}
