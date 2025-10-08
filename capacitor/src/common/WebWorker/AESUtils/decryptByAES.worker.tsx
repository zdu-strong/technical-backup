import registerWebworker from 'webworker-promise/lib/register'
import { createDecipheriv } from 'crypto';

registerWebworker(async ({
    data,
    secretKeyOfAES,
}: {
    data: string,
    secretKeyOfAES: string,
}) => {
    const dataByteList = Buffer.from(data, "hex");
    const salt = Buffer.from(dataByteList.buffer.slice(0, 16)).toString("hex");
    const tag = Buffer.from(dataByteList.buffer.slice(dataByteList.length - 16)).toString("hex");
    const encrypted = Buffer.from(dataByteList.buffer.slice(16, dataByteList.length - 16)).toString("hex");
    const decipher = createDecipheriv("aes-256-gcm", Buffer.from(secretKeyOfAES, "hex"), Buffer.from(salt, "hex"), { authTagLength: 16 });
    decipher.setAuthTag(Buffer.from(tag, "hex"));
    const result = Buffer.concat([Buffer.from(decipher.update(encrypted, "hex", "hex"), "hex"), Buffer.from(decipher.final("hex"), "hex")]).toString("utf-8");
    return result;
});
