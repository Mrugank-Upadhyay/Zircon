-- MariaDB dump 10.19  Distrib 10.5.18-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: mark_downey_db
-- ------------------------------------------------------
-- Server version       10.5.18-MariaDB-0+deb11u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `note_shares`
--

DROP TABLE IF EXISTS `note_shares`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `note_shares` (
                               `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                               `note_id` int(10) unsigned NOT NULL,
                               `share_url` varchar(100) NOT NULL,
                               `created` datetime NOT NULL DEFAULT current_timestamp(),
                               `expiration` datetime DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `note_shares_FK` (`note_id`),
                               CONSTRAINT `note_shares_FK` FOREIGN KEY (`note_id`) REFERENCES `notes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note_shares`
--

LOCK TABLES `note_shares` WRITE;
/*!40000 ALTER TABLE `note_shares` DISABLE KEYS */;
INSERT INTO `note_shares` VALUES (1,1,'test_url','2023-02-10 16:43:08',NULL);
/*!40000 ALTER TABLE `note_shares` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `note_users`
--

DROP TABLE IF EXISTS `note_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `note_users` (
                              `note_id` int(10) unsigned NOT NULL,
                              `user_id` int(10) unsigned NOT NULL,
                              `permission` int(10) unsigned NOT NULL,
                              PRIMARY KEY (`note_id`,`user_id`,`permission`),
                              KEY `user_notes_FK` (`user_id`),
                              CONSTRAINT `note_users_FK` FOREIGN KEY (`note_id`) REFERENCES `notes` (`id`),
                              CONSTRAINT `user_notes_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `note_users`
--

LOCK TABLES `note_users` WRITE;
/*!40000 ALTER TABLE `note_users` DISABLE KEYS */;
INSERT INTO `note_users` VALUES (1,2,0),(4,2,0),(6,2,0),(7,2,0),(8,2,0),(9,2,0),(10,2,0),(11,2,0),(12,2,0),(13,2,0),(14,2,0),(15,2,0),(16,2,0),(17,2,0),(18,2,0),(19,2,0),(20,2,0),(21,2,0),(22,2,0),(23,2,0),(24,2,0),(25,2,0),(26,2,0),(27,2,0),(28,2,0),(29,2,0),(30,2,0),(31,2,0),(32,2,0),(33,2,0),(34,3,0);
/*!40000 ALTER TABLE `note_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notes`
--

DROP TABLE IF EXISTS `notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notes` (
                         `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                         `filename` varchar(100) NOT NULL,
                         `url` varchar(100) NOT NULL DEFAULT '',
                         `created` datetime DEFAULT current_timestamp(),
                         `modified` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
                         `content` longtext NOT NULL DEFAULT '',
                         `owner_id` int(10) unsigned DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `notes_title_IDX` (`filename`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notes`
--

LOCK TABLES `notes` WRITE;
/*!40000 ALTER TABLE `notes` DISABLE KEYS */;
INSERT INTO `notes` VALUES (1,'Testing 3','file://test','2023-02-10 16:25:13','2023-02-24 00:56:12','fnord bazz fizzbuzz aardvark',NULL),(2,'testing','file://','2023-02-24 01:38:35','2023-02-24 21:07:44','',NULL),(3,'testing2','','2023-02-24 01:39:50','2023-02-24 21:12:56','',NULL),(4,'Testing 4','','2023-02-24 21:24:09','2023-02-24 21:24:09','',NULL),(5,'Testing 5','','2023-02-24 21:29:21','2023-02-24 21:29:21','',NULL),(6,'Testing 6','','2023-02-24 21:30:35','2023-02-24 21:30:35','',NULL),(7,'foobar','','2023-02-24 21:46:19','2023-03-01 02:17:11','asdfsaa lksflkasdkl faklsdfnklm masldknf',NULL),(8,'2023-02-28T21:23:57.967283700','','2023-03-01 02:24:03','2023-03-01 02:26:15','kjsdnflkjasndklf ',NULL),(9,'2023-02-28T21:27:09.188287400','','2023-03-01 02:27:20','2023-03-01 02:27:20','',NULL),(10,'2023-02-28T21:33:47.602791800','','2023-03-01 02:33:53','2023-03-01 02:34:19','as',NULL),(11,'2023-02-28T21:33:47.602791800','','2023-03-01 02:34:03','2023-03-01 02:34:19','as',NULL),(12,'2023-03-01T15:18:49.143315802','','2023-03-01 20:19:30','2023-03-01 20:22:10','asdjfn',NULL),(13,'2023-03-01T15:24:06.575879846','','2023-03-01 20:24:14','2023-03-01 20:24:26','asdfkjasd',NULL),(14,'2023-03-01T15:25:54.240996943','','2023-03-01 20:25:59','2023-03-01 20:25:59','asdfasfasdf',NULL),(15,'2023-03-01T15:29:52.342043315','','2023-03-01 20:30:25','2023-03-01 20:30:29','asfsadf',NULL),(16,'2023-03-01T15:38:46.396434981','','2023-03-01 20:38:52','2023-03-01 20:38:53','afdsfasdf',NULL),(17,'2023-03-01T15:40:02.385918567','','2023-03-01 20:40:08','2023-03-01 20:40:10','asdfasdfasd',NULL),(18,'2023-03-01T15:41:17.479102775','','2023-03-01 20:41:21','2023-03-01 20:41:23','adfasdf',NULL),(19,'2023-03-01T15:45:35.665581332','','2023-03-01 20:45:39','2023-03-01 20:45:40','asdf',NULL),(20,'2023-03-01T15:46:35.029312350','','2023-03-01 20:46:39','2023-03-01 20:46:40','ref',NULL),(21,'2023-03-01T15:47:07.903311843','','2023-03-01 20:47:11','2023-03-01 20:47:13','sdf',NULL),(22,'2023-03-01T15:47:24.904692615','','2023-03-01 20:47:28','2023-03-01 20:47:28','',NULL),(23,'2023-03-01T15:48:16.446067461','','2023-03-01 20:48:22','2023-03-01 20:48:29','werewgwerg',NULL),(24,'2023-03-01T15:48:16.446067461','','2023-03-01 20:48:28','2023-03-01 20:48:29','werewgwerg',NULL),(25,'2023-03-01T16:03:37.700268205','','2023-03-01 21:03:53','2023-03-01 21:03:53','',NULL),(26,'2023-03-01T16:06:41.218478167','','2023-03-01 21:06:44','2023-03-01 21:06:44','',NULL),(27,'2023-03-01T16:14:54.931354237','','2023-03-01 21:15:08','2023-03-01 21:15:08','',NULL),(28,'2023-03-01T16:32:16.200247436','','2023-03-01 21:32:20','2023-03-01 21:32:20','',NULL),(29,'2023-03-01T16:33:32.831651276','','2023-03-01 21:33:35','2023-03-01 21:33:35','',NULL),(30,'2023-03-01T16:34:26.618806745','','2023-03-01 21:34:29','2023-03-01 21:34:29','',NULL),(31,'2023-03-01T16:46:16.576474','','2023-03-01 21:47:36','2023-03-01 21:47:36','',NULL),(32,'2023-03-02T15:17:53.760367','','2023-03-02 20:18:47','2023-03-02 20:18:47','',NULL),(33,'2023-03-02T15:25:32.616495961','','2023-03-02 20:25:40','2023-03-02 20:25:40','',NULL),(34,'foobar','','2023-03-08 20:09:47','2023-03-08 20:09:48','hi this is a test',NULL);
/*!40000 ALTER TABLE `notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
                         `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                         `email` varchar(100) NOT NULL,
                         `password` varchar(100) NOT NULL,
                         `type` int(10) unsigned NOT NULL,
                         `name` varchar(100) NOT NULL,
                         `created` datetime NOT NULL DEFAULT current_timestamp(),
                         `preferences` text DEFAULT '',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `users_email_IDX` (`email`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'test2@example.com','barfoo',2,'rich','2023-02-10 16:25:13',''),(3,'markdowney@example.com','$2a$10$/L98eJH6VWUQvJgVZRZZR.AVauDpriS/T7Wn7oJDIYGFgPtpe2gQK',1,'Mark Downey','2023-03-08 19:49:24',''),(4,'richtess@example.com','$2a$10$hPQVSSWsVH4VSIyXPYmOMOs6BTDXnX5h7rcbwyTTGrg9QU0zth65W',1,'Rich Tess','2023-03-08 19:50:38','');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-03-08 20:22:57
