package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
/**
 * Class ListEntries
 * 
 *
 */
public class ListEntries 
{
	
	@NotNull
    @Min( 1 )
    private int _nIdListEntries;
	private int _nIdTaskEntries;
    private int _nIdParentEntry;
    private int _nIdEntry;
    private boolean _bState;
    private boolean _bTempState;
    private int _nIdDirectory;
    
    /**
     * Get Id listEntries
     * @return id listEntries
     */
	public int getIdListEntries( ) 
	{
		return _nIdListEntries;
	}
	/**
	 * Set Id listEntries
	 * @param nIdListEntries new id
	 */
	public void setIdListEntries( int nIdListEntries ) 
	{
		this._nIdListEntries = nIdListEntries;
	}
	/**
	 * Get id Task Entries
	 * @return id Task Entries
	 */
	public int getIdTaskEntries( ) 
	{
		return _nIdTaskEntries;
	}
	/**
	 * Set Id Task Entries
	 * @param nIdTaskEntries new Id
	 */
	public void setIdTaskEntries( int nIdTaskEntries ) 
	{
		this._nIdTaskEntries = nIdTaskEntries;
	}
	/**
	 * Get Id Parent Entry
	 * @return Id Parent Entry
	 */
	public int getIdParentEntry( ) 
	{
		return _nIdParentEntry;
	}
	/**
	 * Set Id Parent Entry
	 * @param nIdParentEntry new  Id 
	 */
	public void setIdParentEntry( int nIdParentEntry ) 
	{
		this._nIdParentEntry = nIdParentEntry;
	}
	/**
	 * Get Id Entry
	 * @return id Entry
	 */
	public int getIdEntry( ) 
	{
		return _nIdEntry;
	}
	/**
	 * Set Id Entry
	 * @param nIdEntry Id 
	 */
	public void setIdEntry( int nIdEntry ) 
	{
		this._nIdEntry = nIdEntry;
	}
	/**
	 * Get state Entry
	 * @return state enty
	 */
	public boolean getState( ) 
	{
		return _bState;
	}
	/**
	 * Set state entry
	 * @param  bState new state
	 */
	public void setState( boolean bState )  
	{
		this._bState = bState;
	}
	/**
	 * Get id directory
	 * @return id directory
	 */
	public int getIdDirectory( ) 
	{
		return _nIdDirectory;
	}
	/**
	 * Set Id directory
	 * @param nIdDirectory directory 
	 */
	public void setIdDirectory( int nIdDirectory ) 
	{
		this._nIdDirectory = nIdDirectory;
	}
	/**
	 * Get Tmp state
	 * @return tmp state
	 */
	public boolean getTempState( ) 
	{

		return _bTempState;
	}
	/**
	 * Set Tmp state
	 * @param bTempState new temp state
	 */
	public void setTempState( boolean bTempState ) 
	{
	
		this._bTempState = bTempState;
	}
	
}
