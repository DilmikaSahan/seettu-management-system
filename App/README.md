# Seettu Management System

A comprehensive system for managing Seettu (rotating savings and credit association) groups with providers and subscribers.

## Features

### For Providers:
- **Create Seettu Packages**: Define packages with name, description, and value
- **Create Seettu Groups**: Set up groups with packages, monthly amounts, duration, and members
- **Member Management**: Search and add subscribers by name, phone, or ID
- **Payment Tracking**: Mark monthly payments as received and send notifications
- **Group Management**: Start groups and monitor progress
- **Add Subscribers**: Register new subscribers to the system

### For Subscribers:
- **Dashboard**: View all joined groups and their status
- **Notifications**: Receive payment confirmations and group updates
- **Group Progress**: Track group progress and package receive dates

## System Architecture

### Backend (Spring Boot)
- **Entities**: User, SeettuPackage, SeettuGroup, Member, Payment, Notification
- **Controllers**: Authentication, Seettu operations
- **Services**: Business logic for groups, payments, notifications
- **Security**: JWT-based authentication

### Frontend (Angular)
- **Provider Dashboard**: Complete management interface
- **Subscriber Dashboard**: Member view and notifications
- **Responsive Design**: Works on desktop and mobile

## How It Works

### Example Workflow:

1. **Provider creates a package**:
   - Package Name: "Electronics Bundle"
   - Value: $1200
   - Description: "Smartphone + Accessories"

2. **Provider creates a group**:
   - Group Name: "Tech Group 2024"
   - Package: Electronics Bundle
   - Monthly Amount: $100
   - Duration: 12 months
   - Start Date: January 1, 2024

3. **Provider adds members**:
   - Search subscribers by name/phone/ID
   - Assign order numbers (1-12)
   - Member with order #4 receives package in month 4

4. **Provider starts the group**:
   - All members get notifications
   - Payment tracking begins

5. **Monthly payment management**:
   - Provider marks payments as received
   - Members get SMS and app notifications
   - System tracks who paid each month

6. **Package distribution**:
   - Each month, one member receives the package
   - Based on their assigned order number

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Packages
- `POST /api/seettu/packages` - Create package
- `GET /api/seettu/packages` - Get provider's packages
- `DELETE /api/seettu/packages/{id}` - Delete package

### Groups
- `POST /api/seettu/groups` - Create group
- `GET /api/seettu/groups` - Get provider's groups
- `GET /api/seettu/groups/{id}` - Get group details
- `POST /api/seettu/groups/{id}/start` - Start group

### Payments
- `POST /api/seettu/payments/{id}/mark-paid` - Mark payment as paid

### Subscribers
- `POST /api/seettu/subscribers` - Add subscriber
- `GET /api/seettu/subscribers/search` - Search subscribers
- `GET /api/seettu/my-groups` - Get subscriber's groups

### Notifications
- `GET /api/seettu/notifications` - Get user notifications
- `POST /api/seettu/notifications/{id}/mark-read` - Mark as read

## Setup Instructions

### Backend Setup:
1. Navigate to `seettu-backend/backend`
2. Run `mvn spring-boot:run`
3. Server starts on `http://localhost:8080`

### Frontend Setup:
1. Navigate to `seettu-frontend`
2. Run `npm install`
3. Run `ng serve`
4. App available on `http://localhost:4200`

### Database:
- Uses H2 in-memory database for development
- Tables auto-created on startup
- Can be configured for MySQL/PostgreSQL in production

## User Roles

### PROVIDER
- Can create packages and groups
- Manage subscribers
- Track payments
- Send notifications

### SUBSCRIBER
- View joined groups
- Receive notifications
- Track group progress

## Key Features

### Payment Tracking
- Monthly payment grid showing all members
- One-click payment confirmation
- Automatic SMS and app notifications

### Member Order System
- Each member gets a unique order number
- Determines when they receive the package
- Prevents conflicts and ensures fairness

### Notification System
- Real-time notifications for payments
- SMS integration (configurable)
- In-app notification center

### Search Functionality
- Find subscribers by name, phone, or custom ID
- Quick member addition to groups

## Technology Stack

### Backend:
- Spring Boot 3.x
- Spring Security with JWT
- JPA/Hibernate
- H2/MySQL Database
- Maven

### Frontend:
- Angular 18
- Angular Material
- TypeScript
- RxJS
- Responsive CSS

## Future Enhancements

- Real SMS integration (Twilio/AWS SNS)
- Email notifications
- Payment gateway integration
- Advanced reporting and analytics
- Mobile app (React Native/Flutter)
- Multi-language support

## Security Features

- JWT-based authentication
- Role-based access control
- Password encryption
- CORS configuration
- Input validation

This system provides a complete solution for managing Seettu groups with automated payment tracking, notifications, and member management.