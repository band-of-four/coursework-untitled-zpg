import vue from 'rollup-plugin-vue';
import babel from 'rollup-plugin-babel';
import replace from 'rollup-plugin-replace';
import resolve from 'rollup-plugin-node-resolve';
import commonjs from 'rollup-plugin-commonjs';

const production = process.env.NODE_ENV === "production",
      development = !production;

export default {
  input: './src/app.js',
  output: {
    file: './dist/app.js',
    format: 'iife',
    sourcemap: development ? 'inline' : false
  },
  plugins: [
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
