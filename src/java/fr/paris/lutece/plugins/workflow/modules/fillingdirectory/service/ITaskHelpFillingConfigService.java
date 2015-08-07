package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.service;

import fr.paris.lutece.plugins.workflow.modules.fillingdirectory.business.TaskHelpFillingConfig;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 *
 * ITaskFillingDirectoryConfigService
 *
 */
public interface ITaskHelpFillingConfigService
{
	/**
     * Select all tasks
     * @return a list of tasks
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    List<TaskHelpFillingConfig> findAll(  );
    /**
     * insert List Entries
     * @param list listEntries
     * @param plugin the plugin
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void insertListEntries( List<ListEntries> list, Plugin plugin );
    /**
     * Select items
     * @param config task management
     * @param strIdEntry idEntry
     * @param idParentEntry idParentEntry
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void selectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry ) ;
    /**
     * Unselect items
     * @param config task management
     * @param strIdEntry idEntry
     * @param idParentEntry idParentEntry
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void unSelectUpdate( TaskHelpFillingConfig config, String strIdEntry, int idParentEntry );
    /**
     * 
     * @param idTask task
     * @param idDirectory directory
     * @return list Selected items
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    Map <Integer,List<Integer>> loadSelectedList( int idTask, int idDirectory );
    
    /**
     * 
     * @param config task management
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void selectedRecords( TaskHelpFillingConfig config );
    
    /**
     * 
     * @param idTask id task 
     * @param idDirectory id directory
     */
    @Transactional( FillingDirectoryPlugin.BEAN_TRANSACTION_MANAGER )
    void loadListEntriesTmp( int idTask, int idDirectory );
}
