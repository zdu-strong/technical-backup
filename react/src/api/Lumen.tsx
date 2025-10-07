import axios from "axios";
import { TypedJSON } from "typedjson";

export async function exchange(sourceCurrencyUnit: string, sourceCurrencyBalance: number) {
  const { data } = await axios.post("/lumen/exchange", null, {
    params: {
      sourceCurrencyUnit,
      sourceCurrencyBalance
    }
  });
  return Number(new TypedJSON(Number).parse(data)!);
}

export async function exchangePreview(sourceCurrencyUnit: string, sourceCurrencyBalance: number) {
  const { data } = await axios.post("/lumen/exchange/preview", null, {
    params: {
      sourceCurrencyUnit,
      sourceCurrencyBalance
    }
  });
  return Number(new TypedJSON(Number).parse(data)!);
}