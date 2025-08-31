import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";

@jsonObject
export class LumenCurrencyModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(String)
  name!: string;

  constructor() {
    makeAutoObservable(this);
  }
}

