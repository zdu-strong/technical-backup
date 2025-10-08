const execa = require("execa")
const path = require("path")

async function main() {
    await startClient();
    process.exit();
}

async function startClient() {
    await execa.command(
        "rsbuild dev",
        {
            stdio: "inherit",
            cwd: path.join(__dirname, ".."),
            extendEnv: true,
            env: {
                "RSBUILD_OPEN": process.env.RSBUILD_PORT || true
            }
        }
    );
}

module.exports = main()