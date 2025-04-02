CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `first_name` varchar(255) DEFAULT NULL,
                         `last_name` varchar(255) DEFAULT NULL,
                         `email` varchar(255) NOT NULL,
                         `phone_number` varchar(20) DEFAULT NULL,
                         `password` varchar(255) NOT NULL,
                         `admin` tinyint(1) DEFAULT '0',
                         `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `locations` (
                             `postal_code` int NOT NULL,
                             `latitude` decimal(10,6) NOT NULL,
                             `longitude` decimal(11,6) NOT NULL,
                             `city` varchar(255) DEFAULT NULL,
                             `country` varchar(255) DEFAULT NULL,
                             `geo` point NOT NULL /*!80003 SRID 4326 */,
                             PRIMARY KEY (`postal_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE categories (
                            `id` int unsigned NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE listings (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `title` varchar(255) NOT NULL,
                          `price` double NOT NULL,
                          `brief_description` varchar(255) DEFAULT NULL,
                          `description` text,
                          `user_id` int NOT NULL,
                          `status` enum('active','sold','reserved','archived') NOT NULL,
                          `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          `reserved_by_user_id` int DEFAULT NULL,
                          `reserved_at` timestamp NULL DEFAULT NULL,
                          `sold_at` timestamp NULL DEFAULT NULL,
                          `sold_to_user_id` int DEFAULT NULL,
                          `postal_code` int DEFAULT NULL,
                          `views_count` int DEFAULT '0',
                          PRIMARY KEY (`id`),
                          KEY `fk_listing_user` (`user_id`),
                          KEY `fk_reserved_user` (`reserved_by_user_id`),
                          KEY `fk_sold_user` (`sold_to_user_id`),
                          KEY `fk_listing_location` (`postal_code`),
                          CONSTRAINT `fk_listing_location` FOREIGN KEY (`postal_code`) REFERENCES `locations` (`postal_code`) ON DELETE SET NULL,
                          CONSTRAINT `fk_listing_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                          CONSTRAINT `fk_reserved_user` FOREIGN KEY (`reserved_by_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
                          CONSTRAINT `fk_sold_user` FOREIGN KEY (`sold_to_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `images` (
                          `id` int NOT NULL AUTO_INCREMENT,
                          `path_to_image` varchar(255) NOT NULL,
                          `listing_id` int DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `listing_id` (`listing_id`),
  CONSTRAINT `images_ibfk_1` FOREIGN KEY (`listing_id`) REFERENCES listings (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





CREATE TABLE messages (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `from_user_id` int NOT NULL,
                           `to_user_id` int NOT NULL,
                           `message` text NOT NULL,
                           `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (`id`),
                           KEY `from_user_id` (`from_user_id`),
  KEY `to_user_id` (`to_user_id`),
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`from_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`to_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `notifications` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `user_id` int NOT NULL,
                                 `sent_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                 `title` varchar(255) DEFAULT NULL,
                                 `message` text,
                                 PRIMARY KEY (`id`),
                                 KEY `user_id` (`user_id`),
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE user_favorites (
                                 `user_id` int NOT NULL,
                                 `listing_id` int NOT NULL,
                                 PRIMARY KEY (`user_id`,`listing_id`),
                                 KEY `listing_id` (`listing_id`),
  CONSTRAINT `user_favorite_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_favorite_ibfk_2` FOREIGN KEY (`listing_id`) REFERENCES listings (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `browsing_history` (
                                    `user_id` int NOT NULL,
                                    `listing_id` int NOT NULL,
                                    `viewed_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                    PRIMARY KEY (`user_id`,`listing_id`),
                                    KEY `listing_id` (`listing_id`),
                                    CONSTRAINT `browsing_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `browsing_history_ibfk_2` FOREIGN KEY (`listing_id`) REFERENCES listings (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


