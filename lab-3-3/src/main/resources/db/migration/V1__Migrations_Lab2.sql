USE itmo_lab2;

CREATE TABLE `employees` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `birthday` date NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `tasks` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `deadline` date NOT NULL,
  `description` text NOT NULL,
  `type` enum('NEW_FUNCTIONALITY', 'ERROR', 'UPGRADE', 'ANALYTICS') NOT NULL,
  `employee_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employees` (`id`)
);
