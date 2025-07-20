# Quick Test Guide - Supabase Migration

## ✅ Database Schema Deployed Successfully!

Your Supabase database is now ready. Let's test the migration step by step.

## 🔧 Build and Run Tests

### 1. Clean Build
```bash
cd "d:\Locker App\MyLocket"
./gradlew clean build
```

### 2. Run the App
```bash
./gradlew installDebug
```

## 🧪 Testing Priority Order

### Phase 1: Authentication (CRITICAL)
Test these first as everything depends on auth:

1. **User Registration**
   - Open app → Should show Welcome screen
   - Tap "Register" → Enter email and password
   - Complete name setup
   - ✅ **Expected**: User created in Supabase auth.users and public.users tables

2. **User Login**
   - Logout if logged in
   - Tap "Login" → Enter credentials
   - ✅ **Expected**: Successful login, navigate to home screen

3. **Session Persistence**
   - Close and reopen app
   - ✅ **Expected**: Should stay logged in

### Phase 2: Database Operations
4. **User Profile**
   - Check if user data displays correctly
   - Try updating profile name
   - ✅ **Expected**: Changes reflected immediately

5. **Posts Creation**
   - Take a photo or select from gallery
   - Add content and select recipients
   - Submit post
   - ✅ **Expected**: Post appears in feed

6. **Friends Management**
   - Open friends bottom sheet
   - Search for users
   - Send friend request
   - ✅ **Expected**: Friend request sent successfully

### Phase 3: Real-time Features
7. **Real-time Updates**
   - Use two devices/emulators if possible
   - Send post from one device
   - ✅ **Expected**: Post appears on other device immediately

## 🐛 Common Issues & Solutions

### Build Issues
```
Error: Unresolved reference: Firebase
```
**Solution**: Some files might still have Firebase imports. Check:
- Look for any remaining `import com.google.firebase.*`
- Replace with appropriate Supabase imports

### Authentication Issues
```
Error: User creation failed
```
**Solution**: Check Supabase dashboard:
1. Go to Authentication → Users
2. Verify user was created
3. Check logs for errors

### Database Issues
```
Error: Permission denied
```
**Solution**: Verify RLS policies:
1. Go to Supabase → Database → Policies
2. Ensure all policies are enabled
3. Check user has proper permissions

### Storage Issues
```
Error: Upload failed
```
**Solution**: Check storage bucket:
1. Go to Supabase → Storage
2. Verify 'images' bucket exists
3. Check bucket policies

## 📊 Verification Checklist

### In Supabase Dashboard

#### Authentication Tab
- [ ] Users appear in auth.users table after registration
- [ ] User metadata contains display_name

#### Database Tab
- [ ] public.users table has user records
- [ ] public.posts table receives new posts
- [ ] public.friends table shows friend relationships

#### Storage Tab
- [ ] images bucket exists
- [ ] Uploaded images appear in bucket
- [ ] Images are publicly accessible

### In Android App

#### Authentication Flow
- [ ] Welcome screen shows for new users
- [ ] Registration creates account successfully
- [ ] Login works with created account
- [ ] Profile name updates work
- [ ] Logout redirects to welcome screen

#### Core Features
- [ ] Camera/gallery selection works
- [ ] Image upload completes successfully
- [ ] Posts display with images
- [ ] Friend search finds users
- [ ] Friend requests can be sent/accepted

#### Real-time Features
- [ ] New posts appear without refresh
- [ ] Friend status updates in real-time
- [ ] User list updates automatically

## 🚨 If Tests Fail

### Step 1: Check Logs
```bash
adb logcat | grep -E "(Supabase|Error|Exception)"
```

### Step 2: Verify Configuration
Check `SupabaseConfig.kt`:
- URL: `https://ftqzohcisqzckecvhvez.supabase.co`
- Key: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`

### Step 3: Database Verification
In Supabase SQL Editor, run:
```sql
-- Check if tables exist
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'public';

-- Check RLS policies
SELECT * FROM pg_policies WHERE schemaname = 'public';

-- Check storage bucket
SELECT * FROM storage.buckets WHERE name = 'images';
```

### Step 4: Test Individual Services
Create simple test functions in your ViewModels to isolate issues:

```kotlin
// Test auth
val user = authService.getCurrentUser()
Log.d("Test", "Current user: ${user?.email}")

// Test database
viewModelScope.launch {
    val result = databaseService.getUsers()
    Log.d("Test", "Users: ${result.getOrNull()?.size}")
}

// Test storage
viewModelScope.launch {
    val testFile = File("path/to/test/image.jpg")
    val result = storageService.uploadImage(testFile)
    Log.d("Test", "Upload result: ${result.isSuccess}")
}
```

## 📞 Next Steps After Testing

### If All Tests Pass ✅
1. Perform thorough user acceptance testing
2. Test with multiple users simultaneously
3. Monitor performance and error rates
4. Plan production deployment

### If Tests Fail ❌
1. Document specific error messages
2. Check Supabase dashboard for clues
3. Review service implementations
4. Test individual components in isolation

## 🎯 Success Criteria

The migration is successful when:
- [ ] All authentication flows work
- [ ] Users can create and view posts
- [ ] Friend management functions properly
- [ ] Real-time updates work correctly
- [ ] No Firebase-related errors in logs
- [ ] App performance is acceptable

Start testing now and let me know if you encounter any issues!
