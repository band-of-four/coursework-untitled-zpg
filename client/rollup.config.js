import vue from 'rollup-plugin-vue';
import babel from 'rollup-plugin-babel';
import replace from 'rollup-plugin-replace';
import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';

const production = process.env.NODE_ENV === "production",
      development = !production;

/* https://github.com/rollup/rollup/issues/558#issuecomment-353797769 */
const fs = require('fs'), path = require('path');
const resolveRoot = ({ root }) => ({
  resolveId: (importee, importer) => {
    if (!importee.startsWith('/')) return;

    if (importee.endsWith('.js') || importee.endsWith('.vue')) {
      const rootPath = path.resolve(__dirname, `${root}${importee}`)
      if (fs.existsSync(rootPath)) return rootPath;
    }
    else {
      const rootPath = path.resolve(__dirname, `${root}${importee}/index.js`)
      if (fs.existsSync(rootPath)) return rootPath;
    }
  }
});

export default {
  input: './src/app.js',
  output: {
    file: './dist/app.js',
    format: 'iife',
    sourcemap: development ? 'inline' : false
  },
  plugins: [
    resolveRoot({
      root: './src'
    }),
    babel({
      exclude: 'node_modules/**',
      plugins: [
        '@babel/plugin-proposal-object-rest-spread',
        ['@babel/plugin-proposal-class-properties', { loose: true }],
        '@babel/plugin-transform-classes'
      ]
    }),
    resolve({
      jsnext: true,
      main: true
    }),
    commonjs({
      sourceMap: false,
    }),
    replace({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV),
      'dist/': production ? 'client/dist/' : 'dist/'
    }),
    vue({ css: false })
  ]
}; 
