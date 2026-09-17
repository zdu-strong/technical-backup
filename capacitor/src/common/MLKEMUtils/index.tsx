import { runWoker } from "@common/WebWorker/WebWorkerUtils";

export async function generateKeyPairOfMLKEM(): Promise<{ privateKey: string, publicKey: string }> {
    return await runWoker(new Worker(new URL('../../common/WebWorker/MLKEMUtils/generateKeyPairOfMLKEM.worker', import.meta.url), { type: "module" }));
}

export async function encryptByPublicKeyOfMLKEM(data: string, publicKeyOfMLKEM: string): Promise<string> {
    return await runWoker(new Worker(new URL('../../common/WebWorker/MLKEMUtils/encryptByPublicKeyOfMLKEM.worker', import.meta.url), { type: "module" }),
        {
            data,
            publicKeyOfMLKEM,
        }
    );
}

export async function decryptByPrivateKeyOfMLKEM(data: string, privateKeyOfMLKEM: string): Promise<string> {
    return await runWoker(new Worker(new URL('../../common/WebWorker/MLKEMUtils/decryptByPrivateKeyOfMLKEM.worker', import.meta.url), { type: "module" }),
        {
            data,
            privateKeyOfMLKEM,
        }
    );
}
