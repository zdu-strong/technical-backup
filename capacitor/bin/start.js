const execa = require('execa')
const treeKill = require('tree-kill')
const util = require('util')
const path = require('path')
const waitOn = require('wait-on')
const axios = require('axios')

async function main() {
  if (isTestEnvironment()) {
    await runCapacitorForCypress();
    process.exit();
  } else {
    const avaliablePort = 3000;
    await startReact(avaliablePort);
    process.exit();
  }
}

async function startReact(avaliablePort) {
  const childProcessOfReact = execa.command(
    [
      "rsbuild dev",
      "--open"
    ].join(" "),
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
        "RSBUILD_PORT": String(avaliablePort),
      },
    }
  );

  await Promise.race([childProcessOfReact, waitOn({ resources: [`http://127.0.0.1:${avaliablePort}`] })]);
  for (let i = 2000; i > 0; i--) {
    await axios.get(`http://127.0.0.1:${avaliablePort}`);
  }
  await Promise.race([childProcessOfReact]);
  await util.promisify(treeKill)(childProcessOfReact.pid).catch(async () => null);
  return { childProcessOfReact };
}

function isTestEnvironment() {
  return process.env.CAPACITOR_CYPRESS_IS_TEST === "true";
}

async function runCapacitorForCypress() {
  const childProcessOfReact = await execa.command(
    "rsbuild dev",
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
      },
    }
  );
  await Promise.race([childProcessOfReact]);
  await util.promisify(treeKill)(childProcessOfReact.pid).catch(async () => null);
}

module.exports = main()