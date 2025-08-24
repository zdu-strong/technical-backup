const execa = require("execa")
const path = require("path")

async function main() {
  await startClient();
  process.exit();
}

async function startClient() {
  await execa.command(
    "rsbuild dev --open",
    {
      stdio: "inherit",
      cwd: path.join(__dirname, ".."),
      extendEnv: true,
      env: {
      }
    }
  );
}

module.exports = main()