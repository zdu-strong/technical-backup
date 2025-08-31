import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import { LumenCurrencyModel } from "@model/LumenCurrencyModel";

@jsonObject
export class LumenCcuBalanceModel {

  @jsonMember(String)
  id!: string;

  @jsonMember(LumenCurrencyModel)
  currency!: LumenCurrencyModel;

  @jsonMember(Number)
  currencyBalance!: number;

  @jsonMember(Number)
  ccuBalance!: number;

  constructor() {
    makeAutoObservable(this);
  }
}

