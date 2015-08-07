DROP TABLE IF EXISTS workflow_helpfilling_list_entries;
DROP TABLE IF EXISTS workflow_helpfilling_cf;

/*==============================================================*/
/* Table structure for table tf_helpfillig_cf					*/
/*==============================================================*/

CREATE TABLE workflow_helpfilling_cf(
  id_task INT NOT NULL,
  id_directory INT NOT NULL,
  help_type VARCHAR(100) DEFAULT NULL,
  is_active SMALLINT DEFAULT 0,
  PRIMARY KEY  (id_task)
  );
/*==============================================================*/
/* Table structure for table tf_helpfilling_selected_entries	*/
/*==============================================================*/
  
  CREATE TABLE workflow_helpfilling_list_entries(
  id_list_entries INT NOT NULL,
  id_task_entry INT DEFAULT NULL,
  id_title_parent_entry INT DEFAULT NULL,
  id_entry INT DEFAULT NULL,
  state  SMALLINT DEFAULT 0,
  state_tmp SMALLINT DEFAULT 0,
  id_directory INT NOT NULL,
  PRIMARY KEY  (id_task_entry,id_title_parent_entry,id_entry,id_directory),
  FOREIGN KEY (id_task_entry) REFERENCES workflow_helpfilling_cf (id_task)
  );
  


