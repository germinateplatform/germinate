/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.4.0 -> v3.5.0                  */
/**************************************************/

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
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_1`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_2`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_3`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_4`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_5`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_6`;
ALTER TABLE `germinatebase` DROP FOREIGN KEY `germinatebase_ibfk_7`;

ALTER TABLE `germinatebase`
ADD CONSTRAINT `germinatebase_ibfk_1` FOREIGN KEY (`subtaxa_id`) REFERENCES `subtaxa` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_3` FOREIGN KEY (`taxonomy_id`) REFERENCES `taxonomies` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_4` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_5` FOREIGN KEY (`biologicalstatus_id`) REFERENCES `biologicalstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_6` FOREIGN KEY (`collsrc_id`) REFERENCES `collectingsources` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_7` FOREIGN KEY (`mlsstatus_id`) REFERENCES `mlsstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_8` FOREIGN KEY (`entitytype_id`) REFERENCES `entitytypes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
ADD CONSTRAINT `germinatebase_ibfk_9` FOREIGN KEY (`entityparent_id`) REFERENCES `germinatebase` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

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