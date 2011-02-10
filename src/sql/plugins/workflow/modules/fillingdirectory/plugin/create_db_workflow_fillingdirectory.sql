DROP TABLE IF EXISTS tf_directory_cf;
DROP TABLE IF EXISTS tf_directory_file;
DROP TABLE IF EXISTS tf_directory_physical_file;
DROP TABLE IF EXISTS tf_record_field_history;

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