import chokidar from 'chokidar';
import { exec } from 'child_process';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// Constants
const PROJECT_ROOT = path.join(__dirname, '../'); // DuduInSpace root directory
const LOG_DIR = path.join(PROJECT_ROOT, 'build/logs'); // Store logs in project's build directory
const WATCH_DIRS = [
    path.join(PROJECT_ROOT, 'src'),
    path.join(PROJECT_ROOT, 'build.gradle.kts'),
    path.join(PROJECT_ROOT, 'settings.gradle.kts')
];

// Ensure log directory exists
if (!fs.existsSync(LOG_DIR)) {
    fs.mkdirSync(LOG_DIR, { recursive: true });
}

class BuildMonitor {
    constructor() {
        this.setupWatcher();
    }

    setupWatcher() {
        const watcher = chokidar.watch(WATCH_DIRS, {
            ignored: [
                /(^|[\/\\])\../,  // dotfiles
                /(^|[\/\\])node_modules/,
                /(^|[\/\\])build\/logs/,  // ignore our own log directory
                /\.git/  // ignore git directory
            ],
            persistent: true
        });

        watcher.on('change', async (filePath) => {
            const relativePath = path.relative(PROJECT_ROOT, filePath);
            console.log(`File changed: ${relativePath}`);
            const buildResult = await this.triggerBuild();
            
            if (!buildResult.success) {
                console.log('Build failed, attempting to fix...');
                await this.fixBuildError(buildResult.logFile);
            }
        });

        console.log(`Watching for changes in ${path.relative(process.cwd(), PROJECT_ROOT)}...`);
        console.log('Monitored directories:');
        WATCH_DIRS.forEach(dir => {
            console.log(` - ${path.relative(PROJECT_ROOT, dir)}`);
        });
    }

    async triggerBuild() {
        return new Promise((resolve, reject) => {
            // Use gradlew.bat on Windows
            const gradleCmd = process.platform === 'win32' ? '.\\gradlew.bat' : './gradlew';
            
            exec(`${gradleCmd} build`, { cwd: PROJECT_ROOT }, (error, stdout, stderr) => {
                const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
                const logFileName = `build-${timestamp}.log`;
                const logFile = path.join(LOG_DIR, logFileName);
                
                const logContent = `Exit Code: ${error ? error.code : 0}\n\nStdout:\n${stdout}\n\nStderr:\n${stderr}`;
                fs.writeFileSync(logFile, logContent);

                if (error) {
                    console.log('Build failed. Error output:');
                    console.log(stderr);
                    resolve({
                        success: false,
                        logFile: logFileName,
                        error: stderr
                    });
                } else {
                    console.log('Build succeeded');
                    resolve({
                        success: true,
                        logFile: logFileName
                    });
                }
            });
        });
    }

    async checkBuildStatus() {
        const files = fs.readdirSync(LOG_DIR);
        if (files.length === 0) {
            return { status: 'no_builds' };
        }

        const latestLog = files
            .map(file => ({
                name: file,
                time: fs.statSync(path.join(LOG_DIR, file)).mtime.getTime()
            }))
            .sort((a, b) => b.time - a.time)[0];

        const logContent = fs.readFileSync(path.join(LOG_DIR, latestLog.name), 'utf8');
        const exitCode = parseInt(logContent.match(/Exit Code: (\d+)/)[1]);

        return {
            status: exitCode === 0 ? 'success' : 'failure',
            logFile: latestLog.name,
            timestamp: new Date(latestLog.time).toISOString()
        };
    }

    async fixBuildError(logFileName) {
        const logFile = path.join(LOG_DIR, logFileName);
        try {
            const logContent = fs.readFileSync(logFile, 'utf8');
            console.log('Analyzing build error...');
            
            // Parse Kotlin compilation errors
            const errors = logContent.match(/e: .*\.kt:\d+:\d+.*(?:\r?\n(?!e: ).*?)*/g) || [];
            
            if (errors.length > 0) {
                console.log('\nFound Kotlin compilation errors:');
                errors.forEach((error, index) => {
                    console.log(`\nError ${index + 1}:`);
                    console.log(error);
                });
            }
            
            return {
                analysis: logContent,
                errors: errors,
                suggestedFixes: []
            };
        } catch (error) {
            console.error('Error reading log file:', error);
            return {
                analysis: 'Failed to read log file',
                errors: [],
                suggestedFixes: []
            };
        }
    }
}

// Start the build monitor
const monitor = new BuildMonitor();
console.log('Build monitor started'); 