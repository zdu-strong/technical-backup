const {
  defineConfig,
  globalIgnores,
} = require("eslint/config");

const tsParser = require("@typescript-eslint/parser");
const typescriptEslintEslintPlugin = require("@typescript-eslint/eslint-plugin");
const globals = require("globals");
const js = require("@eslint/js");

const {
  FlatCompat,
} = require("@eslint/eslintrc");

const compat = new FlatCompat({
  baseDirectory: __dirname,
  recommendedConfig: js.configs.recommended,
  allConfig: js.configs.all
});

module.exports = defineConfig([{
  languageOptions: {
    parser: tsParser,
    sourceType: "module",

    parserOptions: {
      project: "tsconfig.electron.main.json",
      tsconfigRootDir: __dirname,
    },

    globals: {
      ...globals.node,
      ...globals.jest,
    },
  },

  plugins: {
    "@typescript-eslint": typescriptEslintEslintPlugin,
  },

  extends: compat.extends("eslint:recommended", "plugin:@typescript-eslint/recommended"),

  rules: {
    "@typescript-eslint/interface-name-prefix": "off",
    "@typescript-eslint/explicit-function-return-type": "off",
    "@typescript-eslint/explicit-module-boundary-types": "off",
    "@typescript-eslint/no-explicit-any": "off",
    "@typescript-eslint/no-var-requires": "off",
    "@typescript-eslint/no-require-imports": "off",
    "@typescript-eslint/prefer-as-const": "warn",
    "@typescript-eslint/no-empty-object-type": "off",
    "prefer-const": "warn",
    "no-var": "warn",
    "@typescript-eslint/no-unused-vars": "off",
  },
}, globalIgnores(["**/.eslintrc.js"])]);
