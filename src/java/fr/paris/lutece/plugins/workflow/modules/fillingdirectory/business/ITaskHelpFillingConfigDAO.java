package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business;

import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service.ListEntries;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;
import java.util.Map;


/**
 *
 * ITaskHelpFillingConfigDAO
 *
 */
public interface ITaskHelpFillingConfigDAO extends ITaskConfigDAO<TaskHelpFillingConfig>
{
    /**
     * Select all tasks
     * @return a list of tasks
     */
    List<TaskHelpFillingConfig> selectAll(  );
    /**
     * insert ListEntries
     * @param list listEntries
     * @param plugin the plugin
     */
	void insertListEntries( List<ListEntries> list, Plugin plugin );
	/**
	 * selected items
	 * @param config task
	 * @param strIdEntry idEntry
	 * @param idParentEntry idParentEntry
	 */
	void selectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry );
	/**
	 * unselected items
	 * @param config task
	 * @param strIdEntry idEntry
	 * @param idParentEntry idParentEntry
	 */
	void unSelectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry );
	/**
	 * 
	 * @param idTask task
	 * @param idDirectory Directory
	 * @return selected list
	 */
	Map <Integer,List<Integer>> loadSelectedList( int idTask, int idDirectory );
	
	/**
	 * 
	 * @param config task management
	 */
	void selectedRecords( TaskHelpFillingConfig config ) ;
	
	/**
	 * 
	 * @param idTask id task management
	 * @param idDirectory id Directory
	 */
	void loadListEntriesTmp( int idTask, int idDirectory );
}
