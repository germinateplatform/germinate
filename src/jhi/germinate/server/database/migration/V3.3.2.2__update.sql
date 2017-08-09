/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.3.1 -> v3.3.2                  */
/**************************************************/

SET sql_notes = 0;

CREATE TABLE IF NOT EXISTS `datasetmeta` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to [datasets] ([datasets].id).',
  `nr_of_data_objects` int(11) NOT NULL COMMENT 'The number of data objects contained in this dataset.',
  `nr_of_data_points` int(11) NOT NULL COMMENT 'The number of individual data points contained in this dataset.',
  PRIMARY KEY (`id`),
  KEY `datasetmeta_ibfk_datasets` (`dataset_id`) USING BTREE,
  CONSTRAINT `datasetmeta_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Defines dataset sizes for the items in the datasets table. This table is automatically updated every hour.';
SET FOREIGN_KEY_CHECKS=1;

SET sql_notes = 1;