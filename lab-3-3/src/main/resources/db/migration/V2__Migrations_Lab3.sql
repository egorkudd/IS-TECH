USE itmo_lab2;

ALTER TABLE tasks ADD author VARCHAR(255) after type;

CREATE TABLE `comments` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `author` varchar(255) NOT NULL,
  `create_date` date NOT NULL,
  `task_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `task_id` (`task_id`),
  FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`)
);