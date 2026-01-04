const execa = require('execa')
const getPort = require('get-port')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const waitOn = require('wait-on')
const { timer } = require('rxjs')
const { default: axios } = require("axios")

async function main() {
    const { avaliableClientPort, childProcessOfReact } = await startClient();
    const { childProcessOfCypress } = await startCypress(avaliableClientPort);
    const { childProcessOfEslint } = await startEslint();

    await Promise.race([childProcessOfReact, childProcessOfCypress, childProcessOfEslint]);
    await util.promisify(treeKill)(childProcessOfReact.pid).catch(async () => null);
    await util.promisify(treeKill)(childProcessOfCypress.pid).catch(async () => null);
    await util.promisify(treeKill)(childProcessOfEslint.pid).catch(async () => null);

    process.exit();
}

async function startClient() {
    execa.commandSync(
        [
            'cargo make',
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '../../dioxus'),
            extendEnv: true,
        }
    );
    const avaliableClientPort = await getPort();
    const childProcessOfReact = execa.command(
        [
            `serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=${avaliableClientPort} ../dioxus/target/dx/rust-app/release/web/public`,
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '..'),
            extendEnv: true,
        }
    );
    await Promise.race([
        childProcessOfReact,
        waitOn({
            resources: [
                `http://127.0.0.1:${avaliableClientPort}`
            ]
        })
    ]);
    for (let i = 1000; i > 0; i--) {
        await timer(1).toPromise();
    }
    return { avaliableClientPort, childProcessOfReact };
}

async function startCypress(avaliableClientPort) {
    const childProcessOfCypress = execa.command(
        [
            'cypress open',
            "--e2e"
        ].join(' '),
        {
            stdio: 'inherit',
            cwd: path.join(__dirname, '..'),
            extendEnv: true,
            env: {
                "CYPRESS_BASE_URL": `http://127.0.0.1:${avaliableClientPort}`,
                "CYPRESS_VERIFY_TIMEOUT": "120000",
            }
        }
    );
    return { childProcessOfCypress };
}

async function startEslint() {
    const childProcessOfEslint = execa.command(
        [
            "node bin/eslint.js",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, '..'),
            extendEnv: true,
        }
    )
    return { childProcessOfEslint };
}

module.exports = main()