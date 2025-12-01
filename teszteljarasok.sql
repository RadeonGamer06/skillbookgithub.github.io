DELIMITER //

CREATE PROCEDURE EnrollUserInCourse (IN userId INT, IN courseId INT)
BEGIN
  DECLARE enrolled INT;
  DECLARE current_participants INT;
  DECLARE max_part INT;

  SELECT COUNT(*) INTO enrolled FROM enrollments WHERE user_id = userId AND course_id = courseId;
  IF enrolled > 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Already enrolled';
  END IF;

  SELECT COUNT(*) INTO current_participants FROM enrollments WHERE course_id = courseId AND status IN ('pending', 'confirmed', 'completed');
  SELECT max_participants INTO max_part FROM courses WHERE id = courseId;

  IF current_participants >= max_part THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Course is full';
  END IF;

  INSERT INTO enrollments (user_id, course_id, status) VALUES (userId, courseId, 'confirmed');
  INSERT INTO progress (user_id, course_id, percentage) VALUES (userId, courseId, 0);
END //

CREATE PROCEDURE UpdateProgress (IN userId INT, IN courseId INT, IN newPercentage INT)
BEGIN
  UPDATE progress SET percentage = newPercentage, updated_at = CURRENT_TIMESTAMP WHERE user_id = userId AND course_id = courseId;
  IF newPercentage = 100 THEN
    UPDATE enrollments SET status = 'completed' WHERE user_id = userId AND course_id = courseId;
    INSERT IGNORE INTO certificates (user_id, course_id, pdf_url) VALUES (userId, courseId, CONCAT('https://example.com/certificates/user', userId, '_course', courseId, '.pdf'));
  END IF;
END //

CREATE PROCEDURE GetCourseAverageRating (IN courseId INT, OUT avgRating DECIMAL(3,2))
BEGIN
  SELECT AVG(rating) INTO avgRating FROM reviews WHERE course_id = courseId;
END //

CREATE PROCEDURE AddReview (IN userId INT, IN courseId INT, IN rating INT, IN comment TEXT)
BEGIN
  DECLARE completed INT;
  SELECT COUNT(*) INTO completed FROM enrollments WHERE user_id = userId AND course_id = courseId AND status = 'completed';
  IF completed = 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Must complete course to review';
  END IF;
  INSERT INTO reviews (user_id, course_id, rating, comment) VALUES (userId, courseId, rating, comment);
END //

CREATE PROCEDURE GetUserEnrolledCourses (IN userId INT)
BEGIN
  SELECT c.* FROM courses c
  INNER JOIN enrollments e ON c.id = e.course_id
  WHERE e.user_id = userId AND e.status != 'cancelled';
END //

CREATE PROCEDURE IssueCertificate (IN userId INT, IN courseId INT)
BEGIN
  DECLARE prog INT;
  SELECT percentage INTO prog FROM progress WHERE user_id = userId AND course_id = courseId;
  IF prog = 100 THEN
    INSERT IGNORE INTO certificates (user_id, course_id, pdf_url) VALUES (userId, courseId, CONCAT('https://example.com/certificates/user', userId, '_course', courseId, '.pdf'));
  ELSE
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Progress not 100%';
  END IF;
END //

DELIMITER ;