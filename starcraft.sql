-- MySQL dump 10.13  Distrib 5.7.13, for Win64 (x86_64)
--
-- Host: localhost    Database: starcraft
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `savegame`
--

DROP TABLE IF EXISTS `savegame`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `savegame` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `gameName` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=107 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `savegame`
--

LOCK TABLES `savegame` WRITE;
/*!40000 ALTER TABLE `savegame` DISABLE KEYS */;
INSERT INTO `savegame` VALUES (1,'Son'),(2,'Son2'),(3,'Son'),(4,'Son'),(5,'Son'),(6,'Son'),(7,'Son'),(8,'Son2'),(9,'Son2'),(10,'Son'),(11,'Son'),(12,'Son'),(13,'Son'),(14,'Son'),(15,'Son'),(16,'Son'),(17,'Son'),(18,'Son'),(19,'Son'),(20,'Son'),(21,'Son'),(22,'Son'),(23,'Son'),(24,'Son'),(25,'Son'),(26,'Son'),(27,'Son'),(28,'Son'),(29,'Son'),(30,'Son'),(31,'Son'),(32,'Son'),(33,'Son'),(34,'Son'),(35,'Son'),(36,'Son'),(37,'Son'),(38,'Son'),(39,'Son'),(40,'Son'),(41,'Son'),(42,'Son2'),(43,'Son2'),(44,'Son'),(45,'Son2'),(46,'Son2'),(47,'Son2'),(48,'Son'),(49,'Son3'),(50,'Son2'),(51,'Son2'),(52,'Son2'),(53,'Son2'),(54,'Son2'),(55,'Son2'),(56,'Son2'),(57,'Son'),(58,'Son'),(59,'Son'),(60,'Son'),(61,'Son'),(62,'Son'),(63,'Son'),(64,'Son2'),(65,'Son'),(66,'Son'),(67,'Son'),(68,'Son2'),(69,'Son'),(70,'Son'),(71,'Son'),(72,'Son'),(73,'Son'),(74,'son'),(75,'son'),(76,'Son'),(77,'Son'),(78,'Son'),(79,'Son'),(80,'Son'),(81,'Son'),(82,'Son'),(83,'Son2'),(84,'Son2'),(85,'Son'),(86,'Son2'),(87,'Son'),(88,'Son2'),(89,'Son'),(90,'Son2'),(91,'Son2'),(92,'Son2'),(93,'Son2'),(94,'son'),(95,'son'),(96,'son'),(97,'son'),(98,'son'),(99,'son'),(100,'son'),(101,'son'),(102,'son'),(103,'Son2'),(104,'Son2'),(105,'son'),(106,'son');
/*!40000 ALTER TABLE `savegame` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `utilisateur` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mot_de_passe` varchar(56) NOT NULL,
  `nomUtilisateur` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nomUtilisateur` (`nomUtilisateur`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `utilisateur`
--

LOCK TABLES `utilisateur` WRITE;
/*!40000 ALTER TABLE `utilisateur` DISABLE KEYS */;
INSERT INTO `utilisateur` VALUES (1,'bDwW3Xm3fTeIvyQ+clnPge7cIMiJ74tYOI2BZQCCOQCHJqK5+ZIjJQ==','Son'),(2,'ehZQHvElYejyNShYH0wmZmO+hskHSl+6Z/EjXVr5tBPCts256qcTNg==','Son2'),(3,'qWT2TAaNoFp2bkXPZh9rg0500x18/R9LU9T9+rnAE6dX3NUBbnaPpA==','Son3'),(4,'w76h6nLgQExa2fe/Ww7kzML+ytyB13Pw9pji0QqVu7eqXEJeiorOtg==','Son4'),(5,'JTAsieMJnSvzqEHdTfhLTAJU9PKnHqeewtntrYq30QxgQ+WhcNmeRg==','Son5'),(6,'vURV/wXtDG/tVrnrQhLlUVmP0bXT7s9bAa/g1luchDfO/sh/9fBW/A==','Son6'),(7,'SSnUG19ky3af/CzF2neKOmezpBFcLNTgkSFxQWX/6Qhg1mEgjDzZcw==','Son7'),(8,'D828YaeED67YvK2xfTkksnJwlM+AuC5T9b2Xg6eZ+ATL+FrQj7iB7Q==','Son8'),(9,'SXuFJd9TX0rIQAl3HJpBoySwDGp0n9NjUC/u9l8CWrLeDw7SzDNv/g==','Son9');
/*!40000 ALTER TABLE `utilisateur` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-26 10:02:28
