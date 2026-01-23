const withMT = require("@material-tailwind/html/utils/withMT");

module.exports = withMT({
  content: [
    "./src/main/resources/templates/**/*.html",
    "./frontend/js/**/*.js",
    "./node_modules/@material-tailwind/html/theme/components/**/*.{js,ts,jsx,tsx}"
  ],
  theme: {
    extend: {
      colors: {
        // Custom color palette dla Lexpage
        primary: {
          50: '#f0f9ff',
          100: '#e0f2fe',
          200: '#bae6fd',
          300: '#7dd3fc',
          400: '#38bdf8',
          500: '#0ea5e9',
          600: '#0284c7',
          700: '#0369a1',
          800: '#075985',
          900: '#0c4a6e',
          950: '#082f49',
        },
        burgundy: {
          50: '#fdf2f4',
          100: '#fce7eb',
          200: '#f9d0d9',
          300: '#f4a6b8',
          400: '#ec6d8e',
          500: '#e0426a',
          600: '#cc2251',
          700: '#a91741',
          800: '#8d153c',
          900: '#781537',
          950: '#42081b',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
    require('@tailwindcss/typography'),
  ],
});
