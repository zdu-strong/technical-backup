import NodeRSA from "node-rsa";
import registerWebworker from 'webworker-promise/lib/register'

registerWebworker(async () => {
    const rsa = new NodeRSA({ b: 4096 });
    rsa.setOptions({ encryptionScheme: "pkcs1" });
    return {
        privateKey: Buffer.from(rsa.exportKey("pkcs8-private-der")).toString("hex"),
        publicKey: Buffer.from(rsa.exportKey("pkcs8-public-der")).toString("hex"),
    }
});
