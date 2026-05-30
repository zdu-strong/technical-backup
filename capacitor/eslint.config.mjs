import js from '@eslint/js';
import { defineConfig, globalIgnores } from 'eslint/config';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import globals from 'globals';
import tseslint from 'typescript-eslint';
import unusedImports from "eslint-plugin-unused-imports";

export default defineConfig([
    globalIgnores(['dist']),
    {
        files: ['**/*.{ts,tsx}'],
        extends: [
            js.configs.recommended,
            tseslint.configs.recommended,
            reactHooks.configs.flat['recommended-latest'],
            reactRefresh.configs.recommended,
        ],
        plugins: {
            "unused-imports": unusedImports,
        },
        languageOptions: {
            globals: globals.browser,
        },
        "rules": {
            "import/no-anonymous-default-export": [
                "off",
                {
                    "allowArrowFunction": true
                }
            ],
            "react-hooks/exhaustive-deps": "off",
            "@typescript-eslint/no-explicit-any": "off",
            "@typescript-eslint/prefer-as-const": "warn",
            "@typescript-eslint/no-empty-object-type": "off",
            "prefer-const": "warn",
            "no-var": "warn",
            "@typescript-eslint/no-unused-vars": "off",
            "react-refresh/only-export-components": "off",
            "unused-imports/no-unused-imports": "warn",
            "unused-imports/no-unused-vars": "off",
            "no-useless-assignment": "off"
        },
    },
]);