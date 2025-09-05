import { defineConfig, loadEnv } from '@rsbuild/core';
import { pluginReact } from '@rsbuild/plugin-react';
import { pluginBabel } from '@rsbuild/plugin-babel';
import { pluginNodePolyfill } from "@rsbuild/plugin-node-polyfill";
import { pluginEslint } from '@rsbuild/plugin-eslint';
import { pluginTypeCheck } from '@rsbuild/plugin-type-check';

export default defineConfig({
  plugins: [
    pluginReact(),
    pluginBabel({
      babelLoaderOptions: (cfg, { addPlugins }) => {
        addPlugins(['babel-plugin-macros']);
      },
    }),
    pluginNodePolyfill(),
    pluginEslint(),
    pluginTypeCheck(),
  ],
  resolve: {
    "alias": {
      "@": "./src",
      "@component": "./src/component",
      "@common": "./src/common",
      "@api": "./src/api",
      "@enums": "./src/enums",
      "@model": "./src/model",
      "@service": "./src/service",
    }
  },
  tools: {
     rspack(config){
      config.cache = false;
     }
  },
  source: {
    define: loadEnv({ prefixes: ['REACT_APP_'] }).publicVars,
    decorators: {
      version: 'legacy',
    },
  },
  output: {
    distPath: {
      root: 'build'
    },
    assetPrefix: "auto"
  },
  server: {
    port: Number(process.env.RSBUILD_PORT) || undefined,
    cors: true
  },
});
