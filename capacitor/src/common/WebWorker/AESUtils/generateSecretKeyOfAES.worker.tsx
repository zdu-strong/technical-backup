import registerWebworker from 'webworker-promise/lib/register'
import { v7 } from 'uuid';
import { createHash, pbkdf2Sync } from 'crypto';

registerWebworker(async ({
    password,
}: {
    password?: string,
}) => {
    if (!password) {
        password = v7();
    }
    const salt = createHash("MD5").update(Buffer.from(password, "utf-8")).digest("hex");
    return pbkdf2Sync(Buffer.from(password, "utf-8"), Buffer.from(salt, "hex"), 65536, 256 / 8, "sha256").toString("hex");
});
