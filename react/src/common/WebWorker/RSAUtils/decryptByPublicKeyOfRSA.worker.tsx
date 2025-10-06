import NodeRSA from "node-rsa";
import registerWebworker from 'webworker-promise/lib/register'

registerWebworker(async ({
  data,
  publicKeyOfRSA,
}: {
  data: string,
  publicKeyOfRSA: string,
}) => {
  const rsa = new NodeRSA(Buffer.from(publicKeyOfRSA, "hex"), "pkcs8-public-der", { encryptionScheme: "pkcs1" });
  return rsa.decryptPublic(Buffer.from(data, "hex"), "utf8");
});
