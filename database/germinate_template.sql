/*
 *  Copyright 2019 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for analysismethods
-- ----------------------------
DROP TABLE IF EXISTS `analysismethods`;
CREATE TABLE `analysismethods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the analysis method.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Describes the analysis method.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attributedata
-- ----------------------------
DROP TABLE IF EXISTS `attributedata`;
CREATE TABLE `attributedata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `attribute_id` int(11) NOT NULL COMMENT 'Foreign key to attributes (attributes.id).',
  `foreign_id` int(11) NOT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).',
  `value` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The value of the attribute.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `germinatebase_id`(`foreign_id`) USING BTREE,
  INDEX `attribute_id`(`attribute_id`) USING BTREE,
  CONSTRAINT `attributedata_ibfk_1` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines attributes data. Attributes which are defined in attributes can have values associated with them. Data which does not warrant new column in the germinatebase table can be added here. Examples include small amounts of data defining germplasm which only exists for a small sub-group of the total database.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for attributes
-- ----------------------------
DROP TABLE IF EXISTS `attributes`;
CREATE TABLE `attributes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Defines the name of the attribute.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Describes the attribute. This should expand on the name to make it clear what the attribute actually is.',
  `datatype` enum('int','float','char') CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT 'int' COMMENT 'Describes the data type of the attribute. This can be INT, FLOAT or CHAR type.',
  `target_table` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT 'germinatebase',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Describes attributes. Attributes are bits of information that can be joined to, for example, a germinatebase entry. These are bits of data that while important do not warrant adding additional columns in the other tables. Examples would be using this to define ecotypes for germinatebase entries.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for biologicalstatus
-- ----------------------------
DROP TABLE IF EXISTS `biologicalstatus`;
CREATE TABLE `biologicalstatus`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `sampstat` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Previoulsy known as sampstat.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1000 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Based on Multi Crop Passport Descriptors (MCPD V2 2012) - The coding scheme proposed can be used at 3 different levels of detail: either by using the\ngeneral codes (in boldface) such as 100, 200, 300, 400, or by using the more specific codes\nsuch as 110, 120, etc.\n100) Wild\n110) Natural\n120) Semi-natural/wild\n130) Semi-natural/sown\n200) Weedy\n300) Traditional cultivar/landrace\n400) Breeding/research material\n 410) Breeder\'s line\n 411) Synthetic population\n 412) Hybrid\n 413) Founder stock/base population\n 414) Inbred line (parent of hybrid cultivar)\n 415) Segregating population\n 416) Clonal selection\n 420) Genetic stock\n 421) Mutant (e.g. induced/insertion mutants, tilling populations)\n 422) Cytogenetic stocks (e.g. chromosome addition/substitution, aneuploids,\namphiploids)\n 423) Other genetic stocks (e.g. mapping populations)\n500) Advanced or improved cultivar (conventional breeding methods)\n600) GMO (by genetic engineering)\n 999) Other ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of biologicalstatus
-- ----------------------------
INSERT INTO `biologicalstatus` VALUES (100, 'Wild', NULL, '2015-08-20 09:38:37');
INSERT INTO `biologicalstatus` VALUES (110, 'Natural', NULL, '2015-08-20 09:38:47');
INSERT INTO `biologicalstatus` VALUES (120, 'Semi-natural/wild', NULL, '2015-08-20 09:38:57');
INSERT INTO `biologicalstatus` VALUES (130, 'Semi-natural/sown', NULL, '2015-08-20 09:39:07');
INSERT INTO `biologicalstatus` VALUES (200, 'Weedy', NULL, '2015-08-20 09:39:14');
INSERT INTO `biologicalstatus` VALUES (300, 'Traditional cultivar/landrace', NULL, '2015-08-20 09:39:26');
INSERT INTO `biologicalstatus` VALUES (400, 'Breeding/research material', NULL, '2015-08-20 09:39:38');
INSERT INTO `biologicalstatus` VALUES (410, 'Breeder\'s line', NULL, '2015-08-20 09:39:49');
INSERT INTO `biologicalstatus` VALUES (411, 'Synthetic population', NULL, '2015-08-20 09:39:59');
INSERT INTO `biologicalstatus` VALUES (412, 'Hybrid', NULL, '2015-08-20 09:40:05');
INSERT INTO `biologicalstatus` VALUES (413, 'Founder stock/base population', NULL, '2015-08-20 09:40:17');
INSERT INTO `biologicalstatus` VALUES (414, 'Inbred line (parent of hybrid cultivar)', NULL, '2015-08-20 09:40:29');
INSERT INTO `biologicalstatus` VALUES (415, 'Segregating population', NULL, '2015-08-20 09:40:41');
INSERT INTO `biologicalstatus` VALUES (416, 'Clonal selection', NULL, '2015-08-20 09:40:50');
INSERT INTO `biologicalstatus` VALUES (420, 'Genetic stock', NULL, '2015-08-20 09:40:58');
INSERT INTO `biologicalstatus` VALUES (421, 'Mutant (e.g. induced/inserion mutants, tilling populations)', NULL, '2015-08-20 09:41:21');
INSERT INTO `biologicalstatus` VALUES (422, 'Cytogenic stocks (e.g. chromosome addition/substitution, aneuploids, amphiploids)', NULL, '2015-08-20 09:41:52');
INSERT INTO `biologicalstatus` VALUES (423, 'Other genetic stocks (e.g. mapping populations)', NULL, '2015-08-20 09:42:08');
INSERT INTO `biologicalstatus` VALUES (500, 'Advanced or improved cultivar (conventional breeding methods)', NULL, '2015-08-20 09:42:34');
INSERT INTO `biologicalstatus` VALUES (600, 'GMO (by genetic engineering)', NULL, '2015-08-20 09:42:45');
INSERT INTO `biologicalstatus` VALUES (999, 'Other', NULL, '2015-08-20 09:42:52');

-- ----------------------------
-- Table structure for climatedata
-- ----------------------------
DROP TABLE IF EXISTS `climatedata`;
CREATE TABLE `climatedata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `climate_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to climates (climates.id).',
  `location_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to locations (locations.id).',
  `climate_value` double(64, 10) NULL DEFAULT NULL COMMENT 'Value for the specific climate attribute. These are monthly averages and not daily. Monthly data is required for the current Germinate climate viisualizations and interface.',
  `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to datasets (datasets.id).',
  `recording_date` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The month that the data was recorded. This uses an integer to represent the month (1-12).',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  INDEX `climate_id`(`climate_id`) USING BTREE,
  INDEX `location_id`(`location_id`) USING BTREE,
  INDEX `climate_location_id`(`climate_id`, `location_id`) USING BTREE,
  INDEX `recording_date_climate_calue`(`recording_date`, `climate_value`) USING BTREE,
  CONSTRAINT `climatedata_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `climatedata_ibfk_2` FOREIGN KEY (`climate_id`) REFERENCES `climates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `climatedata_ibfk_3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Holds montly average climate data such as rainfall, temperature or cloud cover. This is based on locations rather than accessions like most of the other tables in Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for climateoverlays
-- ----------------------------
DROP TABLE IF EXISTS `climateoverlays`;
CREATE TABLE `climateoverlays`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `climate_id` int(11) NOT NULL COMMENT 'Foreign key to climates (climates.id).',
  `path` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'This is the path for holding images which can be used as overlays for the Google Maps representation in Germinate. The path is relative.',
  `bottom_left_longitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.',
  `bottom_left_latitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.',
  `top_right_longitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.',
  `top_right_latitude` double(64, 10) NULL DEFAULT NULL COMMENT 'Allows the allignment of images against OpenStreetMap API.',
  `is_legend` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'The legend for the image. What colours represent in the overlays. This is not required but used if present. ',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the climate overlay if additional explanation of  the overlay image is required.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `climateoverlays_climate_id`(`climate_id`) USING BTREE,
  INDEX `climateoverlays_description`(`description`) USING BTREE,
  CONSTRAINT `climateoverlays_ibfk_1` FOREIGN KEY (`climate_id`) REFERENCES `climates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Climate overlays can be used in conjunction with OpenStreetMap in order to visualize climate data in a geographic context.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for climates
-- ----------------------------
DROP TABLE IF EXISTS `climates`;
CREATE TABLE `climates`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'Describes the climate.',
  `short_name` char(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Shortened version of the climate name which is used in some table headers.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'A longer description of the climate.',
  `datatype` enum('float','int','char') CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT 'int' COMMENT 'Defines the datatype which can be FLOAT, INT or CHAR type.',
  `unit_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to units (units.id).\n',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `unit_id`(`unit_id`) USING BTREE,
  CONSTRAINT `climates_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines climates. Climates are measureable weather type characteristics such as temperature or cloud cover.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collaborators
-- ----------------------------
DROP TABLE IF EXISTS `collaborators`;
CREATE TABLE `collaborators`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
  `last_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
  `phone` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.',
  `institution_id` int(11) NULL DEFAULT NULL COMMENT 'Author\'s affiliation when the resource was created. Foreign key to \'institutions\'',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `institution_id`(`institution_id`) USING BTREE,
  CONSTRAINT `collaborators_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for collectingsources
-- ----------------------------
DROP TABLE IF EXISTS `collectingsources`;
CREATE TABLE `collectingsources`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `collsrc` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'collsrc in the Multi Crop Passport Descriptors (MCPD V2 2012)\n',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'The coding scheme proposed can be used at 2 different levels of detail: either by using the\r\ngeneral codes such as 10, 20, 30, 40, etc., or by using the more specific codes,\r\nsuch as 11, 12, etc. See Multi Crop Passport Descriptors (MCPD V2 2012) for further definitions.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of collectingsources
-- ----------------------------
INSERT INTO `collectingsources` VALUES (10, 'Wild habitat', NULL, '2015-08-20 09:49:03');
INSERT INTO `collectingsources` VALUES (11, 'Forest or woodland', NULL, '2015-08-20 09:49:13');
INSERT INTO `collectingsources` VALUES (12, 'Shrubland', NULL, '2015-08-20 09:49:18');
INSERT INTO `collectingsources` VALUES (13, 'Grassland', NULL, '2015-08-20 09:49:24');
INSERT INTO `collectingsources` VALUES (14, 'Desert or tundra', NULL, '2015-08-20 09:49:33');
INSERT INTO `collectingsources` VALUES (15, 'Aquatic habitat', NULL, '2015-08-20 09:49:40');
INSERT INTO `collectingsources` VALUES (20, 'Farm or cultivated habitat', NULL, '2015-08-20 09:49:48');
INSERT INTO `collectingsources` VALUES (21, 'Field', NULL, '2015-08-20 09:49:53');
INSERT INTO `collectingsources` VALUES (22, 'Orchard', NULL, '2015-08-20 09:49:59');
INSERT INTO `collectingsources` VALUES (23, 'Backyard, kitchen or home garden (urban, peri-urban or rural)', NULL, '2015-08-20 09:50:17');
INSERT INTO `collectingsources` VALUES (24, 'Fallow land', NULL, '2015-08-20 09:50:24');
INSERT INTO `collectingsources` VALUES (25, 'Pasture', NULL, '2015-08-20 09:50:32');
INSERT INTO `collectingsources` VALUES (26, 'Farm store', NULL, '2015-08-20 09:50:38');
INSERT INTO `collectingsources` VALUES (27, 'Threshing floor', NULL, '2015-08-20 09:50:45');
INSERT INTO `collectingsources` VALUES (28, 'Park', NULL, '2015-08-20 09:50:50');
INSERT INTO `collectingsources` VALUES (30, 'Market or shop', NULL, '2015-08-20 09:50:57');
INSERT INTO `collectingsources` VALUES (40, 'Institute, Experimental station, Research organization, Genebank', NULL, '2015-08-20 09:51:15');
INSERT INTO `collectingsources` VALUES (50, 'Seed company', NULL, '2015-08-20 09:51:21');
INSERT INTO `collectingsources` VALUES (60, 'Weedy, disturbed or ruderal habitat', NULL, '2015-08-20 09:51:36');
INSERT INTO `collectingsources` VALUES (61, 'Roadside', NULL, '2015-08-20 09:51:42');
INSERT INTO `collectingsources` VALUES (62, 'Field margin', NULL, '2015-08-20 09:51:51');
INSERT INTO `collectingsources` VALUES (99, 'Other (Elaborate in REMARKS field)', NULL, '2015-08-20 09:52:04');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `commenttype_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to commentypes (commenttypes.id).',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).',
  `visibility` tinyint(1) NULL DEFAULT NULL COMMENT 'Defines if the comment is available or masked (hidden) from the interface.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The comment content.',
  `reference_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Relates to the UID of the table to which the comment relates',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE,
  INDEX `commenttype_id`(`commenttype_id`) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`commenttype_id`) REFERENCES `commenttypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Comments can be added to different entries in Germinate such as entries from germinatebase or markers from the markers table.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for commenttypes
-- ----------------------------
DROP TABLE IF EXISTS `commenttypes`;
CREATE TABLE `commenttypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the comment type.',
  `reference_table` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'This could include \'germinatebase\' or \'markers\' to define the table that the comment relates to.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines the comment type.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of commenttypes
-- ----------------------------
INSERT INTO `commenttypes` VALUES (1, 'line annotation', 'germinatebase', '2009-03-04 13:43:42', NULL);
INSERT INTO `commenttypes` VALUES (2, 'pedigree annotation', 'germinatebase', '2010-04-29 11:34:59', NULL);
INSERT INTO `commenttypes` VALUES (3, 'location annotations', 'locations', '2013-07-24 11:50:59', NULL);

-- ----------------------------
-- Table structure for compounddata
-- ----------------------------
DROP TABLE IF EXISTS `compounddata`;
CREATE TABLE `compounddata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `compound_id` int(11) NOT NULL COMMENT 'Foreign key compounds (compounds.id).',
  `germinatebase_id` int(11) NOT NULL COMMENT 'Foreign key germinatebase (germinatebase.id).',
  `dataset_id` int(11) NOT NULL COMMENT 'Foreign key datasets (datasets.id).',
  `analysismethod_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key analysismethods (analysismethods.id).',
  `compound_value` decimal(64, 10) NOT NULL COMMENT 'The compound value for this compound_id and germinatebase_id combination.',
  `recording_date` datetime(0) NULL DEFAULT NULL COMMENT 'Date when the phenotypic result was recorded. Should be formatted \'YYYY-MM-DD HH:MM:SS\' or just \'YYYY-MM-DD\' where a timestamp is not available.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `compounddata_ibfk_compound`(`compound_id`) USING BTREE,
  INDEX `compounddata_ibfk_germinatebase`(`germinatebase_id`) USING BTREE,
  INDEX `compounddata_ibfk_dataset`(`dataset_id`) USING BTREE,
  INDEX `compounddata_ibfk_analysismethod`(`analysismethod_id`) USING BTREE,
  CONSTRAINT `compounddata_ibfk_1` FOREIGN KEY (`analysismethod_id`) REFERENCES `analysismethods` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `compounddata_ibfk_2` FOREIGN KEY (`compound_id`) REFERENCES `compounds` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `compounddata_ibfk_3` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `compounddata_ibfk_4` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for compounds
-- ----------------------------
DROP TABLE IF EXISTS `compounds`;
CREATE TABLE `compounds`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Compound full name.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Full description of the compound. This should contain enough infomation to accurately identify the compound and how it was recorded.',
  `molecular_formula` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The molecular formula of the compound.',
  `monoisotopic_mass` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The monoisotopic mass of the compound.',
  `average_mass` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The average mass of the compound.',
  `compound_class` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A classification of the compound.',
  `unit_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign Key to units (units.id).',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `compounds_ibfk_unit`(`unit_id`) USING BTREE,
  CONSTRAINT `compounds_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for countries
-- ----------------------------
DROP TABLE IF EXISTS `countries`;
CREATE TABLE `countries`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `country_code2` char(2) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'ISO 2 Code for country.',
  `country_code3` char(3) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'ISO 3 Code for country.',
  `country_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'Country name.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 250 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Countries that are used in the locations type tables in Germinate. These are the ISO codes for countries.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of countries
-- ----------------------------
INSERT INTO `countries` VALUES (-1, 'UN', 'UNK', 'UNKNOWN COUNTRY ORIGIN', '2007-01-05 14:58:53', NULL);
INSERT INTO `countries` VALUES (1, 'AF', 'AFG', 'Afghanistan', NULL, NULL);
INSERT INTO `countries` VALUES (2, 'AX', 'ALA', 'Åland Islands', NULL, NULL);
INSERT INTO `countries` VALUES (3, 'AL', 'ALB', 'Albania', NULL, NULL);
INSERT INTO `countries` VALUES (4, 'DZ', 'DZA', 'Algeria', NULL, NULL);
INSERT INTO `countries` VALUES (5, 'AS', 'ASM', 'American Samoa', NULL, NULL);
INSERT INTO `countries` VALUES (6, 'AD', 'AND', 'Andorra', NULL, NULL);
INSERT INTO `countries` VALUES (7, 'AO', 'AGO', 'Angola', NULL, NULL);
INSERT INTO `countries` VALUES (8, 'AI', 'AIA', 'Anguilla', NULL, NULL);
INSERT INTO `countries` VALUES (9, 'AQ', 'ATA', 'Antarctica', NULL, NULL);
INSERT INTO `countries` VALUES (10, 'AG', 'ATG', 'Antigua and Barbuda', NULL, NULL);
INSERT INTO `countries` VALUES (11, 'AR', 'ARG', 'Argentina', NULL, NULL);
INSERT INTO `countries` VALUES (12, 'AM', 'ARM', 'Armenia', NULL, NULL);
INSERT INTO `countries` VALUES (13, 'AW', 'ABW', 'Aruba', NULL, NULL);
INSERT INTO `countries` VALUES (14, 'AU', 'AUS', 'Australia', NULL, NULL);
INSERT INTO `countries` VALUES (15, 'AT', 'AUT', 'Austria', NULL, NULL);
INSERT INTO `countries` VALUES (16, 'AZ', 'AZE', 'Azerbaijan', NULL, NULL);
INSERT INTO `countries` VALUES (17, 'BS', 'BHS', 'Bahamas', NULL, NULL);
INSERT INTO `countries` VALUES (18, 'BH', 'BHR', 'Bahrain', NULL, NULL);
INSERT INTO `countries` VALUES (19, 'BD', 'BGD', 'Bangladesh', NULL, NULL);
INSERT INTO `countries` VALUES (20, 'BB', 'BRB', 'Barbados', NULL, NULL);
INSERT INTO `countries` VALUES (21, 'BY', 'BLR', 'Belarus', NULL, NULL);
INSERT INTO `countries` VALUES (22, 'BE', 'BEL', 'Belgium', NULL, NULL);
INSERT INTO `countries` VALUES (23, 'BZ', 'BLZ', 'Belize', NULL, NULL);
INSERT INTO `countries` VALUES (24, 'BJ', 'BEN', 'Benin', NULL, NULL);
INSERT INTO `countries` VALUES (25, 'BM', 'BMU', 'Bermuda', NULL, NULL);
INSERT INTO `countries` VALUES (26, 'BT', 'BTN', 'Bhutan', NULL, NULL);
INSERT INTO `countries` VALUES (27, 'BO', 'BOL', 'Bolivia (Plurinational State of)', NULL, NULL);
INSERT INTO `countries` VALUES (28, 'BQ', 'BES', 'Bonaire, Sint Eustatius and Saba', NULL, NULL);
INSERT INTO `countries` VALUES (29, 'BA', 'BIH', 'Bosnia and Herzegovina', NULL, NULL);
INSERT INTO `countries` VALUES (30, 'BW', 'BWA', 'Botswana', NULL, NULL);
INSERT INTO `countries` VALUES (31, 'BV', 'BVT', 'Bouvet Island', NULL, NULL);
INSERT INTO `countries` VALUES (32, 'BR', 'BRA', 'Brazil', NULL, NULL);
INSERT INTO `countries` VALUES (33, 'IO', 'IOT', 'British Indian Ocean Territory', NULL, NULL);
INSERT INTO `countries` VALUES (34, 'BN', 'BRN', 'Brunei Darussalam', NULL, NULL);
INSERT INTO `countries` VALUES (35, 'BG', 'BGR', 'Bulgaria', NULL, NULL);
INSERT INTO `countries` VALUES (36, 'BF', 'BFA', 'Burkina Faso', NULL, NULL);
INSERT INTO `countries` VALUES (37, 'BI', 'BDI', 'Burundi', NULL, NULL);
INSERT INTO `countries` VALUES (38, 'KH', 'KHM', 'Cambodia', NULL, NULL);
INSERT INTO `countries` VALUES (39, 'CM', 'CMR', 'Cameroon', NULL, NULL);
INSERT INTO `countries` VALUES (40, 'CA', 'CAN', 'Canada', NULL, NULL);
INSERT INTO `countries` VALUES (41, 'CV', 'CPV', 'Cabo Verde', NULL, NULL);
INSERT INTO `countries` VALUES (42, 'KY', 'CYM', 'Cayman Islands', NULL, NULL);
INSERT INTO `countries` VALUES (43, 'CF', 'CAF', 'Central African Republic', NULL, NULL);
INSERT INTO `countries` VALUES (44, 'TD', 'TCD', 'Chad', NULL, NULL);
INSERT INTO `countries` VALUES (45, 'CL', 'CHL', 'Chile', NULL, NULL);
INSERT INTO `countries` VALUES (46, 'CN', 'CHN', 'China', NULL, NULL);
INSERT INTO `countries` VALUES (47, 'CX', 'CXR', 'Christmas Island', NULL, NULL);
INSERT INTO `countries` VALUES (48, 'CC', 'CCK', 'Cocos (Keeling) Islands', NULL, NULL);
INSERT INTO `countries` VALUES (49, 'CO', 'COL', 'Colombia', NULL, NULL);
INSERT INTO `countries` VALUES (50, 'KM', 'COM', 'Comoros', NULL, NULL);
INSERT INTO `countries` VALUES (51, 'CG', 'COG', 'Congo', NULL, NULL);
INSERT INTO `countries` VALUES (52, 'CD', 'COD', 'Congo (Democratic Republic of the)', NULL, NULL);
INSERT INTO `countries` VALUES (53, 'CK', 'COK', 'Cook Islands', NULL, NULL);
INSERT INTO `countries` VALUES (54, 'CR', 'CRI', 'Costa Rica', NULL, NULL);
INSERT INTO `countries` VALUES (55, 'CI', 'CIV', 'Côte d\'Ivoire', NULL, NULL);
INSERT INTO `countries` VALUES (56, 'HR', 'HRV', 'Croatia', NULL, NULL);
INSERT INTO `countries` VALUES (57, 'CU', 'CUB', 'Cuba', NULL, NULL);
INSERT INTO `countries` VALUES (58, 'CW', 'CUW', 'Curaçao', NULL, NULL);
INSERT INTO `countries` VALUES (59, 'CY', 'CYP', 'Cyprus', NULL, NULL);
INSERT INTO `countries` VALUES (60, 'CZ', 'CZE', 'Czech Republic', NULL, NULL);
INSERT INTO `countries` VALUES (61, 'DK', 'DNK', 'Denmark', NULL, NULL);
INSERT INTO `countries` VALUES (62, 'DJ', 'DJI', 'Djibouti', NULL, NULL);
INSERT INTO `countries` VALUES (63, 'DM', 'DMA', 'Dominica', NULL, NULL);
INSERT INTO `countries` VALUES (64, 'DO', 'DOM', 'Dominican Republic', NULL, NULL);
INSERT INTO `countries` VALUES (65, 'EC', 'ECU', 'Ecuador', NULL, NULL);
INSERT INTO `countries` VALUES (66, 'EG', 'EGY', 'Egypt', NULL, NULL);
INSERT INTO `countries` VALUES (67, 'SV', 'SLV', 'El Salvador', NULL, NULL);
INSERT INTO `countries` VALUES (68, 'GQ', 'GNQ', 'Equatorial Guinea', NULL, NULL);
INSERT INTO `countries` VALUES (69, 'ER', 'ERI', 'Eritrea', NULL, NULL);
INSERT INTO `countries` VALUES (70, 'EE', 'EST', 'Estonia', NULL, NULL);
INSERT INTO `countries` VALUES (71, 'ET', 'ETH', 'Ethiopia', NULL, NULL);
INSERT INTO `countries` VALUES (72, 'FK', 'FLK', 'Falkland Islands (Malvinas)', NULL, NULL);
INSERT INTO `countries` VALUES (73, 'FO', 'FRO', 'Faroe Islands', NULL, NULL);
INSERT INTO `countries` VALUES (74, 'FJ', 'FJI', 'Fiji', NULL, NULL);
INSERT INTO `countries` VALUES (75, 'FI', 'FIN', 'Finland', NULL, NULL);
INSERT INTO `countries` VALUES (76, 'FR', 'FRA', 'France', NULL, NULL);
INSERT INTO `countries` VALUES (77, 'GF', 'GUF', 'French Guiana', NULL, NULL);
INSERT INTO `countries` VALUES (78, 'PF', 'PYF', 'French Polynesia', NULL, NULL);
INSERT INTO `countries` VALUES (79, 'TF', 'ATF', 'French Southern Territories', NULL, NULL);
INSERT INTO `countries` VALUES (80, 'GA', 'GAB', 'Gabon', NULL, NULL);
INSERT INTO `countries` VALUES (81, 'GM', 'GMB', 'Gambia', NULL, NULL);
INSERT INTO `countries` VALUES (82, 'GE', 'GEO', 'Georgia', NULL, NULL);
INSERT INTO `countries` VALUES (83, 'DE', 'DEU', 'Germany', NULL, NULL);
INSERT INTO `countries` VALUES (84, 'GH', 'GHA', 'Ghana', NULL, NULL);
INSERT INTO `countries` VALUES (85, 'GI', 'GIB', 'Gibraltar', NULL, NULL);
INSERT INTO `countries` VALUES (86, 'GR', 'GRC', 'Greece', NULL, NULL);
INSERT INTO `countries` VALUES (87, 'GL', 'GRL', 'Greenland', NULL, NULL);
INSERT INTO `countries` VALUES (88, 'GD', 'GRD', 'Grenada', NULL, NULL);
INSERT INTO `countries` VALUES (89, 'GP', 'GLP', 'Guadeloupe', NULL, NULL);
INSERT INTO `countries` VALUES (90, 'GU', 'GUM', 'Guam', NULL, NULL);
INSERT INTO `countries` VALUES (91, 'GT', 'GTM', 'Guatemala', NULL, NULL);
INSERT INTO `countries` VALUES (92, 'GG', 'GGY', 'Guernsey', NULL, NULL);
INSERT INTO `countries` VALUES (93, 'GN', 'GIN', 'Guinea', NULL, NULL);
INSERT INTO `countries` VALUES (94, 'GW', 'GNB', 'Guinea-Bissau', NULL, NULL);
INSERT INTO `countries` VALUES (95, 'GY', 'GUY', 'Guyana', NULL, NULL);
INSERT INTO `countries` VALUES (96, 'HT', 'HTI', 'Haiti', NULL, NULL);
INSERT INTO `countries` VALUES (97, 'HM', 'HMD', 'Heard Island and McDonald Islands', NULL, NULL);
INSERT INTO `countries` VALUES (98, 'VA', 'VAT', 'Holy See', NULL, NULL);
INSERT INTO `countries` VALUES (99, 'HN', 'HND', 'Honduras', NULL, NULL);
INSERT INTO `countries` VALUES (100, 'HK', 'HKG', 'Hong Kong', NULL, NULL);
INSERT INTO `countries` VALUES (101, 'HU', 'HUN', 'Hungary', NULL, NULL);
INSERT INTO `countries` VALUES (102, 'IS', 'ISL', 'Iceland', NULL, NULL);
INSERT INTO `countries` VALUES (103, 'IN', 'IND', 'India', NULL, NULL);
INSERT INTO `countries` VALUES (104, 'ID', 'IDN', 'Indonesia', NULL, NULL);
INSERT INTO `countries` VALUES (105, 'IR', 'IRN', 'Iran (Islamic Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (106, 'IQ', 'IRQ', 'Iraq', NULL, NULL);
INSERT INTO `countries` VALUES (107, 'IE', 'IRL', 'Ireland', NULL, NULL);
INSERT INTO `countries` VALUES (108, 'IM', 'IMN', 'Isle of Man', NULL, NULL);
INSERT INTO `countries` VALUES (109, 'IL', 'ISR', 'Israel', NULL, NULL);
INSERT INTO `countries` VALUES (110, 'IT', 'ITA', 'Italy', NULL, NULL);
INSERT INTO `countries` VALUES (111, 'JM', 'JAM', 'Jamaica', NULL, NULL);
INSERT INTO `countries` VALUES (112, 'JP', 'JPN', 'Japan', NULL, NULL);
INSERT INTO `countries` VALUES (113, 'JE', 'JEY', 'Jersey', NULL, NULL);
INSERT INTO `countries` VALUES (114, 'JO', 'JOR', 'Jordan', NULL, NULL);
INSERT INTO `countries` VALUES (115, 'KZ', 'KAZ', 'Kazakhstan', NULL, NULL);
INSERT INTO `countries` VALUES (116, 'KE', 'KEN', 'Kenya', NULL, NULL);
INSERT INTO `countries` VALUES (117, 'KI', 'KIR', 'Kiribati', NULL, NULL);
INSERT INTO `countries` VALUES (118, 'KP', 'PRK', 'Korea (Democratic People\'s Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (119, 'KR', 'KOR', 'Korea (Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (120, 'KW', 'KWT', 'Kuwait', NULL, NULL);
INSERT INTO `countries` VALUES (121, 'KG', 'KGZ', 'Kyrgyzstan', NULL, NULL);
INSERT INTO `countries` VALUES (122, 'LA', 'LAO', 'Lao People\'s Democratic Republic', NULL, NULL);
INSERT INTO `countries` VALUES (123, 'LV', 'LVA', 'Latvia', NULL, NULL);
INSERT INTO `countries` VALUES (124, 'LB', 'LBN', 'Lebanon', NULL, NULL);
INSERT INTO `countries` VALUES (125, 'LS', 'LSO', 'Lesotho', NULL, NULL);
INSERT INTO `countries` VALUES (126, 'LR', 'LBR', 'Liberia', NULL, NULL);
INSERT INTO `countries` VALUES (127, 'LY', 'LBY', 'Libya', NULL, NULL);
INSERT INTO `countries` VALUES (128, 'LI', 'LIE', 'Liechtenstein', NULL, NULL);
INSERT INTO `countries` VALUES (129, 'LT', 'LTU', 'Lithuania', NULL, NULL);
INSERT INTO `countries` VALUES (130, 'LU', 'LUX', 'Luxembourg', NULL, NULL);
INSERT INTO `countries` VALUES (131, 'MO', 'MAC', 'Macao', NULL, NULL);
INSERT INTO `countries` VALUES (132, 'MK', 'MKD', 'Macedonia (the former Yugoslav Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (133, 'MG', 'MDG', 'Madagascar', NULL, NULL);
INSERT INTO `countries` VALUES (134, 'MW', 'MWI', 'Malawi', NULL, NULL);
INSERT INTO `countries` VALUES (135, 'MY', 'MYS', 'Malaysia', NULL, NULL);
INSERT INTO `countries` VALUES (136, 'MV', 'MDV', 'Maldives', NULL, NULL);
INSERT INTO `countries` VALUES (137, 'ML', 'MLI', 'Mali', NULL, NULL);
INSERT INTO `countries` VALUES (138, 'MT', 'MLT', 'Malta', NULL, NULL);
INSERT INTO `countries` VALUES (139, 'MH', 'MHL', 'Marshall Islands', NULL, NULL);
INSERT INTO `countries` VALUES (140, 'MQ', 'MTQ', 'Martinique', NULL, NULL);
INSERT INTO `countries` VALUES (141, 'MR', 'MRT', 'Mauritania', NULL, NULL);
INSERT INTO `countries` VALUES (142, 'MU', 'MUS', 'Mauritius', NULL, NULL);
INSERT INTO `countries` VALUES (143, 'YT', 'MYT', 'Mayotte', NULL, NULL);
INSERT INTO `countries` VALUES (144, 'MX', 'MEX', 'Mexico', NULL, NULL);
INSERT INTO `countries` VALUES (145, 'FM', 'FSM', 'Micronesia (Federated States of)', NULL, NULL);
INSERT INTO `countries` VALUES (146, 'MD', 'MDA', 'Moldova (Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (147, 'MC', 'MCO', 'Monaco', NULL, NULL);
INSERT INTO `countries` VALUES (148, 'MN', 'MNG', 'Mongolia', NULL, NULL);
INSERT INTO `countries` VALUES (149, 'ME', 'MNE', 'Montenegro', NULL, NULL);
INSERT INTO `countries` VALUES (150, 'MS', 'MSR', 'Montserrat', NULL, NULL);
INSERT INTO `countries` VALUES (151, 'MA', 'MAR', 'Morocco', NULL, NULL);
INSERT INTO `countries` VALUES (152, 'MZ', 'MOZ', 'Mozambique', NULL, NULL);
INSERT INTO `countries` VALUES (153, 'MM', 'MMR', 'Myanmar', NULL, NULL);
INSERT INTO `countries` VALUES (154, 'NA', 'NAM', 'Namibia', NULL, NULL);
INSERT INTO `countries` VALUES (155, 'NR', 'NRU', 'Nauru', NULL, NULL);
INSERT INTO `countries` VALUES (156, 'NP', 'NPL', 'Nepal', NULL, NULL);
INSERT INTO `countries` VALUES (157, 'NL', 'NLD', 'Netherlands', NULL, NULL);
INSERT INTO `countries` VALUES (158, 'NC', 'NCL', 'New Caledonia', NULL, NULL);
INSERT INTO `countries` VALUES (159, 'NZ', 'NZL', 'New Zealand', NULL, NULL);
INSERT INTO `countries` VALUES (160, 'NI', 'NIC', 'Nicaragua', NULL, NULL);
INSERT INTO `countries` VALUES (161, 'NE', 'NER', 'Niger', NULL, NULL);
INSERT INTO `countries` VALUES (162, 'NG', 'NGA', 'Nigeria', NULL, NULL);
INSERT INTO `countries` VALUES (163, 'NU', 'NIU', 'Niue', NULL, NULL);
INSERT INTO `countries` VALUES (164, 'NF', 'NFK', 'Norfolk Island', NULL, NULL);
INSERT INTO `countries` VALUES (165, 'MP', 'MNP', 'Northern Mariana Islands', NULL, NULL);
INSERT INTO `countries` VALUES (166, 'NO', 'NOR', 'Norway', NULL, NULL);
INSERT INTO `countries` VALUES (167, 'OM', 'OMN', 'Oman', NULL, NULL);
INSERT INTO `countries` VALUES (168, 'PK', 'PAK', 'Pakistan', NULL, NULL);
INSERT INTO `countries` VALUES (169, 'PW', 'PLW', 'Palau', NULL, NULL);
INSERT INTO `countries` VALUES (170, 'PS', 'PSE', 'Palestine, State of', NULL, NULL);
INSERT INTO `countries` VALUES (171, 'PA', 'PAN', 'Panama', NULL, NULL);
INSERT INTO `countries` VALUES (172, 'PG', 'PNG', 'Papua New Guinea', NULL, NULL);
INSERT INTO `countries` VALUES (173, 'PY', 'PRY', 'Paraguay', NULL, NULL);
INSERT INTO `countries` VALUES (174, 'PE', 'PER', 'Peru', NULL, NULL);
INSERT INTO `countries` VALUES (175, 'PH', 'PHL', 'Philippines', NULL, NULL);
INSERT INTO `countries` VALUES (176, 'PN', 'PCN', 'Pitcairn', NULL, NULL);
INSERT INTO `countries` VALUES (177, 'PL', 'POL', 'Poland', NULL, NULL);
INSERT INTO `countries` VALUES (178, 'PT', 'PRT', 'Portugal', NULL, NULL);
INSERT INTO `countries` VALUES (179, 'PR', 'PRI', 'Puerto Rico', NULL, NULL);
INSERT INTO `countries` VALUES (180, 'QA', 'QAT', 'Qatar', NULL, NULL);
INSERT INTO `countries` VALUES (181, 'RE', 'REU', 'Réunion', NULL, NULL);
INSERT INTO `countries` VALUES (182, 'RO', 'ROU', 'Romania', NULL, NULL);
INSERT INTO `countries` VALUES (183, 'RU', 'RUS', 'Russian Federation', NULL, NULL);
INSERT INTO `countries` VALUES (184, 'RW', 'RWA', 'Rwanda', NULL, NULL);
INSERT INTO `countries` VALUES (185, 'BL', 'BLM', 'Saint Barthélemy', NULL, NULL);
INSERT INTO `countries` VALUES (186, 'SH', 'SHN', 'Saint Helena, Ascension and Tristan da Cunha', NULL, NULL);
INSERT INTO `countries` VALUES (187, 'KN', 'KNA', 'Saint Kitts and Nevis', NULL, NULL);
INSERT INTO `countries` VALUES (188, 'LC', 'LCA', 'Saint Lucia', NULL, NULL);
INSERT INTO `countries` VALUES (189, 'MF', 'MAF', 'Saint Martin (French part)', NULL, NULL);
INSERT INTO `countries` VALUES (190, 'PM', 'SPM', 'Saint Pierre and Miquelon', NULL, NULL);
INSERT INTO `countries` VALUES (191, 'VC', 'VCT', 'Saint Vincent and the Grenadines', NULL, NULL);
INSERT INTO `countries` VALUES (192, 'WS', 'WSM', 'Samoa', NULL, NULL);
INSERT INTO `countries` VALUES (193, 'SM', 'SMR', 'San Marino', NULL, NULL);
INSERT INTO `countries` VALUES (194, 'ST', 'STP', 'Sao Tome and Principe', NULL, NULL);
INSERT INTO `countries` VALUES (195, 'SA', 'SAU', 'Saudi Arabia', NULL, NULL);
INSERT INTO `countries` VALUES (196, 'SN', 'SEN', 'Senegal', NULL, NULL);
INSERT INTO `countries` VALUES (197, 'RS', 'SRB', 'Serbia', NULL, NULL);
INSERT INTO `countries` VALUES (198, 'SC', 'SYC', 'Seychelles', NULL, NULL);
INSERT INTO `countries` VALUES (199, 'SL', 'SLE', 'Sierra Leone', NULL, NULL);
INSERT INTO `countries` VALUES (200, 'SG', 'SGP', 'Singapore', NULL, NULL);
INSERT INTO `countries` VALUES (201, 'SX', 'SXM', 'Sint Maarten (Dutch part)', NULL, NULL);
INSERT INTO `countries` VALUES (202, 'SK', 'SVK', 'Slovakia', NULL, NULL);
INSERT INTO `countries` VALUES (203, 'SI', 'SVN', 'Slovenia', NULL, NULL);
INSERT INTO `countries` VALUES (204, 'SB', 'SLB', 'Solomon Islands', NULL, NULL);
INSERT INTO `countries` VALUES (205, 'SO', 'SOM', 'Somalia', NULL, NULL);
INSERT INTO `countries` VALUES (206, 'ZA', 'ZAF', 'South Africa', NULL, NULL);
INSERT INTO `countries` VALUES (207, 'GS', 'SGS', 'South Georgia and the South Sandwich Islands', NULL, NULL);
INSERT INTO `countries` VALUES (208, 'SS', 'SSD', 'South Sudan', NULL, NULL);
INSERT INTO `countries` VALUES (209, 'ES', 'ESP', 'Spain', NULL, NULL);
INSERT INTO `countries` VALUES (210, 'LK', 'LKA', 'Sri Lanka', NULL, NULL);
INSERT INTO `countries` VALUES (211, 'SD', 'SDN', 'Sudan', NULL, NULL);
INSERT INTO `countries` VALUES (212, 'SR', 'SUR', 'Suriname', NULL, NULL);
INSERT INTO `countries` VALUES (213, 'SJ', 'SJM', 'Svalbard and Jan Mayen', NULL, NULL);
INSERT INTO `countries` VALUES (214, 'SZ', 'SWZ', 'Swaziland', NULL, NULL);
INSERT INTO `countries` VALUES (215, 'SE', 'SWE', 'Sweden', NULL, NULL);
INSERT INTO `countries` VALUES (216, 'CH', 'CHE', 'Switzerland', NULL, NULL);
INSERT INTO `countries` VALUES (217, 'SY', 'SYR', 'Syrian Arab Republic', NULL, NULL);
INSERT INTO `countries` VALUES (218, 'TW', 'TWN', 'Taiwan, Province of China', NULL, NULL);
INSERT INTO `countries` VALUES (219, 'TJ', 'TJK', 'Tajikistan', NULL, NULL);
INSERT INTO `countries` VALUES (220, 'TZ', 'TZA', 'Tanzania, United Republic of', NULL, NULL);
INSERT INTO `countries` VALUES (221, 'TH', 'THA', 'Thailand', NULL, NULL);
INSERT INTO `countries` VALUES (222, 'TL', 'TLS', 'Timor-Leste', NULL, NULL);
INSERT INTO `countries` VALUES (223, 'TG', 'TGO', 'Togo', NULL, NULL);
INSERT INTO `countries` VALUES (224, 'TK', 'TKL', 'Tokelau', NULL, NULL);
INSERT INTO `countries` VALUES (225, 'TO', 'TON', 'Tonga', NULL, NULL);
INSERT INTO `countries` VALUES (226, 'TT', 'TTO', 'Trinidad and Tobago', NULL, NULL);
INSERT INTO `countries` VALUES (227, 'TN', 'TUN', 'Tunisia', NULL, NULL);
INSERT INTO `countries` VALUES (228, 'TR', 'TUR', 'Turkey', NULL, NULL);
INSERT INTO `countries` VALUES (229, 'TM', 'TKM', 'Turkmenistan', NULL, NULL);
INSERT INTO `countries` VALUES (230, 'TC', 'TCA', 'Turks and Caicos Islands', NULL, NULL);
INSERT INTO `countries` VALUES (231, 'TV', 'TUV', 'Tuvalu', NULL, NULL);
INSERT INTO `countries` VALUES (232, 'UG', 'UGA', 'Uganda', NULL, NULL);
INSERT INTO `countries` VALUES (233, 'UA', 'UKR', 'Ukraine', NULL, NULL);
INSERT INTO `countries` VALUES (234, 'AE', 'ARE', 'United Arab Emirates', NULL, NULL);
INSERT INTO `countries` VALUES (235, 'GB', 'GBR', 'United Kingdom of Great Britain and Northern Ireland', NULL, NULL);
INSERT INTO `countries` VALUES (236, 'US', 'USA', 'United States of America', NULL, NULL);
INSERT INTO `countries` VALUES (237, 'UM', 'UMI', 'United States Minor Outlying Islands', NULL, NULL);
INSERT INTO `countries` VALUES (238, 'UY', 'URY', 'Uruguay', NULL, NULL);
INSERT INTO `countries` VALUES (239, 'UZ', 'UZB', 'Uzbekistan', NULL, NULL);
INSERT INTO `countries` VALUES (240, 'VU', 'VUT', 'Vanuatu', NULL, NULL);
INSERT INTO `countries` VALUES (241, 'VE', 'VEN', 'Venezuela (Bolivarian Republic of)', NULL, NULL);
INSERT INTO `countries` VALUES (242, 'VN', 'VNM', 'Viet Nam', NULL, NULL);
INSERT INTO `countries` VALUES (243, 'VG', 'VGB', 'Virgin Islands (British)', NULL, NULL);
INSERT INTO `countries` VALUES (244, 'VI', 'VIR', 'Virgin Islands (U.S.)', NULL, NULL);
INSERT INTO `countries` VALUES (245, 'WF', 'WLF', 'Wallis and Futuna', NULL, NULL);
INSERT INTO `countries` VALUES (246, 'EH', 'ESH', 'Western Sahara', NULL, NULL);
INSERT INTO `countries` VALUES (247, 'YE', 'YEM', 'Yemen', NULL, NULL);
INSERT INTO `countries` VALUES (248, 'ZM', 'ZMB', 'Zambia', NULL, NULL);
INSERT INTO `countries` VALUES (249, 'ZW', 'ZWE', 'Zimbabwe', NULL, NULL);

-- ----------------------------
-- Table structure for datasetaccesslogs
-- ----------------------------
DROP TABLE IF EXISTS `datasetaccesslogs`;
CREATE TABLE `datasetaccesslogs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `user_id` int(11) NULL DEFAULT NULL,
  `user_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `user_email` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `user_institution` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `dataset_id` int(11) NOT NULL,
  `reason` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  CONSTRAINT `datasetaccesslogs_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'If enabled, tracks which user accessed which datasets.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datasetcollaborators
-- ----------------------------
DROP TABLE IF EXISTS `datasetcollaborators`;
CREATE TABLE `datasetcollaborators`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dataset_id` int(11) NOT NULL,
  `collaborator_id` int(11) NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  INDEX `collaborator_id`(`collaborator_id`) USING BTREE,
  CONSTRAINT `datasetcollaborators_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `datasetcollaborators_ibfk_2` FOREIGN KEY (`collaborator_id`) REFERENCES `collaborators` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datasetmembers
-- ----------------------------
DROP TABLE IF EXISTS `datasetmembers`;
CREATE TABLE `datasetmembers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dataset_id` int(11) NOT NULL,
  `foreign_id` int(11) NOT NULL,
  `datasetmembertype_id` int(11) NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  INDEX `datasetmembertype_id`(`datasetmembertype_id`) USING BTREE,
  INDEX `dataset_id_2`(`dataset_id`, `datasetmembertype_id`) USING BTREE,
  CONSTRAINT `datasetmembers_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `datasetmembers_ibfk_2` FOREIGN KEY (`datasetmembertype_id`) REFERENCES `datasetmembertypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datasetmembertypes
-- ----------------------------
DROP TABLE IF EXISTS `datasetmembertypes`;
CREATE TABLE `datasetmembertypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `target_table` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetmembertypes
-- ----------------------------
INSERT INTO `datasetmembertypes` VALUES (1, 'markers', '2018-03-27 14:26:16', '2018-03-27 14:26:16');
INSERT INTO `datasetmembertypes` VALUES (2, 'germinatebase', '2018-03-27 14:26:16', '2018-03-27 14:26:16');

-- ----------------------------
-- Table structure for datasetmeta
-- ----------------------------
DROP TABLE IF EXISTS `datasetmeta`;
CREATE TABLE `datasetmeta`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to [datasets] ([datasets].id).',
  `nr_of_data_objects` bigint(20) UNSIGNED NOT NULL COMMENT 'The number of data objects contained in this dataset.',
  `nr_of_data_points` bigint(20) UNSIGNED NOT NULL COMMENT 'The number of individual data points contained in this dataset.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `datasetmeta_ibfk_datasets`(`dataset_id`) USING BTREE,
  CONSTRAINT `datasetmeta_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines dataset sizes for the items in the datasets table. This table is automatically updated every hour.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datasetpermissions
-- ----------------------------
DROP TABLE IF EXISTS `datasetpermissions`;
CREATE TABLE `datasetpermissions`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `dataset_id` int(11) NOT NULL COMMENT 'Foreign key to datasets (datasets.id).',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper usersid).',
  `group_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to usergroups table.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `datasetpermissions_ibfk1`(`dataset_id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  CONSTRAINT `datasetpermissions_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `datasetpermissions_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'This defines which users can view which datasets. Requires Germinate Gatekeeper. This overrides the datasets state.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datasets
-- ----------------------------
DROP TABLE IF EXISTS `datasets`;
CREATE TABLE `datasets`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `experiment_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to experiments (experiments.id).',
  `location_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to locations (locations.id).',
  `name` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the dataset.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'The name of this dataset.',
  `date_start` date NULL DEFAULT NULL COMMENT 'Date that the dataset was generated.',
  `date_end` date NULL DEFAULT NULL COMMENT 'Date at which the dataset recording ended.',
  `source_file` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `datatype` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A description of the data type of the contained data. Examples might be: \"raw data\", \"BLUPs\", etc.',
  `dublin_core` json NULL,
  `version` char(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Dataset version if this exists.',
  `created_by` int(11) NULL DEFAULT NULL COMMENT 'Defines who created the dataset. This is a FK in Gatekeeper users table. Foreign key to Gatekeeper users (users.id).',
  `dataset_state_id` int(11) NOT NULL DEFAULT 1 COMMENT 'Foreign key to datasetstates (datasetstates.id).',
  `license_id` int(11) NULL DEFAULT NULL,
  `is_external` tinyint(1) NULL DEFAULT 0 COMMENT 'Defines if the dataset is contained within Germinate or from an external source and not stored in the database.',
  `hyperlink` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Link to access the external dasets.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.\n',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  `contact` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The contact to get more information about this dataset.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `experiment`(`experiment_id`) USING BTREE,
  INDEX `id`(`id`) USING BTREE,
  INDEX `datasets_ibfk_2`(`dataset_state_id`) USING BTREE,
  INDEX `datasets_ibfk_3`(`location_id`) USING BTREE,
  INDEX `license_id`(`license_id`) USING BTREE,
  CONSTRAINT `datasets_ibfk_1` FOREIGN KEY (`experiment_id`) REFERENCES `experiments` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `datasets_ibfk_2` FOREIGN KEY (`dataset_state_id`) REFERENCES `datasetstates` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `datasets_ibfk_3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `datasets_ibfk_4` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Datasets which are defined within Germinate although there can be external datasets which are links out to external data sources most will be held within Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for datasetstates
-- ----------------------------
DROP TABLE IF EXISTS `datasetstates`;
CREATE TABLE `datasetstates`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Defines the datasetstate.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Describes the datasetstate.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of datasetstates
-- ----------------------------
INSERT INTO `datasetstates` VALUES (1, 'public', 'Public datasets are visible to all registered users on private web interfaces and everybody on public web interfaces.', '2014-08-07 11:40:08', '2014-08-07 11:45:38');
INSERT INTO `datasetstates` VALUES (2, 'private', 'Private datasets are visible to all registered admin users and the creator of the dataset. They are not visible on the public web interface.', '2014-08-07 11:40:48', '2014-08-07 11:45:40');
INSERT INTO `datasetstates` VALUES (3, 'hidden', 'Hidden datasets are only visible to admins.', '2014-08-07 11:54:33', '2014-08-07 14:09:50');

-- ----------------------------
-- Table structure for entitytypes
-- ----------------------------
DROP TABLE IF EXISTS `entitytypes`;
CREATE TABLE `entitytypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the entity type.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'Describes the entity type.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of entitytypes
-- ----------------------------
INSERT INTO `entitytypes` VALUES (1, 'Accession', 'The basic working unit of conservation in the genebanks.', '2018-03-27 14:26:06', '2018-03-27 14:26:06');
INSERT INTO `entitytypes` VALUES (2, 'Plant/Plot', 'An individual grown from an accession OR a plot of individuals from the same accession.', '2018-03-27 14:26:06', '2018-03-27 14:26:06');
INSERT INTO `entitytypes` VALUES (3, 'Sample', 'A sample from a plant. An example would be taking multiple readings for the same phenotype from a plant.', '2018-03-27 14:26:06', '2018-03-27 14:26:06');

-- ----------------------------
-- Table structure for experiments
-- ----------------------------
DROP TABLE IF EXISTS `experiments`;
CREATE TABLE `experiments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `experiment_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the experiment.',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).\n',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'Describes the experiment.',
  `experiment_date` date NULL DEFAULT NULL COMMENT 'The date that the experiment was carried out.',
  `experiment_type_id` int(11) NOT NULL COMMENT 'Foreign key to experimenttypes (experimenttypes.id).\n',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `experiment_type_id`(`experiment_type_id`) USING BTREE,
  CONSTRAINT `experiments_ibfk_1` FOREIGN KEY (`experiment_type_id`) REFERENCES `experimenttypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines ecperiments that are held in Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for experimenttypes
-- ----------------------------
DROP TABLE IF EXISTS `experimenttypes`;
CREATE TABLE `experimenttypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Describes the experiment type.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of experimenttypes
-- ----------------------------
INSERT INTO `experimenttypes` VALUES (-1, 'unknown', '2015-09-24 10:30:42', NULL);
INSERT INTO `experimenttypes` VALUES (1, 'genotype', '2013-08-22 14:32:06', NULL);
INSERT INTO `experimenttypes` VALUES (3, 'trials', '2013-09-02 13:16:44', NULL);
INSERT INTO `experimenttypes` VALUES (4, 'allelefreq', '2013-10-11 09:23:15', NULL);
INSERT INTO `experimenttypes` VALUES (5, 'climate', '2015-09-02 10:35:58', NULL);
INSERT INTO `experimenttypes` VALUES (6, 'compound', '2018-11-07 11:49:53', '2018-11-07 11:49:53');

-- ----------------------------
-- Table structure for germinatebase
-- ----------------------------
DROP TABLE IF EXISTS `germinatebase`;
CREATE TABLE `germinatebase`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `general_identifier` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'A unique identifier.',
  `number` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'This is the unique identifier for accessions within a genebank, and is assigned when a sample is\nentered into the genebank collection (e.g. ‘PI 113869’).',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'A unique name which defines an entry in the germinatbase table.',
  `bank_number` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Alternative genebank number.',
  `breeders_code` char(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.',
  `breeders_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.',
  `taxonomy_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to taxonomies (taxonomies.id).',
  `institution_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to institutions (institutions.id).',
  `plant_passport` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Record if the entry has a plant passport.',
  `donor_code` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the donor institute. Follows INSTCODE standard.',
  `donor_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.',
  `donor_number` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Identifier assigned to an accession by the donor. Follows ACCENUMB standard.',
  `acqdate` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Date on which the accession entered the collection where YYYY is the year, MM is the month and\nDD is the day. Missing data (MM or DD) should be indicated with hyphens or ‘00’ [double zero].',
  `collnumb` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Original identifier assigned by the collector(s) of the sample, normally composed of the name or\ninitials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for\nidentifying duplicates held in different collections.',
  `colldate` date NULL DEFAULT NULL COMMENT 'Collecting date of the sample, where YYYY is the year, MM is the month and DD is the day.\nMissing data (MM or DD) should be indicated with hyphens or ‘00’ [double zero]. ',
  `collcode` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the\nmaterial, the collecting institute code (COLLCODE) should be the same as the holding institute\ncode (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon\nwithout space.',
  `collname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.',
  `collmissid` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. \'CIATFOR-052\', \'CN426\').',
  `othernumb` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.',
  `duplsite` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained.\nMultiple values are separated by a semicolon without space. Follows INSTCODE standard.',
  `duplinstname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.',
  `mlsstatus_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to mlsstatus (mlsstatus.id).',
  `puid` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.',
  `biologicalstatus_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to biologicalstatus (biologicalstaus.id).',
  `collsrc_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to collectionsources (collectionsources.id).',
  `location_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to locations (locations.id).',
  `entitytype_id` int(11) NULL DEFAULT 1 COMMENT 'Foreign key to entitytypes (entitytypes.id).',
  `entityparent_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).',
  `pdci` float(64, 10) NULL DEFAULT NULL COMMENT 'Passport Data Completeness Index. This is calculated by Germinate. Manual editing of this field will be overwritten.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `institution_id`(`institution_id`) USING BTREE,
  INDEX `taxonomy_id`(`taxonomy_id`) USING BTREE,
  INDEX `collsite_id`(`location_id`) USING BTREE,
  INDEX `general_identifier`(`general_identifier`) USING BTREE,
  INDEX `germinatebase_ibfk_biologicalstatus`(`biologicalstatus_id`) USING BTREE,
  INDEX `germinatebase_ibfk_collectingsource`(`collsrc_id`) USING BTREE,
  INDEX `germinatebase_ibfk_8`(`mlsstatus_id`) USING BTREE,
  INDEX `germinatebase_ibfk_entitytype`(`entitytype_id`) USING BTREE,
  INDEX `germinatebase_ibfk_entityparent`(`entityparent_id`) USING BTREE,
  CONSTRAINT `germinatebase_ibfk_biologicalstatus` FOREIGN KEY (`biologicalstatus_id`) REFERENCES `biologicalstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_collsrc` FOREIGN KEY (`collsrc_id`) REFERENCES `collectingsources` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_entityparent` FOREIGN KEY (`entityparent_id`) REFERENCES `germinatebase` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_entitytype` FOREIGN KEY (`entitytype_id`) REFERENCES `entitytypes` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_institution` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_location` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_mlsstatus` FOREIGN KEY (`mlsstatus_id`) REFERENCES `mlsstatus` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `germinatebase_ibfk_taxonomy` FOREIGN KEY (`taxonomy_id`) REFERENCES `taxonomies` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Germinatebase is the Germinate base table which contains passport and other germplasm definition data.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groupmembers
-- ----------------------------
DROP TABLE IF EXISTS `groupmembers`;
CREATE TABLE `groupmembers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `foreign_id` int(2) NOT NULL COMMENT 'Foreign key to [table] ([table].id).',
  `group_id` int(2) NOT NULL COMMENT 'Foreign key to groups (groups.id).',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `group_id`(`group_id`) USING BTREE,
  INDEX `groupmembers_foreign`(`foreign_id`) USING BTREE,
  CONSTRAINT `groupmembers_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines which entities are contained within a group. These can be the primary key from any table.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for groups
-- ----------------------------
DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `grouptype_id` int(11) NOT NULL COMMENT 'Foreign key to grouptypes (grouptypes.id).',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The name of the group which can be used to identify it.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'A free text description of the group. This has no length limitations.',
  `visibility` tinyint(1) NULL DEFAULT NULL COMMENT 'Defines if the group is visuble or hidden from the Germinate user interface.',
  `created_by` int(11) NULL DEFAULT NULL COMMENT 'Defines who created the group. Foreign key to Gatekeeper users (Gatekeeper users.id).',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'Foreign key to locations (locations.id).',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `grouptype_id`(`grouptype_id`) USING BTREE,
  CONSTRAINT `groups_ibfk_1` FOREIGN KEY (`grouptype_id`) REFERENCES `grouptypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Allows the definition of groups within Germinate. Germinate supports a number of different group types such as germinatebase accesion groups and marker groups.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for grouptypes
-- ----------------------------
DROP TABLE IF EXISTS `grouptypes`;
CREATE TABLE `grouptypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `target_table` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of grouptypes
-- ----------------------------
INSERT INTO `grouptypes` VALUES (1, 'Collectingsites', 'locations', '2013-07-15 16:19:39', NULL);
INSERT INTO `grouptypes` VALUES (2, 'Markers', 'markers', '2013-07-15 16:19:50', NULL);
INSERT INTO `grouptypes` VALUES (3, 'Accessions', 'germinatebase', '2013-07-29 12:04:37', NULL);

-- ----------------------------
-- Table structure for images
-- ----------------------------
DROP TABLE IF EXISTS `images`;
CREATE TABLE `images`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `imagetype_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to imagetypes (imagetypes.id).',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'A description of what the image shows if required.',
  `foreign_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Relates to the UID of the table to which the comment relates.',
  `path` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The file system path to the image.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `imagetype_id`(`imagetype_id`) USING BTREE,
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`imagetype_id`) REFERENCES `imagetypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for imagetypes
-- ----------------------------
DROP TABLE IF EXISTS `imagetypes`;
CREATE TABLE `imagetypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'A description of the image type. This would usually be a description of what the image was showing in general terms such as \'field image\' or \'insitu hybridisation images\'.',
  `reference_table` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'The table which the image type relates to.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of imagetypes
-- ----------------------------
INSERT INTO `imagetypes` VALUES (1, 'accession images', 'germinatebase', '2009-03-04 14:13:22', NULL);
INSERT INTO `imagetypes` VALUES (2, 'compound images', 'compounds', NULL, NULL);
INSERT INTO `imagetypes` VALUES (3, 'phenotype images', 'phenotypes', '2018-11-06 13:46:11', '2018-11-06 13:46:11');

-- ----------------------------
-- Table structure for institutions
-- ----------------------------
DROP TABLE IF EXISTS `institutions`;
CREATE TABLE `institutions`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `code` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'If there is a defined ISO code for the institute this should be used here.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'The institute name.',
  `acronym` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'If there is an acronym for the institute.',
  `country_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to countries.id.',
  `contact` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The contact at the institute which should be used for correspondence.',
  `phone` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The telephone number for the institute.',
  `email` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The email address to contact the institute.',
  `address` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'The postal address of the institute.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `country_id`(`country_id`) USING BTREE,
  CONSTRAINT `institutions_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines institutions within Germinate. Accessions may be associated with an institute and this can be defined here.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for licensedata
-- ----------------------------
DROP TABLE IF EXISTS `licensedata`;
CREATE TABLE `licensedata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `license_id` int(11) NOT NULL,
  `locale_id` int(11) NOT NULL,
  `content` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `license_id`(`license_id`) USING BTREE,
  INDEX `locale_id`(`locale_id`) USING BTREE,
  CONSTRAINT `licensedata_ibfk_1` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `licensedata_ibfk_2` FOREIGN KEY (`locale_id`) REFERENCES `locales` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for licenselogs
-- ----------------------------
DROP TABLE IF EXISTS `licenselogs`;
CREATE TABLE `licenselogs`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `license_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `accepted_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `license_id`(`license_id`) USING BTREE,
  CONSTRAINT `licenselogs_ibfk_1` FOREIGN KEY (`license_id`) REFERENCES `licenses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for licenses
-- ----------------------------
DROP TABLE IF EXISTS `licenses`;
CREATE TABLE `licenses`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for links
-- ----------------------------
DROP TABLE IF EXISTS `links`;
CREATE TABLE `links`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `linktype_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to linktypes (linktypes.id).',
  `foreign_id` int(11) NULL DEFAULT NULL,
  `hyperlink` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The actual hyperlink.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A description of the link.',
  `visibility` tinyint(1) NULL DEFAULT 1 COMMENT 'Determines if the link is visible or not: {0, 1}',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `links_linktype_id`(`linktype_id`) USING BTREE,
  INDEX `links_id`(`id`) USING BTREE,
  CONSTRAINT `links_ibfk_1` FOREIGN KEY (`linktype_id`) REFERENCES `linktypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Germinate allows to define external links for different types of data. With this feature you can\r\ndefine links to external resources.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for linktypes
-- ----------------------------
DROP TABLE IF EXISTS `linktypes`;
CREATE TABLE `linktypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A description of the link\r.',
  `target_table` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'This is the table that the link links to.',
  `target_column` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'This is the column that is used to generate the link.',
  `placeholder` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The part of the link that will be replaced by the value of the target column.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `linktypes_id`(`id`) USING BTREE,
  INDEX `linktypes_target_table`(`target_table`, `target_column`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'The link type determines which database table and column are used to construct the final\r\nlink. The ”placeholder” in the link (from the links table) will be replaced by the value of the\r\n”target column” in the ”target table”' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for locales
-- ----------------------------
DROP TABLE IF EXISTS `locales`;
CREATE TABLE `locales`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for locations
-- ----------------------------
DROP TABLE IF EXISTS `locations`;
CREATE TABLE `locations`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `locationtype_id` int(11) NOT NULL COMMENT 'Foreign key to locations (locations.id).',
  `country_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key to countries (countries.id).',
  `state` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The state where the location is if this exists.',
  `region` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The region where the location is if this exists.',
  `site_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'The site name where the location is.',
  `site_name_short` varchar(22) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Shortened site name which can be used in tables within Germinate.',
  `elevation` decimal(64, 10) NULL DEFAULT NULL COMMENT 'The elevation of the site in metres.',
  `latitude` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Latitude of the location.',
  `longitude` decimal(64, 10) NULL DEFAULT NULL COMMENT 'Longitude of the location.',
  `coordinate_uncertainty` int(11) NULL DEFAULT NULL COMMENT 'Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown. ',
  `coordinate_datum` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.',
  `georeferencing_method` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `country_id`(`country_id`) USING BTREE,
  INDEX `locations_ibfk_2`(`locationtype_id`) USING BTREE,
  CONSTRAINT `locations_ibfk_1` FOREIGN KEY (`country_id`) REFERENCES `countries` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `locations_ibfk_2` FOREIGN KEY (`locationtype_id`) REFERENCES `locationtypes` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Describes locations. Locations can be collecting sites or the location of any geographical feature such as research institutes or lab locations.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for locationtypes
-- ----------------------------
DROP TABLE IF EXISTS `locationtypes`;
CREATE TABLE `locationtypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the location type. ',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A description of the location type.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Describes a location.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of locationtypes
-- ----------------------------
INSERT INTO `locationtypes` VALUES (1, 'collectingsites', 'Locations where accessions have been collected', '2014-11-27 14:57:36', '2014-11-27 14:57:26');
INSERT INTO `locationtypes` VALUES (2, 'datasets', 'Locations associated with datasets', '2015-01-28 12:49:03', '2015-01-28 12:49:05');
INSERT INTO `locationtypes` VALUES (3, 'trialsite', 'Locations associated with a trial', '2015-01-28 12:49:01', '2015-01-28 12:49:02');

-- ----------------------------
-- Table structure for mapdefinitions
-- ----------------------------
DROP TABLE IF EXISTS `mapdefinitions`;
CREATE TABLE `mapdefinitions`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `mapfeaturetype_id` int(11) NOT NULL COMMENT 'Foreign key to mapfeaturetypes (mapfeaturetypes.id).',
  `marker_id` int(11) NOT NULL COMMENT 'Foreign key to markers (markers.id).',
  `map_id` int(11) NOT NULL COMMENT 'Foreign key to maps (maps.id).',
  `definition_start` double(64, 10) NOT NULL COMMENT 'Used if the markers location spans over an area more than a single point on the maps. Determines the marker start location.',
  `definition_end` double(64, 10) NULL DEFAULT NULL COMMENT 'Used if the markers location spans over an area more than a single point on the maps. Determines the marker end location.',
  `chromosome` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The chromosome/linkage group that this marker is found on.',
  `arm_impute` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'If a chromosome arm is available then this can be entered here.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mapfeaturetype_id`(`mapfeaturetype_id`) USING BTREE,
  INDEX `marker_id`(`marker_id`) USING BTREE,
  INDEX `map_id`(`map_id`) USING BTREE,
  INDEX `marker_id_2`(`marker_id`, `map_id`) USING BTREE,
  CONSTRAINT `mapdefinitions_ibfk_1` FOREIGN KEY (`mapfeaturetype_id`) REFERENCES `mapfeaturetypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `mapdefinitions_ibfk_2` FOREIGN KEY (`marker_id`) REFERENCES `markers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `mapdefinitions_ibfk_3` FOREIGN KEY (`map_id`) REFERENCES `maps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Relates genetic markers to a map and assigns a position (if known). Maps are made up of lists of markers and positions (genetic or physiscal and chromosome/linkage group assignation). In the case of QTL the definition_start and definition_end columns can be used to specify a range across a linkage group.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mapfeaturetypes
-- ----------------------------
DROP TABLE IF EXISTS `mapfeaturetypes`;
CREATE TABLE `mapfeaturetypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Description of the feature type. This could include a definition of the marker type such as \'SNP\', \'KASP\' or \'AFLP\'.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines features which can exist on maps. In general this will be the marker type but it can also be used to identify QTL regions.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for maps
-- ----------------------------
DROP TABLE IF EXISTS `maps`;
CREATE TABLE `maps`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Describes the map.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'The name of this map.',
  `visibility` tinyint(1) NOT NULL DEFAULT 1 COMMENT 'Determines if the map is visible to the Germinate interface or hidden.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to Gatekeeper users (Gatekeeper users.id).',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Describes genetic maps that have been defined within Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for markers
-- ----------------------------
DROP TABLE IF EXISTS `markers`;
CREATE TABLE `markers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `markertype_id` int(11) NOT NULL COMMENT 'Foreign key to locations (locations.id).',
  `marker_name` varchar(45) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the marker. This should be a unique name which identifies the marker.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.\n',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `markertype_id`(`markertype_id`) USING BTREE,
  INDEX `marker_name`(`marker_name`) USING BTREE,
  CONSTRAINT `markers_ibfk_1` FOREIGN KEY (`markertype_id`) REFERENCES `markertypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines genetic markers within the database and assigns a type (markertypes).' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for markertypes
-- ----------------------------
DROP TABLE IF EXISTS `markertypes`;
CREATE TABLE `markertypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on. Used to differentiate markers within the markers table and alllows for mixing of marker types on genetic and physical maps.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for megaenvironmentdata
-- ----------------------------
DROP TABLE IF EXISTS `megaenvironmentdata`;
CREATE TABLE `megaenvironmentdata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `location_id` int(11) NOT NULL COMMENT 'Foreign key to locations (locations.id).',
  `source_id` int(11) NOT NULL COMMENT 'Source ID',
  `megaenvironment_id` int(11) NOT NULL COMMENT 'Foreign key to megaenvironments (megaenvironments.id).',
  `is_final` tinyint(1) NULL DEFAULT NULL COMMENT 'The source that was used to determine the megaenvironment data.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `megaenvironment_id`(`megaenvironment_id`) USING BTREE,
  INDEX `source_id`(`source_id`) USING BTREE,
  INDEX `collectingsite_id`(`location_id`) USING BTREE,
  CONSTRAINT `megaenvironmentdata_ibfk_1` FOREIGN KEY (`megaenvironment_id`) REFERENCES `megaenvironments` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `megaenvironmentdata_ibfk_2` FOREIGN KEY (`source_id`) REFERENCES `megaenvironmentsource` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `megaenvironmentdata_ibfk_3` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Describes mega environment data by grouping collection sites (locations) into mega environments. Mega environments in this context are collections of sites which meet the mega environment definition criteria.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for megaenvironments
-- ----------------------------
DROP TABLE IF EXISTS `megaenvironments`;
CREATE TABLE `megaenvironments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The mega environment name.',
  `max_temp_lower` int(5) NULL DEFAULT NULL COMMENT 'The lower maximum temperature for this environment.',
  `max_temp_upper` int(5) NULL DEFAULT NULL COMMENT 'The maximum temperature for this environment.',
  `precip_lower` int(11) NULL DEFAULT NULL COMMENT 'The minimum precipitation for this environment.',
  `precip_upper` int(11) NULL DEFAULT NULL COMMENT 'the maximum precipitation for this environment.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines the mega environments if used and their temperature and precipitation ranges.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for megaenvironmentsource
-- ----------------------------
DROP TABLE IF EXISTS `megaenvironmentsource`;
CREATE TABLE `megaenvironmentsource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the mega environment source.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'Describes the mega environment source.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Contains information relating to the source of the mega environments. This could be the contributing source including contact and location details or how the mega environments were extracted from current datasets. ' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mlsstatus
-- ----------------------------
DROP TABLE IF EXISTS `mlsstatus`;
CREATE TABLE `mlsstatus`  (
  `id` int(11) NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mlsstatus
-- ----------------------------
INSERT INTO `mlsstatus` VALUES (0, 'No (not included)', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `mlsstatus` VALUES (1, 'Yes (included)', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `mlsstatus` VALUES (99, 'Other (elaborate in REMARKS field, e.g. \'under development\'', '2017-10-12 13:19:34', '2017-10-12 13:19:34');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `newstype_id` int(11) NOT NULL COMMENT 'Foreign key newstypes (newstypes.id).',
  `title` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A title which is used to name this news item. This appears in the Germinate user interface if used.',
  `content` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'The textual content of this news item.',
  `image` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Image to use with this news item.',
  `hyperlink` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'HTML hyperlink to use for this news item. This can be a link to another source which contains more information or a link to the original source.',
  `user_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key users (users.id).',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `news_user_id`(`user_id`) USING BTREE,
  INDEX `news_updated_on`(`updated_on`) USING BTREE,
  INDEX `news_type_id`(`newstype_id`) USING BTREE,
  CONSTRAINT `news_ibfk_1` FOREIGN KEY (`newstype_id`) REFERENCES `newstypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Holds news items that are displayed within Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for newstypes
-- ----------------------------
DROP TABLE IF EXISTS `newstypes`;
CREATE TABLE `newstypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Name of the news type.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A longer description of the news type.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines the news types which are contained the database. The news types are displayed on the Germinate user interface and are not required if the user interface is not used.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of newstypes
-- ----------------------------
INSERT INTO `newstypes` VALUES (1, 'General', 'General news', NULL, NULL);
INSERT INTO `newstypes` VALUES (2, 'Updates', 'News about updates to the page', NULL, NULL);
INSERT INTO `newstypes` VALUES (3, 'Data', 'News about new data', NULL, NULL);
INSERT INTO `newstypes` VALUES (4, 'Projects', 'News about new projects', NULL, NULL);

-- ----------------------------
-- Table structure for pedigreedefinitions
-- ----------------------------
DROP TABLE IF EXISTS `pedigreedefinitions`;
CREATE TABLE `pedigreedefinitions`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `germinatebase_id` int(11) NOT NULL COMMENT 'Foreign key to germinatebase (germinatebase.id).',
  `pedigreenotation_id` int(11) NOT NULL COMMENT 'Foreign key to pedigreenotations (pedigreenotations.id).',
  `pedigreedescription_id` int(11) NULL DEFAULT NULL,
  `definition` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'The pedigree string which is used to represent the germinatebase entry.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pedigreedefinitions_ibfk_pedigreenotations`(`pedigreenotation_id`) USING BTREE,
  INDEX `pedigreedefinitions_ibfk_germinatebase`(`germinatebase_id`) USING BTREE,
  INDEX `pedigreedefinitions_ibfk_3`(`pedigreedescription_id`) USING BTREE,
  CONSTRAINT `pedigreedefinitions_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pedigreedefinitions_ibfk_2` FOREIGN KEY (`pedigreenotation_id`) REFERENCES `pedigreenotations` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pedigreedefinitions_ibfk_3` FOREIGN KEY (`pedigreedescription_id`) REFERENCES `pedigreedescriptions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'This table holds the actual pedigree definition data.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pedigreedescriptions
-- ----------------------------
DROP TABLE IF EXISTS `pedigreedescriptions`;
CREATE TABLE `pedigreedescriptions`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'The name of the pedigree.',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Describes the pedigree in more detail.',
  `author` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Who is responsible for the creation of the pedigree. Attribution should be included in here for pedigree sources.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'Description of pedigrees. Pedigrees can have a description which details additional information about the pedigree, how it was constructed and who the contact is for the pedigree.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pedigreenotations
-- ----------------------------
DROP TABLE IF EXISTS `pedigreenotations`;
CREATE TABLE `pedigreenotations`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Name of the reference notation source.',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'A longer description about the reference notation source.',
  `reference_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'Hyperlink to the notation source.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'Allows additional supporting data to be associated with a pedigree definition such as the contributing data source.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for pedigrees
-- ----------------------------
DROP TABLE IF EXISTS `pedigrees`;
CREATE TABLE `pedigrees`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `germinatebase_id` int(11) NOT NULL COMMENT 'Foreign key germinatebase (germinatebase.id).',
  `parent_id` int(11) NOT NULL COMMENT 'Foreign key germinatebase (germinatebase.id). This is the parrent of the individual identified in the germinatebase_id column.',
  `relationship_type` enum('M','F','OTHER') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'OTHER' COMMENT 'Male or Female parent. Should be recorded as \'M\' (male) or \'F\' (female).',
  `pedigreedescription_id` int(11) NOT NULL COMMENT 'Foreign key pedigreedescriptions (pedigreedescriptions.id).',
  `relationship_description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Can be used as a meta-data field to describe the relationships if a complex rellationship is required. Examples may include, \'is a complex cross containing\', \'F4 generation\' and so on. This is used by the Helium pedigree visualiztion tool.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `pedigrees_ibfk_germinatebase`(`germinatebase_id`) USING BTREE,
  INDEX `pedigrees_ibfk_germinatebase_parent`(`parent_id`) USING BTREE,
  INDEX `pedigrees_ibfk_pedigreedescriptions`(`pedigreedescription_id`) USING BTREE,
  CONSTRAINT `pedigrees_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pedigrees_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `pedigrees_ibfk_3` FOREIGN KEY (`pedigreedescription_id`) REFERENCES `pedigreedescriptions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'Holds pedigree definitions. A pedigree is constructed from a series of individial->parent records. This gives a great deal of flexibility in how pedigree networks can be constructed. This table is required for operation with the Helium pedigree viewer.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for phenotypedata
-- ----------------------------
DROP TABLE IF EXISTS `phenotypedata`;
CREATE TABLE `phenotypedata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `phenotype_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key phenotypes (phenotype.id).',
  `germinatebase_id` int(11) NOT NULL DEFAULT 0 COMMENT 'Foreign key germinatebase (germinatebase.id).',
  `phenotype_value` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The phenotype value for this phenotype_id and germinatebase_id combination.',
  `dataset_id` int(11) NOT NULL COMMENT 'Foreign key datasets (datasets.id).',
  `recording_date` datetime(0) NULL DEFAULT NULL COMMENT 'Date when the phenotypic result was recorded. Should be formatted \'YYYY-MM-DD HH:MM:SS\' or just \'YYYY-MM-DD\' where a timestamp is not available.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  `location_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to locations (locations.id).',
  `treatment_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to treatments (treatments.id).',
  `trialseries_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign key to trialseries (trialseries.id).',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dataset_id`(`dataset_id`) USING BTREE,
  INDEX `phenotype_id`(`phenotype_id`) USING BTREE,
  INDEX `germinatebase_id`(`germinatebase_id`) USING BTREE,
  INDEX `phenotypes_ibfk_locations`(`location_id`) USING BTREE,
  INDEX `phenotypes_ibfk_treatment`(`treatment_id`) USING BTREE,
  INDEX `phenotypes_ibfk_trialseries`(`trialseries_id`) USING BTREE,
  INDEX `trials_query_index`(`phenotype_id`, `germinatebase_id`, `location_id`, `trialseries_id`, `recording_date`, `treatment_id`, `dataset_id`, `phenotype_value`) USING BTREE,
  INDEX `phenotypedata_recording_date`(`recording_date`) USING BTREE,
  INDEX `dataset_id_2`(`dataset_id`, `germinatebase_id`) USING BTREE,
  CONSTRAINT `phenotypedata_ibfk_1` FOREIGN KEY (`dataset_id`) REFERENCES `datasets` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `phenotypedata_ibfk_2` FOREIGN KEY (`phenotype_id`) REFERENCES `phenotypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `phenotypedata_ibfk_3` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `phenotypedata_ibfk_4` FOREIGN KEY (`location_id`) REFERENCES `locations` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `phenotypedata_ibfk_5` FOREIGN KEY (`treatment_id`) REFERENCES `treatments` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `phenotypedata_ibfk_6` FOREIGN KEY (`trialseries_id`) REFERENCES `trialseries` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Contains phenotypic data which has been collected.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for phenotypes
-- ----------------------------
DROP TABLE IF EXISTS `phenotypes`;
CREATE TABLE `phenotypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'Phenotype full name.',
  `short_name` char(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Shortened name for the phenotype. This is used in table columns where space is an issue.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'Full description of the phenotype. This should contain enough infomation to accurately identify the phenoytpe and how it was recorded.',
  `datatype` enum('float','int','char') CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT 'int' COMMENT 'Defines the data type of the phenotype. This can be of float, int or char types.',
  `unit_id` int(11) NULL DEFAULT NULL COMMENT 'Foreign Key to units (units.id).',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `unit_id`(`unit_id`) USING BTREE,
  CONSTRAINT `phenotypes_ibfk_1` FOREIGN KEY (`unit_id`) REFERENCES `units` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Defines phenoytpes which are held in Germinate.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for schema_version
-- ----------------------------
DROP TABLE IF EXISTS `schema_version`;
CREATE TABLE `schema_version`  (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `description` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `type` varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `script` varchar(1000) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `checksum` int(11) NULL DEFAULT NULL,
  `installed_by` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `installed_on` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`) USING BTREE,
  INDEX `schema_version_s_idx`(`success`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schema_version
-- ----------------------------
INSERT INTO `schema_version` VALUES (1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'germinate3', '2016-08-22 16:24:04', 0, 1);
INSERT INTO `schema_version` VALUES (2, '3.3.2', 'update', 'SQL', 'V3.3.2__update.sql', 905709629, 'germinate3', '2016-08-22 16:24:04', 111, 1);
INSERT INTO `schema_version` VALUES (3, '3.3.2.1', 'update', 'SQL', 'V3.3.2.1__update.sql', -256506759, 'germinate3', '2016-11-03 15:46:40', 123, 1);
INSERT INTO `schema_version` VALUES (4, '3.3.2.2', 'update', 'SQL', 'V3.3.2.2__update.sql', 508407614, 'germinate3', '2016-11-04 10:31:18', 9, 1);
INSERT INTO `schema_version` VALUES (5, '3.4.0', 'update', 'SQL', 'V3.4.0__update.sql', 1635546146, 'germinate3', '2017-01-10 14:23:11', 198, 1);
INSERT INTO `schema_version` VALUES (6, '3.4.0.1', 'update', 'SQL', 'V3.4.0.1__update.sql', -1497522993, 'germinate3', '2017-09-28 15:58:00', 161, 1);
INSERT INTO `schema_version` VALUES (7, '3.5.0', 'update', 'SQL', 'V3.5.0__update.sql', -1130493621, 'germinate3', '2018-03-27 14:29:38', 132, 1);
INSERT INTO `schema_version` VALUES (8, '3.6.0', 'update', 'SQL', 'V3.6.0__update.sql', -848461383, 'germinate3', '2019-12-03 11:10:04', 245, 1);

-- ----------------------------
-- Table structure for storage
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage`  (
  `id` int(11) NOT NULL,
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of storage
-- ----------------------------
INSERT INTO `storage` VALUES (10, 'Seed collection', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (11, 'Short term', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (12, 'Medium term', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (13, 'Long term', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (20, 'Field collection', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (30, 'In vitro collection', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (40, 'Cryopreserved collection', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (50, 'DNA collection', '2017-10-12 13:19:34', '2017-10-12 13:19:34');
INSERT INTO `storage` VALUES (99, 'Other (elaborate in REMARKS field', '2017-10-12 13:19:34', '2017-10-12 13:19:34');

-- ----------------------------
-- Table structure for storagedata
-- ----------------------------
DROP TABLE IF EXISTS `storagedata`;
CREATE TABLE `storagedata`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `germinatebase_id` int(11) NOT NULL,
  `storage_id` int(11) NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `germinatebase_id`(`germinatebase_id`) USING BTREE,
  INDEX `storage_id`(`storage_id`) USING BTREE,
  CONSTRAINT `storagedata_ibfk_1` FOREIGN KEY (`germinatebase_id`) REFERENCES `germinatebase` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `storagedata_ibfk_2` FOREIGN KEY (`storage_id`) REFERENCES `storage` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for synonyms
-- ----------------------------
DROP TABLE IF EXISTS `synonyms`;
CREATE TABLE `synonyms`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.\n',
  `foreign_id` int(11) NOT NULL COMMENT 'Foreign key to target table (l[targettable].id).',
  `synonymtype_id` int(11) NOT NULL COMMENT 'Foreign key to synonymtypes (synonymnstypes.id).',
  `synonyms` json NULL COMMENT 'The synonyms as a json array.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `synonyms_ibfk_synonymtypes`(`synonymtype_id`) USING BTREE,
  INDEX `foreign_id`(`foreign_id`) USING BTREE,
  CONSTRAINT `synonyms_ibfk_1` FOREIGN KEY (`synonymtype_id`) REFERENCES `synonymtypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Allows the definition of synonyms for entries such as germinatebase entries or marker names.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for synonymtypes
-- ----------------------------
DROP TABLE IF EXISTS `synonymtypes`;
CREATE TABLE `synonymtypes`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `target_table` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The target table.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'Name of the synonym type.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Description of the type.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Synonym type definitions.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of synonymtypes
-- ----------------------------
INSERT INTO `synonymtypes` VALUES (1, 'germinatebase', 'Accessions', 'Accession synonyms', NULL, NULL);
INSERT INTO `synonymtypes` VALUES (2, 'markers', 'Markers', 'Marker synonyms', NULL, NULL);
INSERT INTO `synonymtypes` VALUES (3, 'compounds', 'Compounds', 'Compound synonyms', NULL, NULL);
INSERT INTO `synonymtypes` VALUES (4, 'phenotypes', 'Phenotypes', 'Phenotype synonyms', '2018-11-06 13:46:11', '2018-11-06 13:46:11');

-- ----------------------------
-- Table structure for taxonomies
-- ----------------------------
DROP TABLE IF EXISTS `taxonomies`;
CREATE TABLE `taxonomies`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `genus` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'Genus name for the species.',
  `species` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'Species name in lowercase.',
  `subtaxa` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'Subtaxa name.',
  `species_author` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.',
  `subtaxa_author` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).',
  `cropname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.',
  `ploidy` int(11) NULL DEFAULT NULL COMMENT 'Defines the ploidy level for the species. Use numbers to reference ploidy for example diploid = 2, tetraploid = 4.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'The species table holds information relating to the species that are deinfed within a particular Germinate instance including common names and ploidy levels.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for treatments
-- ----------------------------
DROP TABLE IF EXISTS `treatments`;
CREATE TABLE `treatments`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name which defines the treatment.',
  `description` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'A longer descripiton of the treatment. This should include enough information to be able to identify what the treatment was and why it was applied.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'For trials data the treatment is used to distinguish between factors. Examples would include whether the trial was treated with fungicides or not.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for trialseries
-- ----------------------------
DROP TABLE IF EXISTS `trialseries`;
CREATE TABLE `trialseries`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `seriesname` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'The description of the trial series name.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'Holds the names of trial series. Trial series define the name of the trial to which trials data is associated. Examples would include the overarching project.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for units
-- ----------------------------
DROP TABLE IF EXISTS `units`;
CREATE TABLE `units`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary id for this table. This uniquely identifies the row.',
  `unit_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL DEFAULT '' COMMENT 'The name of the unit. This should be the name of the unit in full.',
  `unit_abbreviation` char(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT 'This should be the unit abbreviation.',
  `unit_description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'A description of the unit. If the unit is not a standard SI unit then it is beneficial to have a description which explains what the unit it, how it is derived and any other information which would help identifiy it.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci COMMENT = 'The \'units\' table holds descriptions of the various units that are used in the Germinate database. Examples of these would include International System of Units (SI) base units: kilogram, meter, second, ampere, kelvin, candela and mole but can include any units that are required.' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for usergroupmembers
-- ----------------------------
DROP TABLE IF EXISTS `usergroupmembers`;
CREATE TABLE `usergroupmembers`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `usergroup_id` int(11) NOT NULL,
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `usergroup_id`(`usergroup_id`) USING BTREE,
  CONSTRAINT `usergroupmembers_ibfk_1` FOREIGN KEY (`usergroup_id`) REFERENCES `usergroups` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for usergroups
-- ----------------------------
DROP TABLE IF EXISTS `usergroups`;
CREATE TABLE `usergroups`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'The name of the user group.',
  `description` text CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL COMMENT 'A description of the user group.',
  `created_on` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT 'When the record was created.',
  `updated_on` timestamp(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT 'When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
