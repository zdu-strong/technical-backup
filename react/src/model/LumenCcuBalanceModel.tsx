import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import { LumenCurrencyModel } from "@model/LumenCurrencyModel";

@jsonObject
export class LumenCcuBalanceModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(LumenCurrencyModel)
    currency!: LumenCurrencyModel;

    @jsonMember(String)
    currencyBalance!: string;

    @jsonMember(String)
    ccuBalance!: string;

    constructor() {
        makeAutoObservable(this);
    }
}

