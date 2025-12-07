# Wellness Tracker - Mobile Application

## 📱 Overview
Wellness Tracker is a comprehensive Android application designed to help users manage their daily health routines and track their wellness journey. The app combines habit tracking, mood journaling, and hydration monitoring to promote a healthier lifestyle.

### 🎨 App Design & Colors
The Wellness Tracker features a modern, vibrant design with an attractive color palette:

**🎨 Modern Color Scheme**:
- **Primary**: Vibrant Blue-Purple Gradient (#6C63FF to #9D50BB)
- **Accents**: Fresh Green (#00E676), Coral Pink (#FF6B9D), Sky Blue (#26C6DA)
- **Backgrounds**: Clean white cards on soft gray background
- **Mood Colors**: Each mood has its unique vibrant color for easy identification

**📱 App Icon**:
- **❤️ Heart Symbol**: Represents overall health and wellness
- **✅ Green Checkmark**: Symbolizes habit completion and progress tracking
- **💧 Water Drop**: Represents hydration monitoring
- **⭐ Gold Accents**: Mood and positivity indicators
- **🎨 Purple Gradient**: Modern, vibrant wellness theme

## 🎯 Features

### 1. Login & Authentication
**Purpose**: Secure access to your wellness data with user authentication.

**How to Use**:
- **First Launch**: App opens to the login screen
- **Enter Email**: Valid email address (e.g., user@example.com)
- **Enter Password**: Minimum 6 characters
- **Remember Me**: Check to save credentials for next time
- **Forgot Password**: Click to receive password reset instructions
- **Login**: Tap to access your wellness dashboard
- **Skip for Now**: Use app as guest (limited features)
- **Sign Up**: Create new account (coming soon)

**Validations**:
- Email must be in valid format (e.g., user@domain.com)
- Password must be at least 6 characters long
- Real-time validation with error messages
- Empty fields are not allowed

**What Happens**:
- Login state is saved automatically
- Credentials are securely stored if "Remember Me" is checked
- Auto-login on app restart if logged in previously
- Guest mode available for trying the app without registration
- Logout option available in Settings

### 2. Daily Habit Tracker
**Purpose**: Track and manage daily wellness habits to build consistent routines.

**How to Use**:
- **View Habits**: See all your habits on the main screen with completion status
- **Add New Habit**: Tap the green "+" button (FAB) in the bottom-right corner
  - Enter habit name (required)
  - Add description (optional)
  - Select frequency: Daily, Weekly, or Monthly
  - Enable/disable reminders
  - Tap "Save" to add the habit
- **Complete Habits**: Tap the green checkbox next to any habit to mark it as completed
- **Edit Habit**: Tap the pencil icon next to a habit to modify its details
- **Delete Habit**: Tap the red trash icon next to a habit, then confirm deletion
- **Track Progress**: View your daily completion percentage at the top of the screen

**What Happens**:
- Progress bar shows completion percentage (e.g., "2/5" means 2 out of 5 habits completed)
- Completed habits are marked with a checked green box
- All habit data is automatically saved and persists between app sessions
- Default habits (Drink Water, Exercise, Meditation) are provided on first launch

### 3. Mood Journal with Emoji Selector
**Purpose**: Log daily emotions and track mood patterns over time.

**How to Use**:
- **Select Mood**: Choose from 5 emoji options (tap to select):
  - 😠 Angry
  - 😢 Sad
  - 😐 Neutral
  - 😊 Happy
  - 🤩 Excited
  - Buttons are large (70dp) with 32sp emojis for easy tapping
  - Selected button shows purple border (3dp) and light purple background
- **Add Note**: Optionally add a personal note about your mood
- **Save Entry**: Tap "Save Mood" button (enabled only after emoji selection)
- **View History**: Switch between "Calendar View" and "List View" to see past entries
- **Share Mood**: Tap the share icon on any mood entry to share it via text, email, or social media

**What Happens**:
- Mood entries are saved with timestamp and optional notes
- Calendar view shows mood patterns over time
- List view shows recent mood entries with relative timestamps ("2h ago", "Yesterday")
- Share functionality creates a formatted message with your mood and timestamp

### 3. Hydration Reminder
**Purpose**: Track daily water intake and receive reminders to stay hydrated.

**How to Use**:
- **Track Water Intake**: 
  - Tap "+ Add Glass" to record one glass of water
  - Tap "+ Bottle" to record a bottle (counts as 2 glasses)
- **Set Daily Goal**: Use the slider to set your daily water goal (4-20 glasses)
- **Enable Reminders**: Toggle the reminder switch to receive notifications
- **Set Reminder Interval**: Choose from:
  - Every Hour
  - Every 2 Hours
  - Every 3 Hours
- **View Progress**: Circular progress indicator shows completion percentage
- **View History**: See all water intake entries for the day

**What Happens**:
- Progress circle fills up as you drink more water
- Notifications appear at your chosen intervals reminding you to drink water
- Daily goal can be customized based on your needs
- All intake data is tracked and saved automatically

### 4. Home Screen Widget
**Purpose**: Quick access to your daily habit progress without opening the app.

**How to Use**:
- **Add Widget**: Long-press on home screen → Widgets → Find "Wellness Tracker" → Drag to home screen
- **View Progress**: Widget shows current habit completion (e.g., "2/5" and "40%")
- **Quick Access**: Tap the widget to open the app directly
- **Auto-Update**: Widget updates every 5 minutes or when you complete habits

**What Happens**:
- Widget displays real-time habit completion percentage
- Progress bar fills as you complete more habits
- Motivational message encourages continued progress
- Tapping opens the main app for detailed tracking

### 5. Settings & Profile
**Purpose**: Customize the app and manage your personal information.

**How to Use**:
- **Edit Profile**: Tap "Edit" to change your name and email
- **Logout**: Tap to sign out and return to login screen
- **Notification Settings**: Choose which types of notifications to receive
- **Theme Settings**: Select Light, Dark, or System Default theme
- **Export Data**: Save all your wellness data to a file (coming soon)
- **Import Data**: Restore data from a backup file (coming soon)
- **View Statistics**: See your overall progress summary

**What Happens**:
- Profile changes are saved immediately
- Logout clears session and returns to login screen
- Notification preferences control reminder behavior
- Theme changes require app restart to take effect
- Statistics show total days active, habits completed, and mood entries

## 🎨 User Interface

### Navigation
- **Bottom Navigation**: Switch between main features (Habits, Mood Journal, Hydration, Settings)
- **Portrait Mode**: Uses bottom navigation bar
- **Landscape Mode**: Uses navigation drawer for better space utilization
- **Tablet Mode**: Side navigation panel with quick stats

### Design Elements
- **Material Design 3**: Modern, clean interface with consistent styling
- **Color Scheme**: 
  - Primary: Blue (#2196F3)
  - Accent: Green (#4CAF50)
  - Success: Green for completed items
  - Error: Red for delete actions
- **Cards**: Rounded corners with subtle shadows for content organization
- **Progress Indicators**: Visual feedback for completion status

## 💾 Data Management

### Data Persistence
- **SharedPreferences**: All user data is stored locally using SharedPreferences
- **JSON Serialization**: Complex data structures are serialized using Gson
- **Automatic Saving**: Changes are saved immediately without manual intervention
- **Data Models**: 
  - Habits: Name, description, frequency, reminder settings, completion status
  - Mood Entries: Emoji, mood type, note, timestamp
  - Hydration Entries: Amount, timestamp
  - User Settings: Goals, preferences, profile information

### Data Security
- All data is stored locally on the device
- No data is transmitted to external servers
- User privacy is maintained through local storage only

## 🔧 Technical Features

### Architecture
- **Fragment-based**: Each main feature is implemented as a separate fragment
- **MVVM Pattern**: Clean separation of concerns with data managers
- **RecyclerView**: Efficient list rendering for habits and mood entries
- **Material Components**: Consistent UI using Material Design 3 components

### Responsive Design
- **Phone Layouts**: Optimized for portrait and landscape orientations
- **Tablet Layouts**: Two-panel design for larger screens
- **Adaptive UI**: Layouts adjust based on screen size and orientation

### Notifications
- **AlarmManager**: Schedules hydration reminders
- **BroadcastReceiver**: Handles notification delivery
- **Notification Channels**: Proper notification management for Android 8.0+

## 🚀 Getting Started

### Installation
1. Download and install the APK file
2. Grant necessary permissions (notifications, storage)
3. Launch the app and start tracking your wellness journey

### First Time Setup
1. **Add Your First Habit**: Tap the "+" button to create a custom habit
2. **Log Your First Mood**: Select an emoji and save your current mood
3. **Set Water Goal**: Adjust the hydration goal to match your needs
4. **Enable Reminders**: Turn on notifications for hydration reminders
5. **Add Widget**: Long-press home screen → Widgets → Add "Wellness Tracker" widget

### Daily Usage
1. **Morning**: Check your habits for the day (use widget for quick glance)
2. **Throughout Day**: 
   - Mark habits as completed
   - Log mood changes
   - Record water intake
   - Check widget for progress updates
3. **Evening**: Review your progress and plan for tomorrow

## 📊 Tips for Success

### Building Habits
- Start with 2-3 simple habits
- Be consistent rather than perfect
- Use the reminder feature to build routine
- Celebrate small wins with mood logging

### Mood Tracking
- Log mood at different times of day
- Add notes to understand patterns
- Use the calendar view to identify trends
- Share positive moods to spread joy

### Hydration
- Set a realistic daily goal
- Use reminders to build the habit
- Track different types of beverages
- Monitor progress throughout the day

## 🐛 Troubleshooting

### Common Issues
- **App Crashes**: Restart the app and try again
- **Data Not Saving**: Check device storage space
- **Notifications Not Working**: Verify notification permissions
- **UI Issues**: Try rotating device or restarting app

### Support
- Check device compatibility (Android 7.0+)
- Ensure sufficient storage space
- Verify all permissions are granted
- Contact support for persistent issues

## 🔮 Future Features
- **Sensor Integration**: Step counting and shake-to-mood features
- **Charts & Analytics**: Visual mood trends and habit streaks
- **Social Features**: Share progress with friends and family
- **Backup & Sync**: Cloud backup and multi-device sync
- **Advanced Widgets**: Multiple widget sizes and customization options

## 📱 System Requirements
- **Android Version**: 7.0 (API level 24) or higher
- **Storage**: 50MB available space
- **Permissions**: Notifications, Storage (for data export)
- **Screen Size**: Optimized for phones and tablets

## 🏆 Benefits of Using Wellness Tracker
- **Improved Health**: Consistent habit tracking leads to better health outcomes
- **Mood Awareness**: Understanding emotional patterns helps with mental health
- **Hydration**: Proper water intake improves energy and cognitive function
- **Accountability**: Visual progress tracking motivates continued improvement
- **Routine Building**: Structured approach to developing healthy habits

---

**Version**: 1.0.0  
**Last Updated**: December 2024  
**Developer**: Wellness Tracker Team

*Start your wellness journey today and build healthier habits that last a lifetime!* 🌟
