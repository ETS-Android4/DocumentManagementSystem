-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema sni_dms
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema sni_dms
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `sni_dms` DEFAULT CHARACTER SET utf8 ;
USE `sni_dms` ;

-- -----------------------------------------------------
-- Table `sni_dms`.`history_record`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sni_dms`.`history_record` (
  `recordId` INT NOT NULL AUTO_INCREMENT,
  `dateTime` DATETIME NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `action` ENUM('create', 'upload', 'update', 'download', 'move', 'delete', 'createDir', 'deleteDir') NOT NULL,
  `fileName` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`recordId`))
ENGINE = InnoDB
AUTO_INCREMENT = 264
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `sni_dms`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `sni_dms`.`user` (
  `userId` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(70) NOT NULL,
  `role` ENUM('A', 'AD', 'K') NOT NULL,
  `permissions` VARCHAR(5) NOT NULL,
  `ipAddress` VARCHAR(45) NULL DEFAULT NULL,
  `token` VARCHAR(70) NULL DEFAULT NULL,
  `tokenExpiration` DATETIME NULL DEFAULT NULL,
  `rootDir` VARCHAR(45) NOT NULL,
  `salt` BINARY(12) NULL DEFAULT NULL,
  `mail` VARCHAR(45) NOT NULL,
  `logoutTime` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 38
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
