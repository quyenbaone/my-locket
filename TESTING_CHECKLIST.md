# MyLocket Supabase Migration Testing Checklist

## Pre-Testing Setup

### 1. Database Schema Deployment
- [ ] Execute `supabase_schema.sql` in Supabase SQL Editor
- [ ] Verify all tables are created: `users`, `posts`, `friends`
- [ ] Verify storage bucket `images` is created
- [ ] Check Row Level Security policies are active
- [ ] Verify triggers and functions are created

### 2. Configuration Verification
- [ ] Confirm Supabase URL and anon key are correctly set in `SupabaseConfig.kt`
- [ ] Verify app builds without Firebase dependencies
- [ ] Check all imports are updated to use Supabase services

## Authentication Testing

### User Registration
- [ ] Test email/password registration
- [ ] Verify user is created in `auth.users` table
- [ ] Verify user profile is created in `public.users` table
- [ ] Test with invalid email format
- [ ] Test with weak password
- [ ] Test with existing email

### User Login
- [ ] Test login with valid credentials
- [ ] Test login with invalid email
- [ ] Test login with wrong password
- [ ] Verify user session is maintained
- [ ] Test automatic login on app restart

### Password Reset
- [ ] Test password reset email sending
- [ ] Verify reset email is received
- [ ] Test password reset flow (if implemented)

### Profile Management
- [ ] Test display name update
- [ ] Verify name changes are reflected in UI
- [ ] Test profile photo update (if implemented)

### Logout
- [ ] Test user logout
- [ ] Verify user is redirected to welcome screen
- [ ] Verify session is cleared

## Database Operations Testing

### User Management
- [ ] Test user creation on registration
- [ ] Test user data retrieval
- [ ] Test user list display
- [ ] Verify real-time user updates

### Posts Management
- [ ] Test post creation with text content
- [ ] Test post creation with image
- [ ] Test post retrieval for specific user
- [ ] Test posts filtering by `toWho` array
- [ ] Verify posts are displayed in chronological order
- [ ] Test real-time post updates

### Friends Management
- [ ] Test friend request sending
- [ ] Test friend request receiving
- [ ] Test friend request acceptance
- [ ] Test friend request rejection/deletion
- [ ] Test friend status updates (SENT, RECEIVED, FRIENDS)
- [ ] Verify bidirectional friend relationships
- [ ] Test real-time friend updates

## Storage Testing

### Image Upload
- [ ] Test image capture from camera
- [ ] Test image selection from gallery
- [ ] Test image upload to Supabase Storage
- [ ] Verify image URL generation
- [ ] Test image display in posts
- [ ] Test upload error handling

### Image Management
- [ ] Test image deletion (if implemented)
- [ ] Test image access permissions
- [ ] Verify image URLs are publicly accessible

## Real-time Functionality Testing

### Real-time Updates
- [ ] Test real-time post updates across multiple devices
- [ ] Test real-time friend status updates
- [ ] Test real-time user updates
- [ ] Verify connection handling
- [ ] Test reconnection after network loss

## UI/UX Testing

### Navigation
- [ ] Test navigation between screens
- [ ] Verify authentication state routing
- [ ] Test back button functionality
- [ ] Test deep linking (if implemented)

### User Interface
- [ ] Test all forms and inputs
- [ ] Verify error messages are displayed
- [ ] Test loading states
- [ ] Test empty states
- [ ] Verify responsive design

### Bottom Sheets
- [ ] Test profile bottom sheet functionality
- [ ] Test friend management bottom sheet
- [ ] Test name change bottom sheet
- [ ] Test email change bottom sheet (if functional)

## Error Handling Testing

### Network Errors
- [ ] Test app behavior with no internet connection
- [ ] Test app behavior with slow internet
- [ ] Test timeout handling
- [ ] Verify error messages are user-friendly

### Authentication Errors
- [ ] Test invalid credentials error handling
- [ ] Test network errors during auth
- [ ] Test session expiration handling

### Database Errors
- [ ] Test database connection errors
- [ ] Test permission errors
- [ ] Test data validation errors

### Storage Errors
- [ ] Test upload failures
- [ ] Test file size limit errors
- [ ] Test unsupported file format errors

## Performance Testing

### App Performance
- [ ] Test app startup time
- [ ] Test screen transition smoothness
- [ ] Test memory usage
- [ ] Test battery consumption

### Database Performance
- [ ] Test query response times
- [ ] Test large dataset handling
- [ ] Test concurrent user operations
- [ ] Monitor real-time subscription performance

### Storage Performance
- [ ] Test image upload speed
- [ ] Test image loading speed
- [ ] Test multiple concurrent uploads

## Security Testing

### Authentication Security
- [ ] Verify passwords are not stored in plain text
- [ ] Test session security
- [ ] Verify JWT token handling

### Database Security
- [ ] Test Row Level Security policies
- [ ] Verify users can only access their own data
- [ ] Test unauthorized access attempts

### Storage Security
- [ ] Test file access permissions
- [ ] Verify users can only access appropriate files

## Edge Cases Testing

### Data Edge Cases
- [ ] Test with empty strings
- [ ] Test with very long text content
- [ ] Test with special characters
- [ ] Test with emoji content
- [ ] Test with large friend lists

### User Behavior Edge Cases
- [ ] Test rapid button clicking
- [ ] Test simultaneous operations
- [ ] Test app backgrounding/foregrounding
- [ ] Test device rotation

## Regression Testing

### Core Functionality
- [ ] Verify all original features still work
- [ ] Test feature combinations
- [ ] Verify data integrity
- [ ] Test user workflows end-to-end

## Post-Migration Verification

### Data Migration (if applicable)
- [ ] Verify existing user data is preserved
- [ ] Verify existing posts are accessible
- [ ] Verify existing friend relationships are maintained

### Feature Parity
- [ ] Compare functionality with Firebase version
- [ ] Verify no features are lost
- [ ] Document any behavioral changes

## Production Readiness

### Final Checks
- [ ] Remove debug logs
- [ ] Verify production configuration
- [ ] Test with production Supabase instance
- [ ] Verify app signing and deployment
- [ ] Create rollback plan

### Monitoring Setup
- [ ] Set up Supabase monitoring
- [ ] Configure error tracking
- [ ] Set up performance monitoring
- [ ] Create alerting for critical issues

## Sign-off

- [ ] Development team approval
- [ ] QA team approval
- [ ] Product owner approval
- [ ] Security review completed
- [ ] Performance benchmarks met
- [ ] Documentation updated

## Notes

Use this checklist systematically to ensure the migration is complete and the app functions correctly with Supabase. Document any issues found and their resolutions.
