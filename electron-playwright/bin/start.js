const path = require('path')
const { execSync } = require('child_process')

async function main() {
    packElectron();
    startPlaywright();
}

function startPlaywright() {
    execSync(
        [
            "jest --runInBand --watch --config ./test/jest.json",
        ].join(' '),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
            env: {
                ...process.env,
                "ELECTRON_DISABLE_SECURITY_WARNINGS": "true",
                "ELECTRON_IS_TEST": "true",
                "ELECTRON_IS_TEST_AND_NOT_SHOW": "false",
            },
        }
    );
}

function packElectron() {
    execSync(
        [
            'npm run pack',
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '../..', "electron"),
            env: {
                ...process.env,
                "ELECTRON_IS_TEST": "true",
                "ELECTRON_IS_TEST_AND_NOT_SHOW": "false",
            }
        }
    );
}

module.exports = main()