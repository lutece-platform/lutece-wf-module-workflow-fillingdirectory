DROP TABLE IF EXISTS tf_directory_cf;
DROP TABLE IF EXISTS tf_directory_file;
DROP TABLE IF EXISTS tf_directory_physical_file;
DROP TABLE IF EXISTS tf_record_field_history;
DROP TABLE IF EXISTS workflow_helpfilling_list_entries;
DROP TABLE IF EXISTS workflow_helpfilling_cf;

/*==============================================================*/
/* Table structure for table tf_directory_cf					*/
/*==============================================================*/

CREATE TABLE tf_directory_cf(
  id_task INT DEFAULT NULL,
  id_directory INT DEFAULT NULL,
  position_directory_entry INT DEFAULT NULL,
  is_used_task_entry SMALLINT DEFAULT 0,
  id_task_entry INT DEFAULT NULL,
  entry_parameter VARCHAR(100) DEFAULT NULL,
  is_used_user SMALLINT DEFAULT 0,
  is_add_new_value SMALLINT DEFAULT 0,
  PRIMARY KEY  (id_task)
  );

/*==============================================================*/
/* Table structure for table tf_directory_file						*/
/*==============================================================*/
CREATE TABLE tf_directory_file (
  id_file INT DEFAULT 0 NOT NULL,
  title LONG VARCHAR DEFAULT NULL, 
  id_physical_file INT DEFAULT NULL,  
  file_size  INT DEFAULT NULL,
  mime_type VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY  (id_file)
 );


/*==============================================================*/
/* Table structure for table tf_directory_physical_file						*/
/*==============================================================*/
CREATE TABLE tf_directory_physical_file (
  id_physical_file INT DEFAULT 0 NOT NULL,
  file_value LONG VARBINARY,  
  PRIMARY KEY  (id_physical_file)
 );

  
/*==============================================================*/
/* Table structure for table tf_record_field_history		*/
/*==============================================================*/
CREATE TABLE tf_record_field_history (
  id_record_field INT DEFAULT 0 NOT NULL,
  id_record INT DEFAULT NULL,
  record_field_value LONG VARCHAR DEFAULT NULL,
  id_entry INT DEFAULT NULL,
  id_field INT DEFAULT NULL,
  id_file INT DEFAULT NULL,
  id_history INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL,
  PRIMARY KEY  (id_record_field,id_history,id_task)
 );
 

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
/* Table structure for table workflow_helpfilling_list_entries	*/
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
  


