import axios from "axios";
import { Big, BigDecimal } from 'bigdecimal.js';

export async function exchange(sourceCurrencyUnit: string, sourceCurrencyBalance: BigDecimal) {
    const { data } = await axios.post("/lumen/exchange", null, {
        params: {
            sourceCurrencyUnit,
            sourceCurrencyBalance: sourceCurrencyBalance.toString()
        }
    });
    return new Big(data);
}

export async function exchangePreview(sourceCurrencyUnit: string, sourceCurrencyBalance: BigDecimal) {
    const { data } = await axios.post("/lumen/exchange/preview", null, {
        params: {
            sourceCurrencyUnit,
            sourceCurrencyBalance: sourceCurrencyBalance.toString()
        }
    });
    return new Big(data);
}