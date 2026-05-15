const path = require("path")
const { execSync } = require("child_process")

function main(){
    execSync(
        [
            "serve --single --cors --no-clipboard --no-port-switching --no-request-logging --no-compression --listen=443 build",
        ].join(" "),
        {
            stdio: "inherit",
            cwd: path.join(__dirname, "."),
            env: {
                ...process.env,
            },
        }
    );
}

module.exports = main()