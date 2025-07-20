# Firebase to Supabase Migration Guide

## Overview
This guide documents the complete migration from Firebase to Supabase for the MyLocket Android/Kotlin application.

## Migration Summary

### ✅ Completed Changes

#### 1. Dependencies Updated
- **Removed**: Firebase BOM, Auth, Firestore, Storage, Analytics
- **Added**: Supabase Kotlin SDK (PostgREST, GoTrue, Storage, Realtime)
- **Added**: Ktor client, Kotlinx Serialization, Kotlinx DateTime

#### 2. Configuration
- **Removed**: `google-services.json`
- **Added**: `SupabaseConfig.kt` with project URL and anon key
- **Updated**: Build scripts to use Kotlin serialization

#### 3. Data Models
- **Updated**: `Post.kt` - Replaced Firebase Timestamp with Kotlinx Instant
- **Updated**: `User.kt` - Added Kotlinx Serialization annotations
- **Updated**: `Friend.kt` - Added Kotlinx Serialization annotations

#### 4. Service Layer
- **Created**: `SupabaseAuthService.kt` - Handles authentication
- **Created**: `SupabaseDatabaseService.kt` - Handles database operations
- **Created**: `SupabaseStorageService.kt` - Handles file storage

#### 5. Authentication Migration
- **Updated**: All authentication screens to use `SupabaseAuthService`
- **Migrated**: Sign up, sign in, password reset, profile updates
- **Updated**: Navigation to use Supabase auth state

#### 6. Database Migration
- **Updated**: All ViewModels to use `SupabaseDatabaseService`
- **Migrated**: User management, posts, friends functionality
- **Added**: Real-time subscriptions using Supabase Realtime

#### 7. Storage Migration
- **Updated**: `SendingScreen.kt` to use `SupabaseStorageService`
- **Migrated**: Image upload functionality

## Database Schema

### Required Supabase Setup

1. **Create Supabase Project**: Use the provided URL and anon key
2. **Run SQL Schema**: Execute `supabase_schema.sql` in your Supabase SQL editor
3. **Configure Storage**: The schema creates an 'images' bucket automatically

### Key Schema Changes

#### Users Table
```sql
CREATE TABLE public.users (
    id UUID REFERENCES auth.users(id) PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    photo TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

#### Posts Table
```sql
CREATE TABLE public.posts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE NOT NULL,
    content TEXT NOT NULL,
    photo TEXT NOT NULL,
    time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    to_who TEXT[] NOT NULL DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
```

#### Friends Table
```sql
CREATE TABLE public.friends (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES public.users(id) ON DELETE CASCADE NOT NULL,
    friend_id UUID REFERENCES public.users(id) ON DELETE CASCADE NOT NULL,
    friend_name TEXT NOT NULL,
    friend_email TEXT NOT NULL,
    friend_photo TEXT,
    status TEXT NOT NULL CHECK (status IN ('SENT', 'FRIENDS', 'RECEIVED')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    UNIQUE(user_id, friend_id)
);
```

## Key Differences from Firebase

### Authentication
- **Firebase**: Uses FirebaseAuth with email/password
- **Supabase**: Uses GoTrue with email/password, returns UserInfo objects
- **User Data**: Accessed via `userMetadata` instead of direct properties

### Database
- **Firebase**: Firestore with collections and documents
- **Supabase**: PostgreSQL with tables and rows
- **Real-time**: Uses Supabase Realtime instead of Firestore listeners
- **Queries**: SQL-based instead of Firestore queries

### Storage
- **Firebase**: Firebase Storage with download URLs
- **Supabase**: Supabase Storage with public URLs
- **Upload**: Direct file upload instead of URI-based

## Testing Requirements

### 1. Authentication Testing
- [ ] User registration with email/password
- [ ] User login with email/password
- [ ] Password reset functionality
- [ ] Profile name updates
- [ ] User logout
- [ ] Account deletion (Note: Limited in Supabase client)

### 2. Database Testing
- [ ] User creation and retrieval
- [ ] Post creation with image upload
- [ ] Post retrieval for specific users
- [ ] Friend request sending
- [ ] Friend request acceptance
- [ ] Friend deletion
- [ ] Real-time updates for posts and friends

### 3. Storage Testing
- [ ] Image upload from camera/gallery
- [ ] Image URL generation
- [ ] Image display in posts

## Known Limitations

### 1. Account Deletion
- Supabase doesn't support client-side user deletion
- Current implementation only signs out the user
- Consider implementing server-side deletion via Edge Functions

### 2. Email Change
- Complex re-authentication logic needs adaptation
- Current implementation may need server-side support

### 3. Real-time Performance
- Monitor real-time subscription performance
- Consider implementing connection management

## Next Steps

1. **Deploy Database Schema**: Run the SQL schema in your Supabase project
2. **Test Authentication**: Verify all auth flows work correctly
3. **Test Database Operations**: Ensure CRUD operations work
4. **Test Real-time**: Verify real-time updates function properly
5. **Test Storage**: Confirm image upload and retrieval works
6. **Performance Testing**: Monitor app performance with Supabase
7. **Error Handling**: Implement comprehensive error handling
8. **Edge Functions**: Consider implementing server-side functions for complex operations

## Support

For issues or questions about this migration:
1. Check Supabase documentation: https://supabase.com/docs
2. Review the service layer implementations
3. Test individual components in isolation
4. Monitor Supabase dashboard for errors and performance

## Migration Checklist

- [x] Update dependencies
- [x] Create Supabase configuration
- [x] Update data models
- [x] Create service layer
- [x] Migrate authentication
- [x] Migrate database operations
- [x] Migrate storage operations
- [x] Update error handling
- [ ] Deploy database schema
- [ ] Test all functionality
- [ ] Performance optimization
- [ ] Production deployment
