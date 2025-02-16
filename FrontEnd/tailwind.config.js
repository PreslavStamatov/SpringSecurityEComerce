/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {colors: {
      primary: '#EB3986',
      borderPrimary: '#F59CC2',
      primaryBg: '#F6F0EF',
      productFilterText: '#444444',
    }
    },
  },
  plugins: [],
  
}