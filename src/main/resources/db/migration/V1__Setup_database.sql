/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;


-- Volcando estructura de base de datos para cadibot
CREATE DATABASE IF NOT EXISTS `cadibot` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `cadibot`;

-- Volcando estructura para tabla cadibot.cadibot_changelog
CREATE TABLE IF NOT EXISTS `cadibot_changelog`
(
    `major`   int(2) unsigned NOT NULL DEFAULT 2,
    `minor`   int(3) unsigned NOT NULL,
    `changes` varchar(200)    NOT NULL,
    `commit`  char(50)        NOT NULL DEFAULT '',
    PRIMARY KEY (`major`, `minor`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Changelog de versiones del bot';

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cadibot.cadibot_grupos
CREATE TABLE IF NOT EXISTS `cadibot_grupos`
(
    `groupid`     bigint(20) NOT NULL,
    `name`        varchar(128)        DEFAULT NULL,
    `description` varchar(50)         DEFAULT NULL,
    `creation`    timestamp  NOT NULL DEFAULT current_timestamp(),
    `lastAdded`   timestamp  NOT NULL DEFAULT current_timestamp(),
    `valid`       tinyint(1) NOT NULL DEFAULT 1,
    UNIQUE KEY `groupid` (`groupid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Tabla con las IDs de grupos donde está el bot';

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cadibot.cadibot_poles
CREATE TABLE IF NOT EXISTS `cadibot_poles`
(
    `time`     timestamp(4) NOT NULL DEFAULT current_timestamp(4),
    `groupid`  bigint(20)   NOT NULL,
    `userid`   bigint(20)   NOT NULL,
    `poleType` tinyint(4)   NOT NULL,
    KEY `FK_cadibot_poles_cadibot_grupos` (`groupid`),
    KEY `FK_cadibot_poles_cadibot_users` (`userid`),
    CONSTRAINT `FK_cadibot_poles_cadibot_grupos` FOREIGN KEY (`groupid`) REFERENCES `cadibot_grupos` (`groupid`),
    CONSTRAINT `FK_cadibot_poles_cadibot_users` FOREIGN KEY (`userid`) REFERENCES `cadibot_users` (`userid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='TelegramBot poles';

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla cadibot.cadibot_users
CREATE TABLE IF NOT EXISTS `cadibot_users`
(
    `userid`   bigint(20)          NOT NULL,
    `name`     varchar(200)        NOT NULL,
    `username` varchar(50)                  DEFAULT NULL,
    `lang`     varchar(15)                  DEFAULT NULL,
    `creation` timestamp           NULL     DEFAULT current_timestamp(),
    `isBanned` tinyint(1) unsigned NOT NULL DEFAULT 0,
    `banTime`  timestamp           NULL     DEFAULT NULL,
    UNIQUE KEY `Índice 1` (`userid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Tabla con el nombre de usuarios de Telegram por si su API falla';

-- La exportación de datos fue deseleccionada.

/*!40103 SET TIME_ZONE = IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE = IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS = IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES = IFNULL(@OLD_SQL_NOTES, 1) */;
