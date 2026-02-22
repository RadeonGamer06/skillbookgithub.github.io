package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.security.CorsFilter;
import com.mycompany.vizsgaremek.security.JwtAuthFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        resources.add(UsersController.class);
        resources.add(AuthController.class);
        resources.add(CoursesController.class);
        resources.add(ContactController.class);
        resources.add(CategoriesController.class);   // getAllCategories (public)
        resources.add(MessagesController.class);      // chat (user-to-user)
        resources.add(ReviewsController.class);       // user review CRUD
        resources.add(UploadsController.class);       // képek kiszolgálása

        resources.add(AdminUsersController.class);       // GET getAllUsers, PUT setRole/updateUser, DELETE deleteUser
        resources.add(EnrollmentsController.class);      // beiratkozások CRUD
        resources.add(CourseSessionsController.class);   // tanfolyam időpontok CRUD
        resources.add(CourseMaterialsController.class);  // tananyagok (list + delete)
        resources.add(QuizzesController.class);          // kvízek + kérdések + eredmények CRUD
        resources.add(AdminReviewsController.class);     // getAllReviews + adminUpdateReview
        resources.add(AdminMessagesController.class);    // getAllMessages + deleteMessage
        resources.add(JwtAuthFilter.class);
        resources.add(CorsFilter.class);

        return resources;
    }
}