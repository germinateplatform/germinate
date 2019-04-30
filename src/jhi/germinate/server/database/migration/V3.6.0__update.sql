/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.5.0 -> v3.6.0                  */
/**************************************************/

ALTER TABLE `synonyms` ADD INDEX(`foreign_id`) USING BTREE;

ALTER TABLE `maps`
CHANGE COLUMN `description` `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the map.' AFTER `id`,
ADD COLUMN `description` text NULL COMMENT 'The name of this map.' AFTER `name`;

ALTER TABLE `datasets`
CHANGE COLUMN `description` `name` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the dataset.' AFTER `location_id`,
ADD COLUMN `description` text NULL COMMENT 'The name of this dataset.' AFTER `name`;

ALTER TABLE `usergroupmembers`
ADD FOREIGN KEY (`usergroup_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `taxonomies`
ADD COLUMN `subtaxa` varchar(255) NULL COMMENT 'Subtaxa name.' AFTER `species`,
ADD COLUMN `subtaxa_author` varchar(255) NULL COMMENT 'also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).' AFTER `species_author`;


/* Remove subtaxa table and move information to the taxonomies table instead */
UPDATE taxonomies t,
subtaxa s
SET t.subtaxa = s.taxonomic_identifier,
t.subtaxa_author = s.subtaxa_author
WHERE
	s.taxonomy_id = t.id;

ALTER TABLE `germinatebase`
DROP FOREIGN KEY `germinatebase_ibfk_subtaxa`;

ALTER TABLE `germinatebase`
DROP COLUMN `subtaxa_id`;

DROP TABLE `subtaxa`;

INSERT INTO synonymtypes (`id`, `target_table`, `name`, `description`) VALUES (4, "phenotypes", "Phenotypes", "Phenotype synonyms");
INSERT INTO imagetypes (`description`, `reference_table`) VALUES ("phenotype images", "phenotypes");


/* Add a json column */
ALTER TABLE `synonyms`
ADD COLUMN `synonyms` json NULL COMMENT 'The synonyms as a json array.' AFTER `synonymtype_id`;

/* Create a temporary table from a group_concat select */
DROP TABLE IF EXISTS `temp_synonyms`;
CREATE TABLE `temp_synonyms` SELECT `foreign_id`, `synonymtype_id`, CONCAT("[", GROUP_CONCAT(JSON_QUOTE(`synonym`) SEPARATOR ","),"]") AS synonyms FROM `synonyms` GROUP BY `foreign_id`, `synonymtype_id`;

/* Change the column type to Json */
ALTER TABLE `temp_synonyms` CHANGE `synonyms` `synonyms` JSON NULL;

/* Update the original table */
UPDATE `synonyms` SET `synonyms` = (SELECT `synonyms` FROM `temp_synonyms` t WHERE t.`foreign_id` = `synonyms`.`foreign_id` AND t.`synonymtype_id` = `synonyms`.`synonymtype_id`);

/* Delete duplicates */
DELETE s1 FROM `synonyms` s1 INNER JOIN `synonyms` s2 WHERE s1.`id` < s2.`id` AND s1.`foreign_id` = s2.`foreign_id` AND s1.`synonymtype_id` = s2.`synonymtype_id`;

/* Drop original synonym column */
ALTER TABLE `synonyms` DROP COLUMN `synonym`;

/* Drop temporary table */
DROP TABLE IF EXISTS `temp_synonyms`;

/* Add new indices to improve performance of many queries */
ALTER TABLE `datasetmembers`
ADD INDEX(`dataset_id`, `datasetmembertype_id`) USING BTREE;
ALTER TABLE `mapdefinitions`
ADD INDEX(`marker_id`, `map_id`) USING BTREE;
ALTER TABLE `phenotypedata`
ADD INDEX(`dataset_id`, `germinatebase_id`) USING BTREE;

/* Increase the column size for dataset meta colums */
ALTER TABLE `datasetmeta`
MODIFY COLUMN `nr_of_data_objects` bigint(0) UNSIGNED NOT NULL COMMENT 'The number of data objects contained in this dataset.' AFTER `dataset_id`,
MODIFY COLUMN `nr_of_data_points` bigint(0) UNSIGNED NOT NULL COMMENT 'The number of individual data points contained in this dataset.' AFTER `nr_of_data_objects`;