# MentorKE Session Management - Complete UI Flow Documentation

## Overview
This document outlines the complete user interface flow for the session management system, including mentor-mentee matching, session scheduling, messaging, and automated email reminders.

---

## 1. MENTEE FLOW - Browsing & Requesting Mentors

### 1.1 Browse Available Mentors
**URL:** `/mentee-sessions?action=browse`

**Flow:**
```
Mentee Dashboard → Browse Mentors Link
    ↓
MenteeSession.java (doGet) → handleBrowseMentors()
    ↓
SessionMatchingBean.findMentorsBySpecialization()
    ↓
Display: browse-mentors.jsp
    - List of all active mentors (or filtered by specialization)
    - Mentor cards showing:
      * Specialization
      * Years of Experience
      * Bio & Qualifications
      * "Request Mentor" button
```

**Optional Filter:**
- Query Parameter: `specialization=Java`
- Filters mentors by field of study

---

### 1.2 Request a Specific Mentor
**URL:** `/mentee-sessions?action=request`

**Flow:**
```
Browse Mentors → Click "Request Mentor" (mentorId)
    ↓
Display: request-mentor.jsp
    - Mentor profile details
    - Confirmation form
    ↓
Submit Request (POST to /mentee-sessions?action=request-mentor)
    ↓
MenteeSession.java (doPost)
    ↓
MatchRequestBean.requestMentor(menteeId, mentorId, specialization)
    ↓
Actions:
    1. Save MatchRequest with status="PENDING"
    2. Send email notification to mentor
    3. Fire audit trail event
    ↓
Redirect to browse-mentors.jsp
    - Show success message: "Request sent to mentor successfully!"
```

**Data Sent:**
```
POST /mentee-sessions
{
  action: "request-mentor",
  mentorId: "123",
  specialization: "Java"
}
```

---

### 1.3 View My Requests
**URL:** `/mentee-sessions?action=my-requests`

**Flow:**
```
Mentee Dashboard → My Requests Link
    ↓
MenteeSession.java (doGet) → handleMyRequests()
    ↓
MatchRequestBean.getRequestsByMentee(menteeId)
MatchRequestBean.getApprovedMatchForMentee(menteeId)
    ↓
Display: my-match-requests.jsp
    - Pending Requests (status="PENDING")
      * Mentor name, specialization
      * "Cancel" button
    - Approved Match (status="APPROVED")
      * Mentor assigned
      * "View Mentor" button
      * "Message" button
      * "Schedule Session" button
```

**User Actions:**
- **Cancel Request:** POST `/mentee-sessions?action=cancel-request&requestId=xyz`
  - Sets status to "REJECTED"
  - Removes match if not yet approved

---

## 2. MENTOR FLOW - Managing Requests & Mentees

### 2.1 View Pending Requests
**URL:** `/mentor-requests?action=pending`

**Flow:**
```
Mentor Dashboard → Pending Requests Link
    ↓
MentorRequest.java (doGet) → handlePendingRequests()
    ↓
MatchRequestBean.getPendingRequestsForMentor(mentorId)
    ↓
Display: pending-mentee-requests.jsp
    - Table of pending requests:
      * Mentee name, field of study
      * Request date
      * "Approve" button
      * "Reject" button
```

**Actions:**

#### Approve Request
```
POST /mentor-requests?action=approve&requestId=xyz
    ↓
MentorRequest.java (doPost)
    ↓
MatchRequestBean.approveMentorRequest(requestId)
    ↓
Actions:
    1. Update MatchRequest status = "APPROVED"
    2. Update Mentee.mentor_id with current mentor
    3. Fire audit trail event
    ↓
Success: "Request approved! You now have a mentee."
Redirect to: pending-mentee-requests.jsp
```

#### Reject Request
```
POST /mentor-requests?action=reject&requestId=xyz
    ↓
MentorRequest.java (doPost)
    ↓
MatchRequestBean.rejectMentorRequest(requestId)
    ↓
Actions:
    1. Update MatchRequest status = "REJECTED"
    2. Fire audit trail event
    ↓
Success: "Request rejected."
Redirect to: pending-mentee-requests.jsp
```

---

### 2.2 View My Mentees
**URL:** `/mentor-requests?action=my-mentees`

**Flow:**
```
Mentor Dashboard → My Mentees Link
    ↓
MentorRequest.java (doGet) → handleMyMentees()
    ↓
MenteeBean.getMenteesByMentorId(mentorId)
    ↓
Display: mentor-mentees.jsp
    - List of assigned mentees:
      * Mentee name, field of study
      * Learning goals
      * Education level
      * "Message" button
      * "Schedule Session" button
      * "View Profile" button
    - Mentee count badge
```

---

## 3. SESSION SCHEDULING & MANAGEMENT

### 3.1 Schedule a Session
**URL:** `/sessions?action=schedule-form`

**Flow:**
```
My Requests / My Mentees → "Schedule Session" button
    ↓
Display: schedule-session.jsp
    - Session date/time picker (calendar widget)
    - Duration (default: 60 minutes)
    - Topic input field
    - Meeting platform note (Jitsi Meet)
    ↓
Submit Session Details (POST to /sessions?action=create-session)
    ↓
SessionManagement.java (doPost)
    ↓
SessionBean.scheduleSession(mentorId, menteeId, date, duration, topic)
    ↓
Actions:
    1. Create Session object
    2. Generate unique Jitsi Meet link (https://meet.jit.si/mentorke-xxx-xxx)
    3. Save to database with status="PENDING"
    4. Send email notifications to both:
       - Mentor email
       - Mentee email
       (Email includes meeting link and session details)
    5. Fire audit trail event
    ↓
Success: "Session scheduled successfully! Meeting link has been sent to both parties."
Redirect to: session-details.jsp
```

**Data Sent:**
```
POST /sessions
{
  action: "create-session",
  mentorId: "mentor-123",
  menteeId: "mentee-456",
  scheduledDate: "2026-05-15 14:30",
  duration: "60",
  topic: "Java Spring Framework Basics"
}
```

---

### 3.2 View Upcoming Sessions
**URL:** `/sessions?action=upcoming`

**Flow:**
```
Dashboard → Upcoming Sessions Link
    ↓
SessionManagement.java (doGet) → handleUpcomingSessions()
    ↓
SessionBean.getUpcomingSessions(userId)
    (Gets sessions for next 24 hours or beyond, status != COMPLETED/CANCELLED)
    ↓
Display: upcoming-sessions.jsp
    - Session cards showing:
      * Other party name (Mentor/Mentee)
      * Session topic
      * Scheduled date/time
      * Duration
      * Status badge
      * **Blue "Join Session" button** → links to sessionLink
      * "View Details" button
      * "Cancel" button (if not started)
    - Countdown timer (time until session)
```

**User Action - Join Session:**
```
Click "Join Session" button
    ↓
Opens: {session.sessionLink}
Example: https://meet.jit.si/mentorke-123-456-abc1234
```

---

### 3.3 View Completed Sessions
**URL:** `/sessions?action=completed`

**Flow:**
```
Dashboard → Completed Sessions Link
    ↓
SessionManagement.java (doGet) → handleCompletedSessions()
    ↓
SessionBean.getCompletedSessions(userId)
    (Gets sessions with status="COMPLETED")
    ↓
Display: completed-sessions.jsp
    - Completed session records:
      * Other party name
      * Session topic
      * Completed date
      * Duration
      * Post-session notes (if mentor added any)
      * "View Details" button
```

---

### 3.4 View Session Details
**URL:** `/sessions?action=view&sessionId=xyz`

**Flow:**
```
Click "View Details" from any session list
    ↓
SessionManagement.java (doGet) → handleViewSession()
    ↓
SessionBean.getSession(sessionId)
    (Verify user is mentor or mentee in this session)
    ↓
Display: session-details.jsp
    - Full session information:
      * Topic
      * Scheduled date/time
      * Duration
      * Other party details (name, role)
      * Status (PENDING/CONFIRMED/COMPLETED/CANCELLED)
      * Meeting link (clickable button)
      * Session notes (if available)
    
    - Action buttons:
      * "Join Session" (if within 24 hours)
      * "Add Notes" (mentor only, if completed)
      * "Cancel Session" (if not started)
```

---

### 3.5 Cancel Session
**URL:** `/sessions?action=cancel` (POST)

**Flow:**
```
Session Details → "Cancel Session" button
    ↓
SessionManagement.java (doPost)
    ↓
SessionBean.cancelSession(sessionId)
    ↓
Actions:
    1. Update Session status = "CANCELLED"
    2. Fire audit trail event
    3. Send cancellation notification emails (optional)
    ↓
Success: "Session cancelled successfully."
Redirect to: /sessions?action=upcoming
```

---

### 3.6 Add Post-Session Notes (Mentor Only)
**URL:** `/sessions?action=add-notes` (POST)

**Flow:**
```
Completed Session Details → "Add Notes" button (Mentor only)
    ↓
Display: Add Notes form
    - Text area for session feedback/notes
    - "Save Notes" button
    ↓
POST /sessions?action=add-notes
{
  sessionId: "session-123",
  notes: "Discussed Spring annotations and dependency injection..."
}
    ↓
SessionManagement.java (doPost)
    ↓
SessionBean.addSessionNotes(sessionId, notes)
    ↓
Actions:
    1. Update Session.notes field
    2. Update Session.updated_at timestamp
    ↓
Success: "Notes added successfully."
Redirect to: session-details.jsp
```

---

## 4. MESSAGING SYSTEM

### 4.1 View Message Inbox
**URL:** `/messaging?action=list-conversations`

**Flow:**
```
Dashboard → Messages Link
    ↓
Messaging.java (doGet) → handleListConversations()
    ↓
MessageBean.getRecentConversations(userId)
MessageBean.getUnreadMessageCount(userId)
    ↓
Display: message-inbox.jsp
    - Unread message count badge
    - List of conversations:
      * Other user name
      * Last message preview
      * Last message timestamp
      * Unread count (if any)
      * "Open Conversation" link
```

---

### 4.2 Open Conversation
**URL:** `/messaging?action=conversation&userId=xyz`

**Flow:**
```
Message Inbox → Click Conversation
    ↓
Messaging.java (doGet) → handleConversation()
    ↓
MessageBean.getConversation(userId, otherUserId)
MessageBean.markConversationAsRead(userId, otherUserId)
    ↓
Display: conversation.jsp
    - Chat-like interface:
      * Message thread (oldest to newest)
      * Each message shows:
        - Sender name
        - Message text
        - Timestamp
        - "Read" indicator (if applicable)
      * Message input box at bottom
      * "Send Message" button
```

---

### 4.3 Send a Message
**URL:** `/messaging?action=send-message` (POST)

**Flow:**
```
Conversation view → Type message in text box
    ↓
Click "Send Message" button
    ↓
POST /messaging?action=send-message
{
  recipientId: "mentor-123",
  message: "Hi! When can we schedule our first session?"
}
    ↓
Messaging.java (doPost)
    ↓
MessageBean.sendMessage(userId, recipientId, message)
    ↓
Actions:
    1. Create Message object
    2. Set is_read = false
    3. Save to database
    ↓
Redirect to: conversation.jsp (same conversation)
    - Show sent message immediately
    - Message appears in thread with current timestamp
```

---

### 4.4 Get Unread Message Count (AJAX)
**URL:** `/messaging?action=unread-count`

**Flow:**
```
Any dashboard page (runs periodically via JavaScript)
    ↓
GET /messaging?action=unread-count
    ↓
Messaging.java (doGet)
    ↓
MessageBean.getUnreadMessageCount(userId)
    ↓
Response (JSON):
{
  "unreadCount": 3
}
    ↓
Update badge in navbar
    - Show "3" badge on Messages link
    - Enable notification if count > 0
```

---

## 5. AUTOMATED EMAIL REMINDER SCHEDULER

### 5.1 Scheduler Execution
**Trigger:** Every 30 minutes (automatic, server-side)

**Execution:**
```
EmailReminderBean.sendSessionReminders()
    (Runs every 30 minutes via @Schedule annotation)
    ↓
Gets all sessions from database
    ↓
For each session that matches criteria:
    - Status = "PENDING" OR "CONFIRMED"
    - Scheduled date is within next 24 hours
    - Scheduled date is at least 1 hour away (prevent spam)
    - Not yet started (scheduledDate > now)
    ↓
EmailReminderBean.sendSessionReminderEmails(session)
    ↓
Actions:
    1. Get Mentor and Mentee details
    2. Get User records (for email addresses)
    3. Format HTML email body with:
       - Session topic
       - Scheduled date/time
       - Duration
       - Time remaining (human-readable)
       - Meeting link (clickable button)
    4. Send email to mentor
    5. Send email to mentee
    ↓
Log: "Reminders sent: X sessions"
```

**Email Template Example:**

```
Subject: Reminder: Upcoming Session - Java Spring Framework Basics

Body:
Hi John (Mentor),

This is a reminder about your upcoming mentoring session with Jane (Mentee).

Session Details:
- Topic: Java Spring Framework Basics
- Date & Time: Thursday, May 15, 2026 at 2:30 PM
- Duration: 60 minutes
- Time Remaining: 2 hours and 30 minutes

[JOIN SESSION] button → https://meet.jit.si/mentorke-123-456-abc1234

This is an automated reminder. Please do not reply to this email.
```

---

### 5.2 Manual Reminder Trigger (Optional)
**Method:** Can be called from admin panel or session details

```java
emailReminderBean.sendMentorSessionReminder(sessionId);
emailReminderBean.sendMenteeSessionReminder(sessionId);
```

---

## 6. AUTO-MATCHING SYSTEM (Optional Scheduled Job)

### 6.1 Auto-Match Pending Requests
**URL:** Could be triggered manually or via scheduler

**Flow:**
```
MatchRequestBean.autoMatchPendingRequests()
    (Could run nightly or on-demand)
    ↓
Gets all MatchRequests with:
    - status = "PENDING"
    - mentor_id IS NULL (unassigned)
    ↓
For each unassigned request:
    1. Get mentee details
    2. SessionMatchingBean.findOptimalMentor(mentee)
       - Matches by field_of_study
       - Ranks by yearsOfExperience (highest first)
    3. Update MatchRequest:
       - Set mentor_id
       - Set status = "APPROVED"
    4. Update Mentee.mentor_id
    ↓
Notify mentor: "You have been auto-matched with a mentee"
```

---

## 7. COMPLETE USER JOURNEY DIAGRAM

```
┌─────────────────────────────────────────────────────────────────────┐
│                         MENTEE JOURNEY                              │
└─────────────────────────────────────────────────────────────────────┘

Login as Mentee
    ↓
Mentee Dashboard
    ├── Browse Mentors → Select by Specialization → Request Mentor
    │
    ├── My Requests 
    │   ├── View pending requests
    │   └── Monitor for approval
    │
    ├── Upcoming Sessions
    │   ├── View scheduled sessions
    │   ├── Join Session (Jitsi Meet)
    │   └── Cancel if needed
    │
    ├── Messages
    │   ├── View conversations with mentor
    │   ├── Send/receive messages
    │   └── Check unread count
    │
    └── Completed Sessions
        └── View past sessions and mentor notes


┌─────────────────────────────────────────────────────────────────────┐
│                         MENTOR JOURNEY                              │
└─────────────────────────────────────────────────────────────────────┘

Login as Mentor
    ↓
Mentor Dashboard
    ├── Pending Requests
    │   ├── View mentee requests
    │   ├── Approve → adds as assigned mentee
    │   └── Reject → removes request
    │
    ├── My Mentees
    │   ├── View all assigned mentees
    │   └── Browse mentee profiles
    │
    ├── Upcoming Sessions
    │   ├── View scheduled sessions
    │   ├── Join Session (Jitsi Meet)
    │   └── Cancel if needed
    │
    ├── Messages
    │   ├── Message mentees
    │   └── Receive/send updates
    │
    └── Completed Sessions
        ├── Add notes/feedback
        └── Archive for reference


┌─────────────────────────────────────────────────────────────────────┐
│                    BACKGROUND PROCESSES                             │
└─────────────────────────────────────────────────────────────────────┘

Every 30 minutes:
    EmailReminderBean.sendSessionReminders()
    → Emails to mentors and mentees about upcoming sessions
    
Optional (could be nightly):
    MatchRequestBean.autoMatchPendingRequests()
    → Auto-assigns unassigned match requests to optimal mentors
```

---

## 8. API ENDPOINTS SUMMARY

| Method | URL | Action | Description |
|--------|-----|--------|-------------|
| GET | `/mentee-sessions` | browse | Browse available mentors |
| GET | `/mentee-sessions` | request | Show request mentor form |
| GET | `/mentee-sessions` | my-requests | View mentee's requests |
| POST | `/mentee-sessions` | request-mentor | Submit mentor request |
| POST | `/mentee-sessions` | cancel-request | Cancel a request |
| GET | `/mentor-requests` | pending | View pending requests (mentor) |
| GET | `/mentor-requests` | my-mentees | View assigned mentees (mentor) |
| POST | `/mentor-requests` | approve | Approve mentee request |
| POST | `/mentor-requests` | reject | Reject mentee request |
| GET | `/sessions` | upcoming | View upcoming sessions |
| GET | `/sessions` | completed | View completed sessions |
| GET | `/sessions` | view | View session details |
| GET | `/sessions` | schedule-form | Show schedule form |
| POST | `/sessions` | create-session | Create new session |
| POST | `/sessions` | cancel | Cancel session |
| POST | `/sessions` | add-notes | Add post-session notes |
| GET | `/messaging` | list-conversations | View message inbox |
| GET | `/messaging` | conversation | Open specific conversation |
| GET | `/messaging` | unread-count | Get unread message count (JSON) |
| POST | `/messaging` | send-message | Send a message |
| POST | `/messaging` | mark-read | Mark message as read |

---

## 9. Database Relationships

```
users (existing)
  ├── mentors (existing)
  │   └── (1) ─→ (many) MatchRequest (mentor_id)
  │   └── (1) ─→ (many) Session (mentor_id)
  │
  ├── mentees (existing)
  │   ├── mentor_id ─→ mentors.id (foreign key)
  │   └── (1) ─→ (many) Session (mentee_id)
  │   └── (1) ─→ (many) MatchRequest (mentee_id)
  │
  └── (1) ─→ (many) Message (sender_id, recipient_id)

sessions (new)
  ├── mentor_id ─→ mentors.id
  ├── mentee_id ─→ mentees.id
  └── Statuses: PENDING, CONFIRMED, COMPLETED, CANCELLED

match_requests (new)
  ├── mentor_id ─→ mentors.id (nullable for auto-match)
  ├── mentee_id ─→ mentees.id
  └── Statuses: PENDING, APPROVED, REJECTED, MATCHED

messages (new)
  ├── sender_id ─→ users.id
  └── recipient_id ─→ users.id
```

---

## 10. Session Statuses & Transitions

```
Session Status Workflow:

PENDING (initial)
    ↓ (Mentor approves in their system)
CONFIRMED
    ↓ (Session date/time passed)
COMPLETED
    ↓
Archive

Or at any point:
ANY → CANCELLED (user cancels)
```

---

## 11. Key Security Considerations

- **Authentication:** All endpoints check `session.isLoggedIn` and `session.userId`
- **Authorization:** 
  - Mentees can only see their own requests/sessions
  - Mentors can only see requests/sessions for them
  - Users can only message each other if matched
- **CSRF:** Should be added to form submissions (optional: token in all POST forms)
- **Email:** Notifications include unsubscribe link (optional enhancement)

---

## 12. JSP Pages to Create

```
UI Pages:
├── browse-mentors.jsp
├── request-mentor.jsp
├── my-match-requests.jsp
├── pending-mentee-requests.jsp
├── mentor-mentees.jsp
├── schedule-session.jsp
├── upcoming-sessions.jsp
├── completed-sessions.jsp
├── session-details.jsp
├── conversation.jsp
├── message-inbox.jsp
└── mentor-profile.jsp (view mentor details)
```

---

This completes the full UI flow documentation for the MentorKE session management system!
