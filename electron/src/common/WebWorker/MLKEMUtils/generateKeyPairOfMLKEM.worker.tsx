import { createMlKem1024 } from "mlkem";
import registerWebworker from 'webworker-promise/lib/register'

registerWebworker(async () => {
    const mlKemUtil = await createMlKem1024();
    const [publicKey, privateKey] = mlKemUtil.generateKeyPair();
    return {
        privateKey: Buffer.from(privateKey).toString("hex"),
        publicKey: Buffer.from(publicKey).toString("hex"),
    }
});
