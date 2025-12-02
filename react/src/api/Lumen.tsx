import axios from "axios";
import { Big } from 'bigdecimal.js';

export async function exchange(sourceCurrencyUnit: string, sourceCurrencyBalance: string) {
    const { data } = await axios.post("/lumen/exchange", null, {
        params: {
            sourceCurrencyUnit,
            sourceCurrencyBalance: new Big(sourceCurrencyBalance || 0).toPlainString()
        }
    });
    return new Big(data);
}

export async function exchangePreview(sourceCurrencyUnit: string, sourceCurrencyBalance: string) {
    const { data } = await axios.post("/lumen/exchange/preview", null, {
        params: {
            sourceCurrencyUnit,
            sourceCurrencyBalance: new Big(sourceCurrencyBalance || 0).toPlainString()
        }
    });
    return new Big(data);
}