const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;
const LOG_DIR = path.join(__dirname, 'logs');

// Ensure the log directory exists
if (!fs.existsSync(LOG_DIR)) {
    fs.mkdirSync(LOG_DIR);
}

app.use(express.json({ limit: '10mb' })); // Adjust limit as needed

app.post('/logs', (req, res) => {
    const { exitCode, logs } = req.body;
    if (exitCode === 0) {
        // Clear all existing logs on successful build
        fs.readdirSync(LOG_DIR).forEach(file => {
            const filePath = path.join(LOG_DIR, file);
            fs.unlinkSync(filePath);
        });
    }
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const fileName = `build-${timestamp}.log`;
    const filePath = path.join(LOG_DIR, fileName);

    const content = `Exit Code: ${exitCode}\n\n${logs}`;

    fs.writeFile(filePath, content, (err) => {
        if (err) {
            console.error('Error writing log file:', err);
            return res.status(500).json({ status: 'error' });
        }
        res.json({ status: 'ok', file: fileName });
    });
});

app.listen(PORT, () => {
    console.log(`MCP Server listening on port ${PORT}`);
}); 