const execa = require('execa')
const path = require('path')

async function main() {
    packElectron();
    startPlaywright();

    process.exit();
}

function startPlaywright() {
    execa.commandSync(
        [
            "jest --runInBand --config ./test/jest.json",
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '..'),
            extendEnv: true,
            env: {
                "ELECTRON_DISABLE_SECURITY_WARNINGS": "true",
                "ELECTRON_IS_TEST": "true",
                "ELECTRON_IS_TEST_AND_NOT_SHOW": "true",
            }
        }
    );
}

function packElectron() {
    execa.commandSync(
        [
            'npm run pack',
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '../..', "electron"),
            extendEnv: true,
            env: {
                "ELECTRON_IS_TEST": "true",
                "ELECTRON_IS_TEST_AND_NOT_SHOW": "true",
            }
        }
    );
}

module.exports = main()