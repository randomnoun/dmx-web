/* Each of these files start with 2 SQL statements.
 * The first will succeed if the first SQL statement has been applied
 *   (and fail if it has not been applied)
 * The second will succeed if the final SQL statement has been applied
 *   (and fail if it has not been applied)
 * 
 * On appConfig startup:
 *   If the first SQL statement fails, then apply this SQL
 *   If the first SQL statement succeeds and second fails then SQL has been partially applied 
 *     (error, manual fix required)
 *   If the first and second SQL statements succeed, then no need to apply this SQL
 *
 * Checks will be made from last create-mysql-tables-*.sql file backwards, only
 * applying looking at SQL definitions where the first SQL has failed. SQL will
 * applied from first file onwards.
 * 
 * i.e. if first SQL for last create-mysql-tables*.sql file succeeds, then no
 * other SQL file will be inspected.
 */

-- success if fixtureDef table exists
SELECT COUNT(*) FROM fixtureDef;

-- success if fixtureDefImage table exists
SELECT COUNT(*) FROM fixtureDefImage; 

CREATE TABLE `fixtureDef` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `fixtureDefClassName` VARCHAR(100) NOT NULL,
  `fixtureDefScript` MEDIUMTEXT NOT NULL,
  `fixtureControllerClassName` VARCHAR(100) NOT NULL,
  `fixtureControllerScript` MEDIUMTEXT NOT NULL,
  `channelMuxerClassName` VARCHAR(100) DEFAULT NULL,
  `channelMuxerScript` MEDIUMTEXT DEFAULT NULL,
  `dmxChannels` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`Id`)
)
ENGINE = InnoDB;


CREATE TABLE `fixture` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `fixtureDefId` INTEGER UNSIGNED NOT NULL,
  `name` VARCHAR(100),
  `dmxOffset` INTEGER UNSIGNED NOT NULL,
   `x` FLOAT DEFAULT NULL,
   `y` FLOAT DEFAULT NULL,
   `z` FLOAT DEFAULT NULL,
   `lookingAtX` FLOAT DEFAULT NULL,
   `lookingAtY` FLOAT DEFAULT NULL,
   `lookingAtZ` FLOAT DEFAULT NULL,
   `upX` FLOAT DEFAULT NULL,
   `upY` FLOAT DEFAULT NULL,
   `upZ` FLOAT DEFAULT NULL,
   `sortOrder` INT(10) DEFAULT NULL,
  PRIMARY KEY (`Id`)
)
ENGINE = InnoDB;

CREATE TABLE `showDef` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `className` VARCHAR(100) NOT NULL,
  `script` MEDIUMTEXT NOT NULL,
  `javadoc` MEDIUMTEXT DEFAULT NULL,
  PRIMARY KEY (`Id`)
)
ENGINE = InnoDB;

CREATE TABLE `show` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `showDefId` INTEGER UNSIGNED NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `onCancelShowId` INTEGER UNSIGNED,
  `onCompleteShowId` INTEGER UNSIGNED,
  `showGroupId` INTEGER UNSIGNED DEFAULT NULL, 
  PRIMARY KEY (`Id`)
)
ENGINE = InnoDB;


CREATE TABLE `showProperty` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `showId` INTEGER UNSIGNED NOT NULL,
  `key` VARCHAR(100) NOT NULL,
  `value` VARCHAR(255),
  PRIMARY KEY (`Id`)
)
ENGINE = InnoDB;

CREATE TABLE `fixtureDefImage` (
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



