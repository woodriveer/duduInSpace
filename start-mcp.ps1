# PowerShell script to start both mcp-server and mcp-client

# Determine the script's directory
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition

# Start MCP Server
Write-Host 'Starting MCP Server...'
Start-Process powershell -ArgumentList @('-NoExit', "cd '$root\mcp-server'; npm install; npm start")

# Start MCP Client
Write-Host 'Starting MCP Client...'
Start-Process powershell -ArgumentList @('-NoExit', "cd '$root\mcp-client'; npm install; npm start")

Write-Host 'All processes launched.' 