const chokidar = require('chokidar');
const { spawn } = require('child_process');
const axios = require('axios');
const path = require('path');

// Configuration
const MCP_SERVER_URL = process.env.MCP_SERVER_URL || 'http://localhost:3000/logs';
const PROJECT_ROOT = process.env.PROJECT_ROOT || path.resolve(__dirname, '..');

console.log('Starting MCP Client');
console.log(`Watching project: ${PROJECT_ROOT}`);
console.log(`MCP Server endpoint: ${MCP_SERVER_URL}`);

// Watcher setup: exclude node_modules, .git, build folders at project root
const watcher = chokidar.watch([
  path.join(PROJECT_ROOT, 'src', '**', '*.kt'),
  path.join(PROJECT_ROOT, 'build.gradle.kts'),
  path.join(PROJECT_ROOT, 'settings.gradle.kts')
], {
  ignored: [
    path.join(PROJECT_ROOT, '**', 'node_modules', '**'),
    path.join(PROJECT_ROOT, '**', '.git', '**'),
    path.join(PROJECT_ROOT, '**', 'build', '**')
  ],
  persistent: true,
  ignoreInitial: true
});

let buildInProgress = false;
let buildQueued = false;

function runBuildAndSend() {
  if (buildInProgress) {
    buildQueued = true;
    return;
  }
  buildInProgress = true;
  console.log('Changes detected. Running Gradle run...');

  const gradlew = process.platform === 'win32' ? 'gradlew.bat' : './gradlew';
  const buildFn = spawn(gradlew, ['run'], { cwd: PROJECT_ROOT, shell: true });

  let logs = '';
  buildFn.stdout.on('data', data => { logs += data.toString(); });
  buildFn.stderr.on('data', data => { logs += data.toString(); });

  buildFn.on('close', async (code) => {
    console.log(`Build finished with exit code ${code}`);
    try {
      const resp = await axios.post(MCP_SERVER_URL, { exitCode: code, logs }, { timeout: 300000 });
      console.log('Logs sent to MCP server:', resp.data);
    } catch (error) {
      console.error('Error sending logs:', error.message);
    }
    buildInProgress = false;
    if (buildQueued) {
      buildQueued = false;
      runBuildAndSend();
    }
  });
}

watcher.on('change', filePath => {
  console.log(`File changed: ${filePath}`);
  runBuildAndSend();
});

// Trigger initial build
runBuildAndSend(); 