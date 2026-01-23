const fs = require('fs');
const path = require('path');

// Copy Material Tailwind JS components to static/js
const sourcePath = path.join(
  __dirname,
  '..',
  'node_modules',
  '@material-tailwind',
  'html',
  'scripts',
  'ripple.js'
);

const destPath = path.join(
  __dirname,
  '..',
  'src',
  'main',
  'resources',
  'static',
  'js',
  'material-tailwind.js'
);

// Ensure directory exists
const destDir = path.dirname(destPath);
if (!fs.existsSync(destDir)) {
  fs.mkdirSync(destDir, { recursive: true });
}

// Copy file
try {
  fs.copyFileSync(sourcePath, destPath);
  console.log('✓ Material Tailwind JS copied successfully to static/js/');
} catch (error) {
  console.error('✗ Failed to copy Material Tailwind JS:', error.message);
  process.exit(1);
}
