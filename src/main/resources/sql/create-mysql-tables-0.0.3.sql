ALTER TABLE `fixture` ADD COLUMN `stageId` INTEGER UNSIGNED NOT NULL AFTER `dmxOffset`,
 ADD COLUMN `fixPanelType` VARCHAR(1) NOT NULL COMMENT 'L=large (default), S=small (half-size), M=matrix (5x5 intensity only)' AFTER `stageId`,
 ADD COLUMN `fixPanelX` INTEGER UNSIGNED AFTER `fixPanelType`,
 ADD COLUMN `fixPanelY` INTEGER UNSIGNED AFTER `fixPanelX`;

 
CREATE TABLE `stage` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `filename` VARCHAR(255) COMMENT 'when importing/exporting stage',
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

ALTER TABLE `show` ADD COLUMN `stageId` INTEGER UNSIGNED NOT NULL AFTER `showGroupId`;

ALTER TABLE `stage` ADD COLUMN `active` VARCHAR(1) NOT NULL AFTER `filename`;


-- create default stage and assign all existing shows/fixtures to it
INSERT INTO stage(id, name, filename, active) VALUES (1, 'Default', 'default.dws', 'Y')
UPDATE fixture SET stageId=1, fixPanelType='L', fixPanelX=NULL, fixPanelY=NULL
UPDATE `show` SET stageId=1

 