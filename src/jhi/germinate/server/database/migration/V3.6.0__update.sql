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
