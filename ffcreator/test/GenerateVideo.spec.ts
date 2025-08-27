import path from 'path'
import { v7 } from 'uuid'
import execa from 'execa'

it('', async () => {
  const cacheDir = path.join(__dirname, "..", "output", v7());
  const outputFile = path.join(__dirname, "..", "output", v7(), "video.mp4")
  execa.commandSync([
    "npm run production:run",
  ].join(" "), {
    stdio: "inherit",
    cwd: path.join(__dirname, ".."),
    extendEnv: true,
    env: {
      "FFCREATOR_CACHE_DIRECTORY": cacheDir,
      "FFCREATOR_OUTPUT_VIDEO_FILE_PATH": outputFile,
    }
  })
})