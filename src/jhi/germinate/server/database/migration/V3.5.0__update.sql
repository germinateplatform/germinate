/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.4.0 -> v3.5.0                  */
/**************************************************/

/* Create a stored procedure that we use to drop foreign keys */
DELIMITER //

CREATE PROCEDURE drop_all_indexes()

BEGIN

    DECLARE index_name TEXT DEFAULT NULL;
    DECLARE done TINYINT DEFAULT FALSE;

		DECLARE cursor1 CURSOR FOR SELECT constraint_name FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA = database() AND TABLE_NAME = "germinatebase" AND CONSTRAINT_TYPE = "FOREIGN KEY";

		DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

		OPEN cursor1;

		my_loop:
		LOOP

		    FETCH NEXT FROM cursor1 INTO index_name;

				IF done THEN
				    LEAVE my_loop;
				ELSE
				    SET @query =CONCAT('ALTER TABLE `germinatebase` DROP FOREIGN KEY ', index_name );
						PREPARE stmt FROM @query;
						EXECUTE stmt;
						DEALLOCATE PREPARE stmt;

				END IF;
		END LOOP;

END;
//

DELIMITER ;

/* Create the new tables used for different levels of material/entities */
CREATE TABLE `entitytypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) NOT NULL COMMENT 'The name of the entity type.',
  `description` text NULL COMMENT 'Describes the entity type.',
  `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
  `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`)
);

/* Insert the default values */
INSERT INTO `entitytypes` (`id`, `name`, `description`) VALUES (1, 'Accession', 'The basic working unit of conservation in the genebanks.'),
                                                               (2, 'Plant/Plot', 'An individual grown from an accession OR a plot of individuals from the same accession.'),
                                                               (3, 'Sample', 'A sample from a plant. An example would be taking multiple readings for the same phenotype from a plant.');

/* Add the new columns to `germinatebase` */
ALTER TABLE `germinatebase`
ADD COLUMN `entitytype_id` int(11) NULL DEFAULT 1 COMMENT 'Foreign key to entitytypes (entitytypes.id).' AFTER `location_id`,
ADD COLUMN `entityparent_id` int(11) NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).' AFTER `entitytype_id`;

/* Update some foreign keys. This forces the columns to be set to NULL when referenced items are deleted. */
call drop_all_indexes();

ALTER TABLE `germinatebase`
ADD CONSTRAINT `germinatebase_ibfk_subtaxa` FOREIGN KEY (`subtaxa_id`) REFERENCES `subtaxa` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_institution` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_taxonomy` FOREIGN KEY (`taxonomy_id`) REFERENCES `taxonomies` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_biologicalstatus` FOREIGN KEY (`biologicalstatus_id`) REFERENCES `biologicalstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_collsrc` FOREIGN KEY (`collsrc_id`) REFERENCES `collectingsources` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_mlsstatus` FOREIGN KEY (`mlsstatus_id`) REFERENCES `mlsstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_entitytype` FOREIGN KEY (`entitytype_id`) REFERENCES `entitytypes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_entityparent` FOREIGN KEY (`entityparent_id`) REFERENCES `germinatebase` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

/* Create sample entries in `germinatebase` for each sample in the `allelefrequencydata` table */
INSERT INTO germinatebase (general_identifier, name, entitytype_id, entityparent_id)
SELECT DISTINCT CONCAT(germinatebase.name, "-", sample_id), CONCAT(germinatebase.name, "-", sample_id), 2, germinatebase.id
FROM allelefrequencydata LEFT JOIN germinatebase ON germinatebase.id = allelefrequencydata.germinatebase_id;

/* Now drop the whole table, we don't need it anymore */
DROP TABLE `allelefrequencydata`;

ALTER TABLE `institutions`
MODIFY COLUMN `country_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to countries.id.' AFTER `acronym`;

ALTER TABLE `germinatebase`
MODIFY COLUMN `collcode` varchar(255) NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the\nmaterial, the collecting institute code (COLLCODE) should be the same as the holding institute\ncode (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon\nwithout space.' AFTER `colldate`;

ALTER TABLE `maps`
MODIFY COLUMN `visibility` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Determines if the map is visible to the Germinate interface or hidden.' AFTER `description`;

ALTER TABLE `datasetcollaborators` DROP FOREIGN KEY `datasetcollaborators_ibfk_1`;

ALTER TABLE `datasetcollaborators`
ADD CONSTRAINT `datasetcollaborators_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE `datasetmembertypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `target_table` varchar(255) NOT NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`)
);

CREATE TABLE `datasetmembers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dataset_id` int(11) NOT NULL,
  `foreign_id` int(11) NOT NULL,
  `datasetmembertype_id` int(11) NOT NULL,
  `created_on` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
  `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`datasetmembertype_id`) REFERENCES `datasetmembertypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO `datasetmembertypes` VALUES (1, 'markers', NOW(), NOW());
INSERT INTO `datasetmembertypes` VALUES (2, 'germinatebase', NOW(), NOW());

ALTER TABLE `climateoverlays`
MODIFY COLUMN `bottom_left_longitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.' AFTER `path`,
MODIFY COLUMN `bottom_left_latitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.' AFTER `bottom_left_longitude`,
MODIFY COLUMN `top_right_longitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.' AFTER `bottom_left_latitude`,
MODIFY COLUMN `top_right_latitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.' AFTER `top_right_longitude`;

ALTER TABLE `climateoverlays` COMMENT = 'Climate overlays can be used in conjunction with OpenStreetMap in order to visualize climate data in a geographic context.';

CREATE TABLE `datasetaccesslogs` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `user_id` int(11) NULL,
  `user_name` varchar(255) NULL,
  `user_email` varchar(255) NULL,
  `user_institution` varchar(255) NULL,
  `dataset_id` int(11) NOT NULL,
  `reason` text NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) COMMENT = 'If enabled, tracks which user accessed which datasets.';

ALTER TABLE `compounds`
CHANGE COLUMN `class` `compound_class` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL AFTER `average_mass`;

ALTER TABLE `germinatebase`
ADD COLUMN `pdci` float(64, 10) NULL COMMENT 'Passport Data Completeness Index. This is calculated by Germinate. Manual editing of this field will be overwritten.' AFTER `entityparent_id`;

DROP PROCEDURE drop_all_indexes;