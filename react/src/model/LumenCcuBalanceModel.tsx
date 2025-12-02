import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import { LumenCurrencyModel } from "@model/LumenCurrencyModel";
import { BigDecimal, Big } from "bigdecimal.js";

@jsonObject
export class LumenCcuBalanceModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(LumenCurrencyModel)
    currency!: LumenCurrencyModel;

    @jsonMember({ deserializer: (value: any) => value === null || value === undefined ? null :new Big(value) })
    currencyBalance!: BigDecimal;

    @jsonMember({ deserializer: (value: any) => value === null || value === undefined ? null :new Big(value) })
    ccuBalance!: BigDecimal;

    constructor() {
        makeAutoObservable(this);
    }
}

