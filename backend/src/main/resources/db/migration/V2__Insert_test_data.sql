-- Test data for the 'users' table
INSERT INTO users (id, first_name, last_name, email, phone_number, password, admin) VALUES
                                                                                        (1, 'John', 'Doe', 'john.doe@example.com', '123-456-7890', 'password123', 1),
                                                                                        (2, 'Jane', 'Smith', 'jane.smith@example.com', '987-654-3210', 'securepass', 0),
                                                                                        (3, 'Peter', 'Jones', 'peter.jones@example.com', '555-123-4567', 'anotherpwd', 0),
                                                                                        (4, 'Alice', 'Brown', 'alice.brown@example.com', NULL, 'test1234', 0),
                                                                                        (5, 'Bob', 'Green', 'bob.green@example.com', '111-222-3333', 'mysecret', 1);

-- Test data for the 'locations' table
INSERT INTO locations (postal_code, city, country, latitude, longitude) VALUES
                                                            (7000, 'Trondheim', 'Norway', 10.3951, 63.4305),
                                                            (100, 'Oslo', 'Norway', 10.7522, 59.9139),
                                                            (5000, 'Bergen', 'Norway', 5.3221, 60.3913),
                                                            (9000, 'Tromsø', 'Norway', 18.9553, 69.6492),
                                                            (7400, 'Trondheim', 'Norway', 10.4000, 63.4000);

-- Test data for the 'categories' table
INSERT INTO categories (id, name) VALUES
                                      (1, 'Vehicles'),
                                      (2, 'Clothing'),
                                      (3, 'Furniture'),
                                      (4, 'Property'),
                                      (5, 'Activity'),
                                      (6, 'Electronics'),
                                      (7, 'Beauty');

-- Test data for the 'listings' table
INSERT INTO listings (id, title, category_id, price, brief_description, description, user_id, status, `condition`, reserved_by_user_id, reserved_at, sold_at, sold_to_user_id, postal_code) VALUES
                                                                                                                                                                                                (1, 'Used Laptop', 6, 350.00, 'Lightly used laptop for sale.', 'This is a great laptop for everyday use. Specs: Intel Core i5, 8GB RAM, 256GB SSD.', 2, 'active', 'good', NULL, NULL, NULL, NULL, 7000),
                                                                                                                                                                                                (2, 'Wooden Dining Table', 3, 200.00, 'Solid wood dining table.', 'A sturdy wooden dining table with seating for 4-6 people.', 1, 'sold', 'fair', NULL, NULL, '2025-04-05 10:00:00', 3, 100),
                                                                                                                                                                                                (3, 'The Lord of the Rings', 3, 15.00, 'Complete trilogy in paperback.', 'All three books in excellent condition.', 4, 'active', 'like_new', NULL, NULL, NULL, NULL, 5000),
                                                                                                                                                                                                (4, 'Nike Running Shoes', 2, 75.00, 'Barely worn running shoes.', 'Size 10 Nike running shoes, only used a few times.', 2, 'reserved', 'like_new', 3, '2025-04-06 16:15:00', NULL, NULL, 7000),
                                                                                                                                                                                                (5, 'Vintage Armchair', 3, 120.00, 'Comfortable vintage armchair.', 'A stylish and comfortable armchair from the 1950s.', 1, 'active', 'good', NULL, NULL, NULL, NULL, 9000),
                                                                                                                                                                                                (6, 'Programming Book', 6, 25.00, 'Introduction to Python programming.', 'A comprehensive guide for beginners.', 5, 'active', 'new', NULL, NULL, NULL, NULL, 7400),
                                                                                                                                                                                                (7, 'Summer Dress', 2, 30.00, 'Light and airy summer dress.', 'Size medium, floral pattern.', 3, 'sold', 'good', NULL, NULL, '2025-04-04 18:30:00', 1, 100),
                                                                                                                                                                                                (8, 'Tennis Racket', 5, 45.00, 'High-quality tennis racket.', 'Used but in good condition, good for intermediate players.', 4, 'active', 'good', NULL, NULL, NULL, NULL, 5000),
                                                                                                                                                                                                (9, 'Office Desk', 3, 150.00, 'Spacious office desk.', 'Large desk with drawers, perfect for a home office.', 2, 'active', 'fair', NULL, NULL, NULL, NULL, 7000),
                                                                                                                                                                                                (10, 'Science Fiction Novel', 3, 10.00, 'Classic sci-fi novel.', 'A timeless science fiction masterpiece.', 1, 'archived', 'good', NULL, NULL, NULL, NULL, 9000);
-- Test data for the 'listing_images' table
INSERT INTO listing_images (path_to_image, listing_id) VALUES
                                                           ('/images/laptop1.jpg', 1),
                                                           ('/images/laptop2.jpg', 1),
                                                           ('/images/table1.jpg', 2),
                                                           ('/images/book1.jpg', 3),
                                                           ('/images/shoes1.jpg', 4),
                                                           ('/images/armchair1.jpg', 5),
                                                           ('/images/book2.jpg', 6),
                                                           ('/images/dress1.jpg', 7),
                                                           ('/images/racket1.jpg', 8),
                                                           ('/images/desk1.jpg', 9);

-- Test data for the 'messages' table
INSERT INTO messages (from_user_id, to_user_id, message, sent_at, is_read) VALUES
                                                                               (2, 1, 'Hi, is the laptop still available?', '2025-04-06 10:00:00', TRUE),
                                                                               (1, 2, 'Yes, it is!', '2025-04-06 10:05:00', TRUE),
                                                                               (3, 2, 'I am interested in the running shoes. Can I reserve them?', '2025-04-06 15:30:00', TRUE),
                                                                               (2, 3, 'Sure, I can reserve them for you.', '2025-04-06 16:00:00', TRUE),
                                                                               (4, 1, 'What is the condition of the dining table?', '2025-04-04 09:00:00', TRUE),
                                                                               (1, 4, 'It is in fair condition, some scratches on the surface.', '2025-04-04 09:15:00', TRUE),
                                                                               (5, 3, 'Is the summer dress still for sale?', '2025-04-04 17:00:00', TRUE),
                                                                               (3, 5, 'No, it has been sold.', '2025-04-04 17:30:00', TRUE),
                                                                               (1, 5, 'Are you open to offers on the armchair?', '2025-04-05 14:00:00', FALSE),
                                                                               (5, 1, 'Maybe, what is your offer?', '2025-04-05 14:30:00', FALSE);

-- Test data for the 'user_favorites' table
INSERT INTO user_favorites (user_id, listing_id) VALUES
                                                     (1, 3),
                                                     (1, 5),
                                                     (2, 1),
                                                     (3, 4),
                                                     (4, 2),
                                                     (4, 8),
                                                     (5, 6);

-- Test data for the 'browsing_history' table
INSERT INTO browsing_history (user_id, listing_id, viewed_at) VALUES
                                                                  (1, 1, '2025-04-06 09:00:00'),
                                                                  (2, 3, '2025-04-06 11:00:00'),
                                                                  (3, 1, '2025-04-06 12:00:00'),
                                                                  (1, 4, '2025-04-06 14:00:00'),
                                                                  (4, 7, '2025-04-05 17:00:00'),
                                                                  (2, 5, '2025-04-05 19:00:00'),
                                                                  (5, 9, '2025-04-06 08:00:00'),
                                                                  (3, 2, '2025-04-04 10:00:00'),
                                                                  (1, 8, '2025-04-05 20:00:00'),
                                                                  (4, 6, '2025-04-06 13:00:00');