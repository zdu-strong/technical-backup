import registerWebworker from 'webworker-promise/lib/register'
import { v7 } from 'uuid';
import { createHash, createCipheriv } from 'crypto';

registerWebworker(async ({
  data,
  secretKeyOfAES,
}: {
  data: string,
  secretKeyOfAES: string,
}) => {
  const salt = createHash("MD5").update(v7(), "utf-8").digest("hex");
  const cipher = createCipheriv("aes-256-gcm", Buffer.from(secretKeyOfAES, "hex"), Buffer.from(salt, "hex"), { authTagLength: 16 });
  const result = Buffer.concat([Buffer.from(salt, "hex"), Buffer.from(cipher.update(data, "utf-8", "hex"), "hex"), Buffer.from(cipher.final("hex"), "hex"), Buffer.from(cipher.getAuthTag().toString("hex"), "hex")]).toString("hex");
  return result;
});
