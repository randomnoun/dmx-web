-- success if stage.universeCount exists
SELECT COUNT(universeCount) FROM stage;

-- success if stage.backgroundImage exists
SELECT COUNT(fixPanelBackgroundImage) FROM stage;


ALTER TABLE `stage` 
 ADD COLUMN `backgroundImage` VARCHAR(100) DEFAULT NULL AFTER `active`,
 ADD COLUMN `universeCount` INTEGER UNSIGNED NOT NULL DEFAULT 1 AFTER `backgroundImage`;
 
ALTER TABLE `stage` 
  CHANGE COLUMN `backgroundImage` `fixPanelBackgroundImage` VARCHAR(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;
