import { LumenCcuBalanceModel } from "@model/LumenCcuBalanceModel";
import axios from "axios";
import { TypedJSON } from "typedjson";

export async function exchange(sourceCurrencyUnit: string, sourceCurrencyBalance: number) {
  const { data } = await axios.post("/lumen/exchagne", null, {
    params: {
      sourceCurrencyUnit,
      sourceCurrencyBalance
    }
  });
  return new TypedJSON(LumenCcuBalanceModel).parse(data)!;
}