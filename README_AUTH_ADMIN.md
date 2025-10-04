# EV Charging Mobile - JWT Authentication & Admin System

This document describes the JWT authentication system and admin-only onboarding implementation for the EV Charging Mobile Android application.

## Overview

The application now supports **JWT authentication** with **three distinct roles** and **admin-only onboarding**:

- **Backofficer (ADMIN)**: Can create/update/deactivate EV Owners and Station Operators
- **Station Operator**: Logs in with username/password created by admin (no local SQLite storage)
- **EV Owner**: Logs in with NIC/username created by admin; profile is cached in local SQLite

**Important**: There is **NO self-registration** in the app. All user accounts must be created by the Backofficer.

## Configuration

### 1. API Base URL

Update the `BASE_URL` in `app/src/main/java/com/example/evchargingmobile/common/Network.kt`:

```kotlin
const val BASE_URL = "https://your-api-domain.com/api/"
```

### 2. API Endpoints

The app expects the following API endpoints:

#### Authentication
- `POST /api/auth/login` - Login with JWT response

#### Admin Endpoints (Backofficer only)
- `GET /api/admin/owners` - List all owners
- `GET /api/admin/owners/{nic}` - Get specific owner
- `POST /api/admin/owners` - Create new owner
- `PUT /api/admin/owners/{nic}` - Update owner
- `PUT /api/admin/owners/{nic}/deactivate` - Deactivate owner
- `GET /api/admin/operators` - List all operators
- `GET /api/admin/operators/{id}` - Get specific operator
- `POST /api/admin/operators` - Create new operator
- `PUT /api/admin/operators/{id}` - Update operator
- `PUT /api/admin/operators/{id}/deactivate` - Deactivate operator

#### User Endpoints (Owner only)
- `GET /api/user/owners/{nic}` - Get owner profile
- `PUT /api/user/owners/{nic}` - Update owner profile
- `PUT /api/user/owners/{nic}/deactivate` - Deactivate owner account

## API Request/Response Formats

### Login Request
```json
{
  "usernameOrEmail": "admin@example.com",
  "password": "password123",
  "role": "BACKOFFICER"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "BACKOFFICER",
  "nic": "1234567890",
  "backofficerId": "ADM001",
  "operatorId": null
}
```

### Owner DTO
```json
{
  "nic": "1234567890",
  "fullName": "John Doe",
  "email": "john@example.com",
  "phone": "+1234567890",
  "status": "ACTIVE"
}
```

### Operator DTO
```json
{
  "id": "OP001",
  "fullName": "Jane Smith",
  "email": "jane@example.com",
  "phone": "+1234567890",
  "status": "ACTIVE"
}
```

## Authentication Flow

### 1. Login Process
1. User selects role (Backofficer, Owner, Operator)
2. Enters username/email and password
3. App sends login request to `/api/auth/login`
4. Server validates credentials and returns JWT token
5. App stores token, role, and owner NIC (if applicable) in encrypted storage
6. App navigates to appropriate dashboard based on role

### 2. Token Management
- JWT tokens are stored in `EncryptedSharedPreferences`
- Token expiration is checked on app startup and before API calls
- If token is expired, user is automatically logged out
- All API calls include `Authorization: Bearer <token>` header

### 3. Role-Based Navigation
- **BACKOFFICER** → Admin Dashboard (manage owners/operators)
- **OWNER** → Profile Activity (view/edit profile, deactivate account)
- **OPERATOR** → Operator Home (placeholder for future functionality)

## Admin Workflows

### Creating an Owner
1. Backofficer logs in and goes to "Manage Owners"
2. Taps FAB to create new owner
3. Fills in NIC, full name, email, phone, password
4. App sends `POST /api/admin/owners` with JWT token
5. Owner is created on server

### Creating an Operator
1. Backofficer logs in and goes to "Manage Operators"
2. Taps FAB to create new operator
3. Fills in full name, email, phone, username, password
4. App sends `POST /api/admin/operators` with JWT token
5. Operator is created on server

### Managing Users
- **Edit**: Update user details (name, email, phone)
- **Deactivate**: Mark user as inactive (prevents login)

## Owner Profile Management

### Local Caching
- Only the currently logged-in Owner's profile is cached in local SQLite
- Profile is fetched from server on first login and cached locally
- Local updates are synced with server on save

### Profile Operations
- **View**: Display cached profile data
- **Edit**: Update profile (name, email, phone) - syncs with server
- **Deactivate**: Deactivate own account - updates both server and local status

## Database Schema

### Local SQLite (Owner Cache Only)
```sql
CREATE TABLE users (
    nic TEXT PRIMARY KEY,
    full_name TEXT NOT NULL,
    email TEXT,
    phone TEXT,
    hashed_password TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

**Note**: Admin and Operator accounts are **NOT** stored in local SQLite.

## Security Features

### 1. Encrypted Storage
- Uses `EncryptedSharedPreferences` for storing JWT tokens
- Falls back to regular SharedPreferences if encryption fails

### 2. JWT Validation
- Token expiration is checked before API calls
- Automatic logout on token expiry
- Role-based access control

### 3. Input Validation
- NIC: minimum 10 characters
- Full name: minimum 3 characters
- Email: valid format (optional)
- Phone: digits, +, -, spaces only (optional)
- Password: minimum 6 characters

## Error Handling

### Network Errors
- Connection timeouts
- Server errors (4xx, 5xx)
- JSON parsing errors

### Authentication Errors
- Invalid credentials
- Token expiration
- Insufficient permissions

### Validation Errors
- Required field validation
- Format validation
- Business rule validation

## Testing

### Manual Testing Checklist
1. **Login Flow**
   - [ ] Backofficer login works
   - [ ] Owner login works and caches profile
   - [ ] Operator login works
   - [ ] Invalid credentials show error
   - [ ] Token expiry triggers logout

2. **Admin Functions**
   - [ ] Can create owners
   - [ ] Can create operators
   - [ ] Can edit users
   - [ ] Can deactivate users
   - [ ] Lists show correct counts

3. **Owner Profile**
   - [ ] Profile loads from cache
   - [ ] Can edit profile
   - [ ] Changes sync to server
   - [ ] Can deactivate account
   - [ ] Deactivated account shows banner

4. **Security**
   - [ ] JWT tokens are encrypted
   - [ ] Expired tokens trigger logout
   - [ ] API calls include auth headers

## Troubleshooting

### Common Issues

1. **"Network error" on login**
   - Check `BASE_URL` in `Network.kt`
   - Verify API server is running
   - Check network connectivity

2. **"Failed to parse login response"**
   - Verify API response format matches expected DTOs
   - Check JSON structure

3. **"Token expired" immediately**
   - Check JWT token format
   - Verify `exp` claim is valid timestamp

4. **Profile not loading for Owner**
   - Check if owner NIC is returned in login response
   - Verify `/api/user/owners/{nic}` endpoint

### Debug Tips

1. Enable network logging in `Network.kt`
2. Check `SessionStore` for stored values
3. Verify JWT token claims using online JWT decoder
4. Test API endpoints with Postman/curl

## Future Enhancements

1. **Refresh Token Support**: Implement token refresh mechanism
2. **Biometric Authentication**: Add fingerprint/face unlock
3. **Offline Mode**: Cache more data for offline access
4. **Push Notifications**: Notify users of account changes
5. **Audit Logging**: Track admin actions

## Support

For technical support or questions about the authentication system, please refer to the development team or create an issue in the project repository.
