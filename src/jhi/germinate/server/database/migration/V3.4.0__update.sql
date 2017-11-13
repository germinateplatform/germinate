/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.3.2 -> v3.4.0                  */
/**************************************************/

CREATE TABLE `mlsstatus` (
`id`  int(11) NOT NULL,
`description`  varchar(255) NOT NULL,
PRIMARY KEY (`id`)
);

INSERT INTO `mlsstatus` (`id`, `description`) VALUES
(0, "No (not included)"),
(1, "Yes (included)"),
(99, "Other (elaborate in REMARKS field, e.g. 'under development'");

CREATE TABLE `storage` (
`id`  int(11) NOT NULL,
`description`  varchar(255) NOT NULL,
PRIMARY KEY (`id`)
);

INSERT INTO `storage` (`id`, `description`) VALUES
(10, "Seed collection"),
(11, "Short term"),
(12, "Medium term"),
(13, "Long term"),
(20, "Field collection"),
(30, "In vitro collection"),
(40, "Cryopreserved collection"),
(50, "DNA collection"),
(99, "Other (elaborate in REMARKS field");

CREATE TABLE `storagedata` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`germinatebase_id`  int(11) NOT NULL ,
`storage_id`  int(11) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`storage_id`) REFERENCES `storage` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE `germinatebase`
MODIFY COLUMN `collcode`  varchar(255) NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the material, the collecting institute code (COLLCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.' AFTER `colldate`,
MODIFY COLUMN `donor_code`  varchar(255) NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the donor institute. Follows INSTCODE standard.' AFTER `plant_passport`,
ADD COLUMN `breeders_name`  varchar(255) NULL COMMENT 'Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.' AFTER `breeders_code`,
ADD COLUMN `collmissid`  varchar(255) NULL COMMENT 'Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. \'CIATFOR-052\', \'CN426\').' AFTER `collcode`,
ADD COLUMN `donor_name`  varchar(255) NULL COMMENT 'Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.' AFTER `donor_code`,
ADD COLUMN `collname`  varchar(255) NULL COMMENT 'Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.' AFTER `collcode`,
ADD COLUMN `othernumb`  text NULL COMMENT 'Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.' AFTER `collmissid`,
ADD COLUMN `duplinstname`  varchar(255) NULL COMMENT 'Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.' AFTER `duplsite`,
ADD COLUMN `mlsstatus_id`  int(11) NULL AFTER `duplinstname`,
ADD COLUMN `puid`  varchar(255) NULL COMMENT 'Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.' AFTER `mlsstatus_id`;

ALTER TABLE `locations`
ADD COLUMN `coordinate_uncertainty`  int(11) NULL COMMENT 'Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown. ' AFTER `longitude`,
ADD COLUMN `coordinate_datum`  varchar(255) NULL COMMENT 'The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.' AFTER `coordinate_uncertainty`,
ADD COLUMN `georeferencing_method`  varchar(255) NULL COMMENT 'The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.' AFTER `coordinate_datum`;

ALTER TABLE `germinatebase` ADD CONSTRAINT `germinatebase_ibfk_7` FOREIGN KEY (`mlsstatus_id`) REFERENCES `mlsstatus` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE `pedigreedefinitions`
ADD COLUMN `pedigreedescription_id`  int(11) NULL AFTER `pedigreenotation_id`;

ALTER TABLE `pedigreedefinitions` ADD CONSTRAINT `pedigreedefinitions_ibfk_3` FOREIGN KEY (`pedigreedescription_id`) REFERENCES `pedigreedescriptions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `markers`
MODIFY COLUMN `marker_name`  varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the marker. This should be a unique name which identifies the marker.' AFTER `markertype_id`;

ALTER TABLE `maps`
MODIFY COLUMN `user_id`  int(11) NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).' AFTER `updated_on`;

ALTER TABLE `phenotypes` DROP FOREIGN KEY `phenotypes_ibfk_1`;

ALTER TABLE `phenotypes` ADD CONSTRAINT `phenotypes_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `pedigrees`
MODIFY COLUMN `relationship_type`  enum('Female','Male','Unknown') NULL DEFAULT 'Unknown' COMMENT 'Male or Female parent.' AFTER `parent_id`;

ALTER TABLE `markers`
ADD INDEX `marker_name` (`marker_name`) USING BTREE ;

CREATE TABLE IF NOT EXISTS `usergroups` (
  `id`  int(11) NOT NULL AUTO_INCREMENT ,
  `name`  varchar(255) NOT NULL COMMENT 'The name of the user group.',
  `description`  text NULL COMMENT 'A description of the user group.',
  `created_on`  datetime NULL COMMENT 'When the record was created.',
  `updated_on`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `usergroupmembers` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`user_id`  int(11) NOT NULL ,
`usergroup_id`  int(11) NOT NULL ,
PRIMARY KEY (`id`)
);

ALTER TABLE `datasetpermissions`
MODIFY COLUMN `user_id`  int(11) NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper usersid).' AFTER `dataset_id`,
ADD COLUMN `group_id`  int(11) NULL COMMENT 'Foreign key to usergroups table.' AFTER `user_id`;

ALTER TABLE `datasetpermissions` ADD FOREIGN KEY (`group_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS `locales` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`name`  varchar(255) NOT NULL ,
`description`  text NULL ,
`created_on`  datetime NULL COMMENT 'When the record was created.',
`updated_on`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `licenses` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`name`  varchar(255) NOT NULL ,
`description`  text NULL ,
`created_on`  datetime NULL COMMENT 'When the record was created.',
`updated_on`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `licensedata` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`license_id`  int(11) NOT NULL ,
`locale_id`  int(11) NOT NULL ,
`content`  text NULL ,
`created_on`  datetime NULL COMMENT 'When the record was created.',
`updated_on`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
PRIMARY KEY (`id`),
FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS `licenselogs` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`license_id`  int(11) NOT NULL ,
`user_id`  int(11) NOT NULL ,
`accepted_on`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
PRIMARY KEY (`id`),
FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE `datasets`
ADD COLUMN `license_id`  int(11) NULL AFTER `dataset_state_id`;

ALTER TABLE `datasets` ADD FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE `pedigrees`
MODIFY COLUMN `relationship_type`  enum('M','F','OTHER') CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'OTHER' COMMENT 'Male or Female parent. Should be recorded as \'M\' (male) or \'F\' (female).' AFTER `parent_id`;

UPDATE pedigrees SET relationship_type = 'OTHER' WHERE ISNULL(relationship_type);

ALTER TABLE `pedigrees`
MODIFY COLUMN `relationship_type`  enum('M','F','OTHER') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'OTHER' COMMENT 'Male or Female parent. Should be recorded as \'M\' (male) or \'F\' (female).' AFTER `parent_id`;

ALTER TABLE `attributes`
ADD COLUMN `target_table`  varchar(255) NOT NULL DEFAULT "germinatebase" AFTER `datatype`;

ALTER TABLE `attributedata` DROP FOREIGN KEY `attributedata_ibfk_2`;

ALTER TABLE `attributedata`
CHANGE COLUMN `germinatebase_id` `foreign_id`  int(11) NOT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).' AFTER `attribute_id`;

ALTER TABLE `attributedata`
MODIFY COLUMN `value`  text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The value of the attribute.' AFTER `foreign_id`;

UPDATE experiments SET experiment_type_id = 3 WHERE experiment_type_id = 2;
DELETE FROM experimenttypes WHERE id = 2;

ALTER TABLE `datasets`
ADD COLUMN `datatype`  varchar(255) NULL COMMENT 'A description of the data type of the contained data. Examples might be: \"raw data\", \"BLUPs\", etc.' AFTER `source_file`;