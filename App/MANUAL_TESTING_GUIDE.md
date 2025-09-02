# 🧪 Payment Reminder System - Manual Testing Guide

## Prerequisites
1. Backend running on http://localhost:8080 ✅
2. Valid JWT token (Admin or Provider role)
3. PowerShell or any REST client

## Step 1: Get Authentication Token

### Option A: Login with Admin Credentials
```powershell
# Using PowerShell
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@gmail.com","password":"admin123"}'
$token = $loginResponse.token
Write-Host "Token: $token"
```

### Option B: Login with Provider Credentials  
```powershell
# Using existing provider account (from your database logs)
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"dilmikasahan497@gmail.com","password":"yourpassword"}'
$token = $loginResponse.token
Write-Host "Token: $token"
```

### Option C: Using curl (Alternative)
```bash
# Get token using curl
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"dilmikasahan497@gmail.com","password":"yourpassword"}'
```

## Step 2: Manual Trigger Testing

Once you have the token, use these commands:

### 🔔 Trigger Daily Payment Reminders
```powershell
$headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/trigger-daily" -Method POST -Headers $headers
Write-Host $response
```

### ⚠️ Trigger Overdue Payment Alerts  
```powershell
$headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/trigger-overdue" -Method POST -Headers $headers
Write-Host $response
```

### 📊 Trigger Weekly Group Summary
```powershell
$headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/trigger-weekly" -Method POST -Headers $headers
Write-Host $response
```

### 📋 Check Reminder System Status
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/status" -Method GET -Headers $headers
Write-Host $response
```

### ℹ️ Get System Information
```powershell
$headers = @{ "Authorization" = "Bearer $token" }
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/info" -Method GET -Headers $headers
Write-Host $response
```

## Step 3: Using curl Commands (Alternative)

If you prefer curl, here are the commands:

```bash
# Set your token variable first
TOKEN="your_jwt_token_here"

# Trigger daily reminders
curl -X POST "http://localhost:8080/api/admin/reminders/trigger-daily" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Trigger overdue alerts
curl -X POST "http://localhost:8080/api/admin/reminders/trigger-overdue" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Trigger weekly summary
curl -X POST "http://localhost:8080/api/admin/reminders/trigger-weekly" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# Check status
curl -X GET "http://localhost:8080/api/admin/reminders/status" \
  -H "Authorization: Bearer $TOKEN"

# Get info
curl -X GET "http://localhost:8080/api/admin/reminders/info" \
  -H "Authorization: Bearer $TOKEN"
```

## Expected Responses

### Success Response Example:
```json
{
  "message": "Daily payment reminders triggered successfully",
  "status": "success", 
  "timestamp": "2025-09-02T11:45:00"
}
```

### Status Response Example:
```json
{
  "status": "Payment Reminders: ENABLED, Advance Days: 3",
  "message": "Reminder system is operational",
  "timestamp": "2025-09-02T11:45:00"
}
```

### Info Response Example:
```json
{
  "dailyReminderTime": "09:00 AM (every day)",
  "overdueAlertTime": "10:00 AM (every day)", 
  "weeklyReportTime": "08:00 AM (every Monday)",
  "reminderAdvanceDays": 3,
  "status": "ACTIVE",
  "features": [
    "Daily payment reminders",
    "Overdue payment alerts",
    "Weekly group summaries", 
    "SMS notifications",
    "Manual trigger support"
  ]
}
```

## Console Output Monitoring

When you trigger reminders, watch your backend console for:

```
🧪 Manual trigger: Daily payment reminders
🔔 Starting daily payment reminder process...
📱 Reminder sent to: John Doe
⚠️ No phone number for member: Jane Smith
✅ Daily payment reminders sent successfully: 3/5
```

## Troubleshooting

### If Authentication Fails:
1. Check if user exists in database
2. Verify password is correct
3. Ensure user has ADMIN or PROVIDER role

### If No SMS Sent:
1. Check if members have phone numbers
2. Verify Twilio configuration
3. Check console logs for detailed errors

### If No Data Found:
1. Ensure you have active groups in database
2. Check if payments exist with proper due dates
3. Verify database connection

## Testing Scenarios

### Test with Upcoming Payments:
- Create payments with due dates 3 days from today
- Trigger daily reminders
- Should send SMS to members

### Test with Overdue Payments:
- Create payments with past due dates
- Trigger overdue alerts  
- Should send urgent SMS alerts

### Test with Active Groups:
- Ensure groups have status 'ACTIVE' or isActive = true
- Trigger weekly summary
- Should send summary to providers

## Quick Test Script (PowerShell)

Save this as `test-reminders.ps1`:

```powershell
# Quick test script for payment reminders
Write-Host "🧪 Testing Payment Reminder System" -ForegroundColor Green

# Login and get token
try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"dilmikasahan497@gmail.com","password":"yourpassword"}'
    $token = $loginResponse.token
    Write-Host "✅ Login successful" -ForegroundColor Green
    
    $headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }
    
    # Test system status
    Write-Host "📋 Checking system status..." -ForegroundColor Yellow
    $status = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/status" -Method GET -Headers $headers
    Write-Host $status.status -ForegroundColor Cyan
    
    # Test daily reminders
    Write-Host "🔔 Triggering daily reminders..." -ForegroundColor Yellow
    $daily = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/trigger-daily" -Method POST -Headers $headers
    Write-Host $daily.message -ForegroundColor Cyan
    
    # Test overdue alerts
    Write-Host "⚠️ Triggering overdue alerts..." -ForegroundColor Yellow
    $overdue = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/trigger-overdue" -Method POST -Headers $headers
    Write-Host $overdue.message -ForegroundColor Cyan
    
    # Test weekly summary
    Write-Host "📊 Triggering weekly summary..." -ForegroundColor Yellow
    $weekly = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reminders/trigger-weekly" -Method POST -Headers $headers
    Write-Host $weekly.message -ForegroundColor Cyan
    
    Write-Host "🎉 All tests completed successfully!" -ForegroundColor Green
    
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
```

Run with: `.\test-reminders.ps1`

This comprehensive guide should help you test all aspects of the automated payment reminder system!
