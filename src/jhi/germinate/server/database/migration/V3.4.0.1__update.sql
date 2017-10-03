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
  `email`  varchar(255) NULL 'E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.' ,
  `phone`  varchar(255) NULL 'Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.' ,
  `institution_id`  int(11) NULL 'Author''s affiliation when the resource was created. Foreign key to ''institutions''' ,
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

