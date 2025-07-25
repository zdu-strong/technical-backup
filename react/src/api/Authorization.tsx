import { encryptByPublicKeyOfRSA } from "@common/RSAUtils";
import { GlobalUserInfo, removeGlobalUserInfo, setGlobalUserInfo } from "@common/Server";
import { UserEmailModel } from "@model/UserEmailModel";
import { UserModel } from "@model/UserModel";
import { VerificationCodeEmailModel } from "@model/VerificationCodeEmailModel";
import axios from "axios";
import { TypedJSON } from "typedjson";
import { getKeyOfRSAPublicKey } from "@api/EncryptDecrypt";

export async function signUp(password: string, nickname: string, userEmailList: UserEmailModel[]): Promise<void> {
  await signOut();
  const { data } = await axios.post(`/sign-up`, {
    username: nickname,
    password: await encryptByPublicKeyOfRSA(password, await getKeyOfRSAPublicKey()),
    userEmailList: userEmailList,
  });
  const user = new TypedJSON(UserModel).parse(data)!;
  await setGlobalUserInfo(user);
}

export async function sendVerificationCode(email: string) {
  const { data } = await axios.post("/email/send-verification-code", null, { params: { email } });
  return new TypedJSON(VerificationCodeEmailModel).parse(data)!;
}

export async function signIn(username: string, password: string): Promise<void> {
  await signOut();
  const { data } = await axios.post(`/sign-in`, null, {
    params: {
      username: username,
      password: await encryptByPublicKeyOfRSA(password, await getKeyOfRSAPublicKey()),
    }
  });
  const user = new TypedJSON(UserModel).parse(data)!;
  await setGlobalUserInfo(user);
}

export async function signOut() {
  if (GlobalUserInfo.accessToken) {
    try {
      await axios.post("/sign-out");
    } catch {
      // do nothing
    }
    removeGlobalUserInfo();
  }
}

export async function isSignIn() {
  if (!GlobalUserInfo.accessToken) {
    return false;
  }
  try {
    await axios.get("/user/me");
  } catch (e) {
    if (e && (e as any).status === 401) {
      removeGlobalUserInfo();
      return false;
    }
  }
  return true;
}