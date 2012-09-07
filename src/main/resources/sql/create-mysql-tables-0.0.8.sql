-- guard SQL: return success if fisrt SQL statement executed successfully, fail if script not applied
SELECT COUNT(*) FROM device;

-- guard SQL: return success if last SQL statement executed successfully, fail if script not completely applied
SELECT COUNT(*) FROM fixtureDefAttachment;



CREATE TABLE `device` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT 'descriptive name',
  `className` VARCHAR(255) NOT NULL COMMENT 'java class type',
  `type` VARCHAR(1) NOT NULL COMMENT 'D=DMX, S=audioSource, C=audioController',
  `ynActive` VARCHAR(1) NOT NULL,
  `universeNumber` INTEGER UNSIGNED COMMENT 'type=D only',
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;


CREATE TABLE `deviceProperty` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `deviceId` INTEGER UNSIGNED NOT NULL,
  `key` VARCHAR(100) NOT NULL,
  `value` VARCHAR(255),
  PRIMARY KEY (`Id`)
)
ENGINE = InnoDB;

ALTER TABLE `device` CHANGE COLUMN `ynActive` `active` VARCHAR(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL
, AUTO_INCREMENT = 1;

ALTER TABLE `fixture` ADD COLUMN `universeNumber` INT(10) UNSIGNED NOT NULL DEFAULT 1 
  AFTER `name`;
  
UPDATE fixture SET universeNumber=1;

ALTER TABLE `fixtureDef` ADD COLUMN `htmlImg16` VARCHAR(100) DEFAULT NULL AFTER `dmxChannels`;  

CREATE TABLE `fixtureDefAttachment` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `fixtureDefId` INTEGER UNSIGNED NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `size` INTEGER UNSIGNED NOT NULL,
  `contentType` VARCHAR(200) NOT NULL,
  `fileLocation` VARCHAR(200) NOT NULL COMMENT 'relative to app base specified in appConfig',
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

INSERT INTO fixtureDefAttachment (id, fixtureDefId, name, size, contentType, fileLocation, description)
SELECT id, fixtureDefId, name, size, contentType, fileLocation, description
FROM fixtureDefImage;

DROP TABLE fixtureDefImage;



