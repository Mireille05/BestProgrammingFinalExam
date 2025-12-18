# Help Flow - Community Issue Tracking System

## 📋 Project Overview

**Help Flow** is a Java-based desktop application built with Swing that allows users to post issues, interact with the community, and enables administrators to manage the platform. It's a community-driven support system where users can share problems, comment on issues, like posts, and receive help from others.

---

## ✨ Features

### 👤 User Features

- **User Registration & Login** - Secure authentication system
- **Post Issues** - Share problems/questions with the community
- **Comment System** - Engage in discussions on issues
- **Like System** - Show appreciation for helpful posts
- **User Profiles** - View and edit personal information
- **Search Functionality** - Find users and issues quickly
- **Issue Management** - Track your own posted issues
- **Report System** - Report inappropriate content or users

### 👨‍💼 Admin Features

- **Admin Dashboard** - Comprehensive administrative interface
- **User Management** - Create, block, unblock, and delete users
- **Issue Moderation** - Delete inappropriate issues
- **Report Management** - Review and handle user reports
- **Notice System** - Post announcements to all users
- **Statistics Dashboard** - View system-wide metrics
- **Audit Logs** - Track all administrative actions

---

## 🛠️ Technologies Used

- **Language:** Java
- **GUI Framework:** Swing (javax.swing)
- **Database:** PostgreSQL
- **JDBC Driver:** PostgreSQL JDBC Driver (org.postgresql.Driver)
- **Architecture:** MVC (Model-View-Controller)
- **Build System:** Java Projects (VS Code)

---

## 📁 Project Structure

```
BestProgrammingFinal/
│
├── src/
│   ├── Controller/
│   │   ├── UserController.java       # User authentication & management
│   │   ├── AdminController.java      # Admin operations
│   │   ├── IssueController.java      # Issue CRUD operations
│   │   └── ReportController.java     # Report handling
│   │
│   ├── model/
│   │   ├── User.java                 # User entity
│   │   ├── Issue.java                # Issue entity
│   │   ├── Comment.java              # Comment entity
│   │   ├── Like.java                 # Like entity
│   │   ├── Report.java               # Report entity
│   │   ├── Notice.java               # Notice entity
│   │   └── AdminStats.java           # Statistics model
│   │
│   ├── View/
│   │   ├── Main.java                 # Application entry point
│   │   ├── LoginView.java            # Login screen
│   │   ├── RegisterView.java         # Registration screen
│   │   ├── HomePageView.java         # Main user dashboard
│   │   ├── PostIssueView.java        # Create new issues
│   │   ├── IssueDetailView.java      # View issue details
│   │   ├── ProfileView.java          # User profile page
│   │   ├── EditProfilePageView.java  # Edit profile page
│   │   ├── SearchResultsView.java    # Search results display
│   │   └── AdminDashboardView.java   # Admin control panel
│   │
│   └── util/
│       └── DbConnection.java         # Database connection utility
│
├── lib/                              # External dependencies
├── bin/                              # Compiled output files
└── README.md                        # This file

```

---
