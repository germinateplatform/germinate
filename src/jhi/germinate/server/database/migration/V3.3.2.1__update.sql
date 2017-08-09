/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.3.1 -> v3.3.2                  */
/**************************************************/

CREATE TABLE `analysismethods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `compounds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `molecular_formula` varchar(255) DEFAULT NULL,
  `monoisotopic_mass` decimal(64,10) DEFAULT NULL,
  `average_mass` decimal(64,10) DEFAULT NULL,
  `class` varchar(255) DEFAULT NULL,
  `unit_id` int(11) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `compounds_ibfk_unit` (`unit_id`) USING BTREE,
  CONSTRAINT `compounds_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=313 DEFAULT CHARSET=latin1;

CREATE TABLE `compounddata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `compound_id` int(11) NOT NULL,
  `germinatebase_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `analysismethod_id` int(11) DEFAULT NULL,
  `compound_value` decimal(64,10) NOT NULL,
  `recording_date` datetime DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `updated_on` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `compound_value_2` decimal(64,10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `compounddata_ibfk_compound` (`compound_id`) USING BTREE,
  KEY `compounddata_ibfk_germinatebase` (`germinatebase_id`) USING BTREE,
  KEY `compounddata_ibfk_dataset` (`dataset_id`) USING BTREE,
  KEY `compounddata_ibfk_analysismethod` (`analysismethod_id`) USING BTREE,
  CONSTRAINT `compounddata_ibfk_1` FOREIGN KEY (`analysismethod_id`) REFERENCES `analysismethods` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `compounddata_ibfk_2` FOREIGN KEY (`compound_id`) REFERENCES `compounds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `compounddata_ibfk_3` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `compounddata_ibfk_4` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18277 DEFAULT CHARSET=latin1;

ALTER TABLE `linktypes`
MODIFY COLUMN `target_column`  varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'This is the column that is used to generate the link.' AFTER `target_table`,
MODIFY COLUMN `placeholder`  varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'The part of the link that will be replaced by the value of the target column.' AFTER `target_column`,
COMMENT='The link type determines which database table and column are used to construct the final\r\nlink. The ”placeholder” in the link (from the links table) will be replaced by the value of the\r\n”target column” in the ”target table”';

ALTER TABLE `links`
ADD COLUMN `foreign_id` int(11) AFTER `linktype_id`;

/* Set visibility to 1 by default */
ALTER TABLE `links`
MODIFY COLUMN `visibility`  tinyint(1) NULL DEFAULT 1 COMMENT 'Determines if the link is visible or not: {0, 1}' AFTER `description`;

INSERT INTO `synonymtypes` (`target_table`, `name`, `description`) VALUES ('compounds', 'Compounds', 'Compound synonyms');
INSERT INTO `imagetypes` (`description`, `reference_table`) VALUES ('compound images', 'compounds');

/* Increase accuracy of map positions */
ALTER TABLE `mapdefinitions`
MODIFY COLUMN `definition_start`  double(64,10) NOT NULL COMMENT 'Used if the markers location spans over an area more than a single point on the maps. Determines the marker start location.' AFTER `map_id`,
MODIFY COLUMN `definition_end`  double(64,10) NULL DEFAULT NULL COMMENT 'Used if the markers location spans over an area more than a single point on the maps. Determines the marker end location.' AFTER `definition_start`;
