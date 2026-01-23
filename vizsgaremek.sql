-- phpMyAdmin SQL Dump
-- version 5.1.2
-- https://www.phpmyadmin.net/
--
-- Gép: localhost:3306
-- Létrehozás ideje: 2026. Jan 23. 08:41
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

CREATE DEFINER=`root`@`localhost` PROCEDURE `createUser` (IN `nameIN` VARCHAR(255), IN `emailIN` VARCHAR(255), IN `passwordIN` VARCHAR(255), IN `roleIN` ENUM('student','instructor','admin'))   BEGIN
    INSERT INTO users (name, email, password, role)
    VALUES (nameIN, emailIN, passwordIN, roleIN);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCategory` (IN `categoryIdIN` INT)   BEGIN
    DELETE FROM categories WHERE id = categoryIdIN;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteCourse` (IN `courseIdIN` INT)   BEGIN
    DELETE FROM courses WHERE id = courseIdIN;
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
    DELETE FROM users WHERE id = userIdIN;
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
    SELECT cr.*, u.name as reviewer_name
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
    SET name = nameIN, email = emailIN, role = roleIN
    WHERE id = userIdIN;
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
(34, 'Japán nyelv', 'japan-nyelv');

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
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `courses`
--

INSERT INTO `courses` (`id`, `title`, `description`, `price`, `instructor_id`, `category_id`, `max_participants`, `created_at`) VALUES
(1, 'Python kezdőknek', 'Alap Python tanfolyam', 30000, 3, 1, 15, '2025-11-25 18:16:15'),
(2, 'Angol középhaladó', 'Beszédcentrikus angol óra', 25000, 3, 2, 10, '2025-11-25 18:16:15'),
(3, 'Gitár alapok', 'Gitártanulás nulláról', 20000, 3, 3, 8, '2025-11-25 18:16:15'),
(4, 'Photoshop mesterkurzus', 'Képszerkesztés profiknak', 35000, 3, 4, 12, '2025-11-25 18:16:15');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `course_materials`
--

CREATE TABLE `course_materials` (
  `id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `file_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `course_materials`
--

INSERT INTO `course_materials` (`id`, `course_id`, `title`, `file_url`) VALUES
(1, 1, 'Python jegyzet', 'materials/python.pdf'),
(2, 2, 'Angol szólista', 'materials/angol.docx');

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

--
-- A tábla adatainak kiíratása `course_sessions`
--

INSERT INTO `course_sessions` (`id`, `course_id`, `start_at`, `end_at`) VALUES
(1, 1, '2025-03-15 10:00:00', '2025-03-15 12:00:00'),
(2, 1, '2025-03-22 10:00:00', '2025-03-22 12:00:00'),
(3, 2, '2025-03-16 16:00:00', '2025-03-16 18:00:00'),
(4, 3, '2025-03-17 14:00:00', '2025-03-17 16:00:00');

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
(1, 1, 1, 1, 'registered', '2025-11-25 18:16:15'),
(2, 2, 1, 1, 'registered', '2025-11-25 18:16:15'),
(3, 1, 2, 3, 'registered', '2025-11-25 18:16:15');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `sender_id` int(11) DEFAULT NULL,
  `content` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `messages`
--

INSERT INTO `messages` (`id`, `course_id`, `sender_id`, `content`, `created_at`) VALUES
(1, 1, 1, 'Sziasztok! Hol lesz az óra?', '2025-11-25 18:16:15'),
(2, 1, 3, 'Online, Zoom linket küldök.', '2025-11-25 18:16:15'),
(3, 2, 2, 'Kell-e tankönyv a kurzushoz?', '2025-11-25 18:16:15');

-- --------------------------------------------------------

--
-- Tábla szerkezet ehhez a táblához `quizzes`
--

CREATE TABLE `quizzes` (
  `id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `quizzes`
--

INSERT INTO `quizzes` (`id`, `course_id`, `title`) VALUES
(1, 1, 'Python alapok kvíz'),
(2, 2, 'Angol igeidők teszt');

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

--
-- A tábla adatainak kiíratása `quiz_questions`
--

INSERT INTO `quiz_questions` (`id`, `quiz_id`, `question`, `correct_answer`) VALUES
(1, 1, 'Mi a Python változó típusa?', 'dynamic'),
(2, 1, 'Mi a print() feladata?', 'kiírás'),
(3, 2, 'Melyik a past perfect?', 'had + V3');

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
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- A tábla adatainak kiíratása `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password`, `role`, `created_at`) VALUES
(1, 'Kiss Péter', 'peter69@example.com', 'hashed_pw_1', 'student', '2025-11-25 18:16:15'),
(2, 'Nagy Anna', 'anna@example.com', 'hashed_pw_2', 'student', '2025-11-25 18:16:15'),
(3, 'Tóth Béla', 'bela@example.com', 'hashed_pw_3', 'instructor', '2025-11-25 18:16:15'),
(6, 'bagoly donat', 'ikr3erpeti@gmail.com', '$2a$12$VQsr0HKlpdse5sUz08aXUuvulTn90ZOzAAXhI5ijClmnH2e1R6kCy', 'instructor', '2025-12-09 12:52:51'),
(7, 'user 123', 'ikr6erpeti@gmail.com', '$2a$12$acIv7qXxIKHJzOSIhnPvK.ImFP.MT5I6lwOoYIGQH1Uqe2Vy2K3EK', 'admin', '2025-12-16 08:45:40'),
(11, 'user dömötör', 'ikr8erpeti@gmail.com', '$2a$12$eBwNW40n/aMtFxh5UGEz8evg0yP7aiSujNrwNM7ThEBHQR6fRS9B.', 'student', '2025-12-16 09:36:35'),
(16, 'userdomotor dömötör', 'ikr9erpeti@gmail.com', '$2a$12$WDr4l89NeULix8OOi1RrL.D.DTHWJO2rcynQmjnxAK9cc09VkU53u', 'student', '2025-12-16 10:12:49');

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
  ADD KEY `course_id` (`course_id`);

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
  ADD KEY `course_id` (`course_id`);

--
-- A tábla indexei `enrollments`
--
ALTER TABLE `enrollments`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `course_id` (`course_id`),
  ADD KEY `session_id` (`session_id`);

--
-- A tábla indexei `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `course_id` (`course_id`),
  ADD KEY `sender_id` (`sender_id`);

--
-- A tábla indexei `quizzes`
--
ALTER TABLE `quizzes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `course_id` (`course_id`);

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
  ADD UNIQUE KEY `email` (`email`);

--
-- A kiírt táblák AUTO_INCREMENT értéke
--

--
-- AUTO_INCREMENT a táblához `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=35;

--
-- AUTO_INCREMENT a táblához `courses`
--
ALTER TABLE `courses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT a táblához `course_materials`
--
ALTER TABLE `course_materials`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT a táblához `course_reviews`
--
ALTER TABLE `course_reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `course_sessions`
--
ALTER TABLE `course_sessions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT a táblához `enrollments`
--
ALTER TABLE `enrollments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT a táblához `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT a táblához `quizzes`
--
ALTER TABLE `quizzes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT a táblához `quiz_questions`
--
ALTER TABLE `quiz_questions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT a táblához `quiz_results`
--
ALTER TABLE `quiz_results`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT a táblához `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

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
  ADD CONSTRAINT `course_materials_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

--
-- Megkötések a táblához `course_reviews`
--
ALTER TABLE `course_reviews`
  ADD CONSTRAINT `course_reviews_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  ADD CONSTRAINT `course_reviews_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Megkötések a táblához `course_sessions`
--
ALTER TABLE `course_sessions`
  ADD CONSTRAINT `course_sessions_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

--
-- Megkötések a táblához `enrollments`
--
ALTER TABLE `enrollments`
  ADD CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  ADD CONSTRAINT `enrollments_ibfk_3` FOREIGN KEY (`session_id`) REFERENCES `course_sessions` (`id`);

--
-- Megkötések a táblához `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  ADD CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`);

--
-- Megkötések a táblához `quizzes`
--
ALTER TABLE `quizzes`
  ADD CONSTRAINT `quizzes_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`);

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
