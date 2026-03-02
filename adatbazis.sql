-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2026. Már 02. 10:18
-- Kiszolgáló verziója: 5.7.24
-- PHP verzió: 8.3.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Adatbázis: `vizsgaremek`
--

DELIMITER $$
--
-- Eljárások
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `createCategory` (IN `nameIN` VARCHAR(255), IN `slugIN` VARCHAR(255))   BEGIN
    INSERT INTO categories (name, slug)
    VALUES (nameIN, slugIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createCourse` (IN `titleIN` VARCHAR(255), IN `descriptionIN` TEXT, IN `priceIN` INT, IN `instructorIdIN` INT, IN `categoryIdIN` INT, IN `maxParticipantsIN` INT)   BEGIN
    INSERT INTO courses (title, description, price, instructor_id, category_id, max_participants)
    VALUES (titleIN, descriptionIN, priceIN, instructorIdIN, categoryIdIN, maxParticipantsIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createCourseMaterial` (IN `courseIdIN` INT, IN `titleIN` VARCHAR(255), IN `fileUrlIN` VARCHAR(255))   BEGIN
    INSERT INTO course_materials (course_id, title, file_url)
    VALUES (courseIdIN, titleIN, fileUrlIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createCourseReview` (IN `courseIdIN` INT, IN `userIdIN` INT, IN `ratingIN` TINYINT, IN `commentIN` TEXT)   BEGIN
    INSERT INTO course_reviews (course_id, user_id, rating, comment)
    VALUES (courseIdIN, userIdIN, ratingIN, commentIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createCourseSession` (IN `courseIdIN` INT, IN `startAtIN` DATETIME, IN `endAtIN` DATETIME)   BEGIN
    INSERT INTO course_sessions (course_id, start_at, end_at)
    VALUES (courseIdIN, startAtIN, endAtIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createEnrollment` (IN `userIdIN` INT, IN `courseIdIN` INT, IN `sessionIdIN` INT, IN `statusIN` ENUM('registered','canceled'))   BEGIN
    INSERT INTO enrollments (user_id, course_id, session_id, status)
    VALUES (userIdIN, courseIdIN, sessionIdIN, statusIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createMessage` (IN `courseIdIN` INT, IN `senderIdIN` INT, IN `contentIN` TEXT)   BEGIN
    INSERT INTO messages (course_id, sender_id, content)
    VALUES (courseIdIN, senderIdIN, contentIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createQuiz` (IN `courseIdIN` INT, IN `titleIN` VARCHAR(255))   BEGIN
    INSERT INTO quizzes (course_id, title)
    VALUES (courseIdIN, titleIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createQuizQuestion` (IN `quizIdIN` INT, IN `questionIN` TEXT, IN `correctAnswerIN` VARCHAR(255))   BEGIN
    INSERT INTO quiz_questions (quiz_id, question, correct_answer)
    VALUES (quizIdIN, questionIN, correctAnswerIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createQuizResult` (IN `userIdIN` INT, IN `quizIdIN` INT, IN `scoreIN` DECIMAL(5,2))   BEGIN
    INSERT INTO quiz_results (user_id, quiz_id, score)
    VALUES (userIdIN, quizIdIN, scoreIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `createUser` (IN `nameIN` VARCHAR(255), IN `emailIN` VARCHAR(255), IN `passwordIN` VARCHAR(255), IN `roleIN` VARCHAR(50))   BEGIN
    INSERT INTO users (name, email, password, role, created_at, profile_picture)
    VALUES (nameIN, emailIN, passwordIN, roleIN, NOW(), NULL);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCategory` (IN `categoryIdIN` INT)   BEGIN
    DELETE FROM categories WHERE id = categoryIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCourse` (IN `courseIdIN` INT)   BEGIN
    -- Külső kulcs megszorítások ideiglenes kikapcsolása
    SET FOREIGN_KEY_CHECKS = 0;
    
    -- Quiz eredmények törlése
    DELETE FROM quiz_results WHERE quiz_id IN (SELECT id FROM quizzes WHERE course_id = courseIdIN);
    
    -- Quiz kérdések törlése
    DELETE FROM quiz_questions WHERE quiz_id IN (SELECT id FROM quizzes WHERE course_id = courseIdIN);
    
    -- Quizek törlése
    DELETE FROM quizzes WHERE course_id = courseIdIN;
    
    -- Többi tábla
    DELETE FROM messages WHERE course_id = courseIdIN;
    DELETE FROM enrollments WHERE course_id = courseIdIN;
    DELETE FROM course_reviews WHERE course_id = courseIdIN;
    DELETE FROM course_materials WHERE course_id = courseIdIN;
    DELETE FROM course_sessions WHERE course_id = courseIdIN;
    
    -- Tanfolyam törlése
    DELETE FROM courses WHERE id = courseIdIN;
    
    -- Külső kulcs megszorítások visszakapcsolása
    SET FOREIGN_KEY_CHECKS = 1;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCourseMaterial` (IN `materialIdIN` INT)   BEGIN
    DELETE FROM course_materials WHERE id = materialIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCourseReview` (IN `reviewIdIN` INT)   BEGIN
    DELETE FROM course_reviews WHERE id = reviewIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCourseSession` (IN `sessionIdIN` INT)   BEGIN
    DELETE FROM course_sessions WHERE id = sessionIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteEnrollment` (IN `enrollmentIdIN` INT)   BEGIN
    DELETE FROM enrollments WHERE id = enrollmentIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteMessage` (IN `messageIdIN` INT)   BEGIN
    DELETE FROM messages WHERE id = messageIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteQuiz` (IN `quizIdIN` INT)   BEGIN
    DELETE FROM quizzes WHERE id = quizIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteQuizQuestion` (IN `questionIdIN` INT)   BEGIN
    DELETE FROM quiz_questions WHERE id = questionIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteQuizResult` (IN `resultIdIN` INT)   BEGIN
    DELETE FROM quiz_results WHERE id = resultIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteUser` (IN `userIdIN` INT)   BEGIN                                                                       
     DECLARE EXIT HANDLER FOR SQLEXCEPTION                                  
     BEGIN                                                                   
         ROLLBACK;                                                           
         SIGNAL SQLSTATE '45000'                                            
         SET MESSAGE_TEXT = 'Hiba történt a felhasználó törlése során';    
     END;                                                                    
                                                                             
     START TRANSACTION;                                                      
                                                                             
     -- Quiz eredmények törlése                                             
     DELETE FROM quiz_results WHERE user_id = userIdIN;                     
                                                                             
     -- Üzenetek törlése                                                    
     DELETE FROM messages WHERE sender_id = userIdIN;                       
                                                                             
     -- Beiratkozások törlése                                               
     DELETE FROM enrollments WHERE user_id = userIdIN;                      
                                                                             
     -- Értékelések törlése                                                 
     DELETE FROM course_reviews WHERE user_id = userIdIN;                   
                                                                             
     -- Tanfolyamok megtartása, de oktató nélkül                           
     UPDATE courses SET instructor_id = NULL WHERE instructor_id = userIdIN;
                                                                             
     -- Felhasználó törlése                                                 
     DELETE FROM users WHERE id = userIdIN;                                 
                                                                             
     COMMIT;                                                                 
 END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCategories` ()   BEGIN
    SELECT * FROM categories ORDER BY name;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCourseMaterials` ()   BEGIN
    SELECT cm.*, c.title as course_title
    FROM course_materials cm
    LEFT JOIN courses c ON cm.course_id = c.id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCourseReviews` ()   BEGIN
    SELECT cr.*, u.name as reviewer_name, c.title as course_title
    FROM course_reviews cr
    LEFT JOIN users u ON cr.user_id = u.id
    LEFT JOIN courses c ON cr.course_id = c.id
    ORDER BY cr.created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCourses` ()   BEGIN
    SELECT c.*, u.name as instructor_name, cat.name as category_name
    FROM courses c
    LEFT JOIN users u ON c.instructor_id = u.id
    LEFT JOIN categories cat ON c.category_id = cat.id
    ORDER BY c.created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllCourseSessions` ()   BEGIN
    SELECT cs.*, c.title as course_title
    FROM course_sessions cs
    LEFT JOIN courses c ON cs.course_id = c.id
    ORDER BY cs.start_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllEnrollments` ()   BEGIN
    SELECT e.*, u.name as student_name, c.title as course_title
    FROM enrollments e
    LEFT JOIN users u ON e.user_id = u.id
    LEFT JOIN courses c ON e.course_id = c.id
    ORDER BY e.created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllMessages` ()   BEGIN
    SELECT m.*, u.name as sender_name, c.title as course_title
    FROM messages m
    LEFT JOIN users u ON m.sender_id = u.id
    LEFT JOIN courses c ON m.course_id = c.id
    ORDER BY m.created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllQuizQuestions` ()   BEGIN
    SELECT qq.*, q.title as quiz_title
    FROM quiz_questions qq
    LEFT JOIN quizzes q ON qq.quiz_id = q.id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllQuizResults` ()   BEGIN
    SELECT qr.*, u.name as student_name, q.title as quiz_title
    FROM quiz_results qr
    LEFT JOIN users u ON qr.user_id = u.id
    LEFT JOIN quizzes q ON qr.quiz_id = q.id
    ORDER BY qr.completed_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllQuizzes` ()   BEGIN
    SELECT q.*, c.title as course_title
    FROM quizzes q
    LEFT JOIN courses c ON q.course_id = c.id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getAllUsers` ()   BEGIN
    SELECT * FROM users ORDER BY created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCategoryById` (IN `categoryIdIN` INT)   BEGIN
    SELECT * FROM categories WHERE id = categoryIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseById` (IN `courseIdIN` INT)   BEGIN
    SELECT c.*, u.name as instructor_name, cat.name as category_name
    FROM courses c
    LEFT JOIN users u ON c.instructor_id = u.id
    LEFT JOIN categories cat ON c.category_id = cat.id
    WHERE c.id = courseIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseMaterialById` (IN `materialIdIN` INT)   BEGIN
    SELECT cm.*, c.title as course_title
    FROM course_materials cm
    LEFT JOIN courses c ON cm.course_id = c.id
    WHERE cm.id = materialIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseMaterialsByCourse` (IN `courseIdIN` INT)   BEGIN
    SELECT * FROM course_materials 
    WHERE course_id = courseIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseReviewById` (IN `reviewIdIN` INT)   BEGIN
    SELECT cr.*, u.name as reviewer_name, c.title as course_title
    FROM course_reviews cr
    LEFT JOIN users u ON cr.user_id = u.id
    LEFT JOIN courses c ON cr.course_id = c.id
    WHERE cr.id = reviewIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseReviewsByCourse` (IN `courseIdIN` INT)   BEGIN
    SELECT 
        cr.*,
        u.name  AS user_name,
        u.email AS user_email,
        u.profile_picture AS user_profile_picture
    FROM course_reviews cr
    LEFT JOIN users u ON cr.user_id = u.id
    WHERE cr.course_id = courseIdIN
    ORDER BY cr.created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseSessionById` (IN `sessionIdIN` INT)   BEGIN
    SELECT cs.*, c.title as course_title
    FROM course_sessions cs
    LEFT JOIN courses c ON cs.course_id = c.id
    WHERE cs.id = sessionIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getCourseSessionsByCourse` (IN `courseIdIN` INT)   BEGIN
    SELECT * FROM course_sessions 
    WHERE course_id = courseIdIN
    ORDER BY start_at;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getEnrollmentById` (IN `enrollmentIdIN` INT)   BEGIN
    SELECT e.*, u.name as student_name, c.title as course_title
    FROM enrollments e
    LEFT JOIN users u ON e.user_id = u.id
    LEFT JOIN courses c ON e.course_id = c.id
    WHERE e.id = enrollmentIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getEnrollmentsByUser` (IN `userIdIN` INT)   BEGIN
    SELECT e.*, c.title as course_title
    FROM enrollments e
    LEFT JOIN courses c ON e.course_id = c.id
    WHERE e.user_id = userIdIN
    ORDER BY e.created_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMessageById` (IN `messageIdIN` INT)   BEGIN
    SELECT m.*, u.name as sender_name, c.title as course_title
    FROM messages m
    LEFT JOIN users u ON m.sender_id = u.id
    LEFT JOIN courses c ON m.course_id = c.id
    WHERE m.id = messageIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMessagesByCourse` (IN `courseIdIN` INT)   BEGIN
    SELECT m.*, u.name as sender_name
    FROM messages m
    LEFT JOIN users u ON m.sender_id = u.id
    WHERE m.course_id = courseIdIN
    ORDER BY m.created_at ASC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getQuizById` (IN `quizIdIN` INT)   BEGIN
    SELECT q.*, c.title as course_title
    FROM quizzes q
    LEFT JOIN courses c ON q.course_id = c.id
    WHERE q.id = quizIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getQuizQuestionById` (IN `questionIdIN` INT)   BEGIN
    SELECT qq.*, q.title as quiz_title
    FROM quiz_questions qq
    LEFT JOIN quizzes q ON qq.quiz_id = q.id
    WHERE qq.id = questionIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getQuizQuestionsByQuiz` (IN `quizIdIN` INT)   BEGIN
    SELECT * FROM quiz_questions 
    WHERE quiz_id = quizIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getQuizResultById` (IN `resultIdIN` INT)   BEGIN
    SELECT qr.*, u.name as student_name, q.title as quiz_title
    FROM quiz_results qr
    LEFT JOIN users u ON qr.user_id = u.id
    LEFT JOIN quizzes q ON qr.quiz_id = q.id
    WHERE qr.id = resultIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getQuizResultsByUser` (IN `userIdIN` INT)   BEGIN
    SELECT qr.*, q.title as quiz_title
    FROM quiz_results qr
    LEFT JOIN quizzes q ON qr.quiz_id = q.id
    WHERE qr.user_id = userIdIN
    ORDER BY qr.completed_at DESC;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getQuizzesByCourse` (IN `courseIdIN` INT)   BEGIN
    SELECT * FROM quizzes 
    WHERE course_id = courseIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById` (IN `userIdIN` INT)   BEGIN
    SELECT * FROM users WHERE id = userIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCategory` (IN `categoryIdIN` INT, IN `nameIN` VARCHAR(255), IN `slugIN` VARCHAR(255))   BEGIN
    UPDATE categories 
    SET name = nameIN, slug = slugIN
    WHERE id = categoryIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCourse` (IN `courseIdIN` INT, IN `titleIN` VARCHAR(255), IN `descriptionIN` TEXT, IN `priceIN` INT, IN `instructorIdIN` INT, IN `categoryIdIN` INT, IN `maxParticipantsIN` INT)   BEGIN
    UPDATE courses 
    SET title = titleIN, 
        description = descriptionIN, 
        price = priceIN,
        instructor_id = instructorIdIN,
        category_id = categoryIdIN,
        max_participants = maxParticipantsIN
    WHERE id = courseIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCourseMaterial` (IN `materialIdIN` INT, IN `courseIdIN` INT, IN `titleIN` VARCHAR(255), IN `fileUrlIN` VARCHAR(255))   BEGIN
    UPDATE course_materials 
    SET course_id = courseIdIN, 
        title = titleIN, 
        file_url = fileUrlIN
    WHERE id = materialIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCourseReview` (IN `reviewIdIN` INT, IN `ratingIN` TINYINT, IN `commentIN` TEXT)   BEGIN
    UPDATE course_reviews 
    SET rating = ratingIN, 
        comment = commentIN
    WHERE id = reviewIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateCourseSession` (IN `sessionIdIN` INT, IN `courseIdIN` INT, IN `startAtIN` DATETIME, IN `endAtIN` DATETIME)   BEGIN
    UPDATE course_sessions 
    SET course_id = courseIdIN, 
        start_at = startAtIN, 
        end_at = endAtIN
    WHERE id = sessionIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateEnrollment` (IN `enrollmentIdIN` INT, IN `statusIN` ENUM('registered','canceled'))   BEGIN
    UPDATE enrollments 
    SET status = statusIN
    WHERE id = enrollmentIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateMessage` (IN `messageIdIN` INT, IN `contentIN` TEXT)   BEGIN
    UPDATE messages 
    SET content = contentIN
    WHERE id = messageIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateProfilePicture` (IN `userIdIN` INT, IN `pictureIN` VARCHAR(500))   BEGIN
    UPDATE users 
    SET profile_picture = pictureIN 
    WHERE id = userIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateQuiz` (IN `quizIdIN` INT, IN `courseIdIN` INT, IN `titleIN` VARCHAR(255))   BEGIN
    UPDATE quizzes 
    SET course_id = courseIdIN, 
        title = titleIN
    WHERE id = quizIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateQuizQuestion` (IN `questionIdIN` INT, IN `quizIdIN` INT, IN `questionIN` TEXT, IN `correctAnswerIN` VARCHAR(255))   BEGIN
    UPDATE quiz_questions 
    SET quiz_id = quizIdIN, 
        question = questionIN, 
        correct_answer = correctAnswerIN
    WHERE id = questionIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateQuizResult` (IN `resultIdIN` INT, IN `scoreIN` DECIMAL(5,2))   BEGIN
    UPDATE quiz_results 
    SET score = scoreIN
    WHERE id = resultIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUser` (IN `userIdIN` INT, IN `nameIN` VARCHAR(255), IN `emailIN` VARCHAR(255), IN `roleIN` ENUM('student','instructor','admin'))   BEGIN
    UPDATE users 
    SET 
        name = COALESCE(nameIN, name),
        email = COALESCE(emailIN, email),
        role = COALESCE(roleIN, role)
    WHERE id = userIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateUserPassword` (IN `userIdIN` INT, IN `passwordIN` VARCHAR(255))   BEGIN
    UPDATE users 
    SET password = passwordIN
    WHERE id = userIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `uploadCourseMaterial` (IN `p_course_id` INT, IN `p_title` VARCHAR(255), IN `p_file_url` VARCHAR(500), IN `p_uploaded_by` INT)   BEGIN
    INSERT INTO course_materials (
        course_id,
        title,
        file_url,
        uploaded_by,
        uploaded_at
    )
    VALUES (
        p_course_id,
        p_title,
        p_file_url,
        p_uploaded_by,
        CURRENT_TIMESTAMP
    );
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `categories`
--

CREATE TABLE `categories` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `slug` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `categories`
--

INSERT INTO `categories` (`id`, `name`, `slug`) VALUES
(1, 'Programozás', 'programozas'),
(2, 'Nyelvtanulás', 'nyelvtanulas'),
(3, 'Zene', 'zene'),
(4, 'Grafika', 'grafika'),
(5, 'Üzleti készségek', 'uzleti-keszegek'),
(6, 'Marketing', 'marketing'),
(7, 'Adattudomány', 'adattudomany'),
(8, 'Webfejlesztés', 'webfejlesztes'),
(9, 'Mobilalkalmazás fejlesztés', 'mobilalkalmazas-fejlesztes'),
(10, 'Mesterséges intelligencia', 'mesterseges-intelligencia'),
(11, 'Fotózás', 'fotozas'),
(12, 'Videószerkesztés', 'videoszerkesztes'),
(13, '3D modellezés', '3d-modellezes'),
(14, 'UI/UX Design', 'ui-ux-design'),
(15, 'Személyes fejlődés', 'szemelyes-fejlodes'),
(16, 'Egészség és fitness', 'egeszseg-es-fitness'),
(17, 'Főzés', 'fozes'),
(18, 'Matematika', 'matematika'),
(19, 'Fizika', 'fizika'),
(20, 'Kémia', 'kemia'),
(21, 'Történelem', 'tortenelem'),
(22, 'Irodalom', 'irodalom'),
(23, 'Pénzügyek', 'penzugyek'),
(24, 'Jog', 'jog'),
(25, 'Építészet', 'epiteszet'),
(26, 'Hangmérnöki ismeretek', 'hangmernoki-ismeretek'),
(27, 'Zongora', 'zongora'),
(28, 'Énekóra', 'enekora'),
(29, 'Német nyelv', 'nemet-nyelv'),
(30, 'Francia nyelv', 'francia-nyelv'),
(31, 'Spanyol nyelv', 'spanyol-nyelv'),
(32, 'Olasz nyelv', 'olasz-nyelv'),
(33, 'Kínai nyelv', 'kinai-nyelv'),
(34, 'Japán nyelv', 'japan-nyelv'),
(35, 'Backend', 'backend'),
(36, 'Általános', 'altalanos');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `courses`
--

CREATE TABLE `courses` (
  `id` int(11) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `description` text,
  `price` int(11) DEFAULT NULL,
  `instructor_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `max_participants` int(11) DEFAULT '20',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `header_image` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `courses`
--

INSERT INTO `courses` (`id`, `title`, `description`, `price`, `instructor_id`, `category_id`, `max_participants`, `created_at`, `start_date`, `end_date`, `header_image`) VALUES
(57, 'Full Stack Webfejlesztő képzés', 'HTML, CSS, JavaScript, backend alapok, adatbázis, záróprojekt', 249000, 79, 1, 100, '2026-03-02 09:13:49', '2026-03-19', '2026-10-31', '/SkillBook/api/Uploads/course_headers/course_header_57_1772442835571.png'),
(63, 'Junior Python Fejlesztő Program', 'Alapoktól haladó szintig, adatkezelés, API-k, projektmunka', 219000, 79, 1, 200, '2026-03-02 09:17:40', '2026-03-31', '2026-08-31', '/SkillBook/api/Uploads/course_headers/course_header_63_1772443066291.png'),
(65, 'Digitális Marketing Mesterkurzus', 'Social media, hirdetéskezelés, SEO, tartalommarketing', 189000, 79, 6, 300, '2026-03-02 09:46:14', '2026-05-15', '2026-08-15', '/SkillBook/api/Uploads/course_headers/course_header_65_1772445427224.png'),
(66, 'Vállalkozásindító Akadémia', 'Üzleti tervezés, marketing, pénzügy, márkaépítés, mentorálás', 279000, 79, 25, 350, '2026-03-02 09:48:09', '2026-09-01', '2027-01-04', '/SkillBook/api/Uploads/course_headers/course_header_66_1772445905562.png'),
(67, 'Grafikai és UI/UX Designer képzés', 'Figma, arculattervezés, webdesign, portfólióépítés', 229000, 79, 14, 500, '2026-03-02 09:49:22', '2026-05-20', '2026-11-20', '/SkillBook/api/Uploads/course_headers/course_header_67_1772445492635.avif'),
(68, 'Haladó Angol Kommunikációs Program', 'Beszédcentrikus, prezentáció, állásinterjú-felkészítés', 159000, 79, 2, 200, '2026-03-02 09:50:24', '2026-03-10', '2026-07-10', '/SkillBook/api/Uploads/course_headers/course_header_68_1772445555559.png'),
(69, 'Prezentációs és Kommunikációs Akadémia', 'Nyilvános beszéd, vita, érvelés, testbeszéd', 129000, 79, 36, 150, '2026-03-02 09:52:01', '2026-04-20', '2026-08-31', '/SkillBook/api/Uploads/course_headers/course_header_69_1772445683819.jpg'),
(70, 'Pénzügyi Tudatosság és Befektetés', 'Részvények, ETF-ek, költségvetés-tervezés', 169000, 79, 5, 400, '2026-03-02 09:53:29', '2026-09-10', '2026-12-10', '/SkillBook/api/Uploads/course_headers/course_header_70_1772445618852.png'),
(71, 'Tartalomkészítő és Személyes Márkaépítő Program', 'YouTube / TikTok stratégia, branding, monetizáció', 199000, 79, 6, 150, '2026-03-02 09:54:32', '2026-10-15', '2027-02-15', '/SkillBook/api/Uploads/course_headers/course_header_71_1772445860476.jpg'),
(72, 'Projektmenedzsment Alapképzés', 'Agilis módszertan, csapatvezetés, gyakorlati esettanulmányok', 179000, 79, 36, 100, '2026-03-02 09:55:32', '2026-09-30', '2027-02-28', '/SkillBook/api/Uploads/course_headers/course_header_72_1772445792661.jpg'),
(73, 'Grafikus képzés', 'Grafikus képzés (2 hónap) elérhető ár', 70000, 80, 4, 20, '2026-03-02 09:59:18', '2026-04-01', '2026-06-01', '/SkillBook/api/Uploads/course_headers/course_header_73_1772445558876.jpg');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `course_materials`
--

CREATE TABLE `course_materials` (
  `id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL,
  `uploaded_by` int(11) DEFAULT NULL,
  `uploaded_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `course_materials`
--

INSERT INTO `course_materials` (`id`, `course_id`, `title`, `file_url`, `uploaded_by`, `uploaded_at`) VALUES
(9, 73, 'Grafika levke 1.', '/SkillBook/api/Uploads/courses/73/2287baa2_graf.pdf', 80, '2026-03-02 10:05:29'),
(10, 73, 'graf lecke 2', '/SkillBook/api/Uploads/courses/73/236d8af9_4_1031_004_100930.pdf', 80, '2026-03-02 10:13:22'),
(11, 73, 'ez csak egy kep tanfolyam teszt', '/SkillBook/api/Uploads/courses/73/69d6af5f_uhq5zTcMPM3tOW_fbUz4PayDt_5pkEXdyXXoRWs3XOg.webp', 80, '2026-03-02 10:14:51');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `course_reviews`
--

CREATE TABLE `course_reviews` (
  `id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `rating` tinyint(4) NOT NULL,
  `comment` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `course_sessions`
--

CREATE TABLE `course_sessions` (
  `id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `start_at` datetime DEFAULT NULL,
  `end_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `enrollments`
--

CREATE TABLE `enrollments` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `session_id` int(11) DEFAULT NULL,
  `status` enum('registered','canceled') DEFAULT 'registered',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `enrollments`
--

INSERT INTO `enrollments` (`id`, `user_id`, `course_id`, `session_id`, `status`, `created_at`) VALUES
(5, 80, 67, NULL, 'registered', '2026-03-02 10:03:03'),
(6, 79, 73, NULL, 'registered', '2026-03-02 10:06:46'),
(7, 77, 73, NULL, 'registered', '2026-03-02 10:15:55');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `receiver_id` int(11) NOT NULL,
  `content` text NOT NULL,
  `sentAt` datetime DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `messages`
--

INSERT INTO `messages` (`id`, `sender_id`, `receiver_id`, `content`, `sentAt`) VALUES
(1, 79, 80, 'haloooooooo', '2026-02-17 10:06:41'),
(2, 80, 79, 'csa', '2026-02-17 10:06:57'),
(3, 79, 80, 'bttdyn', '2026-02-17 10:07:07'),
(4, 77, 80, 'uzenet teszt', '2026-02-17 10:08:46'),
(5, 80, 77, 'helo', '2026-02-17 10:09:03'),
(13, 80, 77, 'Teszt', '2026-02-20 11:06:26'),
(14, 80, 86, 'Teszt uzenet neki is', '2026-02-22 14:04:29'),
(15, 80, 79, 'asd', '2026-02-25 11:41:13'),
(16, 79, 80, 'asdffhfgh', '2026-02-25 11:41:23'),
(17, 80, 79, 'hello Ehsan', '2026-02-25 11:41:24'),
(18, 80, 79, 'azia reg', '2026-03-02 10:54:06');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `quizzes`
--

CREATE TABLE `quizzes` (
  `id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `quiz_questions`
--

CREATE TABLE `quiz_questions` (
  `id` int(11) NOT NULL,
  `quiz_id` int(11) DEFAULT NULL,
  `question` text,
  `correct_answer` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `quiz_results`
--

CREATE TABLE `quiz_results` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `quiz_id` int(11) NOT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  `completed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('student','instructor','admin') DEFAULT 'student',
  `profile_picture` varchar(500) DEFAULT NULL COMMENT 'Profilkép relatív URL-je',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `role`, `profile_picture`, `created_at`, `updated_at`) VALUES
(75, 'Pettermann Shaun', 'pettermann.shaun.pharell@szechenyi.hu', '$2a$12$kjZaGmzHY/Arbjns8oe.huyczpT6/yGrCjIz03sLPuUHs91f3h5I2', 'admin', NULL, '2026-02-09 09:05:37', '2026-02-17 13:11:24'),
(77, 'Bagoly Doni', 'bagoly.donat@szechenyi.hu', '$2a$12$v3neymlSSwJpnc1QLAaZNu4n.cIzz5wUfLx693DH4jivPQzqqqOlW', 'instructor', '/SkillBook/api/Uploads/profile_pictures/profile_77_53a47b6c-5c84-4f11-828e-8bece9bb39be.jpg', '2026-02-09 20:09:18', '2026-02-26 11:28:51'),
(79, 'Buzasi Regina', 'rbuzasi6@gmail.com', '$2a$12$z.5TN59YX6Tm811OXUMZieMFLaKrS2aqHB8t6hl53bP7D2sIk25fC', 'admin', '/SkillBook/api/Uploads/profile_pictures/profile_79_5d571845-ed03-4d24-8d76-13bec86c1a07.png', '2026-02-10 09:27:39', '2026-02-17 13:11:21'),
(80, 'Test Profile', 'ikr3erpeti@gmail.com', '$2a$12$3lU8c098AZMhVZnLaEAqFe9cAeGaxc9H896zZV2eywJM2aOdCAEVe', 'admin', '/SkillBook/api/Uploads/profile_pictures/profile_80_23c9183a-50e1-49db-a09d-889c81a20920.webp', '2026-02-14 09:07:59', '2026-03-02 10:45:37'),
(86, 'Bagoly Patrik', 'ikr4erpeti@gmail.com', '$2a$12$ubO..4imUjZGhKFTIV2abOwB86.XdjFlBji/a3X8LS/T5eU6VlK6C', 'instructor', '/SkillBook/api/Uploads/profile_pictures/profile_86_a2ff65ae-85cf-4fcf-b958-7fa94e0d4055.jpg', '2026-02-16 08:47:38', '2026-02-26 11:02:27');

--
-- Indexek a kiírt táblákhoz
--

--
-- A tábla indexei `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- A tábla indexei `courses`
--
ALTER TABLE `courses`
  ADD PRIMARY KEY (`id`),
  ADD KEY `instructor_id` (`instructor_id`),
  ADD KEY `category_id` (`category_id`);

--
-- A tábla indexei `course_materials`
--
ALTER TABLE `course_materials`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_course_materials_uploaded_by` (`uploaded_by`),
  ADD KEY `course_materials_ibfk_1` (`course_id`);

--
-- A tábla indexei `course_reviews`
--
ALTER TABLE `course_reviews`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_review` (`course_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- A tábla indexei `course_sessions`
--
ALTER TABLE `course_sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `course_sessions_ibfk_1` (`course_id`);

--
-- A tábla indexei `enrollments`
--
ALTER TABLE `enrollments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `session_id` (`session_id`),
  ADD KEY `enrollments_ibfk_2` (`course_id`);

--
-- A tábla indexei `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sender_id` (`sender_id`),
  ADD KEY `receiver_id` (`receiver_id`);

--
-- A tábla indexei `quizzes`
--
ALTER TABLE `quizzes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `quizzes_ibfk_1` (`course_id`);

--
-- A tábla indexei `quiz_questions`
--
ALTER TABLE `quiz_questions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `quiz_id` (`quiz_id`);

--
-- A tábla indexei `quiz_results`
--
ALTER TABLE `quiz_results`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `quiz_id` (`quiz_id`);

--
-- A tábla indexei `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `name_UNIQUE` (`name`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT a táblához `courses`
--
ALTER TABLE `courses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=74;

--
-- AUTO_INCREMENT a táblához `course_materials`
--
ALTER TABLE `course_materials`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT a táblához `course_reviews`
--
ALTER TABLE `course_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT a táblához `course_sessions`
--
ALTER TABLE `course_sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `enrollments`
--
ALTER TABLE `enrollments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT a táblához `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT a táblához `quizzes`
--
ALTER TABLE `quizzes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `quiz_questions`
--
ALTER TABLE `quiz_questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `quiz_results`
--
ALTER TABLE `quiz_results`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=90;

--
-- Megkötések a kiírt táblákhoz
--

--
-- Megkötések a táblához `courses`
--
ALTER TABLE `courses`
  ADD CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`instructor_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `courses_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);

--
-- Megkötések a táblához `course_materials`
--
ALTER TABLE `course_materials`
  ADD CONSTRAINT `course_materials_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_course_materials_uploaded_by` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`id`) ON DELETE SET NULL;

--
-- Megkötések a táblához `course_reviews`
--
ALTER TABLE `course_reviews`
  ADD CONSTRAINT `course_reviews_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `course_reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Megkötések a táblához `course_sessions`
--
ALTER TABLE `course_sessions`
  ADD CONSTRAINT `course_sessions_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `enrollments`
--
ALTER TABLE `enrollments`
  ADD CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `enrollments_ibfk_3` FOREIGN KEY (`session_id`) REFERENCES `course_sessions` (`id`);

--
-- Megkötések a táblához `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`);

--
-- Megkötések a táblához `quizzes`
--
ALTER TABLE `quizzes`
  ADD CONSTRAINT `quizzes_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE;

--
-- Megkötések a táblához `quiz_questions`
--
ALTER TABLE `quiz_questions`
  ADD CONSTRAINT `quiz_questions_ibfk_1` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`);

--
-- Megkötések a táblához `quiz_results`
--
ALTER TABLE `quiz_results`
  ADD CONSTRAINT `quiz_results_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `quiz_results_ibfk_2` FOREIGN KEY (`quiz_id`) REFERENCES `quizzes` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
