/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.5.0 -> v3.6.0                  */
/**************************************************/
/* Create a stored procedure that we use to drop foreign keys */
DROP PROCEDURE IF EXISTS drop_all_indexes;

DELIMITER //

CREATE PROCEDURE drop_all_indexes()

BEGIN

    DECLARE index_name TEXT DEFAULT NULL;
    DECLARE done TINYINT DEFAULT FALSE;

    DECLARE cursor1 CURSOR FOR SELECT constraint_name
                               FROM information_schema.TABLE_CONSTRAINTS
                               WHERE TABLE_SCHEMA = database()
                                 AND TABLE_NAME = "germinatebase"
                                 AND CONSTRAINT_TYPE = "FOREIGN KEY";

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cursor1;

    my_loop:
        LOOP

            FETCH NEXT FROM cursor1 INTO index_name;

            IF done THEN
                LEAVE my_loop;
            ELSE
                SET @query = CONCAT('ALTER TABLE `germinatebase` DROP FOREIGN KEY ', index_name);
                PREPARE stmt FROM @query;
                EXECUTE stmt;
                DEALLOCATE PREPARE stmt;

            END IF;
        END LOOP;

END;
//

DELIMITER ;

DROP TABLE IF EXISTS `temp_taxonomies`;
CREATE TABLE `temp_taxonomies`
(
    `id`             INT(11)                 NOT NULL AUTO_INCREMENT,
    `genus`          VARCHAR(255) CHARACTER
        SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
    `species`        VARCHAR(255) CHARACTER
        SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
    `subtaxa`        VARCHAR(255) CHARACTER
        SET latin1 COLLATE latin1_swedish_ci NULL     DEFAULT NULL COMMENT 'Subtaxa name.',
    `species_author` VARCHAR(255) CHARACTER
        SET latin1 COLLATE latin1_swedish_ci NULL     DEFAULT NULL COMMENT 'also known as spauthor',
    `subtaxa_author` VARCHAR(255) CHARACTER
        SET latin1 COLLATE latin1_swedish_ci NULL     DEFAULT NULL COMMENT 'also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).',
    `cropname`       VARCHAR(255) CHARACTER
        SET latin1 COLLATE latin1_swedish_ci NULL     DEFAULT NULL,
    `ploidy`         INT(11)                 NULL     DEFAULT NULL,
    `created_on`     datetime(0)             NULL     DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
    `updated_on`     TIMESTAMP(0)            NULL     DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = INNODB
  AUTO_INCREMENT = 1
  CHARACTER
      SET = latin1
  COLLATE = latin1_swedish_ci
  ROW_FORMAT = Dynamic;

INSERT INTO `temp_taxonomies` (genus, species, subtaxa, species_author, subtaxa_author, cropname, ploidy, created_on, updated_on)
SELECT taxonomies.genus,
       taxonomies.species,
       subtaxa.taxonomic_identifier,
       taxonomies.species_author,
       subtaxa.subtaxa_author,
       taxonomies.cropname,
       taxonomies.ploidy,
       taxonomies.created_on,
       taxonomies.updated_on
FROM subtaxa
         LEFT JOIN taxonomies ON taxonomies.id = subtaxa.taxonomy_id;

/* Update some foreign keys. This forces the columns to be set to NULL when referenced items are deleted. */
call drop_all_indexes();


UPDATE `germinatebase`
    LEFT JOIN `taxonomies` ON `taxonomies`.`id` = `germinatebase`.`taxonomy_id`
    LEFT JOIN `subtaxa` ON `subtaxa`.`id` = `germinatebase`.`subtaxa_id`
SET germinatebase.taxonomy_id = (
    SELECT `temp_taxonomies`.`id`
    FROM `temp_taxonomies`
    WHERE temp_taxonomies.genus <=> taxonomies.genus
      AND temp_taxonomies.species <=> taxonomies.species
      AND temp_taxonomies.species_author <=> taxonomies.species_author
      AND subtaxa.taxonomic_identifier <=> temp_taxonomies.subtaxa
      AND subtaxa.subtaxa_author <=> temp_taxonomies.subtaxa_author
      AND taxonomies.cropname <=> temp_taxonomies.cropname
      AND taxonomies.ploidy <=> temp_taxonomies.ploidy);

DROP TABLE `subtaxa`;
DROP TABLE `taxonomies`;

ALTER TABLE `temp_taxonomies` RENAME TO `taxonomies`;

ALTER TABLE `germinatebase`
    ADD CONSTRAINT `germinatebase_ibfk_institution` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_taxonomy` FOREIGN KEY (`taxonomy_id`) REFERENCES `taxonomies` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_biologicalstatus` FOREIGN KEY (`biologicalstatus_id`) REFERENCES `biologicalstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_collsrc` FOREIGN KEY (`collsrc_id`) REFERENCES `collectingsources` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_mlsstatus` FOREIGN KEY (`mlsstatus_id`) REFERENCES `mlsstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_entitytype` FOREIGN KEY (`entitytype_id`) REFERENCES `entitytypes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    ADD CONSTRAINT `germinatebase_ibfk_entityparent` FOREIGN KEY (`entityparent_id`) REFERENCES `germinatebase` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

DROP PROCEDURE IF EXISTS drop_all_indexes;


ALTER TABLE `synonyms`
    ADD INDEX (`foreign_id`) USING BTREE;

ALTER TABLE `maps`
    CHANGE COLUMN `description` `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the map.' AFTER `id`,
    ADD COLUMN `description` text NULL COMMENT 'The name of this map.' AFTER `name`;

ALTER TABLE `datasets`
    CHANGE COLUMN `description` `name` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the dataset.' AFTER `location_id`,
    ADD COLUMN `description` text NULL COMMENT 'The name of this dataset.' AFTER `name`;

ALTER TABLE `usergroupmembers`
    ADD FOREIGN KEY (`usergroup_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

INSERT INTO synonymtypes (`id`, `target_table`, `name`, `description`)
VALUES (4, "phenotypes", "Phenotypes", "Phenotype synonyms");
INSERT INTO imagetypes (`description`, `reference_table`)
VALUES ("phenotype images", "phenotypes");


/* Add a json column */
ALTER TABLE `synonyms`
    ADD COLUMN `synonyms` json NULL COMMENT 'The synonyms as a json array.' AFTER `synonymtype_id`;

/* Create a temporary table from a group_concat select */
DROP TABLE IF EXISTS `temp_synonyms`;
CREATE TABLE `temp_synonyms`
SELECT `foreign_id`, `synonymtype_id`, CONCAT("[", GROUP_CONCAT(JSON_QUOTE(`synonym`) SEPARATOR ","), "]") AS synonyms
FROM `synonyms`
GROUP BY `foreign_id`, `synonymtype_id`;

/* Change the column type to Json */
ALTER TABLE `temp_synonyms`
    CHANGE `synonyms` `synonyms` JSON NULL;

/* Update the original table */
UPDATE `synonyms`
SET `synonyms` = (SELECT `synonyms`
                  FROM `temp_synonyms` t
                  WHERE t.`foreign_id` = `synonyms`.`foreign_id` AND t.`synonymtype_id` = `synonyms`.`synonymtype_id`);

/* Delete duplicates */
DELETE s1
FROM `synonyms` s1
         INNER JOIN `synonyms` s2
WHERE s1.`id` < s2.`id`
  AND s1.`foreign_id` = s2.`foreign_id`
  AND s1.`synonymtype_id` = s2.`synonymtype_id`;

/* Drop original synonym column */
ALTER TABLE `synonyms`
    DROP COLUMN `synonym`;

/* Drop temporary table */
DROP TABLE IF EXISTS `temp_synonyms`;

/* Add new indices to improve performance of many queries */
ALTER TABLE `datasetmembers`
    ADD INDEX (`dataset_id`, `datasetmembertype_id`) USING BTREE;
ALTER TABLE `mapdefinitions`
    ADD INDEX (`marker_id`, `map_id`) USING BTREE;
ALTER TABLE `phenotypedata`
    ADD INDEX (`dataset_id`, `germinatebase_id`) USING BTREE;
ALTER TABLE `groupmembers`
    ADD INDEX (`foreign_id`) USING BTREE;
ALTER TABLE `datasetmembers`
    ADD INDEX (`foreign_id`) USING BTREE;

/* Increase the column size for dataset meta colums */
ALTER TABLE `datasetmeta`
    MODIFY COLUMN `nr_of_data_objects` bigint(0) UNSIGNED NOT NULL COMMENT 'The number of data objects contained in this dataset.' AFTER `dataset_id`,
    MODIFY COLUMN `nr_of_data_points` bigint(0) UNSIGNED NOT NULL COMMENT 'The number of individual data points contained in this dataset.' AFTER `nr_of_data_objects`;



DROP PROCEDURE IF EXISTS drop_all_foreign_keys;
/* Create a stored procedure that we use to drop foreign keys */
DELIMITER //

CREATE PROCEDURE drop_all_foreign_keys()

BEGIN

    DECLARE index_name TEXT DEFAULT NULL;
    DECLARE done TINYINT DEFAULT FALSE;

    DECLARE cursor1 CURSOR FOR SELECT constraint_name
                               FROM information_schema.TABLE_CONSTRAINTS
                               WHERE TABLE_SCHEMA = database()
                                 AND TABLE_NAME = "groupmembers"
                                 AND CONSTRAINT_TYPE = "FOREIGN KEY";

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cursor1;

    my_loop:
        LOOP

            FETCH NEXT FROM cursor1 INTO index_name;

            IF done THEN
                LEAVE my_loop;
            ELSE
                SET @query = CONCAT('ALTER TABLE `groupmembers` DROP FOREIGN KEY ', index_name);
                PREPARE stmt FROM @query;
                EXECUTE stmt;
                DEALLOCATE PREPARE stmt;

            END IF;
        END LOOP;

END;
//

DELIMITER ;

/* Update some foreign keys. This forces the items to be deleted when referenced items are deleted. */
call drop_all_foreign_keys();
ALTER TABLE `groupmembers`
    ADD CONSTRAINT `groupmembers_ibfk_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

DROP PROCEDURE drop_all_foreign_keys;