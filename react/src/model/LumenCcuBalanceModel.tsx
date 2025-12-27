import { makeAutoObservable } from "mobx-react-use-autorun";
import { jsonMember, jsonObject } from "typedjson";
import { LumenCurrencyModel } from "@model/LumenCurrencyModel";
import { Big, BigDecimal } from "bigdecimal.js";

@jsonObject
export class LumenCcuBalanceModel {

    @jsonMember(String)
    id!: string;

    @jsonMember(LumenCurrencyModel)
    currency!: LumenCurrencyModel;

    @jsonMember({ serializer: (value: BigDecimal) => value.toPlainString(), deserializer: (value: any) => new Big(value) })
    currencyBalance!: BigDecimal;

    @jsonMember({ serializer: (value: BigDecimal) => value.toPlainString(), deserializer: (value: any) => new Big(value) })
    ccuBalance!: BigDecimal;

    constructor() {
        makeAutoObservable(this);
    }
}

