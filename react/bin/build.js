const execa = require("execa")
const path = require("path")

async function main() {
  await build();
  process.exit();
}

async function build() {
  await execa.command(
    [
      "rsbuild build",
    ].join(" "),
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