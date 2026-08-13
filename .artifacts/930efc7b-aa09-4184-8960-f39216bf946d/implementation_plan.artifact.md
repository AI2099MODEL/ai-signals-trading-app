# Shoonya WebSocket and Market Session Control Implementation Plan

This plan outlines the integration of Shoonya WebSocket for real-time price streaming and the development of automated market session control based on IST hours.

## User Review Required

> [!IMPORTANT]
> **API Secrets**: Ensure `SHOONYA_SECRET` and other required credentials (like User ID, Password, App Key) are added to `local.properties`. I will implement the code to use these secrets, but they must be present for the app to function.
> **GitHub PAT**: I will use the provided GitHub PAT to push changes to the repository `https://github.com/AI2099MODEL/ai-signals-trading-app`.

## Proposed Changes

### 1. Data Layer (Shoonya Integration)

#### [NEW] [ShoonyaModels.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/data/model/ShoonyaModels.kt)
- Define data classes for Login request/response.
- Define WebSocket message structures for Connection, Subscription, and Price updates.

#### [NEW] [ShoonyaApiService.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/data/api/ShoonyaApiService.kt)
- Retrofit interface for Shoonya REST API (Login, etc.).

#### [NEW] [ShoonyaWebSocketManager.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/data/websocket/ShoonyaWebSocketManager.kt)
- Implement `OkHttp` WebSocket listener.
- Logic for authentication handshake over WebSocket.
- Reconnection logic with exponential backoff.
- Flow-based data streaming for low-latency updates.

### 2. Domain Layer (Market Session Control)

#### [NEW] [MarketSessionManager.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/utils/MarketSessionManager.kt)
- Utility to determine if current time is within IST market hours.
- **Equity/Indices**: 09:15 to 15:30 IST.
- **MCX Commodities**: 09:00 to 23:30/23:50 IST (depending on day/season).

### 3. UI Layer

#### [NEW] [MarketViewModel.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/ui/viewmodel/MarketViewModel.kt)
- ViewModel to manage Shoonya session and WebSocket stream.
- Expose `StateFlow` for live prices and market session status.

#### [MODIFY] [MainActivity.kt](file:///D:/Projects/Stockbreak/MyApplication/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Update UI to show real-time price updates.
- Display market session status (Open/Closed).

### 4. Git Integration

- Initialize Git repository if not present.
- Commit all changes.
- Push to `https://github.com/AI2099MODEL/ai-signals-trading-app` using the provided PAT.

## Verification Plan

### Automated Tests
- Unit tests for `MarketSessionManager` to verify boundary conditions of IST timings.
- JSON parsing tests for Shoonya WebSocket messages.

### Manual Verification
- Build the app and verify the UI updates with (simulated or real) WebSocket data.
- Check logs for WebSocket connection and reconnection events.
