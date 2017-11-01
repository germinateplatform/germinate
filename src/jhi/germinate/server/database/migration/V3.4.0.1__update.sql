/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.3.2 -> v3.4.0                  */
/**************************************************/

ALTER TABLE `datasets`
ADD COLUMN `dublin_core`  json NULL AFTER `datatype`;

CREATE TABLE `collaborators` (
  `id`  int(11) NOT NULL AUTO_INCREMENT ,
  `first_name`  varchar(255) NOT NULL COMMENT 'Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.' ,
  `last_name`  varchar(255) NOT NULL COMMENT 'First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.' ,
  `email`  varchar(255) NULL COMMENT 'E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.' ,
  `phone`  varchar(255) NULL COMMENT 'Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.' ,
  `institution_id`  int(11) NULL COMMENT 'Author''s affiliation when the resource was created. Foreign key to ''institutions''' ,
  `created_on`  datetime NULL ON UPDATE CURRENT_TIMESTAMP ,
  `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `datasetcollaborators` (
  `id`  int(11) NOT NULL AUTO_INCREMENT ,
  `dataset_id`  int(11) NOT NULL ,
  `collaborator_id`  int(11) NOT NULL ,
  `created_on`  datetime NULL ON UPDATE CURRENT_TIMESTAMP ,
  `updated_on`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON UPDATE CASCADE,
  FOREIGN KEY (`collaborator_id`) REFERENCES `collaborators` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

ALTER TABLE `allelefrequencydata`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `value`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `analysismethods`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `attributedata`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `value`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `attributes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `target_table`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `biologicalstatus`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `sampstat`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `climatedata`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `recording_date`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `climateoverlays`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `climates`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `unit_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `collaborators`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `institution_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `collectingsources`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `collsrc`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`,
COMMENT='The coding scheme proposed can be used at 2 different levels of detail: either by using the\r\ngeneral codes such as 10, 20, 30, 40, etc., or by using the more specific codes,\r\nsuch as 11, 12, etc. See Multi Crop Passport Descriptors (MCPD V2 2012) for further definitions.';

ALTER TABLE `comments`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `reference_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `commenttypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `reference_table`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `compounddata`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `recording_date`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `compounds`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `unit_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `countries`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `country_name`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `datasetcollaborators`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `collaborator_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `datasetmeta`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `nr_of_data_points`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `datasetpermissions`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `group_id`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `datasets`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.\n' AFTER `hyperlink`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `datasetstates`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

DROP TABLE IF EXISTS droughtfreqdata;
DROP TABLE IF EXISTS droughtfreqseverity;

ALTER TABLE `experiments`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `experiment_type_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `experimenttypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `germinatebase`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `location_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `groupmembers`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `group_id`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `groups`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Foreign key to locations (locations.id).' AFTER `created_by`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `grouptypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `target_table`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `images`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `path`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `imagetypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `reference_table`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `institutions`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `address`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `licensedata`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `content`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `licenses`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `links`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `visibility`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`,
COMMENT='Germinate allows to define external links for different types of data. With this feature you can\r\ndefine links to external resources.';

ALTER TABLE `linktypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `placeholder`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `locales`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `locations`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `georeferencing_method`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `locationtypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `mapdefinitions`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `arm_impute`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `mapfeaturetypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `maps`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `visibility`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `markers`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.\n' AFTER `marker_name`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `markertypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `megaenvironmentdata`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `is_final`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `megaenvironments`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `precip_upper`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `megaenvironmentsource`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `mlsstatus`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `news`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `user_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `newstypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `pedigreedefinitions`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `definition`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `pedigreedescriptions`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `author`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `pedigreenotations`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `reference_url`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `pedigrees`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `relationship_description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `phenotypedata`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `recording_date`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `phenotypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `unit_id`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

DROP TABLE IF EXISTS soils;

ALTER TABLE `storage`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `storagedata`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `storage_id`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `subtaxa`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `taxonomic_identifier`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

ALTER TABLE `synonyms`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `synonym`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `synonymtypes`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `taxonomies`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `ploidy`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.' AFTER `created_on`;

ALTER TABLE `treatments`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

ALTER TABLE `trialseries`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `seriesname`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

ALTER TABLE `units`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `unit_description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

ALTER TABLE `usergroupmembers`
ADD COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `usergroup_id`,
ADD COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

ALTER TABLE `usergroups`
MODIFY COLUMN `created_on`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'When the record was created.' AFTER `description`,
MODIFY COLUMN `updated_on`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.' AFTER `created_on`;

ALTER TABLE `groups`
CHANGE COLUMN `description` `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The name of the group which can be used to identify it.' AFTER `grouptype_id`,
ADD COLUMN `description` text NULL COMMENT 'A free text description of the group. This has no length limitations.' AFTER `name`;