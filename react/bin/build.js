const execa = require("execa");
const path = require("path");
const fs = require("fs");

async function main() {
    await build();
    await cacheReload();
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

async function cacheReload() {
    const filePathOfIndexHtml = path.join(__dirname, "..", "build", "index.html");
    let fileContentOfIndexHtml = await fs.promises.readFile(filePathOfIndexHtml, "utf-8");
    fileContentOfIndexHtml = fileContentOfIndexHtml.replace("<script", getScriptContentForReload() + "<script");
    await fs.promises.writeFile(filePathOfIndexHtml, fileContentOfIndexHtml, "utf-8");
}

function getScriptContentForReload() {
    return `\n<script>
    window.addEventListener('error', function (e) {
        if (e && e.filename && e.message) {
            if (e.filename.indexOf(".js") > -1 || e.filename.indexOf(".css") > -1) {
                if (e.message.indexOf("SyntaxError") > -1 && e.message.indexOf("Unexpected token '<'") > -1) {
                    console.log("Cache error, reload");
                    window.location.reload(true);
                }
            }
        }
    }, true);
</script>\n`;
}

module.exports = main()