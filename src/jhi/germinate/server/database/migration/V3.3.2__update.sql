/**************************************************/
/*              GERMINATE 3                       */
/*              MIGRATION SCRIPT                  */
/*              v3.3.1 -> v3.3.2                  */
/**************************************************/

DROP TABLE IF EXISTS `databaseversions`;

/* Rename `date` to `date_start` and add a new column called `date_end` */
ALTER TABLE `datasets`
  CHANGE COLUMN `date` `date_start` DATE NULL DEFAULT NULL
  AFTER `description`;
ALTER TABLE `datasets`
  ADD COLUMN `date_end` DATE NULL
  AFTER `date_start`;