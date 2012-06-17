-- success if device exists
SELECT COUNT(*) FROM device;

-- success if xxx
SELECT COUNT(*) FROM xxx;


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

