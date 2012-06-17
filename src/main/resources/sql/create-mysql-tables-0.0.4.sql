-- success if showDef.ynRecorded exists
SELECT COUNT(ynRecorded) FROM showDef;

-- success if showDef.ynRecorded exists
SELECT COUNT(ynRecorded) FROM showDef;


ALTER TABLE `showDef`
  ADD COLUMN `ynRecorded` VARCHAR(1) NOT NULL DEFAULT "N" COMMENT 'set to "Y" if this show extends RecordedShow' AFTER `javadoc`;

UPDATE showDef SET ynRecorded='N';