# Project Plan

Enhance the AI Signals Trading App for real-time trading using Shoonya and Dhan. Focus on fixing WebSocket connectivity, implementing breakout-based signals with market timing awareness, and enabling live trading while maintaining the existing UI. Ensure Play Store compliance and logical correctness across all Kotlin files.

## Project Brief

# Project Brief: AI Signals Trading App

## Features
*   **Real-Time WebSocket Connectivity (Shoonya)**: Integrated low-latency price streaming to ensure the breakout signal engine and live trading execution operate on the most current market data.
*   **AI-Driven Breakout Signal Engine**: Automated backend logic that detects and triggers trading signals based on live market breakouts across Indices, Stocks, and MCX commodities.
*   **Automated Market Session Control**: Intelligent timing logic that restricts trading activity and signal generation based on IST market hours for different asset classes (e.g., Equity session ends at 15:30 IST).
*   **Integrated Live Trading & Portfolio Management**: Full-cycle trade execution via Shoonya APIs with synchronized portfolio and position tracking using Dhan integration.

## High-Level Tech Stack
*   **Core**: Kotlin, Jetpack Compose
*   **Navigation**: Jetpack Navigation 3 (State-driven architecture)
*   **UI/UX**: Compose Material Adaptive Library (Adaptive layouts)
*   **Concurrency**: Kotlin Coroutines & Flow (Real-time data streams)
*   **Networking**: Shoonya & Dhan APIs (REST & WebSockets)
*   **Security**: Gemini AI Studio (Secure Secrets Management)

## Implementation Steps

### Task_1_Infrastructure_And_Secrets: Integrate API secrets using Gemini AI Studio, verify dependencies (Navigation 3, Material Adaptive), and fix any existing Kotlin errors in the codebase.
- **Status:** COMPLETED
- **Updates:** Integrated Shoonya and Dhan API secrets using BuildConfig. Updated dependencies to latest Jetpack Navigation 3 and Compose Material Adaptive versions. Fixed memory allocation issues in Gradle. Verified project builds successfully. User provided GitHub PAT for pushing changes.
- **Acceptance Criteria:**
  - API keys integrated from Gemini secrets
  - Project builds successfully
  - All existing Kotlin files are error-free
- **Duration:** N/A

### Task_2_WebSocket_And_Market_Sessions: Implement or fix Shoonya WebSocket for real-time price streaming and develop automated market session control based on IST IST hours.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Shoonya WebSocket connects and streams live data
  - Market timing logic restricts trading outside IST hours
  - WebSocket handles reconnection and low-latency streaming
- **StartTime:** 2026-08-13 20:20:30 IST

### Task_3_Signal_Engine_And_Trading_Execution: Implement the AI-driven breakout signal engine and integrate trade execution via Shoonya and portfolio synchronization via Dhan, replacing all dummy data.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Breakout signal logic triggers on live market data
  - Trade execution works via Shoonya API
  - Dhan API provides real-time portfolio and position data

### Task_4_PlayStore_Readiness_And_GitHub: Ensure the app meets Play Store requirements, perform final code refinement, and push the project to the GitHub repository.
- **Status:** PENDING
- **Acceptance Criteria:**
  - App is compliant with latest Play Store guidelines
  - No dummy data remains in the application
  - Final code is pushed to GitHub repository

### Task_5_Run_And_Verify: Conduct a final run of the application to ensure stability and verify that all requirements (WebSocket, Signals, Trading) are met without crashes.
- **Status:** PENDING
- **Acceptance Criteria:**
  - App does not crash during operation
  - All features align with the project brief
  - Build pass and all existing tests pass

