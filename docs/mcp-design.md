# Model Context Protocol (MCP) Design

## Overview
The Model Context Protocol (MCP) is a lightweight communication protocol designed to capture build logs from a local Gradle project and send them to an external MCP server. This allows the model (AI assistant or other services) to access real-time build information, errors, and performance metrics, improving the context for future suggestions or analysis.

## Components
1. **MCP Client**  
   - Watches the project source directory for changes.  
   - Triggers `./gradlew build` on file changes (e.g., after accepting code suggestions).  
   - Captures build output (stdout and stderr).  
   - Sends logs to the MCP server via HTTP.

2. **MCP Server**  
   - Receives build logs via HTTP POST requests.  
   - Persists logs to disk under `logs/` directory.  
   - Optionally processes or indexes logs for querying.

## Protocol
- **Endpoint**: `POST /logs`  
- **Payload** (JSON):  
  ```json
  {
    "exitCode": 0,
    "logs": "Full build output as text"
  }
  ```
- **Response** (JSON):  
  ```json
  {
    "status": "ok",
    "file": "build-<timestamp>.log"
  }
  ```

## Usage
1. Start the MCP server:
   ```bash
   cd mcp-server
   npm install
   npm start
   ```
2. Start the MCP client in the root project:
   ```bash
   cd mcp-client
   npm install
   npm start
   ```
3. Make code changes or accept suggestions. The client will automatically rebuild and send logs.

## Extensions
- Add authentication to secure log uploads.
- Parse logs for errors/warnings and send structured data.
- Integrate with ChatGPT via WebSocket for live model context updates. 