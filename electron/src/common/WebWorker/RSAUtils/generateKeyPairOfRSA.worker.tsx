import NodeRSA from "node-rsa";
import registerWebworker from 'webworker-promise/lib/register'

registerWebworker(async () => {
    const rsa = new NodeRSA({ b: 4096 });
    rsa.setOptions({ encryptionScheme: "pkcs1" });
    return {
        privateKey: rsa.exportKey("pkcs8-private-der").toString("hex"),
        publicKey: rsa.exportKey("pkcs8-public-der").toString("hex"),
    }
});
