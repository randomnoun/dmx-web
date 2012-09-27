-- guard SQL: return success if first SQL statement executed successfully, fail if script not yet applied
SELECT COUNT(*) FROM device;

-- guard SQL: return success if last SQL statement executed successfully, fail if script not completely applied
SELECT COUNT(*) FROM stageAttachment;

CREATE TABLE `device` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT 'descriptive name',
  `className` VARCHAR(255) NOT NULL COMMENT 'java class type',
  `type` VARCHAR(1) NOT NULL COMMENT 'D=DMX, S=audioSource, C=audioController',
  `ynActive` VARCHAR(1) NOT NULL,
  `universeNumber` INTEGER UNSIGNED COMMENT '1-based universe number',
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

CREATE TABLE `showDefAttachment` (
  `id` INTEGER(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `showDefId` INTEGER(10) UNSIGNED NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `size` INTEGER(10) UNSIGNED NOT NULL,
  `contentType` VARCHAR(100) NOT NULL,
  `fileLocation` VARCHAR(200) NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

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

ALTER TABLE `fixtureDefAttachment` 
 ADD COLUMN `width` INTEGER UNSIGNED DEFAULT NULL COMMENT 'image/video' AFTER `description`,
 ADD COLUMN `height` INTEGER UNSIGNED DEFAULT NULL COMMENT 'image/video' AFTER `width`,
 ADD COLUMN `length` INTEGER UNSIGNED DEFAULT NULL COMMENT 'audio/video (msec)/anim (frames)' AFTER `height`,
 ADD COLUMN `audioRate` INTEGER UNSIGNED DEFAULT NULL COMMENT 'audio samples/sec' AFTER `length`,
 ADD COLUMN `videoRate` INTEGER UNSIGNED DEFAULT NULL COMMENT 'video fps * 1000' AFTER `audioRate`,
 ADD COLUMN `audioCodec` VARCHAR(100) DEFAULT NULL COMMENT 'encapsulated audio format' AFTER `videoRate`,
 ADD COLUMN `videoCodec` VARCHAR(100) DEFAULT NULL COMMENT 'encapsulated video format' AFTER `audioCodec`;

ALTER TABLE `showDefAttachment` 
 ADD COLUMN `width` INTEGER UNSIGNED DEFAULT NULL COMMENT 'image/video' AFTER `description`,
 ADD COLUMN `height` INTEGER UNSIGNED DEFAULT NULL COMMENT 'image/video' AFTER `width`,
 ADD COLUMN `length` INTEGER UNSIGNED DEFAULT NULL COMMENT 'audio/video (msec)/anim (frames)' AFTER `height`,
 ADD COLUMN `audioRate` INTEGER UNSIGNED DEFAULT NULL COMMENT 'audio samples/sec' AFTER `length`,
 ADD COLUMN `videoRate` INTEGER UNSIGNED DEFAULT NULL COMMENT 'video fps * 1000' AFTER `audioRate`,
 ADD COLUMN `audioCodec` VARCHAR(100) DEFAULT NULL COMMENT 'encapsulated audio format' AFTER `videoRate`,
 ADD COLUMN `videoCodec` VARCHAR(100) DEFAULT NULL COMMENT 'encapsulated video format' AFTER `audioCodec`;

CREATE TABLE `stageAttachment` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `stageId` INTEGER UNSIGNED NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `size` INTEGER UNSIGNED NOT NULL,
  `contentType` VARCHAR(200) NOT NULL,
  `fileLocation` VARCHAR(200) NOT NULL COMMENT 'relative to app base specified in appConfig',
  `description` VARCHAR(255) NOT NULL,
  `width` INTEGER UNSIGNED DEFAULT NULL COMMENT 'image/video',
  `height` INTEGER UNSIGNED DEFAULT NULL COMMENT 'image/video',
  `length` INTEGER UNSIGNED DEFAULT NULL COMMENT 'audio/video (msec)/anim (frames)',
  `audioRate` INTEGER UNSIGNED DEFAULT NULL COMMENT 'audio samples/sec',
  `videoRate` INTEGER UNSIGNED DEFAULT NULL COMMENT 'video fps * 1000',
  `audioCodec` VARCHAR(100) DEFAULT NULL COMMENT 'encapsulated audio format',
  `videoCodec` VARCHAR(100) DEFAULT NULL COMMENT 'encapsulated video format',
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB;

 